package org.jurassicraft.server.entity.vehicle.modules;

import java.util.List;

import javax.vecmathimpl.Matrix4d;
import javax.vecmathimpl.Vector3d;

import org.jurassicraft.server.entity.vehicle.CarEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SeatEntity extends Entity implements IEntityAdditionalSpawnData {
    private int id;
    private float offsetX, offsetY, offsetZ;

    private int parentId;
    private CarEntity parent;

    public SeatEntity(World world) {
        super(world);
    }

    public SeatEntity(World world, CarEntity parent, int id, float offsetX, float offsetY, float offsetZ, float width, float height) {
        super(world);
        this.setSize(width, height + offsetY);
        this.id = id;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.updateParent(parent);
        this.updatePosition();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.parent == null && !this.world.isRemote) {
            this.setDead();
            return;
        } else if (this.parent != null) {
            this.updatePosition();
        }

        this.parent = null;
    }

    private void updatePosition() {
        Matrix4d matrix = new Matrix4d();
        matrix.setIdentity();
        Matrix4d transform = new Matrix4d();
        transform.setIdentity();
        transform.setTranslation(new Vector3d(this.parent.posX, this.parent.posY, this.parent.posZ));
        matrix.mul(transform);
        transform.setIdentity();
        transform.rotY(Math.toRadians(180.0F - this.parent.rotationYaw));
        matrix.mul(transform);
        transform.setIdentity();
        transform.setTranslation(new Vector3d(this.offsetX, 0.0, this.offsetZ));
        matrix.mul(transform);

        this.setPosition(matrix.m03, matrix.m13, matrix.m23);

        this.prevRotationYaw = this.parent.prevRotationYaw;
        this.rotationYaw = this.parent.rotationYaw;
    }

    @Override
    public Entity getControllingPassenger() {
        List<Entity> passengers = this.getPassengers();

        return passengers.size() > 0 ? passengers.get(0) : null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    public void updateParent(CarEntity parent) {
        this.parent = parent;
        this.parentId = parent.getEntityId();
    }

    @Override
 
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d hitPosition, EnumHand hand) {
        if (this.getControllingPassenger() == null) {
            player.startRiding(this);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    @Override
    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);

        if (this.isPassenger(passenger)) {
            passenger.rotationYaw += this.rotationYaw - this.prevRotationYaw;
            this.applyPassengerRotation(passenger);
        }
    }

    protected void applyPassengerRotation(Entity passenger) {
        passenger.setRenderYawOffset(this.rotationYaw);

        if (passenger instanceof EntityLivingBase) {
            ((EntityLivingBase) passenger).prevRenderYawOffset = this.prevRotationYaw;
        }

        float deltaYaw = MathHelper.wrapDegrees(passenger.rotationYaw - this.rotationYaw);
        float clampedDeltaYaw = MathHelper.clamp(deltaYaw, -105.0F, 105.0F);
        passenger.prevRotationYaw += clampedDeltaYaw - deltaYaw;
        passenger.rotationYaw += clampedDeltaYaw - deltaYaw;
        passenger.setRotationYawHead(passenger.rotationYaw);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void applyOrientationToEntity(Entity entityToUpdate) {
        this.applyPassengerRotation(entityToUpdate);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeByte(this.id);
        buffer.writeFloat(this.offsetX);
        buffer.writeFloat(this.offsetY);
        buffer.writeFloat(this.offsetZ);
        buffer.writeFloat(this.width);
        buffer.writeFloat(this.height);
        buffer.writeInt(this.parentId);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.id = buffer.readByte();
        this.offsetX = buffer.readFloat();
        this.offsetY = buffer.readFloat();
        this.offsetZ = buffer.readFloat();
        this.setSize(buffer.readFloat(), buffer.readFloat());

        Entity parent = this.world.getEntityByID(buffer.readInt());

        if (parent instanceof CarEntity) {
            this.parent = (CarEntity) parent;
            this.parent.addSeat(this);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return this.getEntityBoundingBox();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
    }

    @Override
    public double getMountedYOffset() {
        return this.offsetY;
    }

    public int getId() {
        return this.id;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        CarEntity parent = this.parent;
        if (parent == null) {
            List<CarEntity> cars = this.world.getEntitiesWithinAABB(CarEntity.class, this.getEntityBoundingBox());
            for (CarEntity car : cars) {
                if (car.getSeat(this.getId()) == this) {
                    parent = car;
                    break;
                }
            }
        }
        return parent != null && parent.attackEntityFrom(source, amount);
    }
}
