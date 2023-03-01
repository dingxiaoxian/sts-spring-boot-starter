# 阿里云STS授权Spring Boot Starter

[maven中央仓库地址](https://search.maven.org/artifact/tech.xiaoxian.aliyun/sts-spring-boot-starter)

## usage

### maven

```xml

<dependency>
    <groupId>tech.xiaoxian.aliyun</groupId>
    <artifactId>sts-spring-boot-starter</artifactId>
    <version>1.0.4</version>
    <type>pom</type>
</dependency>
```

### gradle

```groovy
implementation 'tech.xiaoxian.aliyun:sts-spring-boot-starter:1.0.4'
```

## starter 配置方法

根据[官方文档](https://help.aliyun.com/document_detail/100624.html)在阿里云控制台中进行相应配置

```yaml
xiaoxian:
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

    // policy and policyStr in below functions are equivalences
    public Credentials getCredentialsByPolicy() throws ClientException {
        Policy policy = new Policy(
                List.of(
                        new Policy.Statement(
                                List.of(
                                        new Policy.Action("oss", "PutObject")
                                ),
                                List.of(
                                        new Policy.Resource("oss", "*", "*", "*")
                                ),
                                Map.of(
                                        Policy.Logic.IpAddress,
                                        Map.of(
                                                new Policy.ServiceCondition("acs", "SourceIp"),
                                                List.of("0.0.0.0/32") // 所有ip
                                        )
                                ),
                                Policy.Effect.Allow
                        )
                )
        );
        return stsTool.assumeRole(policy, "your_session_name");
    }

    public Credentials getCredentialsByPolicyStr() throws ClientException {
        String policyStr = """
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
        return stsTool.assumeRole(policyStr, "your_session_name");
    }
}
```