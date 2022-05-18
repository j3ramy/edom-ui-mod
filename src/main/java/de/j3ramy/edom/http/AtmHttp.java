package de.j3ramy.edom.http;

import de.j3ramy.edom.Utils.URLs;
import net.minecraft.util.math.BlockPos;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AtmHttp {
    public static void createAtmInDatabase(BlockPos pos){
        String position = "x" + pos.getX() + "y" + pos.getY() + "z" + pos.getZ();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("pos", position);

        HttpHelper.executeQuery(URLs.CREATE_ATM_URL, parameters);
    }

    public static void deleteAtmInDatabase(BlockPos pos){
        String position = "x" + pos.getX() + "y" + pos.getY() + "z" + pos.getZ();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("pos", position);

        HttpHelper.executeQuery(URLs.DELETE_ATM_URL, parameters);
    }

    public static boolean sendWithdrawData(BlockPos pos, int value){
        String position = "x" + pos.getX() + "y" + pos.getY() + "z" + pos.getZ();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("pos", position);
        parameters.put("value", Integer.toString(value));

        return HttpHelper.executeQuery(URLs.SET_WITHDRAW_VALUE_URL, parameters);
    }

    public static int getWithdrawValue(BlockPos pos){
        try{
            String position = "x" + pos.getX() + "y" + pos.getY() + "z" + pos.getZ();

            URL connectionUrl = new URL(URLs.GET_WITHDRAW_VALUE_URL + "pos=" + position);
            HttpURLConnection con = (HttpURLConnection) connectionUrl.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            Map<String, String> parameters = new HashMap<>();

            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(HttpHelper.getParamsString(parameters));
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            int withdrawValue = Integer.parseInt(in.readLine());
            in.close();

            con.disconnect();

            if(withdrawValue == HttpHelper.ERROR_CODE){
                return 0;
            }

            return withdrawValue;
        }
        catch(Exception ignored){
            return 0;
        }
    }

    public static String[] getAccountData(String owner){
        try{
            URL url = new URL(URLs.GET_ACC_DATA__URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);

            Map<String, String> parameters = new HashMap<>();
            parameters.put("owner", owner.replace(" ", "_"));

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

    public static void withdraw(String owner, int value, BlockPos pos){
        String position = "x" + pos.getX() + "y" + pos.getY() + "z" + pos.getZ();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("owner", owner.replace(" ", "_"));
        parameters.put("value", Integer.toString(value));
        parameters.put("pos", position);

        HttpHelper.executeQuery(URLs.WITHDRAW_URL, parameters);
    }

    public static void deposit(String owner, int value, BlockPos pos){
        String position = "x" + pos.getX() + "y" + pos.getY() + "z" + pos.getZ();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("owner", owner.replace(" ", "_"));
        parameters.put("value", Integer.toString(value));
        parameters.put("pos", position);

        HttpHelper.executeQuery(URLs.DEPOSIT_URL, parameters);
    }
}
