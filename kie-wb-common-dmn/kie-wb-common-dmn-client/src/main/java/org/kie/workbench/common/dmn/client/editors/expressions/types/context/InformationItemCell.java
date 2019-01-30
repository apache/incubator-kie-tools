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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

import static org.kie.workbench.common.dmn.api.definition.v1_1.common.HasTypeRefHelper.getNotNullHasTypeRefs;

public class InformationItemCell extends ContextGridCell<InformationItemCell.HasNameCell> {

    static final String SET_NAME_UNSUPPORTED_MESSAGE = "HasNameCell.setName(Name) on a wrapped String is not supported.";

    public interface HasNameCell extends HasName {

        Group render(final GridBodyCellRenderContext context);

        static HasNameCell wrap(final String name) {

            return new HasNameCell() {
                @Override
                public Name getName() {
                    return new Name(name);
                }

                @Override
                public void setName(final Name name) {
                    throw new UnsupportedOperationException(SET_NAME_UNSUPPORTED_MESSAGE);
                }

                @Override
                public Group render(final GridBodyCellRenderContext context) {
                    final BaseGridCellValue<String> hasNameCellValue = new BaseGridCellValue<>(this.getName().getValue());
                    return RendererUtils.getCenteredCellText(context, new BaseGridCell<>(hasNameCellValue));
                }
            };
        }
    }

    public interface HasNameAndDataTypeCell extends HasNameCell,
                                                    HasTypeRef {

        boolean hasData();

        String getPlaceHolderText();

        static HasNameAndDataTypeCell wrap(final InformationItem informationItem) {
            return wrap(informationItem, null);
        }

        static HasNameAndDataTypeCell wrap(final InformationItem informationItem, final String placeholder) {

            return new HasNameAndDataTypeCell() {

                @Override
                public boolean hasData() {
                    return !Objects.isNull(informationItem);
                }

                @Override
                public String getPlaceHolderText() {
                    return placeholder;
                }

                @Override
                public QName getTypeRef() {
                    return informationItem.getTypeRef();
                }

                @Override
                public void setTypeRef(final QName typeRef) {
                    informationItem.setTypeRef(typeRef);
                }

                @Override
                public Name getName() {
                    return informationItem.getName();
                }

                @Override
                public void setName(final Name name) {
                    informationItem.setName(name);
                }

                @Override
                public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
                    return informationItem;
                }

                @Override
                public List<HasTypeRef> getHasTypeRefs() {
                    return getNotNullHasTypeRefs(informationItem);
                }

                @Override
                public Group render(final GridBodyCellRenderContext context) {
                    return RendererUtils.getNameAndDataTypeCellText(this, context);
                }
            };
        }
    }

    private final Supplier<HasNameCell> supplier;

    public InformationItemCell(final Supplier<HasNameCell> supplier,
                               final ListSelectorView.Presenter listSelector) {
        super(new BaseGridCellValue<>(supplier.get()),
              listSelector);
        this.supplier = supplier;
    }

    @Override
    public GridCellValue<HasNameCell> getValue() {
        return new BaseGridCellValue<>(supplier.get());
    }

    @Override
    public GridCellEditAction getSupportedEditAction() {
        return GridCellEditAction.SINGLE_CLICK;
    }
}
