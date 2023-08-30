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

package org.kie.workbench.common.stunner.bpmn.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.core.definition.AbstractDefinitionSetResourceType;
import org.uberfire.workbench.annotations.VisibleAsset;
import org.uberfire.workbench.category.Category;

@Default
@VisibleAsset
@ApplicationScoped
public class BPMNDefinitionSetResourceType extends AbstractDefinitionSetResourceType {

    public static final String BPMN_EXTENSION = "bpmn";
    private static final String NAME = "Business Process";
    private static final String DESCRIPTION = "Business Process";

    private Category category;

    public BPMNDefinitionSetResourceType() {

    }

    @Inject
    public BPMNDefinitionSetResourceType(final Process category) {
        this.category = category;
    }

    @Override
    public Category getCategory() {
        return this.category;
    }

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getSuffix() {
        return BPMN_EXTENSION;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Class<?> getDefinitionSetType() {
        return BPMNDefinitionSet.class;
    }
}

