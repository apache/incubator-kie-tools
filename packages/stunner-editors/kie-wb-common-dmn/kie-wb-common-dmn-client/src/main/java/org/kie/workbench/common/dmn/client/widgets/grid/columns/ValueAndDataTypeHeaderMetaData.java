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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasValue;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.util.TypeRefUtils;
import org.kie.workbench.common.dmn.client.editors.types.HasValueAndTypeRef;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;

import static java.util.Collections.singletonList;

public abstract class ValueAndDataTypeHeaderMetaData<V, HV extends HasValue<V>> extends EditablePopupHeaderMetaData<HasValueAndTypeRef, ValueAndDataTypePopoverView.Presenter> implements HasValueAndTypeRef<V> {

    protected final Optional<HV> hasValue;
    protected final Supplier<HasTypeRef> hasTypeRef;
    protected final Consumer<HV> clearValueConsumer;
    protected final BiConsumer<HV, V> setValueConsumer;
    protected final BiConsumer<HasTypeRef, QName> setTypeRefConsumer;
    protected final TranslationService translationService;

    public ValueAndDataTypeHeaderMetaData(final HasExpression hasExpression,
                                          final Optional<HV> hasValue,
                                          final Consumer<HV> clearValueConsumer,
                                          final BiConsumer<HV, V> setValueConsumer,
                                          final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                          final TranslationService translationService,
                                          final CellEditorControlsView.Presenter cellEditorControls,
                                          final ValueAndDataTypePopoverView.Presenter editor) {
        this(hasValue,
             () -> TypeRefUtils.getTypeRefOfExpression(hasExpression.getExpression(), hasExpression),
             clearValueConsumer,
             setValueConsumer,
             setTypeRefConsumer,
             translationService,
             cellEditorControls,
             editor);
    }

    public ValueAndDataTypeHeaderMetaData(final Optional<HV> hasValue,
                                          final Supplier<HasTypeRef> hasTypeRef,
                                          final Consumer<HV> clearValueConsumer,
                                          final BiConsumer<HV, V> setValueConsumer,
                                          final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                          final TranslationService translationService,
                                          final CellEditorControlsView.Presenter cellEditorControls,
                                          final ValueAndDataTypePopoverView.Presenter editor) {
        super(cellEditorControls,
              editor);
        this.hasValue = hasValue;
        this.hasTypeRef = hasTypeRef;
        this.clearValueConsumer = clearValueConsumer;
        this.setValueConsumer = setValueConsumer;
        this.setTypeRefConsumer = setTypeRefConsumer;
        this.translationService = translationService;
    }

    @Override
    protected HasValueAndTypeRef getPresenter() {
        return this;
    }

    @Override
    public String getTitle() {
        return toWidgetValue(getValue());
    }

    @Override
    public void setValue(final V name) {
        hasValue.ifPresent(hn -> {
            if (Objects.equals(name, getValue())) {
                return;
            }

            if (isEmptyValue(name)) {
                clearValueConsumer.accept(hn);
            } else {
                setValueConsumer.accept(hn, name);
            }
        });
    }

    protected abstract boolean isEmptyValue(final V value);

    @Override
    public QName getTypeRef() {
        return hasTypeRef.get().getTypeRef();
    }

    @Override
    public void setTypeRef(final QName typeRef) {
        if (Objects.equals(typeRef, getTypeRef())) {
            return;
        }

        setTypeRefConsumer.accept(hasTypeRef.get(), typeRef);
    }

    @Override
    public Group render(final GridHeaderColumnRenderContext context,
                        final double blockWidth,
                        final double blockHeight) {
        return RendererUtils.getValueAndDataTypeHeaderText(this,
                                                           context,
                                                           blockWidth,
                                                           blockHeight);
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return hasTypeRef.get().asDMNModelInstrumentedBase();
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {
        return new ArrayList<>(singletonList(this));
    }
}
