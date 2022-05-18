package de.j3ramy.edom.Utils;

import de.j3ramy.edom.http.HttpHelper;

import java.util.HashMap;
import java.util.Map;

public class Faction {
    public static boolean isInFraction(String uuid, Factions faction){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("uuid", uuid);
        parameters.put("fraktion", faction.toString());

        return HttpHelper.executeQuery(URLs.GET_FRAKTION_URL, parameters);
    }
}
