package io.github.ivanrosw.fakerest.api.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.conf.ConfigException;
import io.github.ivanrosw.fakerest.core.conf.MappingConfigurator;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.RouterConfig;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conf/mapping")
public class MappingConfiguratorController {

    private static final String ERROR_DESCRIPTION = "description";

    @Autowired
    private MappingConfigurator configurator;
    @Autowired
    private JsonUtils jsonUtils;

    //CONTROLLER

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

    @PostMapping("/controller")
    public ResponseEntity<String> addController(@RequestBody ControllerConfig conf) {
        return updateConfig(new ControllerAdder(), conf);
    }
    private class ControllerAdder implements UpdateProcessor<ControllerConfig> {
        @Override
        public String process(ControllerConfig conf) throws ConfigException {
            configurator.initController(conf);
            return jsonUtils.toObjectNode(conf).toString();
        }
    }

    @DeleteMapping("/controller/{id}")
    public ResponseEntity<String> deleteController(@PathVariable String id) {
        return updateConfig(new ControllerDeleter(), id);
    }
    private class ControllerDeleter implements UpdateProcessor<String> {
        @Override
        public String process(String id) throws ConfigException {
            ControllerConfig conf = configurator.getControllerCopy(id);
            configurator.deleteController(conf);
            return jsonUtils.toObjectNode(conf).toString();
        }
    }

    //ROUTER

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

    @PostMapping("/router")
    public ResponseEntity<String> addRouter(@RequestBody RouterConfig conf) {
        return updateConfig(new RouterAdder(), conf);
    }
    private class RouterAdder implements UpdateProcessor<RouterConfig> {
        @Override
        public String process(RouterConfig conf) throws ConfigException {
            configurator.initRouter(conf);
            return jsonUtils.toObjectNode(conf).toString();
        }
    }

    @DeleteMapping("/router/{id}")
    public ResponseEntity<String> deleteRouter(@PathVariable String id) {
        return updateConfig(new RouterDeleter(), id);
    }
    private class RouterDeleter implements UpdateProcessor<String> {
        @Override
        public String process(String id) throws ConfigException {
            RouterConfig conf = configurator.getRouterCopy(id);
            configurator.deleteRouter(conf);
            return jsonUtils.toObjectNode(conf).toString();
        }
    }

    //GENERAL
    private <T> ResponseEntity<String> updateConfig(UpdateProcessor<T> updater, T data) {
        ResponseEntity<String> response;
        ObjectNode body;
        if (data != null) {
            try {
                response = new ResponseEntity<>(updater.process(data), HttpStatus.OK);
            } catch (ConfigException e) {
                body = jsonUtils.createJson();
                jsonUtils.putString(body, ERROR_DESCRIPTION, e.getMessage());
                response = new ResponseEntity<>(body.toString(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                body = jsonUtils.createJson();
                jsonUtils.putString(body, ERROR_DESCRIPTION, e.getMessage());
                response = new ResponseEntity<>(body.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            body = jsonUtils.createJson();
            jsonUtils.putString(body, ERROR_DESCRIPTION, "Configuration is empty");
            response = new ResponseEntity<>(body.toString(), HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    private interface UpdateProcessor<T> {
        String process(T conf) throws ConfigException;
    }

}
