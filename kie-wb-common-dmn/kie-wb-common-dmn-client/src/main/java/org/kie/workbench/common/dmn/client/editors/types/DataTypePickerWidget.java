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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;

@Dependent
@Templated
public class DataTypePickerWidget extends Composite implements HasValue<QName>,
                                                               HasEnabled {

    static final Comparator<BuiltInType> BUILT_IN_TYPE_COMPARATOR = Comparator.comparing(o -> o.getName());

    static final Comparator<ItemDefinition> ITEM_DEFINITION_COMPARATOR = Comparator.comparing(o -> o.getName().getValue());

    @DataField
    private Button typeButton;

    @DataField
    private Select typeSelector;

    private QNameConverter qNameConverter;

    private DMNGraphUtils dmnGraphUtils;

    private DataTypeModal dataTypeModal;

    private QName type;

    private boolean enabled;

    public DataTypePickerWidget() {
        //CDI proxy
    }

    @Inject
    public DataTypePickerWidget(final Button typeButton,
                                final TranslationService translationService,
                                final QNameConverter qNameConverter,
                                final DMNGraphUtils dmnGraphUtils,
                                final DataTypeModal dataTypeModal) {
        this.typeButton = typeButton;
        this.typeSelector = GWT.create(Select.class);
        this.qNameConverter = qNameConverter;
        this.dmnGraphUtils = dmnGraphUtils;
        this.dataTypeModal = dataTypeModal;

        this.typeSelector.setShowTick(true);
        this.typeSelector.setLiveSearch(true);
        this.typeSelector.setLiveSearchPlaceholder(translationService.getTranslation(DMNEditorConstants.TypePickerWidget_Choose));
        this.typeSelector.getElement().setAttribute("data-container", "body");
        this.typeSelector.refresh();

        this.typeSelector.addValueChangeHandler((event) -> setValue(qNameConverter.toModelValue(event.getValue()), true));
    }

    public void setDMNModel(final DMNModelInstrumentedBase dmnModel) {
        this.qNameConverter.setDMNModel(dmnModel);
        typeSelector.clear();

        addBuiltInTypes();
        addItemDefinitions();

        typeSelector.refresh();
    }

    private void addBuiltInTypes() {
        Stream.of(BuiltInType.values())
                .sorted(BUILT_IN_TYPE_COMPARATOR)
                .map(this::makeTypeSelector)
                .filter(Optional::isPresent)
                .forEach(o -> typeSelector.add(o.get()));
    }

    Optional<Option> makeTypeSelector(final BuiltInType bit) {
        final Option o = GWT.create(Option.class);
        o.setText(bit.getName());
        o.setValue(qNameConverter.toWidgetValue(bit.asQName()));
        return Optional.of(o);
    }

    private void addItemDefinitions() {
        final List<ItemDefinition> itemDefinitions = dmnGraphUtils.getDefinitions().getItemDefinition();

        //There will always be BuiltInTypes so it safe to add a divider
        if (itemDefinitions.size() > 0) {
            addDivider();
        }

        itemDefinitions.stream()
                .sorted(ITEM_DEFINITION_COMPARATOR)
                .map(this::makeTypeSelector)
                .filter(Optional::isPresent)
                .forEach(o -> typeSelector.add(o.get()));
    }

    void addDivider() {
        final Option o = GWT.create(Option.class);
        o.setDivider(true);
        typeSelector.add(o);
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

    @EventHandler("typeButton")
    @SuppressWarnings("unused")
    public void onClickTypeButton(final ClickEvent clickEvent) {
        dataTypeModal.show();
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
        this.enabled = enabled;
        typeButton.setEnabled(enabled);
        typeSelector.setEnabled(enabled);
    }
}
