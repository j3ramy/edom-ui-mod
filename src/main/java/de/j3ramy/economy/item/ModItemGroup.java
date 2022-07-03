package de.j3ramy.economy.item;

import de.j3ramy.economy.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ModItemGroup {

    public static final ItemGroup ECONOMY_GROUP = new ItemGroup("economy"){

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.CREDIT_CARD.get());
        }
    };

}
