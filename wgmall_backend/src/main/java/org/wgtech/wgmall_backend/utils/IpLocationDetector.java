package org.wgtech.wgmall_backend.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IpLocationDetector {

    // 目标 API 服务
    private static final String API_URL = "http://ip-api.com/json/";

    // 根据 IP 获取国家信息
    public String getCountryByIp(String ip) {
        // 使用 OkHttpClient 发起请求
        OkHttpClient client = new OkHttpClient();

        // 构建请求URL，包含IP地址
        String url = API_URL + ip;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // 如果请求成功且响应码为200
            if (response.isSuccessful()) {
                String responseBody = response.body().string();

                // 在响应中提取国家信息（返回是 JSON 格式）
                String country = parseCountryFromJson(responseBody);
                return country;
            } else {
                throw new IOException("Error response from the server: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "无法获取国家信息";
        }
    }

    // 从 JSON 中解析国家信息
    private String parseCountryFromJson(String jsonResponse) {
        // 使用简单的正则或 JSON 解析库来提取数据
        // 这里简单使用字符串查找的方法（可以考虑使用 Jackson 或 Gson 来解析 JSON）
        String countryPrefix = "\"country\":\"";
        int startIndex = jsonResponse.indexOf(countryPrefix);
        if (startIndex != -1) {
            startIndex += countryPrefix.length();
            int endIndex = jsonResponse.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return jsonResponse.substring(startIndex, endIndex);
            }
        }
        return "未知国家";
    }

}
