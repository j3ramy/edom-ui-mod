package de.j3ramy.edomui.debug;

import de.j3ramy.edomui.Main;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup {

    public static final ItemGroup DEBUG_GROUP = new ItemGroup(Main.MOD_ID){

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.DEBUG_ITEM.get());
        }
    };

}
