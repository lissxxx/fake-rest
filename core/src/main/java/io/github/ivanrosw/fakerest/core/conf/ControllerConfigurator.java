package io.github.ivanrosw.fakerest.core.conf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.controller.*;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.ControllerMode;
import io.github.ivanrosw.fakerest.core.model.RouterConfig;
import io.github.ivanrosw.fakerest.core.utils.GeneratorUtils;
import io.github.ivanrosw.fakerest.core.utils.HttpUtils;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import io.github.ivanrosw.fakerest.core.utils.RestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ControllerConfigurator {

    private Map<RequestMethod, List<String>> methodsUrls;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private ControllerData controllerData;
    @Autowired
    private JsonUtils jsonUtils;
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private GeneratorUtils generatorUtils;
    @Autowired
    private RestClient restClient;

    @PostConstruct
    private void init() {
        methodsUrls = new EnumMap<>(RequestMethod.class);
    }

    public void initController(ControllerConfig conf) throws ConfigException {
        beforeInitControllerCheck(conf);
        List<String> idParams = httpUtils.getIdParams(conf.getUri());
        ControllerMode mode = identifyMode(idParams);

        switch (conf.getMethod()) {
            case GET:
                createGetController(conf, idParams, mode);
                break;
            case POST:
                createPostController(conf, idParams, mode);
                break;
            case PUT:
                createPutController(conf, idParams, mode);
                break;
            case DELETE:
                createDeleteController(conf, idParams, mode);
                break;
            default:
                throw new ConfigException(String.format("Method [%s] not supported", conf.getMethod()));
        }

        List<String> urls = methodsUrls.computeIfAbsent(conf.getMethod(), key -> new ArrayList<>());
        urls.add(conf.getUri());
        if (conf.getMethod() == RequestMethod.GET && mode == ControllerMode.COLLECTION) {
            urls.add(httpUtils.getBaseUri(conf.getUri()));
        }
    }

    private void beforeInitControllerCheck(ControllerConfig conf) throws ConfigException {
        if (conf.getUri() == null || conf.getUri().isEmpty()) {
            throw new ConfigException("Controller: Uri must be not blank");
        }
        if (conf.getMethod() == null) {
            throw new ConfigException("Controller: Method must be specified");
        }

        List<String> urls = methodsUrls.computeIfAbsent(conf.getMethod(), key -> new ArrayList<>());
        if (urls.contains(conf.getUri())) {
            throw new ConfigException(String.format("Duplicated urls: %s", conf.getUri()));
        }
    }

    private void createGetController(ControllerConfig conf, List<String> idParams, ControllerMode mode) throws ConfigException {
        if (mode == ControllerMode.COLLECTION) {
            conf.setIdParams(idParams);

            String baseUri = httpUtils.getBaseUri(conf.getUri());
            RequestMappingInfo getAllMappingInfo = RequestMappingInfo
                    .paths(baseUri)
                    .methods(RequestMethod.GET)
                    .build();

            FakeController getAllController = GetController.builder()
                    .mode(ControllerMode.COLLECTION_ALL)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(getAllMappingInfo, getAllController);

            RequestMappingInfo getOneMappingInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(RequestMethod.GET)
                    .build();

            FakeController getOneController = GetController.builder()
                    .mode(ControllerMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(getOneMappingInfo, getOneController);

            loadAnswerData(conf);
        } else {
            RequestMappingInfo getStaticMappingInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(RequestMethod.GET)
                    .build();

            FakeController getStaticController =  GetController.builder()
                    .mode(ControllerMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(getStaticMappingInfo, getStaticController);
        }
    }

    private void createPostController(ControllerConfig conf, List<String> idParams, ControllerMode mode) throws ConfigException {
        if (mode == ControllerMode.COLLECTION) {
            conf.setIdParams(idParams);

            String baseUri = httpUtils.getBaseUri(conf.getUri());
            RequestMappingInfo createOneInfo = RequestMappingInfo
                    .paths(baseUri)
                    .methods(RequestMethod.POST)
                    .build();

            FakeController createOneController = PostController.builder()
                    .mode(ControllerMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(createOneInfo, createOneController);

            loadAnswerData(conf);
        } else {
            RequestMappingInfo createStaticInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(RequestMethod.POST)
                    .build();

            FakeController createStaticController = PostController.builder()
                    .mode(ControllerMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(createStaticInfo, createStaticController);
        }
    }

    private void createPutController(ControllerConfig conf, List<String> idParams, ControllerMode mode) throws ConfigException {
        if (mode == ControllerMode.COLLECTION) {
            conf.setIdParams(idParams);

            RequestMappingInfo updateOneInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(RequestMethod.PUT)
                    .build();

            FakeController updateOneController = PutController.builder()
                    .mode(ControllerMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(updateOneInfo, updateOneController);

            loadAnswerData(conf);
        } else {
            RequestMappingInfo updateStaticInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(RequestMethod.PUT)
                    .build();

            FakeController updateStaticController = PutController.builder()
                    .mode(ControllerMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(updateStaticInfo, updateStaticController);
        }
    }

    private void createDeleteController(ControllerConfig conf, List<String> idParams, ControllerMode mode) throws ConfigException {
        if (mode == ControllerMode.COLLECTION) {
            conf.setIdParams(idParams);

            RequestMappingInfo deleteOneInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(RequestMethod.DELETE)
                    .build();

            FakeController deleteOneController = DeleteController.builder()
                    .mode(ControllerMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(deleteOneInfo, deleteOneController);

            loadAnswerData(conf);
        } else {
            RequestMappingInfo deleteStaticInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(RequestMethod.DELETE)
                    .build();

            FakeController deleteStaticController = DeleteController.builder()
                    .mode(ControllerMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(deleteStaticInfo, deleteStaticController);
        }
    }

    private ControllerMode identifyMode(List<String> idParams) {
        return idParams.isEmpty() ? ControllerMode.STATIC : ControllerMode.COLLECTION;
    }

    private void loadAnswerData(ControllerConfig conf) {
        if (conf.getAnswer() != null) {
            JsonNode answer = jsonUtils.toJsonNode(conf.getAnswer());

            if (answer instanceof ArrayNode) {
                ArrayNode array = (ArrayNode) answer;
                array.forEach(jsonNode -> addAnswerData(conf, (ObjectNode) jsonNode));
            } else if (answer instanceof ObjectNode) {
                addAnswerData(conf, (ObjectNode) answer);
            } else {
                log.warn("Cant put data [{}] to collection [{}]. Its not json", answer, conf.getUri());
            }
        }
    }

    private void addAnswerData(ControllerConfig controllerConfig, ObjectNode data) {
        String key = controllerData.buildKey(data, controllerConfig.getIdParams());
        controllerData.putData(controllerConfig.getUri(), key, data);
    }

    public void initRouter(RouterConfig conf) throws ConfigException {
        beforeInitRouterCheck(conf);
        RequestMappingInfo routerInfo = RequestMappingInfo
                .paths(conf.getUri())
                .methods(conf.getMethod())
                .build();

        RouterController routerController = new RouterController(conf, httpUtils, restClient);
        try {
            handlerMapping.registerMapping(routerInfo, routerController,
                    RouterController.class.getMethod("handle", HttpServletRequest.class));
        } catch (Exception e) {
            throw new ConfigException(String.format("Error while register router [%s]", routerInfo), e);
        }

        List<String> urls = methodsUrls.computeIfAbsent(conf.getMethod(), key -> new ArrayList<>());
        urls.add(conf.getUri());
    }

    private void beforeInitRouterCheck(RouterConfig conf) throws ConfigException {
        if (conf.getUri() == null || conf.getToUrl() == null || conf.getUri().isEmpty() || conf.getToUrl().isEmpty()) {
            throw new ConfigException("Router: Uri and toUrl must be not blank");
        }
        if (conf.getMethod() == null) {
            throw new ConfigException("Router: Method must be specified");
        }

        List<String> urls = methodsUrls.computeIfAbsent(conf.getMethod(), key -> new ArrayList<>());
        if (urls.contains(conf.getUri())) {
            throw new ConfigException(String.format("Duplicated urls: %s", conf.getUri()));
        }
    }

    private void registerController(RequestMappingInfo requestMappingInfo, FakeController fakeController) throws ConfigException {
        try {
            handlerMapping.registerMapping(requestMappingInfo, fakeController,
                    FakeController.class.getMethod("handle", HttpServletRequest.class));
        } catch (Exception e) {
            throw new ConfigException(String.format("Error while register controller [%s]", requestMappingInfo), e);
        }
    }

    public void printUrls() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("**** Configured URLs ****\n");

        methodsUrls.forEach((method, urls) -> {
            builder.append(method);
            builder.append(":\n");
            urls.forEach(url -> {
                builder.append("    ");
                builder.append(url);
                builder.append("\n");
            });
        });

        log.info(builder.toString());
    }
}
