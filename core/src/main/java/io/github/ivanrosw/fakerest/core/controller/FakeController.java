package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.conf.ConfigException;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.ControllerMode;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class FakeController {

    protected ControllerMode mode;
    protected ControllerData controllerData;
    protected ControllerConfig controllerConfig;

    protected JsonUtils jsonUtils;

    public FakeController(ControllerConfig controllerConfig, ControllerData controllerData, JsonUtils jsonUtils) throws ConfigException {
        this.controllerConfig = controllerConfig;
        this.jsonUtils = jsonUtils;
        this.controllerData = controllerData;

        Pattern pattern = Pattern.compile("(?<=\\{).*?(?=\\})");
        Matcher matcher = pattern.matcher(controllerConfig.getUri());

        if (matcher.find()) {
            mode = ControllerMode.ID;
            initId(pattern);
        } else {
            mode = ControllerMode.NO_ID;
        }
    }

    private void initId(Pattern pattern) {
        initIdParams(pattern);
        initIdAnswerData();
    }

    private void initIdParams(Pattern pattern) {
        List<String> idParams = new ArrayList<>();
        Matcher matcher = pattern.matcher(controllerConfig.getUri());
        while (matcher.find()) {
            idParams.add(matcher.group());
        }
        controllerConfig.setIdParams(idParams);
    }

    private void initIdAnswerData() {
        if (controllerConfig.getAnswer() != null) {
            JsonNode answer = jsonUtils.toJsonNode(controllerConfig.getAnswer());

            if (answer instanceof ArrayNode) {
                ArrayNode array = (ArrayNode) answer;
                array.forEach(jsonNode -> addAnswerData((ObjectNode) jsonNode));
            } else if (answer instanceof ObjectNode) {
                addAnswerData((ObjectNode) answer);
            } else {
                log.info("Cant put data [{}] to collection. Its not json", answer);
            }
        }
    }

    private void addAnswerData(ObjectNode data) {
        String key = controllerData.buildKey(data, controllerConfig.getIdParams());
        controllerData.putData(controllerConfig.getUri(), key, data);
    }

    protected Map<String, String> getUrlIds(HttpServletRequest request) {
        return  (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    public abstract ResponseEntity<String> handle(HttpServletRequest request);
}
