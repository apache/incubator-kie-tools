/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.themes;

import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RuleNameColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours;

public class GuidedDecisionTableTheme implements GridRendererTheme {

    private static final double GRID_LINE_WIDTH = 1.0;

    public static final double GRID_SELECTOR_LINE_WIDTH = 2.0;

    private static final String GRID_FONT_FAMILY = "Open Sans, Helvetica, Arial, sans-serif";

    private static final int GRID_FONT_SIZE = 10;

    private final GuidedDecisionTableUiModel uiModel;
    private final GuidedDecisionTable52 model;

    public GuidedDecisionTableTheme(final GuidedDecisionTableUiModel uiModel,
                                    final GuidedDecisionTable52 model) {
        this.uiModel = PortablePreconditions.checkNotNull("uiModel",
                                                          uiModel);
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
    }

    @Override
    public String getName() {
        return "Guided Decision Table Editor";
    }

    @Override
    public MultiPath getSelector() {
        final MultiPath selector = new MultiPath()
                .setStrokeColor(KIEColours.CELL_FOCUS)
                .setStrokeWidth(GRID_LINE_WIDTH);
        return selector;
    }

    @Override
    public Rectangle getCellSelectorBorder() {
        final Rectangle selector = new Rectangle(0, 0)
                .setStrokeColor(KIEColours.CELL_FOCUS)
                .setStrokeWidth(GRID_SELECTOR_LINE_WIDTH);
        return selector;
    }

    @Override
    public Rectangle getCellSelectorBackground() {
        return new Rectangle(0, 0).setVisible(false);
    }

    @Override
    public Rectangle getHeaderBackground(final GridColumn<?> column) {
        return getBaseRectangle(column);
    }

    @Override
    public Rectangle getHeaderLinkBackground(final GridColumn<?> column) {
        return getBaseRectangle(column);
    }

    @Override
    public MultiPath getHeaderGridLine() {
        final MultiPath headerGrid = new MultiPath()
                .setStrokeColor(KIEColours.TABLE_GRID)
                .setStrokeWidth(GRID_LINE_WIDTH)
                .setListening(false);
        return headerGrid;
    }

    @Override
    public Text getHeaderText() {
        final Text t = new Text("")
                .setFillColor(KIEColours.TABLE_TEXT)
                .setFontSize(GRID_FONT_SIZE)
                .setTextUnit(TextUnit.PT)
                .setFontFamily(GRID_FONT_FAMILY)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER);
        return t;
    }

    @Override
    public Rectangle getBodyBackground(final GridColumn<?> column) {
        final Rectangle r = new Rectangle(0, 0);
        r.setFillColor(KIEColours.CELL_CONTENT);
        return r;
    }

    @Override
    public MultiPath getBodyGridLine() {
        final MultiPath bodyGrid = new MultiPath()
                .setStrokeColor(KIEColours.TABLE_GRID)
                .setStrokeWidth(GRID_LINE_WIDTH)
                .setListening(false);
        return bodyGrid;
    }

    @Override
    public Text getBodyText() {
        final Text t = new Text("")
                .setFillColor(KIEColours.TABLE_TEXT)
                .setFontSize(GRID_FONT_SIZE)
                .setTextUnit(TextUnit.PT)
                .setFontFamily(GRID_FONT_FAMILY)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER);
        return t;
    }

    @Override
    public Rectangle getGridBoundary() {
        final Rectangle boundary = new Rectangle(0,
                                                 0)
                .setStrokeColor(KIEColours.TABLE_GRID)
                .setStrokeWidth(GRID_LINE_WIDTH)
                .setListening(false);
        return boundary;
    }

    @Override
    public Line getGridHeaderBodyDivider() {
        final Line divider = new Line()
                .setStrokeColor(KIEColours.TABLE_GRID)
                .setStrokeWidth(GRID_LINE_WIDTH);
        return divider;
    }

    private Rectangle getBaseRectangle(final GridColumn<?> uiColumn) {
        final ModelColumnType columnType = getModelColumnType(uiColumn);
        return getBaseRectangle(columnType);
    }

    public Rectangle getBaseRectangle(final ModelColumnType columnType) {
        final Rectangle r = new Rectangle(0,
                                          0);
        switch (columnType) {
            case ROW_NUMBER:
                r.setFillColor(KIEColours.HEADER_BACKGROUND_WHITE);
                break;
            case DESCRIPTION:
                r.setFillColor(KIEColours.HEADER_BACKGROUND_WHITE);
                break;
            case METADATA:
                r.setFillColor(KIEColours.HEADER_BACKGROUND_LIGHT_GREY);
                break;
            case ATTRIBUTE:
                r.setFillColor(KIEColours.HEADER_BACKGROUND_LIGHT_GREY);
                break;
            case CONDITION:
                r.setFillColor(KIEColours.HEADER_BACKGROUND_LIGHT_BLUE);
                break;
            case ACTION:
                r.setFillColor(KIEColours.HEADER_BACKGROUND_DARK_BLUE);
                break;
            case CAPTION:
                r.setFillColor(KIEColours.HEADER_BACKGROUND_WHITE);
                break;
            case UNKNOWN:
                r.setFillColor(KIEColours.HEADER_BACKGROUND_WHITE);
                break;
        }
        return r;
    }

    public enum ModelColumnType {
        ROW_NUMBER,
        DESCRIPTION,
        METADATA,
        ATTRIBUTE,
        CONDITION,
        ACTION,
        CAPTION,
        UNKNOWN
    }

    ModelColumnType getModelColumnType(final GridColumn<?> uiColumn) {
        final int uiColumnIndex = uiModel.getColumns().indexOf(uiColumn);
        final BaseColumn modelColumn = model.getExpandedColumns().get(uiColumnIndex);
        if (modelColumn instanceof RowNumberCol52) {
            return ModelColumnType.ROW_NUMBER;
        } else if (modelColumn instanceof DescriptionCol52 || modelColumn instanceof RuleNameColumn) {
            return ModelColumnType.DESCRIPTION;
        } else if (modelColumn instanceof MetadataCol52) {
            return ModelColumnType.METADATA;
        } else if (modelColumn instanceof AttributeCol52) {
            return ModelColumnType.ATTRIBUTE;
        } else if (modelColumn instanceof ConditionCol52) {
            return ModelColumnType.CONDITION;
        } else if (modelColumn instanceof ActionCol52) {
            return ModelColumnType.ACTION;
        }

        // UNKNOWN should *never* happen as there are no sub-classes in the current column definition
        // class hierarchy that does not extend one of the above; however I thought it better to future
        // proof rendering than throw an exception.
        return ModelColumnType.UNKNOWN;
    }

}
