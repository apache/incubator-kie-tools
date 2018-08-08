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

package org.kie.workbench.common.dmn.client.editors.types.treegrid;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.CSSProperties.MarginLeftUnionType;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTableRowElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.DataType;

import static org.kie.workbench.common.dmn.client.editors.types.treegrid.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.treegrid.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.treegrid.common.TreeGridItemCssHelper.asDownArrow;
import static org.kie.workbench.common.dmn.client.editors.types.treegrid.common.TreeGridItemCssHelper.asRightArrow;
import static org.kie.workbench.common.dmn.client.editors.types.treegrid.common.TreeGridItemCssHelper.isRightArrow;

@Dependent
@Templated
public class DataTypeTreeGridItemView implements DataTypeTreeGridItem.View {

    @DataField("row")
    private final HTMLTableRowElement row;

    @DataField("level")
    private final HTMLElement level;

    @DataField("arrow-button")
    private final HTMLElement arrow;

    @DataField("name")
    private final HTMLElement name;

    @DataField("type")
    private final HTMLElement type;

    private DataTypeTreeGridItem presenter;

    @Inject
    public DataTypeTreeGridItemView(final HTMLTableRowElement row,
                                    final @Named("span") HTMLElement level,
                                    final @Named("span") HTMLElement arrow,
                                    final @Named("span") HTMLElement name,
                                    final @Named("td") HTMLElement type) {
        this.row = row;
        this.level = level;
        this.arrow = arrow;
        this.name = name;
        this.type = type;
    }

    @Override
    public void init(final DataTypeTreeGridItem presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return row;
    }

    @Override
    public void setDataType(final DataType dataType) {
        setupRowMetadata(dataType);
        setupArrow(dataType);
        setupIndentationLevel();
        setupDataTypeValues(dataType);
    }

    void setupRowMetadata(final DataType dataType) {

        row.classList.add(dataType.isBasic() ? "basic-row" : "structure-row");
        row.classList.add(dataType.isDefault() ? "default-row" : "custom-row");
        row.classList.add(dataType.isExternal() ? "external-row" : "nested-row");

        row.setAttribute("data-row-uuid", dataType.getUUID());
    }

    void setupArrow(final DataType dataType) {
        if (dataType.hasSubDataTypes()) {
            show(arrow);
        } else {
            hide(arrow);
        }
    }

    @Override
    public void expand() {
        asDownArrow(getArrow());
        presenter.expandSubDataTypes();
    }

    @Override
    public void collapse() {
        asRightArrow(getArrow());
        presenter.collapseSubDataTypes();
    }

    void setupIndentationLevel() {

        final int indentationLevel = presenter.getLevel();
        final int pixelsPerLevel = 20;
        final int marginPixels = pixelsPerLevel * indentationLevel;

        this.level.style.marginLeft = margin(marginPixels);
    }

    void setupDataTypeValues(final DataType dataType) {
        name.textContent = dataType.getName();
    }

    @EventHandler("arrow-button")
    public void onArrowClickEvent(final ClickEvent event) {
        presenter.expandOrCollapseSubTypes();
    }

    HTMLElement getArrow() {
        return arrow;
    }

    @Override
    public void setupSelectComponent(final DataTypeSelect typeSelect) {
        type.innerHTML = "";
        type.appendChild(typeSelect.getElement());
    }

    @Override
    public boolean isCollapsed() {
        return isCollapsed(getArrow());
    }

    @Override
    public void expandSubType(final DataType subDataType) {
        final Element subDataTypeRow = getRowElement(subDataType);
        final Element arrow = getArrowElement(subDataTypeRow);

        show(subDataTypeRow);

        if (!isCollapsed(arrow)) {
            presenter.expandSubDataTypes(subDataType);
        }
    }

    @Override
    public void collapseSubType(final DataType subDataType) {
        final Element subDataTypeRow = getRowElement(subDataType);

        hide(subDataTypeRow);

        presenter.collapseSubDataTypes(subDataType);
    }

    Element getRowElement(final DataType dataType) {
        return getElement().parentNode.querySelector("[data-row-uuid='" + dataType.getUUID() + "']");
    }

    Element getArrowElement(final Element subDataTypeRow) {
        return subDataTypeRow.querySelector("[data-field='arrow-button']");
    }

    MarginLeftUnionType margin(final int pixels) {
        return MarginLeftUnionType.of(pixels + "px");
    }

    boolean isCollapsed(final Element arrow) {
        return isRightArrow(arrow);
    }
}
