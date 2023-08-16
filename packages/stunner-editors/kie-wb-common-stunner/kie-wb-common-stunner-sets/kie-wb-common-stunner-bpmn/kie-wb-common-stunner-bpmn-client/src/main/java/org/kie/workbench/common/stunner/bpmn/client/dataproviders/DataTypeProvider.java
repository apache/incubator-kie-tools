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


package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.createDataTypeDisplayName;

public class DataTypeProvider implements SelectorDataProvider {

    private static List<String> defaultTypes = new ArrayList<>(Arrays.asList("Boolean", "Float", "Integer", "Object", "String"));
    protected SessionManager sessionManager;
    protected DataTypeNamesService dataTypeNamesService;
    protected Event<NotificationEvent> notification;
    protected Map<String, String> dataTypes = new TreeMap<>();

    @Inject
    public DataTypeProvider(final SessionManager sessionManager,
                            final DataTypeNamesService dataTypeNamesService) {
        this.sessionManager = sessionManager;
        this.dataTypeNamesService = dataTypeNamesService;
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public SelectorData getSelectorData(FormRenderingContext context) {
        loadDefaultDataTypes();
        loadServerDataTypes();
        return new SelectorData(dataTypes, defaultTypes.get(3));
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
}