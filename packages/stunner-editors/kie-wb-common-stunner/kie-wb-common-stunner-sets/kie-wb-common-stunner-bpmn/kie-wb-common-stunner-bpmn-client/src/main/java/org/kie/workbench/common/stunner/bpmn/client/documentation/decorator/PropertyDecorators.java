/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.documentation.decorator;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.ActivityRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.DefaultRoute;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class PropertyDecorators {

    private Map<Class<?>, Function<Object, PropertyDecorator>> decorators;
    private final SessionManager sessionManager;
    private final DefinitionUtils definitionUtils;

    @Inject
    public PropertyDecorators(final SessionManager sessionManager, final DefinitionUtils definitionUtils) {
        this.sessionManager = sessionManager;
        this.definitionUtils = definitionUtils;
    }

    @PostConstruct
    private void init() {
        decorators =
                new Maps.Builder<Class<?>, Function<Object, PropertyDecorator>>()
                        .put(AssignmentsInfo.class,
                             af -> new AssignmentsInfoDecorator((AssignmentsInfo) af, getDiagram()))
                        .put(DefaultRoute.class,
                             dr -> new DefaultRouteDecorator((DefaultRoute) dr, () -> sessionManager.getCurrentSession().getCanvasHandler(), definitionUtils))
                        .put(ActivityRef.class,
                             dr -> new ActivityRefDecorator((ActivityRef) dr,
                                                            () -> sessionManager.getCurrentSession().getCanvasHandler().getDiagram(), definitionUtils))
                        .build();
    }

    private Diagram getDiagram() {
        return sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
    }

    public Optional<PropertyDecorator> getDecorator(Object property) {
        return Optional.ofNullable(decorators.get(property.getClass())).map(d -> d.apply(property));
    }
}