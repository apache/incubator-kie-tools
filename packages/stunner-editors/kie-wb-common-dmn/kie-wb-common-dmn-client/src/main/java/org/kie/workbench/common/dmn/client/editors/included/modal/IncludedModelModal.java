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

package org.kie.workbench.common.dmn.client.editors.included.modal;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPagePresenter;
import org.kie.workbench.common.dmn.client.editors.included.commands.AddIncludedModelCommand;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdown;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.dmn.client.events.IncludedPMMLModelUpdate;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.client.editors.expressions.util.NameUtils.normaliseName;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class IncludedModelModal extends Elemental2Modal<IncludedModelModal.View> {

    static final String WIDTH = "600px";

    private final DMNAssetsDropdown dropdown;

    private final ImportRecordEngine recordEngine;

    private final DMNIncludeModelsClient client;

    private final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent;

    private final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    private final Event<RefreshDecisionComponents> refreshPMMLComponentsEvent;

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    private final SessionManager sessionManager;

    private IncludedModelsPagePresenter grid;

    @Inject
    public IncludedModelModal(final View view,
                              final DMNAssetsDropdown dropdown,
                              final ImportRecordEngine recordEngine,
                              final DMNIncludeModelsClient client,
                              final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent,
                              final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent,
                              final @IncludedPMMLModelUpdate Event<RefreshDecisionComponents> refreshPMMLComponentsEvent,
                              final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final SessionManager sessionManager) {
        super(view);
        this.dropdown = dropdown;
        this.recordEngine = recordEngine;
        this.client = client;
        this.refreshDataTypesListEvent = refreshDataTypesListEvent;
        this.refreshDecisionComponentsEvent = refreshDecisionComponentsEvent;
        this.refreshPMMLComponentsEvent = refreshPMMLComponentsEvent;
        this.sessionCommandManager = sessionCommandManager;
        this.sessionManager = sessionManager;
    }

    @Override
    @PostConstruct
    public void setup() {
        superSetup();
        setWidth(WIDTH);
        getView().init(this);
        getView().setupAssetsDropdown(getInitializedDropdownElement());
    }

    public void init(final IncludedModelsPagePresenter grid) {
        this.grid = grid;
    }

    @Override
    public void show() {
        getDropdown().loadAssets();
        getView().initialize();
        getView().disableIncludeButton();
        superShow();
    }

    void superShow() {
        super.show();
    }

    HTMLElement getInitializedDropdownElement() {
        getDropdown().initialize();
        getDropdown().registerOnChangeHandler(getOnValueChanged());
        return getDropdown().getElement();
    }

    public void include() {
        getDropdown()
                .getValue()
                .ifPresent(value -> {
                    sessionCommandManager.execute(getCanvasHandler(), createAddIncludedModelCommand(value));
                    hide();
                });
    }

    AbstractCanvasHandler getCanvasHandler() {
        return (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
    }

    AddIncludedModelCommand createAddIncludedModelCommand(final KieAssetsDropdownItem value) {
        return new AddIncludedModelCommand(value,
                                           grid,
                                           refreshDecisionComponentsEvent,
                                           refreshPMMLComponentsEvent,
                                           refreshDataTypesListEvent,
                                           recordEngine,
                                           client,
                                           normaliseName(getView().getModelNameInput()));
    }

    @Override
    public void hide() {
        superHide();
        getDropdown().clear();
    }

    private DMNAssetsDropdown getDropdown() {
        return dropdown;
    }

    void superHide() {
        super.hide();
    }

    Command getOnValueChanged() {
        return this::onValueChanged;
    }

    void onValueChanged() {
        if (isValidValues()) {
            getView().enableIncludeButton();
        } else {
            getView().disableIncludeButton();
        }
    }

    boolean isValidValues() {
        return !isEmpty(getView().getModelNameInput()) && getDropdown().getValue().isPresent();
    }

    public interface View extends Elemental2Modal.View<IncludedModelModal> {

        String getModelNameInput();

        void setupAssetsDropdown(final HTMLElement dropdown);

        void initialize();

        void disableIncludeButton();

        void enableIncludeButton();
    }
}
