package io.github.ivanrosw.fakerest.core.model;

import lombok.*;
import org.springframework.web.bind.annotation.RequestMethod;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class BaseUriConfig {

    private String id;

    private String uri;

    private RequestMethod method;
}
