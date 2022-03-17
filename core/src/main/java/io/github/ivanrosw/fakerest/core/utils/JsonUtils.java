/*
 * Copyright (C) 2022 Ivan Rosinskii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    public <T> ObjectNode toObjectNode(T obj) {
        ObjectNode result = null;
        if (obj != null) {
            try {
                result = mapper.convertValue(obj, ObjectNode.class);
            } catch (Exception e) {
                log.error("Error while converting {} to json", obj.getClass().getSimpleName(), e);
            }
        }
        return result;
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

    public ObjectNode getJson(ObjectNode json, String key) {
        return getJsonObject(json, key);
    }

    public ArrayNode getArray(ObjectNode json, String key) {
        return getJsonObject(json, key);
    }

    private <T> T getJsonObject(ObjectNode json, String key) {
        T result = null;
        if (json != null && json.has(key)) {
            try {
                result = (T) json.get(key);
            } catch (Exception e) {
                log.error("Error while getting json", e);
            }
        }
        return result;
    }

    public void putString(ObjectNode json, String key, String value) {
        if (json != null && key != null && !key.isEmpty()) {
            json.put(key, value);
        } else {
            if (log.isTraceEnabled()) log.trace("Data [{}] with key [{}] not put to json [{}]", value, key, json);
        }
    }

    public void putJson(ObjectNode json, String key, JsonNode value) {
        if (json != null && key != null && !key.isEmpty()) {
            json.replace(key, value);
        } else {
            if (log.isTraceEnabled()) log.trace("Data [{}] with key [{}] not put to json [{}]", value, key, json);
        }
    }
}
