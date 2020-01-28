/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.workitems.service;

import java.util.Set;

import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.screens.workitems.model.WorkItemDefinitionElements;
import org.drools.workbench.screens.workitems.model.WorkItemsModelContent;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.ValidationService;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsCreate;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRead;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;

@Remote
public interface WorkItemsEditorService
        extends
        BuildValidationHelper,
        ValidationService<String>,
        SupportsCreate<String>,
        SupportsRead<String>,
        SupportsSaveAndRename<String, Metadata>,
        SupportsDelete,
        SupportsCopy {

    public static final String WORK_ITEM_DEFINITION = "work-item-definition";

    public static final String WORK_ITEMS_EDITOR_SETTINGS = "work-items-editor-settings";

    public static final String WORK_ITEMS_EDITOR_SETTINGS_DEFINITION = "Definition";

    public static final String WORK_ITEMS_EDITOR_SETTINGS_PARAMETER = "Parameter";

    public static final String WORK_ITEMS_EDITOR_SETTINGS_RESULT = "Result";

    public static final String WORK_ITEMS_EDITOR_SETTINGS_DISPLAY_NAME = "DisplayName";

    public static final String WORK_ITEMS_EDITOR_SETTINGS_CUSTOM_EDITOR = "CustomEditor";

    public static final String WORK_ITEMS_EDITOR_SETTINGS_PARAMETER_VALUES = "ParameterValues";

    public static final String WORK_ITEMS_EDITOR_SETTINGS_DEFAULT_HANDLER = "DefaultHandler";

    WorkItemsModelContent loadContent(final Path path);

    WorkItemDefinitionElements loadDefinitionElements();

    Set<PortableWorkDefinition> loadWorkItemDefinitions(final Path path);
}
