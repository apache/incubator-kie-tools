/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.security.authz;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.ResourceType;

/**
 * A instance holding a resource reference plus an action
 */
@JsType
public class ResourceActionRef {

    private Resource resource = null;
    private ResourceAction action = null;

    @JsIgnore
    public ResourceActionRef(Resource resource) {
        this(resource,
             ResourceAction.READ);
    }

    @JsIgnore
    public ResourceActionRef(Resource resource,
                             ResourceAction action) {
        this.resource = resource;
        this.action = action;
    }

    @JsIgnore
    public ResourceActionRef(ResourceType type,
                             ResourceAction action) {
        this.resource = new ResourceRef(null,
                                        type);
        this.action = action;
    }

    @JsIgnore
    public ResourceActionRef(ResourceType type,
                             Resource resource,
                             ResourceAction action) {
        this.resource = resource != null ? resource : new ResourceRef(null,
                                                                      type);
        this.action = action;
    }

    public Resource getResource() {
        return resource;
    }

    public ResourceAction getAction() {
        return action;
    }
}
