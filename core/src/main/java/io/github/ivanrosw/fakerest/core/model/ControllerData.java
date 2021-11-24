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

    public ObjectNode getData(String url, String key) {
        return getDataCollection(url).get(key);
    }

    public void putData(String url, String key, ObjectNode data) {
        getDataCollection(url).put(key, data);
    }

    public boolean containsKey(String url, String key) {
        return getDataCollection(url).containsKey(key);
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
