package com.example.capstoneback.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@RequiredArgsConstructor
public class AwsConfig {
    private final Environment env;

    @Bean
    public SesClient sesClient() {
        String accessKey = env.getProperty("spring.aws.ses.access-id");
        String secretKey = env.getProperty("spring.aws.ses.secret-key");
        String region = env.getProperty("spring.aws.region", "ap-northeast-2");

        // 디버깅을 위한 환경 변수 출력 (필수!)
        System.out.println("AWS_ACCESS_KEY_ID: " + accessKey);
        System.out.println("AWS_SECRET_ACCESS_KEY: " + secretKey);
        System.out.println("AWS_REGION: " + region);

        // 환경 변수 값이 null이면 예외 발생
        if (accessKey == null || secretKey == null) {
            throw new IllegalArgumentException("AWS_ACCESS_KEY_ID 또는 AWS_SECRET_ACCESS_KEY가 설정되지 않았습니다!");
        }

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        return SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}
