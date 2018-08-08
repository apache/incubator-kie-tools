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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTableSectionElement;
import elemental2.dom.NodeList;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.kie.workbench.common.dmn.client.editors.types.treegrid.common.JQueryTooltip.$;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeTreeGridView_AttributeTooltip;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeTreeGridView_TypeTooltip;

@Templated
@ApplicationScoped
public class DataTypeTreeGridView implements DataTypeTreeGrid.View {

    @DataField("grid-items")
    private final HTMLTableSectionElement gridItems;

    @DataField("attribute-tooltip")
    private final HTMLElement attributeTooltip;

    @DataField("type-tooltip")
    private final HTMLElement typeTooltip;

    private final TranslationService translationService;

    private DataTypeTreeGrid presenter;

    @Inject
    public DataTypeTreeGridView(final @Named("tbody") HTMLTableSectionElement gridItems,
                                final @Named("span") HTMLElement attributeTooltip,
                                final @Named("span") HTMLElement typeTooltip,
                                final TranslationService translationService) {
        this.gridItems = gridItems;
        this.attributeTooltip = attributeTooltip;
        this.typeTooltip = typeTooltip;
        this.translationService = translationService;
    }

    @PostConstruct
    public void setupTooltips() {

        final NodeList<Element> element = getElement().querySelectorAll("[data-toggle='tooltip']");

        attributeTooltip.setAttribute("title", translationService.format(DataTypeTreeGridView_AttributeTooltip));
        typeTooltip.setAttribute("title", translationService.format(DataTypeTreeGridView_TypeTooltip));

        $(element).tooltip();
    }

    @Override
    public void init(final DataTypeTreeGrid presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setupGridItems(final List<DataTypeTreeGridItem> treeGridItems) {
        gridItems.innerHTML = "";
        treeGridItems.forEach(this::appendItem);
    }

    private void appendItem(final DataTypeTreeGridItem gridItem) {
        gridItems.appendChild(gridItem.getElement());
    }
}
