package de.j3ramy.economy.container;

import de.j3ramy.economy.utils.CreditCardData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nullable;

public class CreditCardContainer extends Container {

    public CreditCardContainer(int windowId){
        super(ModContainers.CREDIT_CARD_CONTAINER.get(), windowId);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
