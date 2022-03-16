/*
 * Copyright (C) 2022 Ivan Rosinskii
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
    private ControllerConfigurator configurator;

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
