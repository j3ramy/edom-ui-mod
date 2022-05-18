package de.j3ramy.edom.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.j3ramy.edom.Utils.Faction;
import de.j3ramy.edom.Utils.Factions;
import de.j3ramy.edom.Utils.Server;
import de.j3ramy.edom.http.IdCardHttp;
import de.j3ramy.edom.item.IdCard;
import de.j3ramy.edom.item.ServiceCard;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityHallCommands {

    public CityHallCommands(CommandDispatcher<CommandSource> dispatcher){

        dispatcher.register(Commands.literal("idcard").then(Commands.argument("Vorname_Nachname", StringArgumentType.word())
               .executes(commandContext -> createIdCard(commandContext.getSource(), StringArgumentType.getString(commandContext, "Vorname_Nachname")))));


        dispatcher.register(Commands.literal("service_card").then(Commands.literal("city_hall").executes((p_198861_0_) -> {
            return 1;
        }).then(Commands.argument("Vorname_Nachname", StringArgumentType.word()).executes((p_198864_0_) -> {
            return createServiceCard(p_198864_0_.getSource(), StringArgumentType.getString(p_198864_0_, "Vorname_Nachname"), "Rathaus");
        }))).then(Commands.literal("justice").executes((p_198860_0_) -> {
            return 1;
        }).then(Commands.argument("Vorname_Nachname", StringArgumentType.word()).executes((p_198866_0_) -> {
            return createServiceCard(p_198866_0_.getSource(), StringArgumentType.getString(p_198866_0_, "Vorname_Nachname"), "Justiz");
        }))));
    }

    private int createIdCard(CommandSource source, String owner) throws CommandSyntaxException{

        ServerPlayerEntity player = source.asPlayer();

        if(!Server.IS_DEBUG){
            if(!Server.isEdomServer(player))
                return 1;

            if(source.getServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile()) == null){
                if(!Faction.isInFraction(player.getUniqueID().toString(), Factions.city_hall)){
                    player.sendMessage(new StringTextComponent(TextFormatting.RED + "Keine Berechtigung"), player.getUniqueID());
                    return 1;
                }
            }
        }

        if(!hasOwner(owner)){
            player.sendMessage(new StringTextComponent(TextFormatting.RED + "Bitte gebe einen Inhaber an"), player.getUniqueID());
            return 1;
        }

        if(owner.contains("_")){
            ItemStack card = player.inventory.getCurrentItem();
            if(card.getItem() instanceof IdCard){
                player.sendMessage(new StringTextComponent(TextFormatting.YELLOW + "Ausweis wird gedruckt..."), player.getUniqueID());

                if(IdCardHttp.insertCitizen(owner))
                    player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Neuer Einwohner erfolgreich registriert"), player.getUniqueID());
                else
                    player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Vorhandenen Einwohner neu zugewiesen"), player.getUniqueID());

                CompoundNBT nbt = new CompoundNBT();
                nbt.putString("owner", owner);
                card.setTag(nbt);
            }
            else{
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "Nimm einen Ausweis in die Hand"), player.getUniqueID());
            }
        }
        else{
            player.sendMessage(new StringTextComponent(TextFormatting.RED + "Falsches Format"), player.getUniqueID());
        }

        return 1;
    }
    private int createServiceCard(CommandSource source, String owner, String division) throws CommandSyntaxException{

        ServerPlayerEntity player = source.asPlayer();

        if(!Server.IS_DEBUG){
            if(!Server.isEdomServer(player))
                return 1;

            if(source.getServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile()) == null){
                if(!Faction.isInFraction(player.getUniqueID().toString(), Factions.city_hall)){
                    player.sendMessage(new StringTextComponent(TextFormatting.RED + "Keine Berechtigung"), player.getUniqueID());
                    return 1;
                }
            }
        }

        if(!hasOwner(owner)){
            player.sendMessage(new StringTextComponent(TextFormatting.RED + "Bitte gebe einen Inhaber an"), player.getUniqueID());
            return 1;
        }

        if(owner.contains("_")){
            ItemStack card = player.inventory.getCurrentItem();
            if(card.getItem() instanceof ServiceCard){
                player.sendMessage(new StringTextComponent(TextFormatting.YELLOW + "Dienstausweis wird gedruckt..."), player.getUniqueID());

                if(card.hasTag() && card.getTag().contains("owner")){
                    player.sendMessage(new StringTextComponent(TextFormatting.RED + "Dieser Dienstausweis ist bereits bedruckt"), player.getUniqueID());
                    return 1;
                }

                CompoundNBT nbt = new CompoundNBT();
                nbt.putString("owner", owner.replace("_", " "));
                nbt.putString("division", division);
                card.setTag(nbt);
            }
            else{
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "Nimm einen Dienstausweis in die Hand"), player.getUniqueID());
            }
        }
        else{
            player.sendMessage(new StringTextComponent(TextFormatting.RED + "Falsches Format"), player.getUniqueID());
        }

        return 1;
    }

    boolean hasOwner(String owner){
        return !owner.equals("");
    }

}
