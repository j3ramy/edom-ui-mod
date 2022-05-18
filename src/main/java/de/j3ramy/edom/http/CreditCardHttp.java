package de.j3ramy.edom.http;

import de.j3ramy.edom.Utils.URLs;

import java.util.HashMap;
import java.util.Map;

public class CreditCardHttp {

    private static String generateNumber(int length){
        String AlphaNumericString = "0123456789";

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    public static boolean createBankAccount(String type, String owner){
        try{
            String accountNumber = generateNumber(8);
            String pin = generateNumber(4);

            Map<String, String> parameters = new HashMap<>();
            parameters.put("type", type);
            parameters.put("owner", owner);
            parameters.put("accNr", accountNumber);
            parameters.put("pin", pin);

            return HttpHelper.executeQuery(URLs.CREATE_ACC_URL, parameters);
        }
        catch(Exception e){
            return false;
        }
    }
}
