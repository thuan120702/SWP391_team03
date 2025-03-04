/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author ptd
 */
public class Utilities {

    public static boolean isPositiveNumber(String str) {
        Pattern pattern = Pattern.compile("^\\d+(\\.\\d+)?$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
     public static String calculateHMac(String data, String algorithm, String key) throws Exception {
            Mac Hmac = Mac.getInstance(algorithm);
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), algorithm);
            Hmac.init(secret_key);

            return byteArrayToHex(Hmac.doFinal(data.getBytes("UTF-8")));
      }

      public static String byteArrayToHex(byte[] a) {
            StringBuilder sb = new StringBuilder(a.length * 2);
            for (byte b : a)
                  sb.append(String.format("%02x", b));
            return sb.toString();
      }
      
       public static String getCurrentDateByFormat(String format) {
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

            SimpleDateFormat formatter = new SimpleDateFormat(format);
            String vnp_CreateDate = formatter.format(cld.getTime());

            return vnp_CreateDate;
      }
       
        public static String getExpireDate(String format) {
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            cld.add(Calendar.MINUTE, 15);
            return formatter.format(cld.getTime());
      }
        
        public static String hmacSHA512(final String key, final String data) {
            try {

                  if (key == null || data == null) {
                        throw new NullPointerException();
                  }
                  final Mac hmac512 = Mac.getInstance("HmacSHA512");
                  byte[] hmacKeyBytes = key.getBytes();
                  final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
                  hmac512.init(secretKey);
                  byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
                  byte[] result = hmac512.doFinal(dataBytes);
                  StringBuilder sb = new StringBuilder(2 * result.length);
                  for (byte b : result) {
                        sb.append(String.format("%02x", b & 0xff));
                  }
                  return sb.toString();

            } catch (Exception ex) {
                  return "";
            }
      }
}
