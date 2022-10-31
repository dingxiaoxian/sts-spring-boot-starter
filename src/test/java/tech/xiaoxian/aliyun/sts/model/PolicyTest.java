package tech.xiaoxian.aliyun.sts.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolicyTest {

    @Test
    void toJsonString() {
        Policy policy = new Policy(
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

        String expectedPolicyStr = """
                {"Version":"1","Statement":[{"Action":["oss:PutObject"],"Resource":["acs:oss:*:*:*"],"Condition":{"IpAddress":{"acs:SourceIp":["0.0.0.0/32"]}},"Effect":"Allow"}]}
                """.strip();
        assertEquals(expectedPolicyStr, policy.toString());
    }
}