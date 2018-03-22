/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertyManager;
import org.kie.workbench.common.stunner.core.backend.util.BackendBindableDefinitionUtils;

@ApplicationScoped
public class Bpmn2OryxManager extends BaseOryxManager {

    public static final String SOURCE = ".source";
    public static final String TARGET = ".target";
    public static final String MAGNET_AUTO_CONNECTION = "isAutoConnection";

    // CDI Proxy.
    protected Bpmn2OryxManager() {
        this(null,
             null);
    }

    @Inject
    public Bpmn2OryxManager(final OryxIdMappings oryxIdMappings,
                            final Bpmn2OryxPropertyManager oryxPropertyManager) {
        super(oryxIdMappings,
              oryxPropertyManager);
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public Set<Class<?>> getDefinitionClasses() {
        final BPMNDefinitionSet set = new BPMNDefinitionSet.BPMNDefinitionSetBuilder().build();
        return BackendBindableDefinitionUtils.getDefinitions(set);
    }
}
