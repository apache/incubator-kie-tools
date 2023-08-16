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


package org.kie.workbench.common.stunner.bpmn.client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.client.shape.factory.BPMNShapeFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.AbstractBindableShapeSet;

@ApplicationScoped
public class BPMNShapeSet extends AbstractBindableShapeSet<BPMNShapeFactory> {

    private final DefinitionManager definitionManager;
    private final BPMNShapeFactory factory;

    protected BPMNShapeSet() {
        this(null,
             null);
    }

    @Inject
    public BPMNShapeSet(final DefinitionManager definitionManager,
                        final BPMNShapeFactory factory) {
        this.definitionManager = definitionManager;
        this.factory = factory;
    }

    @Override
    protected DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    @Override
    protected Class<?> getDefinitionSetClass() {
        return BPMNDefinitionSet.class;
    }

    @Override
    public BPMNShapeFactory getShapeFactory() {
        return factory;
    }
}
