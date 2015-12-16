/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.guided.template.client.editor;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

/**
 * Guided Rule Template Data View definition
 */
public interface GuidedRuleTemplateDataView extends IsWidget {

    void setContent( final TemplateModel model,
                     final AsyncPackageDataModelOracle oracle,
                     final EventBus eventBus,
                     final boolean isReadOnly );

}
