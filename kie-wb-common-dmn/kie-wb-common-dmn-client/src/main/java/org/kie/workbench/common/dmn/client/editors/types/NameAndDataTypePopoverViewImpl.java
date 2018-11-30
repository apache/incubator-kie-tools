/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.kie.workbench.common.dmn.client.editors.types.listview.common.JQuerySelectPicker.$;

@Templated
@ApplicationScoped
public class NameAndDataTypePopoverViewImpl extends AbstractPopoverViewImpl implements NameAndDataTypePopoverView {

    @DataField("nameEditor")
    private Input nameEditor;

    @DataField("typeRefSelector")
    private DataTypePickerWidget typeRefEditor;

    @DataField("nameLabel")
    private Span nameLabel;

    @DataField("dataTypeLabel")
    private Span dataTypeLabel;

    private Presenter presenter;

    private BootstrapSelectDropDownMonitor monitor;

    /**
     * This is a workaround for dismissing a Bootstrap {@link Select} {@link DropDown} child element when
     * the containing {@link Popover} closes. Unfortunately {@link Select} does not have a method to close the
     * {@link DropDown} child element thus leading to this workaround.
     * <p>
     * The {@link DropDown} element of a {@link Select} is closed when a 'click' event is received on a DOM element
     * other than the {@link DropDown} however the 'click' event does not propagate to the {@link Popover} children
     * once the {@link Popover} closes (as it is removed from the DOM) and hence the {@link DropDown} remains visible.
     * This class defers closure of the {@link Popover} until the {@link DropDown} has been hidden; when required.
     * i.e. The {@link DropDown} was visible.
     */
    static class BootstrapSelectDropDownMonitor {

        static final String SELECT_ID = "#kieDataType";

        static final String BOOTSTRAP_SELECT_SHOWN_EVENT = "shown.bs.select";

        static final String BOOTSTRAP_SELECT_HIDDEN_EVENT = "hidden.bs.select";

        boolean isSelectDropDownShown = false;

        private final ParameterizedCommand<Optional<String>> commandShow;

        private final Command commandHide;

        BootstrapSelectDropDownMonitor(final ParameterizedCommand<Optional<String>> commandShow,
                                       final Command commandHide) {
            this.commandShow = commandShow;
            this.commandHide = commandHide;
        }

        void show(final Optional<String> editorTitle) {
            commandShow.execute(editorTitle);

            //Track state of drop-down element
            $(SELECT_ID).on(BOOTSTRAP_SELECT_SHOWN_EVENT, (event) -> isSelectDropDownShown = true);
            $(SELECT_ID).on(BOOTSTRAP_SELECT_HIDDEN_EVENT, (event) -> isSelectDropDownShown = false);

            isSelectDropDownShown = false;
        }

        void hide() {
            $(SELECT_ID).off(BOOTSTRAP_SELECT_SHOWN_EVENT);

            //If drop-down is visible defer closure of popover until drop-down has closed.
            if (isSelectDropDownShown) {
                $(SELECT_ID).on(BOOTSTRAP_SELECT_HIDDEN_EVENT, (event) -> onHide());
            } else {
                onHide();
            }
        }

        void onHide() {
            $(SELECT_ID).off(BOOTSTRAP_SELECT_HIDDEN_EVENT);
            commandHide.execute();
        }
    }

    public NameAndDataTypePopoverViewImpl() {
        //CDI proxy
    }

    @Inject
    public NameAndDataTypePopoverViewImpl(final Input nameEditor,
                                          final DataTypePickerWidget typeRefEditor,
                                          final Div popoverElement,
                                          final Div popoverContentElement,
                                          final Span nameLabel,
                                          final Span dataTypeLabel,
                                          final JQueryProducer.JQuery<Popover> jQueryPopover,
                                          final TranslationService translationService) {
        super(popoverElement,
              popoverContentElement,
              jQueryPopover);

        this.nameEditor = nameEditor;
        this.typeRefEditor = typeRefEditor;
        this.popoverElement = popoverElement;
        this.popoverContentElement = popoverContentElement;
        this.nameLabel = nameLabel;
        this.dataTypeLabel = dataTypeLabel;
        this.jQueryPopover = jQueryPopover;

        this.nameLabel.setTextContent(translationService.getTranslation(DMNEditorConstants.NameAndDataTypePopover_NameLabel));
        this.dataTypeLabel.setTextContent(translationService.getTranslation(DMNEditorConstants.NameAndDataTypePopover_DataTypeLabel));

        //GWT runs into an infinite loop if these are defined as method references :-(
        this.monitor = new BootstrapSelectDropDownMonitor((editorTitle) -> NameAndDataTypePopoverViewImpl.super.show(editorTitle),
                                                          () -> NameAndDataTypePopoverViewImpl.super.hide());
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;

        typeRefEditor.addValueChangeHandler(e -> presenter.setTypeRef(e.getValue()));
    }

    @Override
    public void setDMNModel(final DMNModelInstrumentedBase dmnModel) {
        typeRefEditor.setDMNModel(dmnModel);
    }

    @Override
    public void initName(final String name) {
        nameEditor.setValue(name);
    }

    @Override
    public void initSelectedTypeRef(final QName typeRef) {
        typeRefEditor.setValue(typeRef,
                               false);
    }

    @Override
    public void show(final Optional<String> editorTitle) {
        monitor.show(editorTitle);
    }

    @Override
    public void hide() {
        monitor.hide();
    }

    @EventHandler("nameEditor")
    @SuppressWarnings("unused")
    void onNameChange(final BlurEvent event) {
        presenter.setName(nameEditor.getValue());
    }

    public void onDataTypePageNavTabActiveEvent(final @Observes DataTypePageTabActiveEvent event) {
        hide();
    }
}
