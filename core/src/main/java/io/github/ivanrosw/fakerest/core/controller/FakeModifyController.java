package io.github.ivanrosw.fakerest.core.controller;

import io.github.ivanrosw.fakerest.core.model.ControllerMode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class FakeModifyController extends FakeController {

    @Override
    public ResponseEntity<String> handle(HttpServletRequest request) {
        ResponseEntity<String> result;
        if (mode == ControllerMode.COLLECTION_ONE) {
            result = handleOne(request);
        } else {
            result = returnBody(request);
        }
        return result;
    }

    protected ResponseEntity<String> returnBody(HttpServletRequest request) {
        ResponseEntity<String> result;
        try {
            String body = httpUtils.readBody(request);
            if (body != null && !body.isBlank()) {
                result = new ResponseEntity<>(body, HttpStatus.OK);
            } else {
                result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    protected abstract ResponseEntity<String> handleOne(HttpServletRequest request);
}
