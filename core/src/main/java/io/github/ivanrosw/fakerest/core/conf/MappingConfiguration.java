package io.github.ivanrosw.fakerest.core.conf;

import io.github.ivanrosw.fakerest.core.controller.FakeController;
import io.github.ivanrosw.fakerest.core.controller.GetController;
import io.github.ivanrosw.fakerest.core.controller.PostController;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "rest")
public class MappingConfiguration {

    @Setter(AccessLevel.PACKAGE)
    private List<ControllerConfig> list;

    @Autowired
    private JsonUtils jsonUtils;
    @Autowired
    private ControllerData controllerData;
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @PostConstruct
    private void init() throws ConfigException, NoSuchMethodException {
        beforeInitCheck();
        initRest();
    }

    private void beforeInitCheck() throws ConfigException {
        if (list == null || list.isEmpty()) {
            throw new ConfigException("Rest list must be specified. See readme");
        }

        Map<RequestMethod, List<String>> methodsUrls = new HashMap<>();

        for (ControllerConfig conf : list) {
            if (conf.getUri() == null || conf.getUri().isBlank()) {
                throw new ConfigException("Uri must be not blank");
            }
            if (conf.getMethod() == null) {
                throw new ConfigException("Method must be specified");
            }

            List<String> urls = methodsUrls.computeIfAbsent(conf.getMethod(), key -> new ArrayList<>());
            if (urls.contains(conf.getUri())) {
                throw new ConfigException(String.format("Duplicated urls: %s", conf.getUri()));
            }

            urls.add(conf.getUri());
        }


    }

    private void initRest() throws NoSuchMethodException, ConfigException {

        for (ControllerConfig conf : list) {
            RequestMappingInfo requestMappingInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController fakeController = null;
            if (conf.getMethod() == RequestMethod.GET) {
                fakeController = new GetController(conf, controllerData, jsonUtils);
            } else if (conf.getMethod() == RequestMethod.POST) {
                fakeController = new PostController(conf, controllerData, jsonUtils);
            }

            handlerMapping.registerMapping(requestMappingInfo, fakeController,
                    GetController.class.getMethod("handle", HttpServletRequest.class));

        }

    }

}
