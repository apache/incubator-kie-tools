/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.widgets.dataset.editor.remote;

import javax.enterprise.context.Dependent;

import com.google.gwt.safehtml.shared.SafeUri;
import org.dashbuilder.client.widgets.common.DataSetEditorPlugin;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.RemoteDataSetBasicAttributesWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.edit.RemoteDataSetEditWorkflow;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.client.resources.bundles.DataSetClientResources;
import org.dashbuilder.kieserver.RuntimeKieServerDataSetProviderType;

@Dependent
public class RemoteDataSetEditorPlugin implements DataSetEditorPlugin {

    private static DataSetProviderType TYPE = new RuntimeKieServerDataSetProviderType();
    
    @Override
    public String getTypeSelectorTitle() {
        return DataSetEditorConstants.INSTANCE.remoteDataSetEditor();
    }

    @Override
    public String getTypeSelectorText() {
        return DataSetEditorConstants.INSTANCE.remoteDataSetEditorDescription();
    }

    @Override
    public SafeUri getTypeSelectorImageUri() {
        return DataSetClientResources.INSTANCE.images().sqlIcon160().getSafeUri();
    }

    @Override
    public DataSetProviderType getProviderType() {
        return TYPE;
    }

    @Override
    public Class<?> getBasicAttributesWorkflowClass() {
        return RemoteDataSetBasicAttributesWorkflow.class;
    }

    @Override
    public Class<?> getWorkflowClass() {
        return RemoteDataSetEditWorkflow.class;
    }

}