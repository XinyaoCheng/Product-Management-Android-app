package com.example.a82.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.a82.model.ProductModel;
import com.google.common.io.BaseEncoding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TranslateUtil{
    private String API_KEY = "AIzaSyCxDc0s5_Qor-_kzKXCfof5esTuwR7csxI";
    private static String REQUEST_URL = "https://www.googleapis.com/language/translate/v2";
    private static String PACKAGE_KEY = "X-Android-Package";
    private static String SHA1_KEY = "X-Android-Cert";
    private String result = "";

    public String translate(Context context,String content){
        JSONArray array = new JSONArray();
        array.put(content);
        OkHttpClient okHttpClient  = new OkHttpClient();
        String signiture = getSignature(context.getPackageManager(), context.getPackageName());
        FormBody.Builder builder = new FormBody.Builder()
                .add("key",API_KEY)
                .add("target", "en")
                .add("q",content);
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(REQUEST_URL)
                .header(PACKAGE_KEY,context.getPackageName())
                .header(SHA1_KEY,signiture)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("翻译错误", e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject response_json = new JSONObject(res);
                    Log.v("翻译相应结果",response_json.toString());
                    JSONObject resObject = response_json.getJSONObject("data");
                    JSONArray translations = resObject.getJSONArray("translations");
                    result = translations.getJSONObject(0).getString("translatedText");
                    Log.v("翻译结果",result);

                } catch (JSONException e) {
                    Log.e("翻译错误",e.getMessage());
                }
            }
        });
        
                

        return result;
    }

    private String getSignature(PackageManager packageManager, String packageName) {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo == null
                    || packageInfo.signatures == null
                    || packageInfo.signatures.length == 0
                    || packageInfo.signatures[0] == null) {
                return null;
            }
            return signatureDigest(packageInfo.signatures[0]);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }


    }

    private String signatureDigest(Signature sig) {
        byte[] signature = sig.toByteArray();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(signature);
            return BaseEncoding.base16().lowerCase().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

    }
}