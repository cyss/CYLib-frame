package com.cyss.android.lib.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by cyjss on 2015/8/10.
 */
public class HttpUtils {

    private int connectTimeout = 20 * 1000;
    private int requestTimeout = 20 * 1000;
    private static final BasicHttpParams httpParams = new BasicHttpParams();
    private static final SchemeRegistry supportedSchemes = new SchemeRegistry();
    private String reqUrl = null;

    public static HttpUtils create(String url) {
        return new HttpUtils(url);
    }

    private HttpUtils() {
    }

    private HttpUtils(String url) {
        HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeout);
        HttpConnectionParams.setSoTimeout(httpParams, requestTimeout);
        supportedSchemes.register(new Scheme("http",
                PlainSocketFactory.getSocketFactory(), 80));
        this.reqUrl = url;
    }

    public void setSSLInfo(String password, InputStream cer) {

    }

    public String doPostWithFile(Map<String, Object> params) throws Exception {
        String strResult = null;
        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        for (String key : params.keySet()) {
            Object val = params.get(key);
            if (val instanceof File) {
                entity.addBinaryBody(key, (File) params.get(key));
            } else if (val instanceof String) {
                entity.addTextBody(key, (String) params.get(key));
            } else if (val instanceof byte[]) {
                entity.addBinaryBody(key, (byte[]) params.get(key));
            }
        }
        HttpPost post = new HttpPost(reqUrl);
        post.setEntity(entity.build());
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpResponse res = httpClient.execute(post);
        if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            strResult = EntityUtils.toString(res.getEntity(), "utf-8");
            strResult = URLDecoder.decode(strResult, "utf-8");
        } else {
            throw new Exception(res.getStatusLine().toString());
        }
        return strResult;
    }

    public String doPostSingleFile(File file, String contentType) throws Exception {
        String strResult = null;
        FileEntity fe = new FileEntity(file, contentType);
        HttpPost post = new HttpPost(reqUrl);
        post.setEntity(fe);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpResponse res = httpClient.execute(post);
        if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            strResult = EntityUtils.toString(res.getEntity(), "utf-8");
            strResult = URLDecoder.decode(strResult, "utf-8");
        } else {
            throw new Exception(res.getStatusLine().toString());
        }
        return strResult;
    }

    public String doBytesPost(byte[] bytes) throws Exception {
        String strResult = null;
        ByteArrayEntity bae = new ByteArrayEntity(bytes);
        HttpPost post = new HttpPost(reqUrl);
        post.setEntity(bae);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpResponse res = httpClient.execute(post);
        if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            strResult = EntityUtils.toString(res.getEntity(), "utf-8");
            strResult = URLDecoder.decode(strResult, "utf-8");
        } else {
            throw new Exception(res.getStatusLine().toString());
        }
        return strResult;
    }

    public String doStringPost(String text) throws Exception {
        String strResult = null;
        StringEntity se = new StringEntity(text, "UTF-8");
        HttpPost post = new HttpPost(reqUrl);
        post.setEntity(se);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpResponse res = httpClient.execute(post);
        if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            strResult = EntityUtils.toString(res.getEntity(), "utf-8");
            strResult = URLDecoder.decode(strResult, "utf-8");
        } else {
            throw new Exception(res.getStatusLine().toString());
        }
        return strResult;
    }

    public String doNormalPost(Map<String, String> args) throws Exception {
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpPost post = new HttpPost(reqUrl);
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
        } else {
            throw new Exception(response.getStatusLine().toString());
        }
        return strResult;
    }

    public String doNormalGet(Map<String, String> args) throws Exception {
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpGet get = new HttpGet(reqUrl);
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
        } else {
            throw new Exception(response.getStatusLine().toString());
        }
        return strResult;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}
