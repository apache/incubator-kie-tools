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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.BindableShapeSetThumbProvider;

@Dependent
public class BPMNThumbnailProvider extends BindableShapeSetThumbProvider {

    @Inject
    public BPMNThumbnailProvider(final DefinitionManager definitionManager) {
        super(definitionManager);
    }

    @Override
    protected boolean thumbFor(final Class<?> clazz) {
        return isSameClass(clazz,
                           BPMNDefinitionSet.class);
    }

    @Override
    public SafeUri getThumbnailUri() {
        return BPMNImageResources.INSTANCE.bpmnSetThumb().getSafeUri();
    }
}
