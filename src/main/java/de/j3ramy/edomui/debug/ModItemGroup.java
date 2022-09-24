package de.j3ramy.edomui.debug;

import de.j3ramy.edomui.EdomUiMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup {

    public static final ItemGroup DEBUG_GROUP = new ItemGroup(EdomUiMod.MOD_ID){

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.DEBUG_ITEM.get());
        }
    };

}
