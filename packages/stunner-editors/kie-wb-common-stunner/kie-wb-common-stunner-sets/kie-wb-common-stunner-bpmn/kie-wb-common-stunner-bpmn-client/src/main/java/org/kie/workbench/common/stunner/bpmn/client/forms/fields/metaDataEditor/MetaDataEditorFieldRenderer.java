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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.metaDataEditor;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataAttribute;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.forms.model.MetaDataEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.MetaDataEditorFieldType;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Renderer(type = MetaDataEditorFieldType.class)
public class MetaDataEditorFieldRenderer extends FieldRenderer<MetaDataEditorFieldDefinition, DefaultFormGroup>
        implements MetaDataEditorWidgetView.Presenter {

    private MetaDataEditorWidgetView view;
    private final SessionManager sessionManager;
    private Path path;
    private final Event<NotificationEvent> notification;

    private static final String DELIMITER = "Ø";

    @Inject
    public MetaDataEditorFieldRenderer(final MetaDataEditorWidgetView metaDataEditor,
                                       final SessionManager sessionManager,
                                       final Event<NotificationEvent> notification) {
        this.view = metaDataEditor;
        this.sessionManager = sessionManager;
        this.notification = notification;
    }

    @Override
    public String getName() {
        return MetaDataEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();
        view.init(this);

        final Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        path = diagram.getMetadata().getPath();

        formGroup.render(view.asWidget(), field);

        return formGroup;
    }

    @Override
    protected void setReadOnly(final boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    @Override
    public void doSave() {
        view.doSave();
    }

    @Override
    public void addAttribute() {
        List<MetaDataRow> as = view.getMetaDataRows();
        if (as.isEmpty()) {
            view.setTableDisplayStyle();
        }
        MetaDataRow newAttribute = new MetaDataRow();
        as.add(newAttribute);
        MetaDataListItemWidgetView widget = view.getMetaDataWidget(view.getMetaDataRowsCount() - 1);

        widget.setParentWidget(this);
    }

    @Override
    public void notifyModelChanged() {
        doSave();
    }

    @Override
    public List<MetaDataRow> deserializeMetaDataAttributes(final String s) {
        List<MetaDataRow> attributeRows = new ArrayList<>();
        if (s != null && !s.isEmpty()) {
            String[] vs = s.split(DELIMITER);
            for (String v : vs) {
                if (!v.isEmpty()) {
                    MetaDataAttribute att = MetaDataAttribute.deserialize(v);
                    if (null != att.getAttribute() && !att.getAttribute().isEmpty()) {
                        attributeRows.add(new MetaDataRow(att));
                    }
                }
            }
        }
        return attributeRows;
    }

    @Override
    public String serializeMetaDataAttributes(final List<MetaDataRow> metaDataRows) {
        List<MetaDataAttribute> metaData = new ArrayList<>();
        for (MetaDataRow row : metaDataRows) {
            if (row.getAttribute() != null && row.getAttribute().length() > 0) {
                metaData.add(new MetaDataAttribute(row));
            }
        }
        return StringUtils.getStringForList(metaData, DELIMITER);
    }

    @Override
    public boolean isDuplicateAttribute(final String attribute) {
        if (attribute == null || attribute.trim().isEmpty()) {
            return false;
        }
        List<MetaDataRow> as = view.getMetaDataRows();
        if (as != null && !as.isEmpty()) {
            int nameCount = 0;
            String currName = attribute.trim();
            for (MetaDataRow row : as) {
                String rowName = row.getAttribute();
                if (rowName != null && currName.compareTo(rowName.trim()) == 0) {
                    nameCount++;
                    if (nameCount > 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void removeMetaData(final MetaDataRow metaDataRow) {
        view.getMetaDataRows().remove(metaDataRow);
        doSave();
    }

    @Override
    public Path getDiagramPath() {
        return path;
    }

    @Override
    public void showErrorMessage(String message) {
        notification.fire(new NotificationEvent(message,
                                                NotificationEvent.NotificationType.ERROR));
    }
}