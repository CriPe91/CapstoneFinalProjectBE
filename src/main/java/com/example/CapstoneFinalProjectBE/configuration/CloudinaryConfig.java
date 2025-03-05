package com.example.CapstoneFinalProjectBE.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {


    @Bean
    public com.cloudinary.Cloudinary uploaderImg(){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dwsgqdsfn");
        config.put("api_key", "546196251472678");
        config.put("api_secret", "5UQDUBYWI3AqzDlzkecCb4Ey4LY");
        return new com.cloudinary.Cloudinary(config);
    }

}
