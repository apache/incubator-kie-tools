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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
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
@ApplicationScoped
public class NameAndDataTypePopoverViewImpl extends AbstractPopoverViewImpl implements NameAndDataTypePopoverView {

    static final String TYPE_SELECTOR_BUTTON_SELECTOR = "button.dropdown-toggle.btn-default";
    static final String MANAGE_BUTTON_SELECTOR = "#typeButton";
    static final String TAB_KEY = "Tab";
    static final String ESCAPE_KEY = "Escape";
    static final String ESC_KEY = "Esc";
    static final String ENTER_KEY = "Enter";
    static final String DROPDOWN_ELEMENT_SELECTOR = ".bs-container.btn-group.bootstrap-select.show-tick.input-group-btn";

    @DataField("nameEditor")
    private Input nameEditor;

    @DataField("typeRefSelector")
    private DataTypePickerWidget typeRefEditor;

    @DataField("nameLabel")
    private Span nameLabel;

    @DataField("dataTypeLabel")
    private Span dataTypeLabel;

    private Presenter presenter;

    private String previousName;

    private String currentName;

    private QName previousTypeRef;

    private QName currentTypeRef;

    private BootstrapSelectDropDownMonitor monitor;

    private Optional<Consumer> closedByKeyboardCallback;

    @Override
    public void setOnClosedByKeyboardCallback(final Consumer callback) {
        closedByKeyboardCallback = Optional.ofNullable(callback);
    }

    public void onClosedByKeyboard() {
        getClosedByKeyboardCallback().ifPresent(c -> c.accept(this));
    }

    Optional<Consumer> getClosedByKeyboardCallback() {
        return closedByKeyboardCallback;
    }

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

        void show(final Optional<String> editorTitle) {
            commandShow.execute(editorTitle);
        }

        void hide() {
            kieDataTypeSelect().off(BOOTSTRAP_SELECT_SHOWN_EVENT);

            //If drop-down is visible defer closure of popover until drop-down has closed.
            if (isVisible()) {
                kieDataTypeSelect().on(BOOTSTRAP_SELECT_HIDDEN_EVENT, (event) -> onHide());
            } else {
                onHide();
            }
        }

        void onHide() {
            kieDataTypeSelect().off(BOOTSTRAP_SELECT_HIDDEN_EVENT);
            commandHide.execute();
        }

        boolean isVisible() {
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

        this.closedByKeyboardCallback = Optional.empty();
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;

        typeRefEditor.addValueChangeHandler(e -> currentTypeRef = e.getValue());

        setKeyDownListeners();
    }

    public QName getCurrentTypeRef() {
        return currentTypeRef;
    }

    public String getCurrentName() {
        return currentName;
    }

    void setKeyDownListeners() {
        popoverElement.addEventListener(BrowserEvents.KEYDOWN,
                                        getKeyDownEventListener(),
                                        false);

        final Element manageButton = getManageButton();
        manageButton.addEventListener(BrowserEvents.KEYDOWN,
                                      getManageButtonKeyDownEventListener(),
                                      false);

        final Element typeSelectorButton = getTypeSelectorButton();
        typeSelectorButton.addEventListener(BrowserEvents.KEYDOWN,
                                            getTypeSelectorKeyDownEventListener(),
                                            false);
    }

    EventListener getTypeSelectorKeyDownEventListener() {
        return (e) -> typeSelectorKeyDownEventListener(e);
    }

    EventListener getManageButtonKeyDownEventListener() {
        return (e) -> manageButtonKeyDownEventListener(e);
    }

    EventListener getKeyDownEventListener() {
        return (e) -> keyDownEventListener(e);
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
                    nameEditor.focus();
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

    void keyDownEventListener(final Object event) {
        if (event instanceof KeyboardEvent) {
            final KeyboardEvent keyEvent = (KeyboardEvent) event;
            if (isEnterKeyPressed(keyEvent)) {
                hide(true);
                keyEvent.stopPropagation();
                onClosedByKeyboard();
            } else if (isEscapeKeyPressed(keyEvent)) {
                reset();
                hide(false);
                onClosedByKeyboard();
            }
        }
    }

    boolean isTabKeyPressed(final KeyboardEvent event) {
        return Objects.equals(event.key, TAB_KEY);
    }

    boolean isEscapeKeyPressed(final KeyboardEvent event) {
        return Objects.equals(event.key, ESC_KEY) || Objects.equals(event.key, ESCAPE_KEY);
    }

    boolean isEnterKeyPressed(final KeyboardEvent event) {
        return Objects.equals(event.key, ENTER_KEY);
    }

    @Override
    public void setDMNModel(final DMNModelInstrumentedBase dmnModel) {
        typeRefEditor.setDMNModel(dmnModel);
        previousTypeRef = typeRefEditor.getValue();
    }

    @Override
    public void initName(final String name) {
        nameEditor.setValue(name);
        previousName = name;
        currentName = name;
    }

    @Override
    public void initSelectedTypeRef(final QName typeRef) {
        typeRefEditor.setValue(typeRef,
                               false);
    }

    @Override
    public void show(final Optional<String> editorTitle) {
        getMonitor().show(editorTitle);
        nameEditor.focus();
    }

    @Override
    public void hide() {
        hide(true);
    }

    public void hide(boolean applyChanges) {
        nameEditor.blur();
        getMonitor().hide();
        if (applyChanges) {
            applyChanges();
        }
    }

    BootstrapSelectDropDownMonitor getMonitor() {
        return monitor;
    }

    @EventHandler("nameEditor")
    @SuppressWarnings("unused")
    void onNameChange(final BlurEvent event) {
        currentName = nameEditor.getValue();
    }

    void applyChanges() {
        presenter.setName(currentName);
        if (!Objects.isNull(currentTypeRef)) {
            presenter.setTypeRef(currentTypeRef);
        }
    }

    void reset() {
        nameEditor.setValue(previousName);
        if (!Objects.isNull(previousTypeRef)) {
            typeRefEditor.setValue(previousTypeRef);
        }
        currentName = previousName;
        previousTypeRef = null;
        currentTypeRef = null;
    }

    @EventHandler("nameEditor")
    public void onNameEditorKeyDown(final KeyDownEvent event) {
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

    public void onDataTypePageNavTabActiveEvent(final @Observes DataTypePageTabActiveEvent event) {
        hide();
    }
}
