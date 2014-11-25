/*
* Copyright 2014 JBoss Inc
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
package org.drools.workbench.screens.guided.dtree.client.editor;

import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.client.mvp.UberView;

/**
 * Guided Decision Tree Editor View definition
 */
public interface GuidedDecisionTreeEditorView extends KieEditorView,
                                                      UberView<GuidedDecisionTreeEditorPresenter> {

    void setModel( final GuidedDecisionTree model,
                   final boolean isReadOnly );

    void setDataModel( final AsyncPackageDataModelOracle oracle,
                       final boolean isReadOnly );

}
