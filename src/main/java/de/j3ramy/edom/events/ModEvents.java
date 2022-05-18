package de.j3ramy.edom.events;

import de.j3ramy.edom.EdomMod;
import de.j3ramy.edom.Utils.Faction;
import de.j3ramy.edom.Utils.Factions;
import de.j3ramy.edom.Utils.Server;
import de.j3ramy.edom.commands.CityHallCommands;
import de.j3ramy.edom.commands.EdomBankCommands;
import de.j3ramy.edom.http.ServerEventsHttp;
import de.j3ramy.edom.item.PoliceGloves;
import de.j3ramy.edom.item.ServiceCard;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = EdomMod.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event){
        new EdomBankCommands(event.getDispatcher());
        //new AdjustAtm(event.getDispatcher());
        new CityHallCommands(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event){

        if(event.getEntity() instanceof PlayerEntity){
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

            if(!Server.IS_DEBUG){
                if(!Server.isEdomServer(player))
                    return;
            }

            //if(player.getEntityWorld().isRemote()){
                //if(Minecraft.getInstance().gameSettings.guiScale != 4)
                //    player.connection.disconnect(new StringTextComponent(TextFormatting.BLUE + "" + TextFormatting.BOLD +
                //            "\nFast geschafft!\n\n" + TextFormatting.WHITE + "Bitte stelle dein GUI Scale auf 4\n\n"));
            //}

            /*
            if(!ServerEventsHttpMethods.isPlayerWhitelisted(player.getUniqueID().toString()))
                player.connection.disconnect(new StringTextComponent(TextFormatting.BLUE + "" + TextFormatting.BOLD +
                        "\nDu bist nicht gewhitelisted!\n\n" + TextFormatting.WHITE + "Melde dich im Support\n\n"));
            else if(ServerEventsHttpMethods.isPlayerDead(player.getUniqueID().toString()))
                player.connection.disconnect(new StringTextComponent(TextFormatting.BLUE + "" + TextFormatting.BOLD +
                        "\nDu bist bewusstlos!\n\n" + TextFormatting.WHITE + "Melde dich im Support\n\n"));
            else{
                ServerEventsHttpMethods.insertLogin(player.getScoreboardName());
            }

             */
        }
    }

    @SubscribeEvent
    public static void onPlayerLeftServer(PlayerEvent.PlayerLoggedOutEvent event){
        if(event.getEntity() instanceof PlayerEntity){
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

            if(!Server.IS_DEBUG){
                if(!Server.isEdomServer(player))
                    return;
            }

            if(Server.isPlayerWhitelisted(player.getUniqueID().toString()) && !Server.isPlayerDead(player.getUniqueID().toString())){
                ServerEventsHttp.insertLogout(player.getScoreboardName());
            }
        }

    }
    /*
    @SubscribeEvent
    public static void onChatMessageSend(ServerChatEvent event){

        System.out.println("---------------------------------" + event.getMessage());
    }

    //Für Container/Inevntory logging
    @SubscribeEvent
    public static void onCloseContainer(PlayerContainerEvent event){
        System.out.println("------------------------------------Container placed");
    }

    @SubscribeEvent
    public static void onPlayerPickup(EntityItemPickupEvent event){

    }

    //Für Container Platzierung
    @SubscribeEvent
    public static void onContainerPlace(BlockEvent.EntityPlaceEvent event){

    }
    */

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event){

        if(event.getEntity() instanceof PlayerEntity){
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();

            if(!Server.IS_DEBUG){
                if(!Server.isEdomServer(player))
                    return;

                player.connection.disconnect(new StringTextComponent(TextFormatting.BLUE + "" + TextFormatting.BOLD +
                        "\nDu bist bewusstlos!\n\n" + TextFormatting.WHITE + "Melde dich im Support\n\n"));

                ServerEventsHttp.setPlayerDead(event.getEntity().getUniqueID().toString(), event.getSource().getDeathMessage(player).getString());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event){

        if(event.getPlayer().getEntityWorld().isRemote)
            return;

        if(event.getHand() != Hand.MAIN_HAND)
            return;

        if(event.getTarget() instanceof PlayerEntity){
            //search player as police member
            if(event.getPlayer().getHeldItemMainhand().getItem() instanceof PoliceGloves){
                PlayerEntity player = event.getPlayer();
                if(Faction.isInFraction(player.getUniqueID().toString(), Factions.police)){
                    PlayerInventory targetInv = ((PlayerEntity) event.getTarget()).inventory;

                    player.sendMessage(new StringTextComponent(TextFormatting.WHITE + "---------------------------------------------------"), player.getUniqueID());
                    for(int i = 0; i < targetInv.getSizeInventory(); i++){

                        ItemStack itemStack = targetInv.getStackInSlot(i);
                        if(itemStack != ItemStack.EMPTY){
                            player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "" + itemStack.getCount() + "x "
                                    + itemStack.getItem().getDisplayName(itemStack).getString()), player.getUniqueID());
                        }
                    }
                }
            }

            //show clicked player service card (players that are not in faction doesnt show anything to the target player)
            if(event.getPlayer().getHeldItemMainhand().getItem() instanceof ServiceCard){
                PlayerEntity player = event.getPlayer();
                String uuid = player.getUniqueID().toString();
                if(Faction.isInFraction(uuid, Factions.city_hall) || Faction.isInFraction(uuid, Factions.justice)){
                   PlayerEntity clickedPlayer = (PlayerEntity) event.getEntity();
                   ItemStack card = event.getPlayer().getHeldItemMainhand();

                   if(!card.hasTag())
                       return;

                   if(!card.getTag().contains("division") || !card.getTag().contains("owner"))
                       return;

                   clickedPlayer.sendMessage(new StringTextComponent("-----DIENSTAUSWEIS-----"), clickedPlayer.getUniqueID());
                   clickedPlayer.sendMessage(new StringTextComponent("Abteilung: " + card.getTag().getString("division")), clickedPlayer.getUniqueID());
                   clickedPlayer.sendMessage(new StringTextComponent("Name: " + card.getTag().getString("owner")), clickedPlayer.getUniqueID());
                   clickedPlayer.sendMessage(new StringTextComponent("-----------------------"), clickedPlayer.getUniqueID());
                }
            }
        }
    }
}
