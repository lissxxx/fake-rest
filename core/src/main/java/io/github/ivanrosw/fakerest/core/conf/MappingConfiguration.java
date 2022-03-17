package io.github.ivanrosw.fakerest.core.conf;

import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.RouterConfig;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "rest")
public class MappingConfiguration {

    @Setter(AccessLevel.PACKAGE)
    private List<ControllerConfig> controllers;

    @Setter(AccessLevel.PACKAGE)
    private List<RouterConfig> routers;

    @Autowired
    private MappingConfigurator configurator;

    @PostConstruct
    private void init() throws ConfigException {
        initControllers();
        initRouters();
        configurator.printUrls();
    }

    private void initControllers() throws ConfigException {
        if (controllers != null) {
            for (ControllerConfig conf : controllers) {
                configurator.initController(conf);
            }
        }
    }

    private void initRouters() throws ConfigException {
        if (routers != null) {
            for (RouterConfig conf : routers) {
                configurator.initRouter(conf);
            }
        }
    }
}
