# 阿里云STS授权Spring Boot Starter

## starter 配置方法
根据[官方文档](https://help.aliyun.com/document_detail/100624.html)在阿里云控制台中进行相应配置
```yaml
aliyun:
  sts:
    access-key-id: "RAM用户的accessKeyId"
    access-key-secret: "RAM用户的accessKeySecret"
    endpoint: "例如sts.cn-hangzhou.aliyuncs.com"
    role-arn: "RAM角色的ARN配置"
    region-id: "例如cn-hangzhou，也可以为空"
    duration-seconds: 3600 # 数字，为授权有效期秒数
```
## starter 使用方法
```java
@Service
public class YourService {
    @Resource
    private AliyunStsTool stsTool;
    private String policyStr = """
            {
              "Version": "1",
              "Statement": [
                {
                  "Action": [
                    "oss:PutObject"
                  ],
                  "Resource": [
                    "acs:oss:*:*:*"
                  ],
                  "Condition": {
                    "IpAddress": {
                      "acs:SourceIp": [
                        "0.0.0.0/32"
                      ]
                    }
                  },
                  "Effect": "Allow"
                }
              ]
            }
            """;
    private Policy policy = new Policy(
            Collections.singletonList(
                    new Policy.Statement(
                            Collections.singletonList(
                                    new Policy.Action("oss", "PutObject")
                            ),
                            Collections.singletonList(
                                    new Policy.Resource("oss", "*", "*", "*")
                            ),
                            Collections.singletonMap(
                                    Policy.Logic.IpAddress,
                                    Collections.singletonMap(
                                            new Policy.ServiceCondition("acs", "SourceIp"),
                                            Collections.singletonList("0.0.0.0/32") // 所有ip
                                    )
                            ),
                            Policy.Effect.Allow
                    )
            )
    );
    public Credentials getCredentials() throws ClientException {
        Credentials credentials = stsTool.assumeRole(policy, "your_session_name");
        // above and below statement are equivalence
        // Credentials credentials = stsTool.assumeRole(policyStr, "your_session_name");
        return credentials;
    }
}
```