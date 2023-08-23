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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.KeyboardEvent;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.$;

@Templated
@Dependent
public class ValueAndDataTypePopoverViewImpl extends AbstractPopoverViewImpl implements ValueAndDataTypePopoverView {

    static final String TYPE_SELECTOR_BUTTON_SELECTOR = "button.dropdown-toggle.btn-default";
    static final String MANAGE_BUTTON_SELECTOR = "#typeButton";
    static final String TAB_KEY = "Tab";
    static final String ESCAPE_KEY = "Escape";
    static final String ESC_KEY = "Esc";
    static final String ENTER_KEY = "Enter";
    static final String DROPDOWN_ELEMENT_SELECTOR = ".bs-container.btn-group.bootstrap-select.show-tick.input-group-btn";

    @DataField("valueEditor")
    private Input valueEditor;

    @DataField("typeRefSelector")
    private DataTypePickerWidget typeRefEditor;

    @DataField("valueLabel")
    private Span valueLabel;

    @DataField("dataTypeLabel")
    private Span dataTypeLabel;

    private Presenter presenter;

    private String currentValue;

    private String previousValue;

    private QName currentTypeRef;

    private QName previousTypeRef;

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

        static final String OPEN_CLASS = "open";

        private final ParameterizedCommand<Optional<String>> commandShow;

        private final Command commandHide;

        BootstrapSelectDropDownMonitor(final ParameterizedCommand<Optional<String>> commandShow,
                                       final Command commandHide) {
            this.commandShow = commandShow;
            this.commandHide = commandHide;
        }

        void show(final Optional<String> popoverTitle) {
            commandShow.execute(popoverTitle);
        }

        void hide() {
            kieDataTypeSelect().off(BOOTSTRAP_SELECT_SHOWN_EVENT);

            //If drop-down is visible defer closure of popover until drop-down has closed.
            if (isDropDownVisible()) {
                kieDataTypeSelect().on(BOOTSTRAP_SELECT_HIDDEN_EVENT, (event) -> onHide());
            } else {
                onHide();
            }
        }

        void onHide() {
            kieDataTypeSelect().off(BOOTSTRAP_SELECT_HIDDEN_EVENT);
            commandHide.execute();
        }

        boolean isDropDownVisible() {
            final elemental2.dom.Element menuElement = getMenuElement();
            return Optional
                    .ofNullable(menuElement)
                    .map(element -> element.classList.contains(OPEN_CLASS))
                    .orElse(false);
        }

        elemental2.dom.Element getMenuElement() {
            return DomGlobal.document.querySelector(DROPDOWN_ELEMENT_SELECTOR);
        }

