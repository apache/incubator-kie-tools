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

package org.kie.workbench.common.dmn.client.shape.factory;

import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.client.shape.def.DMNConnectorShapeDefImpl;
import org.kie.workbench.common.dmn.client.shape.def.DMNSVGShapeDefImpl;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNShapeFactoryTest {

    private static final String DEFINITION_ID = "definition-id";

    @Mock
    private SVGShapeFactory svgShapeFactory;

    @Mock
    private DMNConnectorShapeFactory dmnConnectorShapeFactory;

    @Mock
    private DMNDecisionServiceShapeFactory dmnDecisionServiceShapeFactory;

    @Mock
    private DelegateShapeFactory<DMNDefinition, Shape> delegateShapeFactory;

    @Mock
    private DMNDefinition definition;

    @Captor
    private ArgumentCaptor<Supplier> shapeDefFactoryCaptor;

    private DMNShapeFactory factory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.factory = new DMNShapeFactory(svgShapeFactory,
                                           dmnConnectorShapeFactory,
                                           dmnDecisionServiceShapeFactory,
                                           delegateShapeFactory);

        when(delegateShapeFactory.delegate(Mockito.<Class>any(),
                                           Mockito.<ShapeDef>any(),
                                           Mockito.<Supplier>any())).thenReturn(delegateShapeFactory);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        factory.init();

        verify(delegateShapeFactory).delegate(eq(DMNDiagram.class),
                                              Mockito.<DMNSVGShapeDefImpl>any(),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(InputData.class),
                                              Mockito.<DMNSVGShapeDefImpl>any(),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(KnowledgeSource.class),
                                              Mockito.<DMNSVGShapeDefImpl>any(),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(BusinessKnowledgeModel.class),
                                              Mockito.<DMNSVGShapeDefImpl>any(),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(Decision.class),
                                              Mockito.<DMNSVGShapeDefImpl>any(),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(TextAnnotation.class),
                                              Mockito.<DMNSVGShapeDefImpl>any(),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(Association.class),
                                              Mockito.<DMNConnectorShapeDefImpl>any(),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(dmnConnectorShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(AuthorityRequirement.class),
                                              Mockito.<DMNConnectorShapeDefImpl>any(),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(dmnConnectorShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(InformationRequirement.class),
                                              Mockito.<DMNConnectorShapeDefImpl>any(),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(dmnConnectorShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(KnowledgeRequirement.class),
                                              Mockito.<DMNConnectorShapeDefImpl>any(),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(dmnConnectorShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(DecisionService.class),
                                              Mockito.<DMNSVGShapeDefImpl>any(),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(dmnDecisionServiceShapeFactory, shapeDefFactoryCaptor.getValue().get());
    }

    @Test
    public void testNewShape() {
        factory.newShape(definition);

        verify(delegateShapeFactory).newShape(definition);
    }

    @Test
    public void testGetGlyph() {
        factory.getGlyph(DEFINITION_ID);

        verify(delegateShapeFactory).getGlyph(DEFINITION_ID);
    }

    @Test
    public void testGetGlyphWithConsumer() {
        factory.getGlyph(DEFINITION_ID, ShapeFactory.GlyphConsumer.class);

        verify(delegateShapeFactory).getGlyph(DEFINITION_ID, ShapeFactory.GlyphConsumer.class);
    }
}
