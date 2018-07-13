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

package org.kie.workbench.common.dmn.client.shape.factory;

import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
                                           delegateShapeFactory);

        when(delegateShapeFactory.delegate(any(Class.class),
                                           any(ShapeDef.class),
                                           any(Supplier.class))).thenReturn(delegateShapeFactory);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        factory.init();

        verify(delegateShapeFactory).delegate(eq(DMNDiagram.class),
                                              any(DMNSVGShapeDefImpl.class),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(InputData.class),
                                              any(DMNSVGShapeDefImpl.class),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(KnowledgeSource.class),
                                              any(DMNSVGShapeDefImpl.class),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(BusinessKnowledgeModel.class),
                                              any(DMNSVGShapeDefImpl.class),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(Decision.class),
                                              any(DMNSVGShapeDefImpl.class),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(TextAnnotation.class),
                                              any(DMNSVGShapeDefImpl.class),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(svgShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(Association.class),
                                              any(DMNConnectorShapeDefImpl.class),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(dmnConnectorShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(AuthorityRequirement.class),
                                              any(DMNConnectorShapeDefImpl.class),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(dmnConnectorShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(InformationRequirement.class),
                                              any(DMNConnectorShapeDefImpl.class),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(dmnConnectorShapeFactory, shapeDefFactoryCaptor.getValue().get());

        verify(delegateShapeFactory).delegate(eq(KnowledgeRequirement.class),
                                              any(DMNConnectorShapeDefImpl.class),
                                              shapeDefFactoryCaptor.capture());
        assertEquals(dmnConnectorShapeFactory, shapeDefFactoryCaptor.getValue().get());
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
