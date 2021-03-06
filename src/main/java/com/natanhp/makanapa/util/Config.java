package com.natanhp.makanapa.util;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingClientBuilder;
import com.linecorp.bot.client.LineSignatureValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:application.properties")
public class Config {

    @Autowired
    private Environment environment;

    @Bean(name = "com.linecorp.channel_secret")
    public String getChannelSecret() {
        return environment.getProperty("com.linecorp.channel_secret");
    }

    @Bean(name = "com.linecorp.channel_access_token")
    public String getChannelAccessToken() {
        return environment.getProperty("com.linecorp.channel_access_token");
    }

    @Bean(name = "lineMessagingClient")
    public LineMessagingClient getMessagingClient() {
        return LineMessagingClient
                .builder(getChannelAccessToken())
                .apiEndPoint(LineMessagingClientBuilder.DEFAULT_API_END_POINT)
                .connectTimeout(LineMessagingClientBuilder.DEFAULT_CONNECT_TIMEOUT)
                .readTimeout(LineMessagingClientBuilder.DEFAULT_READ_TIMEOUT)
                .writeTimeout(LineMessagingClientBuilder.DEFAULT_WRITE_TIMEOUT)
                .build();
    }

    @Bean(name = "lineSignatureValidator")
    public LineSignatureValidator getSignatureValidator() {
        return new LineSignatureValidator(getChannelSecret().getBytes());
    }
}
