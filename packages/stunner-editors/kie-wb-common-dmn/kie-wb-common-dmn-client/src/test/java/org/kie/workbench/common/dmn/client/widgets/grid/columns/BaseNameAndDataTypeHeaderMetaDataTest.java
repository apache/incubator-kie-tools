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

import java.util.Optional;

import com.ait.lienzo.client.core.shape.Text;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseNameAndDataTypeHeaderMetaDataTest extends BaseValueAndDataTypeHeaderMetaDataTest<Name, HasName> {

    protected static final String NAME_DATA_TYPE_COLUMN_GROUP = "NameAndDataTypeHeaderMetaDataTest$NameAndDataTypeColumn";

    protected static final Name NAME = new Name("name");

    protected static final double SPACING = 8.0;

    protected static final String FONT_STYLE_TYPE_REF = "italic";

    protected Decision hasExpression = new Decision();

    @Override
    protected Name makeValue() {
        return NAME;
    }

    @Override
    protected Name makeEmptyValue() {
        return new Name();
    }

    @Override
    protected HasName makeHasValue() {
        return hasExpression;
    }

    @Test
    public void testGetTitleWithHasName() {
        final Decision decision = new Decision();
        decision.setName(NAME);
        setup(Optional.of(decision));

        assertThat(metaData.getTitle()).isEqualTo(NAME.getValue());
    }

    @Test
    public void testGetDisplayNameWithHasName() {
        final Decision decision = new Decision();
        decision.setName(NAME);
        setup(Optional.of(decision));

        assertThat(metaData.getValue()).isEqualTo(NAME);
    }

    @Test
    public void testSetDisplayNameWithHasName() {
        final Decision decision = new Decision();
        setup(Optional.of(decision));

        metaData.setValue(NAME);

        verify(setValueConsumer).accept(eq(decision), eq(NAME));
    }

    @Test
    public void testSetValueWithHasNameWithEmptyValue() {
        final Decision decision = new Decision();
        decision.setName(NAME);
        setup(Optional.of(decision));

        metaData.setValue(new Name());

        verify(clearValueConsumer).accept(eq(decision));
    }

    @Test
    public void testSetValueWithHasNameWithoutChange() {
        final Decision decision = new Decision();
        decision.setName(NAME);
        setup(Optional.of(decision));

        metaData.setValue(NAME);

        verify(clearValueConsumer, never()).accept(any(HasName.class));
        verify(setValueConsumer, never()).accept(any(HasName.class), any(Name.class));
    }

    @Test
    public void testSetValueWithoutHasName() {
        setup(Optional.empty());

        metaData.setValue(NAME);

        verify(setValueConsumer, never()).accept(any(HasName.class), any(Name.class));
    }

    @Test
    public void testToModelValue() {
        setup(Optional.empty());

        assertThat(metaData.toModelValue(NAME.getValue())).isEqualTo(NAME);
    }

    @Test
    public void testToWidgetValue() {
        setup(Optional.empty());

        assertThat(metaData.toWidgetValue(NAME)).isEqualTo(NAME.getValue());
    }

    @Test
    public void testGetValueLabel() {
        setup(Optional.empty());

        assertThat(metaData.getValueLabel()).isEqualTo(DMNEditorConstants.NameAndDataTypePopover_NameLabel);
    }

    @Test
    public void testNormaliseValue() {
        setup(Optional.empty());

        final String value = "   " + NAME.getValue() + "   ";
        assertThat(metaData.normaliseValue(value)).isEqualTo(NAME.getValue());
    }

    @Test
    public void testRender() {
        final QName typeRef = BuiltInType.DATE.asQName();
        final GridRenderer renderer = mock(GridRenderer.class);
        final GridRendererTheme theme = mock(GridRendererTheme.class);
        final Text tName = mock(Text.class);
        final InformationItemPrimary hasExpressionVariable = new InformationItemPrimary();
        hasExpressionVariable.setTypeRef(typeRef);
        hasExpression.setVariable(hasExpressionVariable);
        final Decision decision = new Decision();
        decision.setName(NAME);
        setup(Optional.of(decision));

        when(theme.getHeaderText()).thenReturn(tName);
        when(context.getRenderer()).thenReturn(renderer);
        when(renderer.getTheme()).thenReturn(theme);
        when(hasTypeRef.getTypeRef()).thenReturn(typeRef);

        metaData.render(context, BLOCK_WIDTH, BLOCK_HEIGHT);

        verify(tName).setText(NAME.getValue());
        verify(tName, times(2)).setListening(false);
        verify(tName, times(2)).setX(BLOCK_WIDTH / 2);
        verify(tName).setY(BLOCK_HEIGHT / 2 - SPACING);
        verify(tName).setFontStyle(FONT_STYLE_TYPE_REF);
        verify(tName).setFontSize(BaseExpressionGridTheme.FONT_SIZE - 2.0);
        verify(tName).setText("(" + typeRef.toString() + ")");
        verify(tName).setY(BLOCK_HEIGHT / 2 + SPACING);
    }
}