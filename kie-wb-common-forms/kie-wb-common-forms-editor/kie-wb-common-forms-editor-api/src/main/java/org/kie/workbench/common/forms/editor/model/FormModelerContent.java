/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.uberfire.backend.vfs.Path;

@Portable
public class FormModelerContent {

    private Path path;
    private Overview overview;
    private FormDefinition definition;
    private List<String> modelProperties = new ArrayList<>();
    private Map<String, List<FieldDefinition>> availableFields;
    private FormEditorRenderingContext renderingContext;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Overview getOverview() {
        return overview;
    }

    public void setOverview(Overview overview) {
        this.overview = overview;
    }

    public FormDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(FormDefinition definition) {
        this.definition = definition;
    }

    public void setAvailableFields(Map<String, List<FieldDefinition>> availableFields) {
        this.availableFields = availableFields;
    }

    public FormEditorRenderingContext getRenderingContext() {
        return renderingContext;
    }

    public void setRenderingContext(FormEditorRenderingContext renderingContext) {
        this.renderingContext = renderingContext;
    }

    public Map<String, List<FieldDefinition>> getAvailableFields() {
        return availableFields;
    }

    public List<String> getModelProperties() {
        return modelProperties;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        FormModelerContent content = (FormModelerContent) other;

        return definition.equals(content);
    }

    @Override
    public int hashCode() {
        int result = definition != null ? definition.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (overview != null ? overview.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
