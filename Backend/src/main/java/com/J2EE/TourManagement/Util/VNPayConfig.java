package com.J2EE.TourManagement.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;

public class VNPayConfig {
  @Value("${vnpay.tmnCode}") static private String vnp_TmnCode;

  @Value("${vnpay.hashSecret}") static private String vnp_HashSecret;

  @Value("${vnpay.url}") static private String vnp_Url;

  @Value("${vnpay.returnUrl}")static private String vnp_ReturnUrl;

  public static String getRandomNumber(int len) {
    Random rnd = new Random();
    String chars = "0123456789";
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      sb.append(chars.charAt(rnd.nextInt(chars.length())));
    }
    return sb.toString();
  }

  // Hàm build query URL cho VNPay
  public static String buildUrl(Map<String, String> params)
      throws UnsupportedEncodingException {
    List<String> fieldNames = new ArrayList<>(params.keySet());
    Collections.sort(fieldNames);
    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();
    Iterator<String> itr = fieldNames.iterator();

    while (itr.hasNext()) {
      String fieldName = itr.next();
      String fieldValue = params.get(fieldName);
      if ((fieldValue != null) && (fieldValue.length() > 0)) {
        hashData.append(fieldName);
        hashData.append('=');
        hashData.append(URLEncoder.encode(fieldValue, "UTF-8"));
        query.append(URLEncoder.encode(fieldName, "UTF-8"));
        query.append('=');
        query.append(URLEncoder.encode(fieldValue, "UTF-8"));
        if (itr.hasNext()) {
          query.append('&');
          hashData.append('&');
        }
      }
    }
    return query.toString();
  }
}
