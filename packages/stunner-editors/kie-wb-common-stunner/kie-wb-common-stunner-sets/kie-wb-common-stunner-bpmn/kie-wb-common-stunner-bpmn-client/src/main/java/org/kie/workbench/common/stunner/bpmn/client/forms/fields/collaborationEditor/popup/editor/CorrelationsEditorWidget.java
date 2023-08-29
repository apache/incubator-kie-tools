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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.createDataTypeDisplayName;

@Dependent
public class CorrelationsEditorWidget implements CorrelationsEditorWidgetView.Presenter {

    protected SessionManager sessionManager;
    protected DataTypeNamesService dataTypeNamesService;
    protected Event<NotificationEvent> notification;

    private static List<String> defaultTypes = new ArrayList<>(Arrays.asList("Boolean", "Float", "Integer", "Object", "String"));
    protected Map<String, String> dataTypes = new TreeMap<>();

    @Inject
    protected CorrelationsEditorWidgetView correlationsEditorWidgetView;

    @Inject
    public CorrelationsEditorWidget(final SessionManager sessionManager,
                                    final DataTypeNamesService dataTypeNamesService,
                                    final Event<NotificationEvent> notification) {
        this.sessionManager = sessionManager;
        this.dataTypeNamesService = dataTypeNamesService;
        this.notification = notification;

        loadDefaultPropertyTypes();
        loadServerPropertyTypes();
    }

    @PostConstruct
    public void init() {
        correlationsEditorWidgetView.init(this);
    }

    @Override
    public Widget getWidget() {
        return correlationsEditorWidgetView.getWidget();
    }

    @Override
    public List<Correlation> getCorrelations() {
        return correlationsEditorWidgetView.getCorrelations();
    }

    @Override
    public void setCorrelations(final List<Correlation> correlations) {
        correlationsEditorWidgetView.setCorrelations(correlations);
    }

    @Override
    public void addCorrelation() {
        getCorrelations().add(new Correlation());
    }

    @Override
    public void removeCorrelation(final Correlation correlation) {
        getCorrelations().remove(correlation);
    }

    @Override
    public void update(final List<CorrelationsEditorValidationItem> validationItems) {
        correlationsEditorWidgetView.update(validationItems);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<List<Correlation>> handler) {
        return getWidget().addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void fireEvent(GwtEvent<?> gwtEvent) {
        getWidget().fireEvent(gwtEvent);
    }

    public void addDataTypes() {
        for (Correlation correlation : correlationsEditorWidgetView.getCorrelations()) {
            if (correlation.getPropertyType() != null && !defaultTypes.contains(correlation.getPropertyType())) {
                dataTypeNamesService.add(correlation.getPropertyType(), null);
            }
        }

        loadServerPropertyTypes();
    }

    public Map<String, String> getPropertyTypes() {
        return dataTypes;
    }

    public String getPropertyType(String displayName) {
        return dataTypes.keySet()
                .stream()
                .filter(key -> displayName.equals(dataTypes.get(key)))
                .findFirst()
                .orElse(displayName);
    }

    private void loadDefaultPropertyTypes() {
        addPropertyTypes(defaultTypes, false);
    }

    private void loadServerPropertyTypes() {
        final Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        final Path path = diagram.getMetadata().getPath();
        dataTypeNamesService
                .call(path)
                .then(serverDataTypes -> {
                    addPropertyTypes(serverDataTypes, true);
                    return null;
                })
                .catch_(exception -> {
                    notification.fire(new NotificationEvent(StunnerFormsClientFieldsConstants.CONSTANTS.Error_retrieving_datatypes(),
                                                            NotificationEvent.NotificationType.ERROR));
                    return null;
                });
    }

    private void addPropertyTypes(List<String> dataTypesList, boolean useDisplayNames) {
        for (String dataType : dataTypesList) {
            if (dataType.contains("Asset-")) {
                dataType = dataType.substring(6);
            }

            String displayName = useDisplayNames ? createDataTypeDisplayName(dataType) : dataType;
            dataTypes.put(dataType, displayName);
        }
    }
}