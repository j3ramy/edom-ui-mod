package de.j3ramy.edom.Utils;

import de.j3ramy.edom.http.HttpHelper;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class Server {
    public static final String EDOM_SERVER_IP = "5.83.168.240";
    public static final boolean IS_DEBUG = true;


    public static boolean isEdomServer(PlayerEntity player){
        if(player.getServer() == null)
            return false;

        if(player.getServer().isSinglePlayer())
            return false;

        return player.getServer().getServerHostname().equals(EDOM_SERVER_IP);
    }

    public static boolean isPlayerWhitelisted(String uuid){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("uuid", uuid);

        return HttpHelper.executeQuery(URLs.GET_PLAYER_WHITELIST_STATE, parameters);
    }

    public static boolean isPlayerDead(String uuid){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("uuid", uuid);

        return HttpHelper.executeQuery(URLs.GET_PLAYER_DEAD_STATE, parameters);
    }


}
