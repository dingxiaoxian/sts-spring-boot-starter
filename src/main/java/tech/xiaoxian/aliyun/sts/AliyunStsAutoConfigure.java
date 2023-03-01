package tech.xiaoxian.aliyun.sts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云STS自动配置类
 */
@Configuration
@ConditionalOnProperty("xiaoxian.aliyun.sts.endpoint")
@EnableConfigurationProperties(AliyunStsProperties.class)
public class AliyunStsAutoConfigure {
    private final AliyunStsProperties properties;

    @Autowired
    public AliyunStsAutoConfigure(AliyunStsProperties aliyunStsProperties) {
        this.properties = aliyunStsProperties;
    }

    @Bean
    AliyunStsTool aliyunStsTool() {
        return new AliyunStsTool(properties);
    }
}
