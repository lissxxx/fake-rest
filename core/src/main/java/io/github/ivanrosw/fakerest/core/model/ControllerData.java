/*
 * Copyright (C) 2021 Ivan Rosinskii
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
package io.github.ivanrosw.fakerest.core.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ControllerData {

    private static final String KEY_DELIMITER = ":::";

    @Autowired
    private JsonUtils jsonUtils;

    /**
     * Collection with url - controller data
     */
    private Map<String, Map<String, ObjectNode>> allData;

    @PostConstruct
    private void init() {
        allData = new ConcurrentHashMap<>();
    }

    public Map<String, ObjectNode> getAllData(String url) {
        return getDataCollection(url);
    }

    public ObjectNode getData(String url, String key) {
        return getDataCollection(url).get(key);
    }

    public void putData(String url, String key, ObjectNode data) {
        getDataCollection(url).put(key, data);
    }

    public boolean containsKey(String url, String key) {
        return getDataCollection(url).containsKey(key);
    }

    public void deleteData(String url, String key) {
        getDataCollection(url).remove(key);
    }

    private Map<String, ObjectNode> getDataCollection(String url) {
        Map<String, ObjectNode> result;
        if (allData.containsKey(url)) {
            result = allData.get(url);
        } else {
            result = new ConcurrentHashMap<>();
            allData.put(url, result);
        }
        return result;
    }

    public String buildKey(ObjectNode data, List<String> idParams) {
        List<String> ids = idParams.stream().map(param -> jsonUtils.getString(data, param)).collect(Collectors.toList());
        return String.join(KEY_DELIMITER, ids);
    }

    public String buildKey(Map<String, String> data, List<String> idParams) {
        List<String> ids = idParams.stream().map(data::get).collect(Collectors.toList());
        return String.join(KEY_DELIMITER, ids);
    }


}
