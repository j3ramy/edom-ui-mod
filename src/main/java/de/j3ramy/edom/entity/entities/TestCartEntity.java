package de.j3ramy.edom.entity.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TestCartEntity extends AbstractMinecartEntity {

    public TestCartEntity(EntityType<?> type, World world, double x, double y, double z) {
        super(type, world);
        System.out.println("CREATED");
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
    public EntitySize getSize(Pose p_213305_1_) {
        return new EntitySize(0.98F, 0.7F, true);
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
