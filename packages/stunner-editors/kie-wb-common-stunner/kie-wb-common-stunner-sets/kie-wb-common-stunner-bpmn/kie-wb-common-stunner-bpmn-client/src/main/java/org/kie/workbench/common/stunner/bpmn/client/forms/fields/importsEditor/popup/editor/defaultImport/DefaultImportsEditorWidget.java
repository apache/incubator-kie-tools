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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.defaultImport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.ImportsEditorWidget;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.createDataTypeDisplayName;

@Dependent
public class DefaultImportsEditorWidget extends ImportsEditorWidget<DefaultImport> {

    private static List<String> defaultTypes = new ArrayList<>(Arrays.asList("Boolean", "Float", "Integer", "Object", "String"));
    protected SessionManager sessionManager;
    protected DataTypeNamesService dataTypeNamesService;
    protected Event<NotificationEvent> notification;
    protected Event<RefreshFormPropertiesEvent> refreshFormsEvent;
    protected Map<String, String> dataTypes = new TreeMap<>();

    @Inject
    public DefaultImportsEditorWidget(final SessionManager sessionManager,
                                      final DataTypeNamesService dataTypeNamesService,
                                      final Event<NotificationEvent> notification,
                                      final Event<RefreshFormPropertiesEvent> refreshFormsEvent) {
        this.sessionManager = sessionManager;
        this.dataTypeNamesService = dataTypeNamesService;
        this.notification = notification;
        this.refreshFormsEvent = refreshFormsEvent;

        loadDefaultDataTypes();
        loadServerDataTypes();
    }

    public Map<String, String> getDataTypes() {
        return dataTypes;
    }

    public String getDataType(String displayName) {
        return dataTypes.keySet()
                .stream()
                .filter(key -> displayName.equals(dataTypes.get(key)))
                .findFirst()
                .orElse(displayName);
    }

    @Override
    public DefaultImport createImport() {
        return new DefaultImport();
    }

    protected void loadDefaultDataTypes() {
        addDataTypes(defaultTypes, false);
    }

    protected void loadServerDataTypes() {
        final Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        final Path path = diagram.getMetadata().getPath();
        dataTypeNamesService
                .call(path)
                .then(serverDataTypes -> {
                    addDataTypes(serverDataTypes, true);
                    return null;
                })
                .catch_(exception -> {
                    notification.fire(new NotificationEvent(StunnerFormsClientFieldsConstants.CONSTANTS.Error_retrieving_datatypes(),
                                                            NotificationEvent.NotificationType.ERROR));
                    return null;
                });
    }

    protected void addDataTypes(List<String> dataTypesList, boolean useDisplayNames) {
        for (String dataType : dataTypesList) {
            if (dataType.contains("Asset-")) {
                dataType = dataType.substring(6);
            }

            String displayName = useDisplayNames ? createDataTypeDisplayName(dataType) : dataType;
            dataTypes.put(dataType, displayName);
        }
    }

    public void addDataTypes(ImportsValue imports) {
        boolean updated = false;
        for (DefaultImport imported : imports.getDefaultImports()) {
            if (imported.getClassName() != null && !defaultTypes.contains(imported.getClassName())) {
                updated = true;
                dataTypeNamesService.add(imported.getClassName(), null);
            }
        }

        if (updated) {
            refreshFormsEvent.fire(new RefreshFormPropertiesEvent(sessionManager.getCurrentSession()));
        }
    }
}
