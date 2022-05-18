package de.j3ramy.edom.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

import java.util.Objects;
import java.util.Random;

public class FireHose extends Item {

    public final static int MAX_HEALTH = 11;
    public final static int HOSE_USES = 5;

    public FireHose(Properties props) {

        super(props);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return MAX_HEALTH;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();

        if(context.getPlayer() == null)
            return ActionResultType.SUCCESS;

        if(MAX_HEALTH - stack.getDamage() > 1){
            extinguish(stack, context);
        }

        return ActionResultType.SUCCESS;
    }

    void extinguish(ItemStack stack, ItemUseContext context){
        //Set damage
        int damage = (MAX_HEALTH - 1) / HOSE_USES;
        stack.damageItem(damage, Objects.requireNonNull(context.getPlayer()), (player) -> stack.setDamage(1));

        //Spawn particle
        Random rand = new Random();
        for(int i = 0; i <= 10; i++)
        {
            context.getWorld().addParticle(ParticleTypes.FALLING_WATER,
                    context.getPos().getX() + rand.nextDouble(),
                    context.getPos().getY() + rand.nextDouble() + .5,
                    context.getPos().getZ() + rand.nextDouble(), 0, 0, 0);
        }

        //Remove fire block and play sound
        Block block = context.getWorld().getBlockState(context.getPos()).getBlock();
        if(block.matchesBlock(Blocks.FIRE)){
            context.getWorld().playSound(null, context.getPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1);
            context.getWorld().destroyBlock(context.getPos(), false);
        }

    }

}