        /**
         * Wrapper due to a testing purpose
         */
        JQuerySelectPicker kieDataTypeSelect() {
            return $(SELECT_ID);
        }
    }

    public ValueAndDataTypePopoverViewImpl() {
        //CDI proxy
    }

    @Inject
    public ValueAndDataTypePopoverViewImpl(final Input valueEditor,
                                           final DataTypePickerWidget typeRefEditor,
                                           final Div popoverElement,
                                           final Div popoverContentElement,
                                           final Span valueLabel,
                                           final Span dataTypeLabel,
                                           final JQueryProducer.JQuery<Popover> jQueryPopover,
                                           final TranslationService translationService) {
        super(popoverElement,
              popoverContentElement,
              jQueryPopover);

        this.valueEditor = valueEditor;
        this.typeRefEditor = typeRefEditor;
        this.popoverElement = popoverElement;
        this.popoverContentElement = popoverContentElement;
        this.valueLabel = valueLabel;
        this.dataTypeLabel = dataTypeLabel;
        this.jQueryPopover = jQueryPopover;

        this.dataTypeLabel.setTextContent(translationService.getTranslation(DMNEditorConstants.NameAndDataTypePopover_DataTypeLabel));

        //GWT runs into an infinite loop if these are defined as method references :-(
        this.monitor = new BootstrapSelectDropDownMonitor(popoverTitle -> ValueAndDataTypePopoverViewImpl.super.show(popoverTitle),
                                                          () -> ValueAndDataTypePopoverViewImpl.super.hide());
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;

        typeRefEditor.addValueChangeHandler(e -> currentTypeRef = e.getValue());

        setKeyDownListeners();
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public QName getCurrentTypeRef() {
        return currentTypeRef;
    }

    @Override
    protected void setKeyDownListeners() {
        super.setKeyDownListeners();

        final Element manageButton = getManageButton();
        manageButton.addEventListener(BrowserEvents.KEYDOWN,
                                      getManageButtonKeyDownEventListener(),
                                      false);

        final Element typeSelectorButton = getTypeSelectorButton();
        typeSelectorButton.addEventListener(BrowserEvents.KEYDOWN,
                                            getTypeSelectorKeyDownEventListener(),
                                            false);
    }

    @Override
    //Overridden with identical access modifier to make visible to tests in this package.
    protected EventListener getKeyDownEventListener() {
        return super.getKeyDownEventListener();
    }

    EventListener getTypeSelectorKeyDownEventListener() {
        return (e) -> typeSelectorKeyDownEventListener(e);
    }

    EventListener getManageButtonKeyDownEventListener() {
        return (e) -> manageButtonKeyDownEventListener(e);
    }

    Button getTypeSelectorButton() {
        return (Button) getElement().querySelector(TYPE_SELECTOR_BUTTON_SELECTOR);
    }

    Button getManageButton() {
        return (Button) getElement().querySelector(MANAGE_BUTTON_SELECTOR);
    }

    void typeSelectorKeyDownEventListener(final Object event) {
        if (event instanceof KeyboardEvent) {
            final KeyboardEvent keyEvent = (KeyboardEvent) event;
            if (isEnterKeyPressed(keyEvent)) {
                hide(true);
                keyEvent.preventDefault();
                onClosedByKeyboard();
            } else if (isEscapeKeyPressed(keyEvent)) {
                reset();
                hide(false);
                onClosedByKeyboard();
            } else if (isTabKeyPressed(keyEvent)) {
                if (keyEvent.shiftKey) {
                    final Button manageButton = getManageButton();
                    manageButton.focus();
                } else {
                    valueEditor.focus();
                }
                keyEvent.preventDefault();
            }
        }
    }

    void manageButtonKeyDownEventListener(final Object event) {
        if (event instanceof KeyboardEvent) {
            final KeyboardEvent keyEvent = (KeyboardEvent) event;
            if (isEscapeKeyPressed(keyEvent)) {
                reset();
                hide(false);
                onClosedByKeyboard();
            }
        }
    }

    boolean isTabKeyPressed(final KeyboardEvent event) {
        return Objects.equals(event.key, TAB_KEY);
    }

    @Override
    public void setDMNModel(final DMNModelInstrumentedBase dmnModel) {
        typeRefEditor.setDMNModel(dmnModel);
        previousTypeRef = typeRefEditor.getValue();
    }

    @Override
    public void initValue(final String value) {
        valueEditor.setValue(value);
        currentValue = value;
        previousValue = value;
    }

    @Override
    public void initSelectedTypeRef(final QName typeRef) {
        typeRefEditor.setValue(typeRef, false);
        currentTypeRef = typeRef;
        previousTypeRef = typeRef;
    }

    @Override
    public void show(final Optional<String> popoverTitle) {
        valueLabel.setTextContent(presenter.getValueLabel());
        getMonitor().show(popoverTitle);
    }

    @Override
    protected void onShownFocus() {
        valueEditor.focus();
    }

    @Override
    public void hide() {
        hide(true);
    }

    public void hide(final boolean applyChanges) {
        if (isVisible()) {
            valueEditor.blur();
            getMonitor().hide();
            if (applyChanges) {
                applyChanges();
            }
        }
    }

    BootstrapSelectDropDownMonitor getMonitor() {
        return monitor;
    }

    @SuppressWarnings("unused")
    @EventHandler("valueEditor")
    void onValueChange(final BlurEvent event) {
        final String value = valueEditor.getValue();
        final String normalisedValue = presenter.normaliseValue(value);
        if (!Objects.equals(normalisedValue, value)) {
            valueEditor.setValue(normalisedValue);
        }
        currentValue = normalisedValue;
    }

    void applyChanges() {
        presenter.setValue(currentValue);
        if (!Objects.isNull(currentTypeRef)) {
            presenter.setTypeRef(currentTypeRef);
        }
    }

    @Override
    public void reset() {
        valueEditor.setValue(previousValue);
        if (!Objects.isNull(previousTypeRef)) {
            typeRefEditor.setValue(previousTypeRef);
        }
        currentValue = previousValue;
        currentTypeRef = previousTypeRef;
    }

    @SuppressWarnings("unused")
    @EventHandler("valueEditor")
    public void onValueEditorKeyDown(final KeyDownEvent event) {
        if (isEnter(event)) {
            hide(true);
            onClosedByKeyboard();
        } else if (isEsc(event)) {
            reset();
            hide(false);
            onClosedByKeyboard();
        } else if (event.isShiftKeyDown() && isTab(event)) {
            final Button typeSelectorButton = getTypeSelectorButton();
            typeSelectorButton.focus();
            event.preventDefault();
        }
    }

    boolean isTab(final KeyDownEvent event) {
        return Objects.equals(event.getNativeKeyCode(), KeyCodes.KEY_TAB);
    }

    boolean isEsc(final KeyDownEvent event) {
        return Objects.equals(event.getNativeKeyCode(), KeyCodes.KEY_ESCAPE);
    }

    boolean isEnter(final KeyDownEvent event) {
        return Objects.equals(event.getNativeKeyCode(), KeyCodes.KEY_ENTER);
    }
}
