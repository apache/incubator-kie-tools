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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.ReassignmentRow;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

@Dependent
public class ReassignmentEditorWidget implements IsWidget,
                                                 ReassignmentEditorWidgetView.Presenter {

    private ReassignmentEditorWidgetView view;

    private ClientTranslationService translationService;

    @Inject
    public ReassignmentEditorWidget(ReassignmentEditorWidgetView view,
                                    ClientTranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
        this.view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getNameHeader() {
        return translationService.getValue(StunnerBPMNConstants.REASSIGNMENT_LABEL);
    }

    @Override
    public void createOrEdit(ReassignmentWidgetView parent, ReassignmentRow row) {
        view.createOrEdit(parent, row);
    }

    public void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }
}
