package io.github.ivanrosw.fakerest.core.conf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.controller.*;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.ControllerMode;
import io.github.ivanrosw.fakerest.core.utils.GeneratorUtils;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import io.github.ivanrosw.fakerest.core.utils.UrlUtils;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "rest")
public class MappingConfiguration {

    @Setter(AccessLevel.PACKAGE)
    private List<ControllerConfig> list;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private ControllerData controllerData;
    @Autowired
    private JsonUtils jsonUtils;
    @Autowired
    private UrlUtils urlUtils;
    @Autowired
    private GeneratorUtils generatorUtils;

    @PostConstruct
    private void init() throws ConfigException {
        beforeInitCheck();
        initRest();
    }

    private void beforeInitCheck() throws ConfigException {
        if (list == null || list.isEmpty()) {
            throw new ConfigException("Rest list must be specified. See readme");
        }

        Map<RequestMethod, List<String>> methodsUrls = new EnumMap<>(RequestMethod.class);

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

    private void initRest() throws ConfigException {
        for (ControllerConfig conf : list) {
            switch (conf.getMethod()) {
                case GET:
                    createGetController(conf);
                    break;
                case POST:
                    createPostController(conf);
                    break;
                case PUT:
                    createPutController(conf);
                    break;
                case DELETE:
                    createDeleteController(conf);
                    break;
                default:
                    throw new ConfigException(String.format("Method [%s] not supported", conf.getMethod()));
            }
            loadAnswerData(conf);
        }
    }

    private void createGetController(ControllerConfig conf) throws ConfigException {
        List<String> idParams = urlUtils.getIdParams(conf.getUri());
        ControllerMode mode = identifyMode(idParams);

        if (mode == ControllerMode.COLLECTION) {
            conf.setIdParams(idParams);

            String baseUri = urlUtils.getBaseUri(conf.getUri());
            RequestMappingInfo getAllMappingInfo = RequestMappingInfo
                    .paths(baseUri)
                    .methods(RequestMethod.GET)
                    .build();

            FakeController getAllController = GetController.builder()
                    .mode(ControllerMode.COLLECTION_ALL)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
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
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(getOneMappingInfo, getOneController);
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
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(getStaticMappingInfo, getStaticController);
        }
    }

    private void createPostController(ControllerConfig conf) throws ConfigException {
        List<String> idParams = urlUtils.getIdParams(conf.getUri());
        ControllerMode mode = identifyMode(idParams);

        if (mode == ControllerMode.COLLECTION) {
            conf.setIdParams(idParams);

            String baseUri = urlUtils.getBaseUri(conf.getUri());
            RequestMappingInfo createOneInfo = RequestMappingInfo
                    .paths(baseUri)
                    .methods(RequestMethod.POST)
                    .build();

            FakeController createOneController = PostController.builder()
                    .mode(ControllerMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(createOneInfo, createOneController);
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
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(createStaticInfo, createStaticController);
        }
    }

    private void createPutController(ControllerConfig conf) throws ConfigException {
        List<String> idParams = urlUtils.getIdParams(conf.getUri());
        ControllerMode mode = identifyMode(idParams);

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
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(updateOneInfo, updateOneController);
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
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(updateStaticInfo, updateStaticController);
        }
    }

    private void createDeleteController(ControllerConfig conf) throws ConfigException {
        List<String> idParams = urlUtils.getIdParams(conf.getUri());
        ControllerMode mode = identifyMode(idParams);

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
                    .generatorUtils(generatorUtils)
                    .build();
            registerController(deleteOneInfo, deleteOneController);
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

    private void registerController(RequestMappingInfo requestMappingInfo, FakeController fakeController) throws ConfigException {
        try {
            handlerMapping.registerMapping(requestMappingInfo, fakeController,
                    FakeController.class.getMethod("handle", HttpServletRequest.class));
        } catch (Exception e) {
            throw new ConfigException(String.format("Error while register controller [%s]", requestMappingInfo), e);
        }
    }

}
