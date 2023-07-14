package com.Pie4u.animalcare;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;

public class DownloadMapUrls {

    public String readsurl(String myurl) throws IOException{
    String data="";
        InputStream inputStream=null;
        HttpURLConnection httpURLConnection=null;

        try{
            URL url=new URL(myurl);
            httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream=httpURLConnection.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb=new StringBuffer();

            String line="";
            while((line=br.readLine())!=null){
                sb.append(line);

            }
            data=sb.toString();
            br.close();
        }catch (MalformedInputException e){
            Log.i("DownloadUrl", "readsurl: "+e.getMessage());

        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return data;
    }
}
