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
public class RouterConfig implements Copyable<RouterConfig> {

    private String id;

    private String uri;

    private RequestMethod method;

    private String toUrl;

    @Override
    public RouterConfig copy() {
        RouterConfig copy = new RouterConfig();
        copy.setId(this.id);
        copy.setUri(this.uri);
        copy.setMethod(method);
        copy.setToUrl(toUrl);
        return copy;
    }
}
