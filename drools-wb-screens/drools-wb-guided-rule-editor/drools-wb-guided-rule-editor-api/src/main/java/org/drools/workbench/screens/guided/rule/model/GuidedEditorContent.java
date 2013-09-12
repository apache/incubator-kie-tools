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

package org.drools.workbench.screens.guided.rule.model;

import org.drools.workbench.models.commons.shared.rule.RuleModel;
import org.drools.workbench.models.commons.shared.oracle.PackageDataModelOracle;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class GuidedEditorContent {

    private PackageDataModelOracle dataModel;
    private RuleModel ruleModel;

    public GuidedEditorContent() {

    }

    public GuidedEditorContent( final PackageDataModelOracle dataModel,
                                final RuleModel ruleModel ) {
        this.dataModel = dataModel;
        this.ruleModel = ruleModel;
    }

    public PackageDataModelOracle getDataModel() {
        return dataModel;
    }

    public RuleModel getRuleModel() {
        return ruleModel;
    }

}
