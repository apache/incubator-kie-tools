/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.guided.dtable.model;

import java.util.Set;

import org.drools.guvnor.models.commons.shared.workitems.PortableWorkDefinition;
import org.drools.guvnor.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;

@Portable
public class GuidedDecisionTableEditorContent {

    private PackageDataModelOracle dataModel;
    private GuidedDecisionTable52 ruleModel;
    private Set<PortableWorkDefinition> workItemDefinitions;

    public GuidedDecisionTableEditorContent() {
    }

    public GuidedDecisionTableEditorContent( final PackageDataModelOracle dataModel,
                                             final GuidedDecisionTable52 ruleModel,
                                             final Set<PortableWorkDefinition> workItemDefinitions ) {
        this.dataModel = dataModel;
        this.ruleModel = ruleModel;
        this.workItemDefinitions = workItemDefinitions;
    }

    public PackageDataModelOracle getDataModel() {
        return dataModel;
    }

    public GuidedDecisionTable52 getRuleModel() {
        return ruleModel;
    }

    public Set<PortableWorkDefinition> getWorkItemDefinitions() {
        return workItemDefinitions;
    }

}
