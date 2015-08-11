package com.cyss.android.lib.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by cyjss on 2015/8/10.
 */
public class HttpUtils {

    private int connectTimeout = 20;
    private int requestTimeout = 20;

    public String doNormalPost(String url, Map<String, String> args) throws IOException {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeout);
        HttpConnectionParams.setSoTimeout(httpParams, requestTimeout);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpPost post = new HttpPost(url);
        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        for (Map.Entry<String, String> item : args.entrySet()) {
            entity.addPart(item.getKey(), new StringBody(item.getValue(), ContentType.TEXT_PLAIN));
        }
        HttpResponse response = httpClient.execute(post);
        String strResult = null;
        int stateCode = response.getStatusLine().getStatusCode();
        if (stateCode == HttpStatus.SC_OK) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                strResult = EntityUtils.toString(responseEntity, "utf-8");
            }
        }
        return strResult;
    }

    public String doNormalGet(String url, Map<String, String> args) throws IOException {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeout);
        HttpConnectionParams.setSoTimeout(httpParams, requestTimeout);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpGet get = new HttpGet(url);
        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        for (Map.Entry<String, String> item : args.entrySet()) {
            entity.addPart(item.getKey(), new StringBody(item.getValue(), ContentType.DEFAULT_TEXT));
        }
        HttpResponse response = httpClient.execute(get);
        String strResult = null;
        int stateCode = response.getStatusLine().getStatusCode();
        if (stateCode == HttpStatus.SC_OK) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                strResult = EntityUtils.toString(responseEntity, "utf-8");
            }
        }
        return strResult;
    }

}
