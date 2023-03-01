package tech.xiaoxian.aliyun.sts.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Policy {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Action.class, new CharSequenceTypeAdapter())
            .registerTypeAdapter(Resource.class, new CharSequenceTypeAdapter())
            .registerTypeAdapter(ServiceCondition.class, new CharSequenceTypeAdapter())
            .create();

    /**
     * Aliyun Policy 版本号
     */
    @SuppressWarnings("unused")
    private final String Version = "1";
    public List<Statement> Statement;

    public Policy(List<Statement> statement) {
        this.Statement = statement;
    }

    public Policy(Statement... statement) {
        this(List.of(statement));
    }

    public static class Statement {
        public List<Action> Action;
        public List<Resource> Resource;
        public Map<Logic, Map<ServiceCondition, List<String>>> Condition;
        public Effect Effect;

        public Statement(List<Action> action, List<Resource> resource,
                         Map<Logic, Map<ServiceCondition, List<String>>> condition, Effect effect) {
            this.Action = action;
            this.Resource = resource;
            this.Condition = condition;
            this.Effect = effect;
        }
    }

    public enum Effect {
        Allow,
        Deny,
    }

    public interface IP {

    }

    public enum Logic {
        StringEquals(String.class),
        StringNotEquals(String.class),
        StringEqualsIgnoreCase(String.class),
        StringNotEqualsIgnoreCase(String.class),
        StringLike(String.class),
        StringNotLike(String.class),
        NumericEquals(Number.class),
        NumericNotEquals(Number.class),
        NumericLessThan(Number.class),
        NumericLessThanEquals(Number.class),
        NumericGreaterThan(Number.class),
        NumericGreaterThanEquals(Number.class),
        DateEquals(Date.class),
        DateNotEquals(Date.class),
        DateLessThan(Date.class),
        DateLessThanEquals(Date.class),
        DateGreaterThan(Date.class),
        DateGreaterThanEquals(Date.class),
        Bool(Boolean.class),
        IpAddress(IP.class),
        NotIpAddress(IP.class),
        ;
        public final Class<?> clazz;

        Logic(Class<?> clazz) {
            this.clazz = clazz;
        }
    }

    public static class ServiceCondition extends BaseString {
        public ServiceCondition(String ramCode, String conditionKey) {
            if (ramCode == null) {
                ramCode = "acs";
            }
            this.value = ramCode + ":" + conditionKey;
        }

        public ServiceCondition(String raw) {
            if (raw.indexOf(':') == -1) {
                raw = "acs:" + raw;
            }
            this.value = raw;
        }
    }

    public static class Action extends BaseString {
        public Action(String ramCode, String actionName) {
            this(ramCode + ":" + actionName);
        }

        public Action(String raw) {
            this.value = raw;
        }
    }

    public static class Resource extends BaseString {
        public Resource(String ramCode, String region, String accountId, String relativeId) {
            this(String.format("acs:%s:%s:%s:%s", ramCode, region, accountId, relativeId));
        }

        public Resource(String raw) {
            this.value = raw.startsWith("acs:") ? raw : "acs:" + raw;
        }
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

    private static class BaseString implements Comparable<Object> {
        protected String value;

        @Override
        public String toString() {
            return value;
        }

        @Override
        public int compareTo(Object o) {
            return value.compareTo(o.toString());
        }
    }

    private static class CharSequenceTypeAdapter extends TypeAdapter<BaseString> {
        @Override
        public void write(JsonWriter out, BaseString value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public BaseString read(JsonReader in) throws IOException {
            throw new IOException("this TypeAdapter do not support read raw string");
        }
    }
}
