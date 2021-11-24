package io.github.ivanrosw.fakerest.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class JsonUtils {

    private ObjectMapper mapper;

    @PostConstruct
    private void init() {
        mapper = new ObjectMapper();
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

    public ArrayNode toArrayNode(String json) {
        return toJson(json, ArrayNode.class);
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
}
