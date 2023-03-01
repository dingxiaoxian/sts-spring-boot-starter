package tech.xiaoxian.aliyun.sts.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolicyTest {

    @Test
    void toJsonString() {
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

        String expectedPolicyStr = """
                {"Version":"1","Statement":[{"Action":["oss:PutObject"],"Resource":["acs:oss:*:*:*"],"Condition":{"IpAddress":{"acs:SourceIp":["0.0.0.0/32"]}},"Effect":"Allow"}]}
                """.strip();
        assertEquals(expectedPolicyStr, policy.toString());
    }
}