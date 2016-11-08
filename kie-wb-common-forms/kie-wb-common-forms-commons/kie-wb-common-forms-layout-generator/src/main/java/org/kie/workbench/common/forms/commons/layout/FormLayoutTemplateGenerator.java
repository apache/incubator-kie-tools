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
package org.kie.workbench.common.forms.commons.layout;

import java.util.List;

import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

/**
 * Component to generate LayoutTemplates for FormDefinition
 */
public interface FormLayoutTemplateGenerator {

    /**
     * Generates a default LayoutTemplate for the given FormDefinition.
     */
    void generateLayoutTemplate( FormDefinition formDefinition );

    /**
     * Returns the DragabbleType type name that is going to be use on the layout.
     */
    public String getDraggableType();

    /**
     * Updates the current layout to add the newFields.
     */
    void updateLayoutTemplate( FormDefinition form, List<FieldDefinition> newFields );
}
