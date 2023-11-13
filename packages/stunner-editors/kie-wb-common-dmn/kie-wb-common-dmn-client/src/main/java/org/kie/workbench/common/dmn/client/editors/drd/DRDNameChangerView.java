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

package org.kie.workbench.common.dmn.client.editors.drd;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import elemental2.dom.HTMLAnchorElement;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;

import static com.google.gwt.dom.client.Style.Display.BLOCK;
import static com.google.gwt.dom.client.Style.Display.NONE;

@Templated
@ApplicationScoped
public class DRDNameChangerView implements DRDNameChanger {

    private static final int SESSION_HEADER_HEIGHT = 50;

    private final DMNDiagramsSession dmnDiagramsSession;
    private final Event<DMNDiagramSelected> selectedEvent;
    private SessionPresenter.View sessionPresenterView;

    @DataField("viewMode")
    private final DivElement viewMode;

    @DataField("editMode")
    private final DivElement editMode;

    @DataField("returnToDRG")
    private final HTMLAnchorElement returnToDRG;

    @DataField("drdName")
    private final Span drdName;

    @DataField("drdNameInput")
    private final InputElement drdNameInput;

    @Inject
    public DRDNameChangerView(final DMNDiagramsSession dmnDiagramsSession, final Event<DMNDiagramSelected> selectedEvent, final DivElement viewMode, final DivElement editMode, final HTMLAnchorElement returnToDRG, final Span drdName, final InputElement drdNameInput) {
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.selectedEvent = selectedEvent;
        this.viewMode = viewMode;
        this.editMode = editMode;
        this.returnToDRG = returnToDRG;
        this.drdName = drdName;
        this.drdNameInput = drdNameInput;
    }

    @Override
    public void setSessionPresenterView(SessionPresenter.View sessionPresenterView) {
        this.sessionPresenterView = sessionPresenterView;
        if (dmnDiagramsSession.isGlobalGraphSelected()) {
            hideDRDNameChanger();
        } else {
            showDRDNameChanger();
        }
    }

    @Override
    public void showDRDNameChanger() {
        sessionPresenterView.showSessionHeaderContainer(SESSION_HEADER_HEIGHT);
    }

    @Override
    public void hideDRDNameChanger() {
        sessionPresenterView.hideSessionHeaderContainer();
    }

    void onSettingCurrentDMNDiagramElement(final @Observes DMNDiagramSelected selected) {
        if (dmnDiagramsSession.isGlobalGraphSelected()) {
            hideDRDNameChanger();
        } else {
            this.drdName.setText(selected.getDiagramElement().getName().getValue());
            enableEditMode(false);
            showDRDNameChanger();
        }
    }

    @EventHandler("returnToDRG")
    void onClickReturnToDRG(final ClickEvent event) {
        hideDRDNameChanger();
        selectedEvent.fire(new DMNDiagramSelected(dmnDiagramsSession.getDRGDiagramElement()));
    }

    @EventHandler("viewMode")
    void enableEdit(final ClickEvent event) {
        drdNameInput.setValue(drdName.getText());
        enableEditMode(true);
        drdNameInput.focus();
    }

    @EventHandler("drdNameInput")
    void onInputTextKeyPress(final KeyDownEvent event) {
        if (event.getNativeEvent().getKeyCode() == 13) {
            saveForTheCurrentDiagram();
        }
    }

    @EventHandler("drdNameInput")
    void onInputTextBlur(final BlurEvent event) {
        saveForTheCurrentDiagram();
    }

    void saveForTheCurrentDiagram() {
        dmnDiagramsSession.getCurrentDMNDiagramElement().ifPresent(this::performSave);
    }

    private void performSave(final DMNDiagramElement dmnDiagramElement) {
        if (!Objects.equals(dmnDiagramElement.getName().getValue(), drdNameInput.getValue())) {
            dmnDiagramElement.getName().setValue(drdNameInput.getValue());
            selectedEvent.fire(new DMNDiagramSelected(dmnDiagramElement));
        } else {
            enableEditMode(false);
        }
    }

    private void enableEditMode(boolean enabled) {
        viewMode.getStyle().setDisplay(enabled ? NONE : BLOCK);
        editMode.getStyle().setDisplay(enabled ? BLOCK : NONE);
    }
}
