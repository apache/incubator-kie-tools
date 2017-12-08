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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FunctionColumnParametersHeaderMetaDataTest {

    private FunctionDefinition.Kind kind;

    private List<InformationItem> parameters = new ArrayList<>();

    private Supplier<FunctionDefinition.Kind> expressionLanguageSupplier;

    private Supplier<List<InformationItem>> formalParametersSupplier;

    private FunctionColumnParametersHeaderMetaData header;

    @Before
    public void setup() {
        this.expressionLanguageSupplier = () -> kind;
        this.formalParametersSupplier = () -> parameters;
        this.header = new FunctionColumnParametersHeaderMetaData(expressionLanguageSupplier,
                                                                 formalParametersSupplier);
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
        this.kind = FunctionDefinition.Kind.FEEL;

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
        this.kind = FunctionDefinition.Kind.FEEL;
        setupFormalParameters("p0", "p1");

        assertEquals("F : (p0, p1)",
                     header.getTitle());
    }

    private void setupFormalParameters(final String... parameters) {
        Arrays.asList(parameters).forEach(p -> this.parameters.add(new InformationItem() {{
            setName(new Name(p));
        }}));
    }
}
