package com.cainiao.common.network.support;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.EncryptUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cainiao.common.network.config.CniaoConfigKt.NET_CONFIG_APP_KEY;


/**
 *
 */
public class CniaoUtils {

    private CniaoUtils() {
    }

    /**
     * 中文转 unicode
     *
     * @param string
     * @return
     */
    public static String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        StringBuilder unicodeBytes = new StringBuilder();
        for (char utfByte : utfBytes) {
            String hexB = Integer.toHexString(utfByte);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes.append("\\u").append(hexB);
        }
        return unicodeBytes.toString();
    }

    /**
     * unicode 转中文
     * @param string
     * @return
     */
    static Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
    public static String unicodeDecode(String string) {
        Matcher matcher = pattern.matcher(string);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(Objects.requireNonNull(matcher.group(2)), 16);
            string = string.replace(Objects.requireNonNull(matcher.group(1)), ch + "");
        }
        return string;
    }

    /**
     * 解析返回的data数据
     *
     * @param dataStr 未解密的响应数据
     * @return 解密后的数据String
     */
    @Nullable
    public static String decodeData(@Nullable String dataStr) {
        //java代码，无自动null判断，需要自行处理
        if (dataStr != null) {
            return new String(EncryptUtils.decryptBase64AES(
                    dataStr.getBytes(), NET_CONFIG_APP_KEY.getBytes(),
                    "AES/CBC/PKCS7Padding",
                    "J#y9sJesv*5HmqLq".getBytes()
            ));
        } else {
            return null;
        }
    }
}
