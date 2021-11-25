package io.github.ivanrosw.fakerest.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Component
public class JsonUtils {

    private ObjectMapper mapper;

    @PostConstruct
    private void init() {
        mapper = new ObjectMapper();
    }

    public ObjectNode createJson() {
        return mapper.createObjectNode();
    }

    public ArrayNode createArray() {
        return mapper.createArrayNode();
    }

    private <T> T toJson(String json, Class<T> type) {
        T result = null;
        if (json != null) {
            try {
                result = mapper.readValue(json, type);
            } catch (Exception e) {
                log.error("Error while converting string to json", e);

            }
        }
        return result;
    }

    public JsonNode toJsonNode(String json) {
        return toJson(json, JsonNode.class);
    }

    public ObjectNode toObjectNode(String json) {
        return toJson(json, ObjectNode.class);
    }

    public String getString(JsonNode json, String key) {
        String result = null;
        if (json != null && json.has(key)) {
            JsonNode value = json.get(key);
            if (!value.isNull()) {
                result = value.asText();
            }
        }
        return result;
    }

    public void putString(ObjectNode json, String key, String value) {
        if (json != null && key != null && !key.isBlank()) {
            json.put(key, value);
        } else {
            if (log.isTraceEnabled()) log.trace("Data [{}] with key [{}] not put to json [{}]", value, key, json);
        }
    }
}
