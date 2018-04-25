/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.kie.workbench.common.screens.library.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;

@ApplicationScoped
public class ResourceHandlerManager {

    private ManagedInstance<NewResourceHandler> newResourceHandlers;

    public ResourceHandlerManager() {
    }

    @Inject
    public ResourceHandlerManager(final ManagedInstance<NewResourceHandler> newResourceHandlers) {
        this.newResourceHandlers = newResourceHandlers;
    }

    public List<NewResourceHandler> getNewResourceHandlers(final Function<NewResourceHandler, Boolean> matcher) {
        List<NewResourceHandler> matchedNewResourceHandlers = new ArrayList<>();

        getNewResourceHandlers().forEach(newResourceHandler -> {
            if (matcher.apply(newResourceHandler)) {
                matchedNewResourceHandlers.add(newResourceHandler);
            }
        });

        return matchedNewResourceHandlers;
    }

    public Iterable<NewResourceHandler> getNewResourceHandlers() {
        return newResourceHandlers;
    }
}
