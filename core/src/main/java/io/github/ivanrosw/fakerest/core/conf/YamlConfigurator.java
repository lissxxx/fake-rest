package io.github.ivanrosw.fakerest.core.conf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.ivanrosw.fakerest.core.model.BaseUriConfig;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.RouterConfig;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Slf4j
@Component
public class YamlConfigurator {

    private static final String YAML_NAME = "application.yml";

    private static final String REST_PARAM = "rest";
    private static final String CONTROLLERS_PARAM = "controllers";
    private static final String ROUTERS_PARAM = "routers";
    private static final String ID_PARAM = "id";
    private static final String URI_PARAM = "uri";
    private static final String METHOD_PARAM = "method";

    private static final String CONTROLLER_PARAM = "controller";
    private static final String ROUTER_PARAM = "router";

    private ObjectMapper mapper;

    @Autowired
    private JsonUtils jsonUtils;

    @PostConstruct
    private void init() {
        mapper = new ObjectMapper(new YAMLFactory());
    }

    //CONTROLLER

    void addController(ControllerConfig conf) {
        addConfig(conf, CONTROLLERS_PARAM);
    }

    void deleteController(ControllerConfig conf) {
        deleteConfig(conf, CONTROLLERS_PARAM);
    }

    boolean isControllerExist(ControllerConfig conf) {
        return isConfigExist(conf, CONTROLLERS_PARAM);
    }

    //ROUTER

    void addRouter(RouterConfig conf) {
        addConfig(conf, ROUTERS_PARAM);
    }

    void deleteRouter(RouterConfig conf) {
        deleteConfig(conf, ROUTERS_PARAM);
    }

    boolean isRouterExist(RouterConfig conf) {
        return isConfigExist(conf, ROUTERS_PARAM);
    }

    //GENERALE

    private void addConfig(BaseUriConfig conf, String keyParam) {
        ObjectNode yaml = getConfig();
        ArrayNode configs = getControllersOrRouters(yaml, keyParam);
        ObjectNode jsonConf = jsonUtils.toObjectNode(conf);
        jsonConf.remove(ID_PARAM);
        configs.add(jsonConf);
        writeConfig(yaml);
        log.info("Added {} to config. Method: {}, uri: {}", conf instanceof ControllerConfig ? CONTROLLER_PARAM : ROUTER_PARAM,
                                                            conf.getMethod(),
                                                            conf.getUri());
    }

    private void deleteConfig(BaseUriConfig conf, String keyParam) {
        ObjectNode yaml = getConfig();
        ArrayNode configs = getControllersOrRouters(yaml, keyParam);

        boolean isDeleted = false;
        for (int i = 0; i < configs.size(); i++) {
            JsonNode configsConf = configs.get(i);
            String configsConfUri = jsonUtils.getString(configsConf, URI_PARAM);
            String configsConfMethod = jsonUtils.getString(configsConf, METHOD_PARAM);

            if (conf.getMethod().toString().equals(configsConfMethod) && conf.getUri().equals(configsConfUri)) {
                configs.remove(i);
                isDeleted = true;
                break;
            }
        }

        if (isDeleted) {
            writeConfig(yaml);
            log.info("Deleted {} from config. Method: {}, uri: {}", conf instanceof ControllerConfig ? CONTROLLER_PARAM : ROUTER_PARAM,
                                                                    conf.getMethod(),
                                                                    conf.getUri());
        }
    }

    private boolean isConfigExist(BaseUriConfig conf, String keyParam) {
        ObjectNode yaml = getConfig();
        ArrayNode configs = getControllersOrRouters(yaml, keyParam);

        boolean result = false;
        for (int i = 0; i < configs.size(); i++) {
            JsonNode configsConf = configs.get(i);
            String configsConfUri = jsonUtils.getString(configsConf, URI_PARAM);
            String configsConfMethod = jsonUtils.getString(configsConf, METHOD_PARAM);

            if (conf.getMethod().toString().equals(configsConfMethod) && conf.getUri().equals(configsConfUri)) {
                result = true;
                break;
            }
        }

        return result;
    }

    private ArrayNode getControllersOrRouters(ObjectNode yaml, String key) {
        ObjectNode rest = getRest(yaml);

        ArrayNode value;
        if (rest.has(key)) {
            value = jsonUtils.getArray(rest, key);
        } else {
            value = jsonUtils.createArray();
            jsonUtils.putJson(rest, key, value);
        }
        return value;
    }

    private ObjectNode getRest(ObjectNode yaml) {
        ObjectNode rest;
        if (yaml.has(REST_PARAM)) {
            rest = jsonUtils.getJson(yaml, REST_PARAM);
        } else {
            rest = jsonUtils.createJson();
            jsonUtils.putJson(yaml, REST_PARAM, rest);
        }
        return rest;
    }

    //FILE

    private ObjectNode getConfig() {
        ObjectNode conf;
        try {
            conf = mapper.readValue(getConfigFile(), ObjectNode.class);
        } catch (Exception e) {
            conf = jsonUtils.createJson();
            log.warn("Error while parse configuration file. Creating new one", e);
        }
        return conf;
    }

    private File getConfigFile() throws IOException {
        String yamlPath = getYamlPath();
        log.info("Getting file {}", yamlPath);
        File confFile = new File(yamlPath);
        if (!confFile.exists()) confFile.createNewFile();
        return confFile;
    }

    private String getYamlPath() throws UnsupportedEncodingException {
        String path = System.getProperty("user.dir");
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        return decodedPath + File.separator + YAML_NAME;
    }

    private void writeConfig(ObjectNode conf) {
        try {
            File file = getConfigFile();
            log.info("Writing file {}", file.getAbsolutePath());
            mapper.writer().writeValue(getConfigFile(), conf);
        } catch (Exception e) {
            log.error("Error while writing config", e);
        }
    }
}
