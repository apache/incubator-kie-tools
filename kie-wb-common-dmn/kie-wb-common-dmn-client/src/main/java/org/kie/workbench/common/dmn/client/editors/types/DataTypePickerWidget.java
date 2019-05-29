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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import org.gwtbootstrap3.extras.select.client.ui.OptGroup;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;

@Dependent
@Templated
public class DataTypePickerWidget extends Composite implements HasValue<QName>,
                                                               HasEnabled {

    static final String CSS_DISPLAY = "display";

    static final String CSS_DISPLAY_NONE = "none";

    static final Comparator<BuiltInType> BUILT_IN_TYPE_COMPARATOR = Comparator.comparing(o -> {
        if (o == BuiltInType.UNDEFINED) {
            return "";
        }
        return o.getName();
    });

    static final Comparator<ItemDefinition> ITEM_DEFINITION_COMPARATOR = Comparator.comparing(o -> o.getName().getValue());

    @DataField
    private Anchor typeButton;

    @DataField
    private Div manageContainer;

    @DataField
    private Span manageLabel;

    @DataField
    private Select typeSelector;

    private TranslationService translationService;

    private QNameConverter qNameConverter;

    private DMNGraphUtils dmnGraphUtils;

    private Event<DataTypePageTabActiveEvent> dataTypePageActiveEvent;

    private ItemDefinitionUtils itemDefinitionUtils;

    private QName type;

    private boolean enabled;

    private boolean modelAllowsOnlyVisualChange;

    public DataTypePickerWidget() {
        //CDI proxy
    }

    @Inject
    public DataTypePickerWidget(final Anchor typeButton,
                                final Div manageContainer,
                                final Span manageLabel,
                                final TranslationService translationService,
                                final QNameConverter qNameConverter,
                                final DMNGraphUtils dmnGraphUtils,
                                final Event<DataTypePageTabActiveEvent> dataTypePageActiveEvent,
                                final ItemDefinitionUtils itemDefinitionUtils) {
        this.typeButton = typeButton;
        this.manageContainer = manageContainer;
        this.manageLabel = manageLabel;
        this.translationService = translationService;
        this.typeSelector = GWT.create(Select.class);
        this.qNameConverter = qNameConverter;
        this.dmnGraphUtils = dmnGraphUtils;
        this.dataTypePageActiveEvent = dataTypePageActiveEvent;
        this.itemDefinitionUtils = itemDefinitionUtils;

        this.typeSelector.setShowTick(true);
        this.typeSelector.setLiveSearch(true);
        this.typeSelector.getElement().setAttribute("data-container", "body");
        this.typeSelector.refresh();

        this.typeSelector.setLiveSearchPlaceholder(translationService.getTranslation(DMNEditorConstants.TypePickerWidget_Choose));
        this.manageLabel.setTextContent(translationService.getTranslation(DMNEditorConstants.TypePickerWidget_Manage));

        this.typeSelector.addValueChangeHandler((event) -> setValue(qNameConverter.toModelValue(event.getValue()), true));
    }

    public void setDMNModel(final DMNModelInstrumentedBase dmnModel) {
        if (dmnModel instanceof DynamicReadOnly) {
            modelAllowsOnlyVisualChange = ((DynamicReadOnly) dmnModel).isAllowOnlyVisualChange();
            if (modelAllowsOnlyVisualChange) {
                this.enabled = false;
                this.typeSelector.setEnabled(false);
            }
        }
        this.qNameConverter.setDMNModel(dmnModel);
        populateTypeSelector();
    }

    void populateTypeSelector(){
        typeSelector.clear();

        addBuiltInTypes();
        addItemDefinitions();

        typeSelector.refresh();
    }

    void addBuiltInTypes() {
        final OptGroup group = GWT.create(OptGroup.class);
        group.setLabel(translationService.getTranslation(DMNEditorConstants.DataTypeSelectView_DefaultTitle));

        Stream.of(BuiltInType.values())
                .sorted(BUILT_IN_TYPE_COMPARATOR)
                .map(this::makeTypeSelector)
                .filter(Optional::isPresent)
                .forEach(o -> group.add(o.get()));

        typeSelector.add(group);
    }

    Optional<Option> makeTypeSelector(final BuiltInType bit) {
        final Option o = GWT.create(Option.class);
        o.setText(bit.getName());

        o.setValue(qNameConverter.toWidgetValue(normaliseBuiltInTypeTypeRef(bit.asQName())));
        return Optional.of(o);
    }

    QName normaliseBuiltInTypeTypeRef(final QName typeRef) {
        return itemDefinitionUtils.normaliseTypeRef(typeRef);
    }

    void addItemDefinitions() {
        final Definitions definitions = dmnGraphUtils.getDefinitions();
        final List<ItemDefinition> itemDefinitions = definitions != null ? definitions.getItemDefinition() : Collections.emptyList();

        final OptGroup group = GWT.create(OptGroup.class);
        group.setLabel(translationService.getTranslation(DMNEditorConstants.DataTypeSelectView_CustomTitle));

        itemDefinitions.stream()
                .sorted(ITEM_DEFINITION_COMPARATOR)
                .map(this::makeTypeSelector)
                .filter(Optional::isPresent)
                .forEach(o -> group.add(o.get()));

        typeSelector.add(group);
    }

    Optional<Option> makeTypeSelector(final ItemDefinition id) {
        Option o = null;
        if (id.getName() != null) {
            final Name name = id.getName();
            o = GWT.create(Option.class);
            o.setText(name.getValue());
            o.setValue(qNameConverter.toWidgetValue(new QName(QName.NULL_NS_URI,
                                                              name.getValue(),
                                                              QName.DEFAULT_NS_PREFIX)));
        }
        return Optional.ofNullable(o);
    }

    @SuppressWarnings("unused")
    public void onDataTypePageNavTabActiveEvent(final @Observes DataTypeChangedEvent event) {
        populateTypeSelector();
    }

    @EventHandler("typeButton")
    @SuppressWarnings("unused")
    public void onClickTypeButton(final ClickEvent clickEvent) {
        dataTypePageActiveEvent.fire(new DataTypePageTabActiveEvent());
    }

    public void showManageLabel() {
        manageContainer.getStyle().removeProperty(CSS_DISPLAY);
    }

    public void hideManageLabel() {
        manageContainer.getStyle().setProperty(CSS_DISPLAY, CSS_DISPLAY_NONE);
    }

    @Override
    public QName getValue() {
        return type;
    }

    @Override
    public void setValue(final QName value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(final QName value,
                         final boolean fireEvents) {
        final QName oldValue = type;
        type = value;
        typeSelector.setValue(qNameConverter.toWidgetValue(type), false);

        if (fireEvents) {
            fireValueChangeEvent(oldValue);
        }
    }

    void fireValueChangeEvent(final QName oldValue) {
        ValueChangeEvent.fireIfNotEqual(this,
                                        oldValue,
                                        type);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<QName> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (modelAllowsOnlyVisualChange) {
            return;
        }
        this.enabled = enabled;
        this.typeSelector.setEnabled(enabled);
    }
}
