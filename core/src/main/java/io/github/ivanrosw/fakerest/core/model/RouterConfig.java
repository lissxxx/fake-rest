package io.github.ivanrosw.fakerest.core.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RouterConfig extends BaseUriConfig implements Copyable<RouterConfig> {

    private String toUrl;

    @Override
    public RouterConfig copy() {
        RouterConfig copy = new RouterConfig();
        copy.setId(this.getId());
        copy.setUri(this.getUri());
        copy.setMethod(this.getMethod());
        copy.setToUrl(toUrl);
        return copy;
    }
}
