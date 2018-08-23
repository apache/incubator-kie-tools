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

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.HasNameAndDataTypeControl;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypeEditorView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;

public abstract class NameAndDataTypeHeaderMetaData extends EditablePopupHeaderMetaData<HasNameAndDataTypeControl, NameAndDataTypeEditorView.Presenter> implements HasNameAndDataTypeControl {

    private final Optional<HasName> hasName;
    private final HasTypeRef hasTypeRef;
    private final Consumer<HasName> clearDisplayNameConsumer;
    private final BiConsumer<HasName, String> setDisplayNameConsumer;
    private final BiConsumer<HasTypeRef, QName> setTypeRefConsumer;

    public NameAndDataTypeHeaderMetaData(final Optional<HasName> hasName,
                                         final HasTypeRef hasTypeRef,
                                         final Consumer<HasName> clearDisplayNameConsumer,
                                         final BiConsumer<HasName, String> setDisplayNameConsumer,
                                         final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                         final CellEditorControlsView.Presenter cellEditorControls,
                                         final NameAndDataTypeEditorView.Presenter headerEditor) {
        super(cellEditorControls,
              headerEditor);
        this.hasName = hasName;
        this.hasTypeRef = hasTypeRef;
        this.clearDisplayNameConsumer = clearDisplayNameConsumer;
        this.setDisplayNameConsumer = setDisplayNameConsumer;
        this.setTypeRefConsumer = setTypeRefConsumer;
    }

    @Override
    protected HasNameAndDataTypeControl getPresenter() {
        return this;
    }

    @Override
    public String getTitle() {
        return getDisplayName();
    }

    @Override
    public String getDisplayName() {
        return hasName.orElse(HasName.NOP).getName().getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDisplayName(final String name) {
        hasName.ifPresent(hn -> {
            if (Objects.equals(name, getDisplayName())) {
                return;
            }

            if (name == null || name.trim().isEmpty()) {
                clearDisplayNameConsumer.accept(hn);
            } else {
                setDisplayNameConsumer.accept(hn, name);
            }
        });
    }

    @Override
    public QName getTypeRef() {
        return hasTypeRef.getTypeRef();
    }

    @Override
    public void setTypeRef(final QName typeRef) {
        if (Objects.equals(typeRef, getTypeRef())) {
            return;
        }

        setTypeRefConsumer.accept(hasTypeRef, typeRef);
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return hasTypeRef.asDMNModelInstrumentedBase();
    }
}
