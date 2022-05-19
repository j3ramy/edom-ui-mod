package de.j3ramy.edom.entity.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TestCartEntity extends AbstractMinecartEntity {

    public TestCartEntity(World world, double x, double y, double z) {
        super(EntityType.MINECART, world, x, y, z);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void applyEntityCollision(Entity entity) {

    }

    @Override
    public void addVelocity(double x, double y, double z) {

    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return .1f;
    }
}
