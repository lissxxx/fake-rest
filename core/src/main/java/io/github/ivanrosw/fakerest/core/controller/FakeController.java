package io.github.ivanrosw.fakerest.core.controller;

import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.ControllerMode;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.utils.GeneratorUtils;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class FakeController {

    protected static final String KEY_NOT_FOUND = "key [%s] not found";
    protected static final String DESCRIPTION_PARAM = "description";

    protected ControllerMode mode;
    protected ControllerData controllerData;
    protected ControllerConfig controllerConfig;

    protected JsonUtils jsonUtils;
    protected GeneratorUtils generatorUtils;

    protected Map<String, String> getUrlIds(HttpServletRequest request) {
        return (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    public abstract ResponseEntity<String> handle(HttpServletRequest request);
}
