package tech.xiaoxian.aliyun.sts.model;

import com.aliyuncs.auth.sts.AssumeRoleResponse;

public class Credentials {
    private final String securityToken;

    private final String accessKeySecret;

    private final String accessKeyId;

    private final String expiration;

    public Credentials(String securityToken, String accessKeySecret, String accessKeyId, String expiration) {
        this.securityToken = securityToken;
        this.accessKeySecret = accessKeySecret;
        this.accessKeyId = accessKeyId;
        this.expiration = expiration;
    }

    public Credentials(AssumeRoleResponse response) {
        this(
                response.getCredentials().getSecurityToken(),
                response.getCredentials().getAccessKeySecret(),
                response.getCredentials().getAccessKeyId(),
                response.getCredentials().getExpiration()
        );
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getExpiration() {
        return expiration;
    }
}
