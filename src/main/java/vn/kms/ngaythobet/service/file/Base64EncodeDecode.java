package vn.kms.ngaythobet.service.file;

import java.util.Base64;


public class Base64EncodeDecode {
    public static String encode(String fileName)
    {
        byte[] encodedBytes = Base64.getEncoder().encode(fileName.getBytes());
        return new String(encodedBytes);
    }
    public static String decode(String fileName)
    {
        byte[] decodedBytes = Base64.getDecoder().decode(fileName.getBytes());
        return new String(decodedBytes);
    }
}
