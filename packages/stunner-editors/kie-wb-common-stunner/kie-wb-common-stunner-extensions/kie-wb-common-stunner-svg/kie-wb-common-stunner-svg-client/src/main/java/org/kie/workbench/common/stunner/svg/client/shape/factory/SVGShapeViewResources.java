/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.svg.client.shape.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;

public class SVGShapeViewResources<T, F> {

    private final Map<Class<? extends T>, Function<F, SVGShapeViewResource>> resourceMap;

    public SVGShapeViewResources() {
        this.resourceMap = new HashMap<>();
    }

    public SVGShapeViewResources<T, F> put(final Class<? extends T> type,
                                           final Function<F, SVGShapeViewResource> resource) {
        resourceMap.put(type, resource);
        return this;
    }

    public SVGShapeViewResource getResource(final F factory,
                                            final T bean) {
        return resourceMap.get(bean.getClass())
                .apply(factory);
    }
}
