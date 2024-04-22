package com.demo.filestorage.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class S3Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = S3Config.class.getResourceAsStream("/s3.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getBucketName() {
        return properties.getProperty("bucketName");
    }

    public static String getBucketRegion() {
        return properties.getProperty("region");
    }
}