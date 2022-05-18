package de.j3ramy.edom.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.j3ramy.edom.Utils.Faction;
import de.j3ramy.edom.Utils.Factions;
import de.j3ramy.edom.Utils.Server;
import de.j3ramy.edom.http.AtmHttp;
import de.j3ramy.edom.http.CreditCardHttp;
import de.j3ramy.edom.item.CreditCard;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class EdomBankCommands {

    public EdomBankCommands(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(Commands.literal("creditcard").then(Commands.literal("private").executes((p_198861_0_) -> {
            return 1;
        }).then(Commands.argument("Vorname_Nachname", StringArgumentType.word()).executes((p_198864_0_) -> {
            return createCreditCard(p_198864_0_.getSource(), StringArgumentType.getString(p_198864_0_, "Vorname_Nachname"), true);
        }))).then(Commands.literal("company").executes((p_198860_0_) -> {
            return 1;
        }).then(Commands.argument("Firma", StringArgumentType.word()).executes((p_198866_0_) -> {
            return createCreditCard(p_198866_0_.getSource(), StringArgumentType.getString(p_198866_0_, "Firma"), false);
            /*
        }))).then(Commands.literal("savings").executes((p_198860_0_) -> {
            return 1;
        }).then(Commands.argument("Sparkonto", StringArgumentType.word()).executes((p_198866_0_) -> {
            return createCreditCard(p_198866_0_.getSource(), StringArgumentType.getString(p_198866_0_, "Sparkonto"), true);

             */
        }))));
    }

    private int createCreditCard(CommandSource source, String owner, boolean isPrivate) throws CommandSyntaxException{

        ServerPlayerEntity player = source.asPlayer();

        if(!Server.IS_DEBUG){
            if(!Server.isEdomServer(player))
                return 1;

            //Is player op or has permission
            if(source.getServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile()) == null){
                if(!Faction.isInFraction(player.getUniqueID().toString(), Factions.bank)){
                    player.sendMessage(new StringTextComponent(TextFormatting.RED + "Keine Berechtigung"), player.getUniqueID());
                    return 1;
                }
            }
        }

        if(!hasOwner(owner)){
            player.sendMessage(new StringTextComponent(TextFormatting.RED + "Bitte gebe einen Inhaber an"), player.getUniqueID());
            return 1;
        }

        if(!isCorrectFormat(owner, isPrivate)){
            player.sendMessage(new StringTextComponent(TextFormatting.RED + "Falsches Format"), player.getUniqueID());
            return 1;
        }

        ItemStack card = player.inventory.getCurrentItem();
        if(card.getItem() instanceof CreditCard){
            if(isPrivate && !CreditCardHttp.createBankAccount("Privat", owner) || !isPrivate && !CreditCardHttp.createBankAccount("Unternehmen", owner))
                player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Vorhandenes Konto der Kreditkarte zugewiesen"), player.getUniqueID());
            else
                player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Neues Konto der Kreditkarte zugewiesen"), player.getUniqueID());

            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("owner", owner.replace("_", " "));
            card.setTag(nbt);
        }
        else{
            player.sendMessage(new StringTextComponent(TextFormatting.RED + "Nimm eine Kreditkarte in die Hand"), player.getUniqueID());
        }

        return 1;
    }

    boolean hasOwner(String owner){
        return !owner.equals("");
    }

    boolean isCorrectFormat(String owner, boolean isPrivate){
        if(isPrivate){
            return owner.contains("_");
        }
        else{
            return !owner.contains("_");
        }
    }
}
