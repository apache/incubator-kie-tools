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

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.util.NameUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.util.TypeRefUtils;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public abstract class NameAndDataTypeHeaderMetaData extends ValueAndDataTypeHeaderMetaData<Name, HasName> {

    public NameAndDataTypeHeaderMetaData(final HasExpression hasExpression,
                                         final Optional<HasName> hasValue,
                                         final Consumer<HasName> clearValueConsumer,
                                         final BiConsumer<HasName, Name> setValueConsumer,
                                         final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                         final TranslationService translationService,
                                         final CellEditorControlsView.Presenter cellEditorControls,
                                         final ValueAndDataTypePopoverView.Presenter editor) {
        super(hasValue,
              () -> TypeRefUtils.getTypeRefOfExpression(hasExpression.getExpression(), hasExpression),
              clearValueConsumer,
              setValueConsumer,
              setTypeRefConsumer,
              translationService,
              cellEditorControls,
              editor);
    }

    public NameAndDataTypeHeaderMetaData(final Optional<HasName> hasValue,
                                         final Supplier<HasTypeRef> hasTypeRef,
                                         final Consumer<HasName> clearValueConsumer,
                                         final BiConsumer<HasName, Name> setValueConsumer,
                                         final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                         final TranslationService translationService,
                                         final CellEditorControlsView.Presenter cellEditorControls,
                                         final ValueAndDataTypePopoverView.Presenter editor) {
        super(hasValue,
              hasTypeRef,
              clearValueConsumer,
              setValueConsumer,
              setTypeRefConsumer,
              translationService,
              cellEditorControls,
              editor);
    }

    @Override
    protected boolean isEmptyValue(final Name value) {
        return Objects.isNull(value) || StringUtils.isEmpty(value.getValue());
    }

    @Override
    public Name toModelValue(final String componentValue) {
        return new Name(componentValue);
    }

    @Override
    public String toWidgetValue(final Name modelValue) {
        return modelValue.getValue();
    }

    @Override
    public String getValueLabel() {
        return translationService.getTranslation(DMNEditorConstants.NameAndDataTypePopover_NameLabel);
    }

    @Override
    public String normaliseValue(final String componentValue) {
        return NameUtils.normaliseName(componentValue);
    }

    @Override
    public Name getValue() {
        return hasValue.orElse(HasName.NOP).getValue();
    }
}
