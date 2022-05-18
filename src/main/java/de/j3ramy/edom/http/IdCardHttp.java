package de.j3ramy.edom.http;

import de.j3ramy.edom.Utils.URLs;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class IdCardHttp {
    private static String generateIdNumber(){
        final int LENGTH = 9;
        String AlphaNumericString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder sb = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    public static boolean insertCitizen(String owner){
        String[] name = owner.split("_");
        String idCardNumber = generateIdNumber();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("vorname", name[0]);
        parameters.put("nachname", name[1]);
        parameters.put("idNumber", idCardNumber);

        return HttpHelper.executeQuery(URLs.CREATE_CITIZEN_URL, parameters);
    }

    public static String[] getCitizenData(String owner){
        try{
            String[] name = owner.split("_");

            URL url = new URL(URLs.GET_CITIZEN_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);

            Map<String, String> parameters = new HashMap<>();
            parameters.put("vorname", name[0]);
            parameters.put("nachname", name[1]);

            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(HttpHelper.getParamsString(parameters));
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String data = in.readLine();

            in.close();
            con.disconnect();

            return data.split("#");
        }
        catch(Exception e){
            return new String[0];
        }
    }
}
