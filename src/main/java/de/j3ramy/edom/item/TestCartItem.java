package de.j3ramy.edom.item;

import de.j3ramy.edom.entity.entities.TestCartEntity;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

public class TestCartItem extends Item {

    private final RegistryObject<?> registryObject;

    public TestCartItem(RegistryObject<?> registryObject, Item.Properties properties) {
        super(properties);
        this.registryObject = registryObject;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState blockstate = world.getBlockState(blockpos);

        if (!blockstate.isIn(BlockTags.RAILS)) {
            return ActionResultType.FAIL;
        } else {
            ItemStack itemstack = context.getItem();

            if (!world.isRemote) {
                RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
                double yOffset = 0.0D;

                if (railshape.isAscending()) {
                    yOffset = 0.5D;
                }

                TestCartEntity cart = new TestCartEntity((EntityType<?>) this.registryObject.get(), world, (double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.0625D + yOffset, (double) blockpos.getZ() + 0.5D);

                if (itemstack.hasDisplayName()) {
                    cart.setCustomName(itemstack.getDisplayName());
                }

                world.addEntity(cart);
            }

            itemstack.shrink(1);
            return ActionResultType.func_233537_a_(world.isRemote);
        }
    }
}
