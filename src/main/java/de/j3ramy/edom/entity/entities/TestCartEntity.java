package de.j3ramy.edom.entity.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TestCartEntity extends AbstractMinecartEntity {

    public TestCartEntity(EntityType<?> type, World world, double x, double y, double z) {
        super(type, world);
        System.out.println("CREATED");
    }

    public TestCartEntity(EntityType<TestCartEntity> type, World world) {
        super(type, world);
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
