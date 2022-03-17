package io.github.ivanrosw.fakerest.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseUriConfig {

    private String id;

    private String uri;

    private RequestMethod method;
}
