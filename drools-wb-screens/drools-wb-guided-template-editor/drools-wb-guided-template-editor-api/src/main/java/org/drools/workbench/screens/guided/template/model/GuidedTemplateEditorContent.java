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

package org.drools.workbench.screens.guided.template.model;

import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Container for data needed to edit a Guided Template
 */
@Portable
public class GuidedTemplateEditorContent {

    private TemplateModel model;
    private PackageDataModelOracleBaselinePayload dataModel;

    public GuidedTemplateEditorContent() {
    }

    public GuidedTemplateEditorContent( final TemplateModel model,
                                        final PackageDataModelOracleBaselinePayload dataModel ) {
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
        this.dataModel = PortablePreconditions.checkNotNull( "dataModel",
                                                             dataModel );
    }

    public TemplateModel getModel() {
        return model;
    }

    public PackageDataModelOracleBaselinePayload getDataModel() {
        return dataModel;
    }

}
