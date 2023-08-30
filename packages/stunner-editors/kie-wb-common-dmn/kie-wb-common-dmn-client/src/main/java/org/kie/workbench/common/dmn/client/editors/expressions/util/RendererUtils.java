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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextLineBreakWrap;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.ValueAndDataTypeHeaderMetaData;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

public class RendererUtils {

    public static final double EXPRESSION_TEXT_PADDING = 5.0;

    public static final String FONT_STYLE_TYPE_REF = "italic";

    public static final double SPACING = 8.0;

    public static Group getExpressionCellText(final GridBodyCellRenderContext context,
                                              final GridCell<String> gridCell) {
        final GridRenderer gridRenderer = context.getRenderer();
        final GridRendererTheme theme = gridRenderer.getTheme();
        return getExpressionText(theme,
                                 gridCell.getValue().getValue());
    }

    public static Group getCenteredCellText(final GridBodyCellRenderContext context,
                                            final GridCell<String> gridCell) {
        final GridRenderer gridRenderer = context.getRenderer();
        final GridRendererTheme theme = gridRenderer.getTheme();

        final Group g = GWT.create(Group.class);

        String value = gridCell.getValue().getValue();
        final Text t;
        if (!StringUtils.isEmpty(value)) {
            t = theme.getBodyText();
        } else {
            value = gridCell.getValue().getPlaceHolder();
            t = theme.getPlaceholderText();
        }

        t.setText(value);
        t.setListening(false);
        t.setX(context.getCellWidth() / 2);
        t.setY(context.getCellHeight() / 2);
        g.add(t);
        return g;
    }

    public static Group getNameAndDataTypeCellText(final InformationItemCell.HasNameAndDataTypeCell hasNameAndDataTypeCell,
                                                   final GridBodyCellRenderContext context) {
        if (!hasNameAndDataTypeCell.hasData()) {
            final BaseGridCellValue<String> cell = new BaseGridCellValue<>(null, hasNameAndDataTypeCell.getPlaceHolderText());
            return getCenteredCellText(context, new BaseGridCell<>(cell));
        }

        return getNameAndDataTypeText(context.getRenderer().getTheme(),
                                      hasNameAndDataTypeCell.getName().getValue(),
                                      hasNameAndDataTypeCell.getTypeRef(),
                                      context.getCellWidth(),
                                      context.getCellHeight());
    }

    public static Group getExpressionHeaderText(final EditableHeaderMetaData headerMetaData,
                                                final GridHeaderColumnRenderContext context) {
        final GridRenderer gridRenderer = context.getRenderer();
        final GridRendererTheme theme = gridRenderer.getTheme();
        return getExpressionText(theme,
                                 headerMetaData.getTitle());
    }

    public static Group getValueAndDataTypeHeaderText(final ValueAndDataTypeHeaderMetaData headerMetaData,
                                                      final GridHeaderColumnRenderContext context,
                                                      final double blockWidth,
                                                      final double blockHeight) {
        return getNameAndDataTypeText(context.getRenderer().getTheme(),
                                      headerMetaData.getTitle(),
                                      headerMetaData.getTypeRef(),
                                      blockWidth,
                                      blockHeight);
    }

    public static Group getEditableHeaderText(final EditableHeaderMetaData headerMetaData,
                                              final GridHeaderColumnRenderContext context,
                                              final double blockWidth,
                                              final double blockHeight) {
        final Group headerGroup = GWT.create(Group.class);
        final GridRenderer renderer = context.getRenderer();
        final GridRendererTheme theme = renderer.getTheme();
        final Text text = theme.getHeaderText();
        final String value = headerMetaData.getTitle();

        text.setX(blockWidth / 2);
        text.setY(blockHeight / 2);
        text.setText(value);
        text.setListening(false);

        headerGroup.add(text);
        return headerGroup;
    }

    public static Group getEditableHeaderPlaceHolderText(final EditableHeaderMetaData headerMetaData,
                                                         final GridHeaderColumnRenderContext context,
                                                         final double blockWidth,
                                                         final double blockHeight) {
        final Group headerGroup = GWT.create(Group.class);

        headerMetaData.getPlaceHolder().ifPresent(placeHolder -> {
            final GridRenderer renderer = context.getRenderer();
            final GridRendererTheme theme = renderer.getTheme();
            final Text text = theme.getPlaceholderText();

            text.setX(blockWidth / 2);
            text.setY(blockHeight / 2);
            text.setText(placeHolder);
            text.setListening(false);

            headerGroup.add(text);
        });

        return headerGroup;
    }

    private static Group getExpressionText(final GridRendererTheme theme,
                                           final String text) {
        final Group g = GWT.create(Group.class);

        final Text t = makeExpressionText(theme);
        t.setText(text);
        t.setListening(false);
        t.setX(EXPRESSION_TEXT_PADDING);
        t.setY(EXPRESSION_TEXT_PADDING);
        t.setTextAlign(TextAlign.LEFT);
        t.setWrapper(new TextLineBreakWrap(t));
        g.add(t);

        return g;
    }

    private static Text makeExpressionText(final GridRendererTheme theme) {
        final Text t = theme.getBodyText();
        t.setFontFamily(BaseExpressionGridTheme.FONT_FAMILY_EXPRESSION);
        return t;
    }

    public static double getExpressionTextLineHeight(final GridRendererTheme theme) {
        //The parameter to Text.getLineHeight(..) is not used so passing null is safe (for now).
        return makeExpressionText(theme).getLineHeight(null);
    }

    private static Group getNameAndDataTypeText(final GridRendererTheme theme,
                                                final String name,
                                                final QName typeRef,
                                                final double blockWidth,
                                                final double blockHeight) {
        final Group headerGroup = GWT.create(Group.class);

        final Text tName = theme.getHeaderText();
        tName.setText(name);
        tName.setListening(false);
        tName.setX(blockWidth / 2);
        tName.setY(blockHeight / 2 - SPACING);

        final Text tTypeRef = theme.getHeaderText();
        tTypeRef.setFontStyle(FONT_STYLE_TYPE_REF);
        tTypeRef.setFontSize(BaseExpressionGridTheme.FONT_SIZE - 2.0);
        tTypeRef.setText("(" + typeRef.toString() + ")");
        tTypeRef.setListening(false);
        tTypeRef.setX(blockWidth / 2);
        tTypeRef.setY(blockHeight / 2 + SPACING);

        headerGroup.add(tName);
        headerGroup.add(tTypeRef);

        return headerGroup;
    }
}
