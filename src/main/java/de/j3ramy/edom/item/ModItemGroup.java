package de.j3ramy.edom.item;

import de.j3ramy.edom.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup {

    public static final ItemGroup EDOM_ECONOMY_GROUP = new ItemGroup("economy"){

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.CREDIT_CARD.get());
        }
    };

    public static final ItemGroup EDOM_SAFETY_GROUP = new ItemGroup("safety"){

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.FIRE_HOSE_BOX.get());
        }
    };

    public static final ItemGroup EDOM_POLICE_GROUP = new ItemGroup("police"){

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.POLICE_GLOVES.get());
        }
    };

    public static final ItemGroup EDOM_CITY_GROUP = new ItemGroup("city"){

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.ID_CARD.get());
        }
    };
}
