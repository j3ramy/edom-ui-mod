package de.j3ramy.edomui.debug;

import de.j3ramy.edomui.EdomUiMod;
import de.j3ramy.edomui.gui.screen.CustomScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EdomUiMod.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void onMouseClickEvent(GuiScreenEvent.MouseClickedEvent event){
        if(CustomScreen.screen != null)
            CustomScreen.screen.onClick(event.getButton());
    }

    @SubscribeEvent
    public static void onKeyPressedEvent(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event){
        if(CustomScreen.screen != null)
            CustomScreen.screen.onKeyPressed(event.getKeyCode());
    }

    @SubscribeEvent
    public static void onCharTypedEvent(GuiScreenEvent.KeyboardCharTypedEvent.Post event){
        if(CustomScreen.screen != null)
            CustomScreen.screen.onCharTyped(event.getCodePoint());
    }

    @SubscribeEvent
    public static void onMouseScrollEvent(GuiScreenEvent.MouseScrollEvent event){
        if(CustomScreen.screen != null)
            CustomScreen.screen.onScroll((int) event.getScrollDelta());
    }
}
