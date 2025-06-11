package com.filmus.backend.common.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchConfig {

    @Value("${opensearch.host}")
    private String host;

    @Value("${opensearch.port:443}")  // 기본 HTTPS 포트 443
    private int port;

    @Value("${opensearch.username}")
    private String username;

    @Value("${opensearch.password}")
    private String password;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password)
        );

        RestClientBuilder builder = RestClient.builder(
                        new HttpHost(host, port, "https")  // HTTPS 사용
                )
                .setHttpClientConfigCallback((HttpAsyncClientBuilder httpClientBuilder) ->
                        httpClientBuilder
                                .setDefaultCredentialsProvider(credsProvider)
                                .setKeepAliveStrategy((response, context) -> 120_000)
                )
                .setRequestConfigCallback(req -> req
                        .setConnectTimeout(30_000)
                        .setSocketTimeout(600_000)      // 10분
                );

        return new RestHighLevelClient(builder);
    }
}