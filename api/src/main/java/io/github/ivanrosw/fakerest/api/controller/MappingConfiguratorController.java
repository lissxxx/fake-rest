package io.github.ivanrosw.fakerest.api.controller;

import io.github.ivanrosw.fakerest.core.conf.MappingConfigurator;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.RouterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

@RestController
@RequestMapping("/api/mapping/conf")
public class MappingConfiguratorController {

    @Autowired
    private MappingConfigurator configurator;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @GetMapping("/controller")
    public ResponseEntity<List<ControllerConfig>> getAllControllers() {
        return new ResponseEntity<>(configurator.getAllControllersCopy(), HttpStatus.OK);
    }

    @GetMapping("/controller/{id}")
    public ResponseEntity<ControllerConfig> getController(@PathVariable String id) {
        ControllerConfig controller = configurator.getControllerCopy(id);
        ResponseEntity<ControllerConfig> response;
        if (controller != null) {
            response = new ResponseEntity<>(controller, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @GetMapping("/router")
    public ResponseEntity<List<RouterConfig>> getAllRouters() {
        return new ResponseEntity<>(configurator.getAllRoutersCopy(), HttpStatus.OK);
    }

    @GetMapping("/router/{id}")
    public ResponseEntity<RouterConfig> getRouter(@PathVariable String id) {
        RouterConfig router = configurator.getRouterCopy(id);
        ResponseEntity<RouterConfig> response;
        if (router != null) {
            response = new ResponseEntity<>(router, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

}
