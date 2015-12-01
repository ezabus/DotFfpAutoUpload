package org.zabus.dotffp;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.List;

/**
 * Created by Egor on 01.12.2015.
 */
public class HttpClient {

    public BasicCookieStore cookieStore;

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(BasicCookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public HttpResponse executeGet(String url, String urlParams) {
        HttpResponse response = null;
        try {
            CloseableHttpClient httpClient = getHttpClient(cookieStore);
            HttpGet httpGet = new HttpGet(url + urlParams);
            response = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public HttpResponse executePost(String url, List<NameValuePair> nvps, CloseableHttpClient httpClient) {
        HttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public CloseableHttpClient getHttpClient(BasicCookieStore cookieStore) {
        return HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    public CloseableHttpClient getHttpClient() {
        cookieStore = new BasicCookieStore();
        return HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
    }


}
