package de.j3ramy.edom.http;

import de.j3ramy.edom.Utils.URLs;

import java.util.HashMap;
import java.util.Map;

public class ServerEventsHttp {
    public static void insertLogin(String name){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", name);

        HttpHelper.executeQuery(URLs.INSERT_LOGIN_URL, parameters);
    }

    public static void insertLogout(String name){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", name);

        HttpHelper.executeQuery(URLs.INSERT_LOGOUT_URL, parameters);
    }

    public static void setPlayerDead(String uuid, String deathCause){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("uuid", uuid);
        parameters.put("dead", "1");
        parameters.put("cause", deathCause.replace(" ", "_"));

        HttpHelper.executeQuery(URLs.SET_PLAYER_DEAD, parameters);
    }
}
