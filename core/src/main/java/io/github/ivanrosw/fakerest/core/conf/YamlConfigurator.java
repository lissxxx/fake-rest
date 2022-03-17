package io.github.ivanrosw.fakerest.core.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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

    private ObjectMapper mapper;

    @Autowired
    private JsonUtils jsonUtils;

    @PostConstruct
    private void init() {
        mapper = new ObjectMapper(new YAMLFactory());
    }

    void addController(ControllerConfig conf) {
        //TODO
    }

    void deleteController(ControllerConfig conf) {
        //TODO
    }

    void addRouter(RouterConfig conf) {
        //TODO
    }

    void deleteRouter(RouterConfig conf) {
        //TODO
    }

    private ObjectNode getConfig() {
        ObjectNode conf;
        try {
            conf = mapper.readValue(getConfigFile(), ObjectNode.class);
        } catch (Exception e) {
            conf = jsonUtils.createJson();
            log.warn("Error while parse configuration file. Creating new one");
        }
        return conf;
    }

    private void writeConfig(ObjectNode conf) {
        try {
            mapper.writer().writeValue(getConfigFile(), conf);
        } catch (Exception e) {
            log.error("Error while writing config");
        }
    }

    private File getConfigFile() throws IOException {
        String yamlPath = getYamlPath();
        File confFile = new File(yamlPath);
        if (!confFile.exists()) confFile.createNewFile();
        return confFile;
    }

    private String getYamlPath() throws UnsupportedEncodingException {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        return decodedPath + YAML_NAME;
    }
}
