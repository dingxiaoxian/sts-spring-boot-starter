package tech.xiaoxian.aliyun.sts;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import tech.xiaoxian.aliyun.sts.model.Credentials;
import tech.xiaoxian.aliyun.sts.model.Policy;

public class AliyunStsTool {
    public static final String DEFAULT_REGION_ID = "";
    public static final long DEFAULT_DURATION_SECONDS = 3600L;
    private final String endpoint;
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String roleArn;
    private final String regionId;
    private long durationSeconds = DEFAULT_DURATION_SECONDS;

    public AliyunStsTool(AliyunStsProperties properties) {
        this.endpoint = properties.getEndpoint();
        this.accessKeyId = properties.getAccessKeyId();
        this.accessKeySecret = properties.getAccessKeySecret();
        this.roleArn = properties.getRoleArn();

        if (properties.getRegionId() != null) {
            this.regionId = properties.getRegionId();
        } else {
            this.regionId = DEFAULT_REGION_ID;
        }

        if (properties.getDurationSeconds() != null) {
            this.durationSeconds = properties.getDurationSeconds();
        }
    }

    public Credentials assumeRole(Policy policy, String sessionName) throws ClientException {
        return assumeRole(policy.toString(), sessionName);
    }

    public Credentials assumeRole(Policy policy, String sessionName, long durationSeconds) throws ClientException {
        return assumeRole(policy.toString(), sessionName, durationSeconds);
    }

    public Credentials assumeRole(String endpoint, String accessKeyId, String accessKeySecret,
                                  String roleArn, String regionId,
                                  Policy policy, String sessionName, Long durationSeconds) throws ClientException {
        return assumeRole(endpoint, accessKeyId, accessKeySecret, roleArn, regionId,
                policy.toString(), sessionName, durationSeconds);
    }

    public Credentials assumeRole(String policy, String sessionName) throws ClientException {
        return requestAssumeRole(endpoint, accessKeyId, accessKeySecret, roleArn, regionId,
                policy, sessionName, durationSeconds);
    }

    public Credentials assumeRole(String policy, String sessionName, long durationSeconds) throws ClientException {
        return requestAssumeRole(endpoint, accessKeyId, accessKeySecret, roleArn, regionId,
                policy, sessionName, durationSeconds);
    }

    public Credentials assumeRole(String endpoint, String accessKeyId, String accessKeySecret,
                                  String roleArn, String regionId,
                                  String policy, String sessionName, Long durationSeconds) throws ClientException {
        endpoint = endpoint == null ? this.endpoint : endpoint;
        accessKeyId = accessKeyId == null ? this.accessKeyId : accessKeyId;
        accessKeySecret = accessKeySecret == null ? this.accessKeySecret : accessKeySecret;
        roleArn = roleArn == null ? this.roleArn : roleArn;
        regionId = regionId == null ? this.regionId : regionId;
        durationSeconds = durationSeconds == null ? this.durationSeconds : durationSeconds;
        return requestAssumeRole(endpoint, accessKeyId, accessKeySecret, roleArn, regionId,
                policy, sessionName, durationSeconds);
    }

    private Credentials requestAssumeRole(String endpoint, String accessKeyId, String accessKeySecret, String roleArn,
                                          String regionId, String policy,
                                          String sessionName, long durationSeconds) throws ClientException {
        // 添加endpoint
        DefaultProfile.addEndpoint(regionId, "Sts", endpoint);
        // 构造default profile。
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        // 构造client。
        DefaultAcsClient client = new DefaultAcsClient(profile);
        final AssumeRoleRequest request = new AssumeRoleRequest();
        // 适用于Java SDK 3.12.0及以上版本。
        request.setSysMethod(MethodType.POST);
        request.setRoleArn(roleArn);
        request.setRoleSessionName(sessionName);
        request.setPolicy(policy); // 如果policy为空，则用户将获得该角色下所有权限。
        request.setDurationSeconds(durationSeconds); // 设置临时访问凭证的有效时间为3600秒。
        final AssumeRoleResponse response = client.getAcsResponse(request);
        return new Credentials(response);
    }
}
