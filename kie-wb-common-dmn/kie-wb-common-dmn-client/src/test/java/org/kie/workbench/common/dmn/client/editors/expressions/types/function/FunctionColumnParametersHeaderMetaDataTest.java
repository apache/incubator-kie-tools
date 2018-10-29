/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersPopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class FunctionColumnParametersHeaderMetaDataTest {

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ParametersPopoverView.Presenter parametersEditor;

    @Mock
    private FunctionGrid gridWidget;

    @Mock
    private Transform transform;

    @Mock
    private GridRenderer renderer;

    @Mock
    private TranslationService translationService;

    private FunctionDefinition function;

    private Supplier<FunctionDefinition> functionSupplier;

    private FunctionColumnParametersHeaderMetaData header;

    @Before
    public void setup() {
        this.function = new FunctionDefinition();
        this.functionSupplier = () -> function;
        this.header = new FunctionColumnParametersHeaderMetaData(functionSupplier,
                                                                 translationService,
                                                                 cellEditorControls,
                                                                 parametersEditor,
                                                                 Optional.of(DMNEditorConstants.FunctionEditor_EditParameters),
                                                                 gridWidget);

        when(translationService.getTranslation(anyString())).thenAnswer((i) -> i.getArguments()[0]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetColumnGroup() {
        header.setColumnGroup("group");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetTitle() {
        header.setTitle("title");
    }

    @Test
    public void testGetExpressionLanguageTitle() {
        KindUtilities.setKind(this.function,
                              FunctionDefinition.Kind.FEEL);

        assertEquals(FunctionDefinition.Kind.FEEL.code(),
                     header.getExpressionLanguageTitle());
    }

    @Test
    public void testGetFormalParametersTitleOneParameter() {
        setupFormalParameters("p0");

        assertEquals("(p0)",
                     header.getFormalParametersTitle());
    }

    @Test
    public void testGetFormalParametersTitleMultipleParameters() {
        setupFormalParameters("p0", "p1");

        assertEquals("(p0, p1)",
                     header.getFormalParametersTitle());
    }

    @Test
    public void testGetColumnGroup() {
        assertEquals(FunctionColumnParametersHeaderMetaData.PARAMETER_COLUMN_GROUP,
                     header.getColumnGroup());
    }

    @Test
    public void testGetTitle() {
        KindUtilities.setKind(this.function,
                              FunctionDefinition.Kind.FEEL);

        setupFormalParameters("p0", "p1");

        assertEquals("F : (p0, p1)",
                     header.getTitle());
    }

    private void setupFormalParameters(final String... parameters) {
        Arrays.asList(parameters).forEach(p -> this.function.getFormalParameter().add(new InformationItem() {{
            setName(new Name(p));
        }}));
    }
}
