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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.util.TypeRefUtils;
import org.kie.workbench.common.dmn.client.editors.types.HasNameAndTypeRef;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;

import static java.util.Collections.singletonList;

public abstract class NameAndDataTypeHeaderMetaData<E extends Expression> extends EditablePopupHeaderMetaData<HasNameAndTypeRef, NameAndDataTypePopoverView.Presenter> implements HasNameAndTypeRef {

    private final Optional<HasName> hasName;
    private final Supplier<HasTypeRef> hasTypeRef;
    private final Consumer<HasName> clearDisplayNameConsumer;
    private final BiConsumer<HasName, Name> setDisplayNameConsumer;
    private final BiConsumer<HasTypeRef, QName> setTypeRefConsumer;

    public NameAndDataTypeHeaderMetaData(final HasExpression hasExpression,
                                         final Optional<E> expression,
                                         final Optional<HasName> hasName,
                                         final Consumer<HasName> clearDisplayNameConsumer,
                                         final BiConsumer<HasName, Name> setDisplayNameConsumer,
                                         final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                         final CellEditorControlsView.Presenter cellEditorControls,
                                         final NameAndDataTypePopoverView.Presenter editor,
                                         final Optional<String> editorTitle) {
        this(hasName,
             () -> TypeRefUtils.getTypeRefOfExpression(expression.get(), hasExpression),
             clearDisplayNameConsumer,
             setDisplayNameConsumer,
             setTypeRefConsumer,
             cellEditorControls,
             editor,
             editorTitle);
    }

    public NameAndDataTypeHeaderMetaData(final Optional<HasName> hasName,
                                         final Supplier<HasTypeRef> hasTypeRef,
                                         final Consumer<HasName> clearDisplayNameConsumer,
                                         final BiConsumer<HasName, Name> setDisplayNameConsumer,
                                         final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                         final CellEditorControlsView.Presenter cellEditorControls,
                                         final NameAndDataTypePopoverView.Presenter editor,
                                         final Optional<String> editorTitle) {
        super(cellEditorControls,
              editor,
              editorTitle);
        this.hasName = hasName;
        this.hasTypeRef = hasTypeRef;
        this.clearDisplayNameConsumer = clearDisplayNameConsumer;
        this.setDisplayNameConsumer = setDisplayNameConsumer;
        this.setTypeRefConsumer = setTypeRefConsumer;
    }

    @Override
    protected HasNameAndTypeRef getPresenter() {
        return this;
    }

    @Override
    public String getTitle() {
        return getName().getValue();
    }

    @Override
    public Name getName() {
        return hasName.orElse(HasName.NOP).getName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setName(final Name name) {
        hasName.ifPresent(hn -> {
            if (Objects.equals(name, getName())) {
                return;
            }

            if (name == null || name.getValue() == null || name.getValue().trim().isEmpty()) {
                clearDisplayNameConsumer.accept(hn);
            } else {
                setDisplayNameConsumer.accept(hn, name);
            }
        });
    }

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
        return RendererUtils.getNameAndDataTypeHeaderText(this,
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
