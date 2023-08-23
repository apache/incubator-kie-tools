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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersPopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
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
    private TranslationService translationService;

    private FunctionDefinition function;

    private Supplier<Optional<FunctionDefinition>> functionSupplier;

    private FunctionColumnParametersHeaderMetaData header;

    @Before
    public void setup() {
        this.function = new FunctionDefinition();
        this.functionSupplier = () -> Optional.ofNullable(function);
        this.header = new FunctionColumnParametersHeaderMetaData(functionSupplier,
                                                                 translationService,
                                                                 cellEditorControls,
                                                                 parametersEditor,
                                                                 gridWidget);

        when(translationService.getTranslation(Mockito.<String>any())).thenAnswer((i) -> i.getArguments()[0]);
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

        assertEquals("(p0, p1)",
                     header.getTitle());
    }

    private void setupFormalParameters(final String... parameters) {
        Arrays.asList(parameters).forEach(p -> this.function.getFormalParameter().add(new InformationItem() {{
            setName(new Name(p));
        }}));
    }
}
