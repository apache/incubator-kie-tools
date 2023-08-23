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

package org.kie.workbench.common.dmn.client.shape.def;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.client.resources.DMNSVGGlyphFactory;
import org.kie.workbench.common.dmn.client.resources.DMNSVGViewFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette.PaletteGlyphConsumer;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNSVGShapeDefImplTest {

    private static final String DEFINITION_ID = "definition-id";

    @Mock
    private DMNSVGViewFactory viewFactory;

    @Mock
    private SVGShapeViewResource viewResource;

    private DMNSVGShapeDefImpl shapeDef;

    @Before
    public void setup() {
        this.shapeDef = new DMNSVGShapeDefImpl();

        when(viewFactory.businessKnowledgeModel()).thenReturn(viewResource);
        when(viewFactory.decision()).thenReturn(viewResource);
        when(viewFactory.diagram()).thenReturn(viewResource);
        when(viewFactory.inputData()).thenReturn(viewResource);
        when(viewFactory.knowledgeSource()).thenReturn(viewResource);
        when(viewFactory.textAnnotation()).thenReturn(viewResource);
    }

    @Test
    public void testNewViewInstance() {
        final BusinessKnowledgeModel businessKnowledgeModel = new BusinessKnowledgeModel();
        shapeDef.newViewInstance(viewFactory, businessKnowledgeModel);
        verify(viewFactory).businessKnowledgeModel();
        verify(viewResource).build(businessKnowledgeModel.getDimensionsSet().getWidth().getValue(),
                                   businessKnowledgeModel.getDimensionsSet().getHeight().getValue(),
                                   true);

        reset(viewResource);
        final Decision decision = new Decision();
        shapeDef.newViewInstance(viewFactory, decision);
        verify(viewFactory).decision();
        verify(viewResource).build(decision.getDimensionsSet().getWidth().getValue(),
                                   decision.getDimensionsSet().getHeight().getValue(),
                                   true);

        reset(viewResource);
        shapeDef.newViewInstance(viewFactory, new InputData());
        verify(viewFactory).inputData();
        verify(viewResource).build(businessKnowledgeModel.getDimensionsSet().getWidth().getValue(),
                                   businessKnowledgeModel.getDimensionsSet().getHeight().getValue(),
                                   true);

        reset(viewResource);
        final KnowledgeSource knowledgeSource = new KnowledgeSource();
        shapeDef.newViewInstance(viewFactory, knowledgeSource);
        verify(viewFactory).knowledgeSource();
        verify(viewResource).build(knowledgeSource.getDimensionsSet().getWidth().getValue(),
                                   knowledgeSource.getDimensionsSet().getHeight().getValue(),
                                   true);

        reset(viewResource);
        final TextAnnotation textAnnotation = new TextAnnotation();
        shapeDef.newViewInstance(viewFactory, textAnnotation);
        verify(viewFactory).textAnnotation();
        verify(viewResource).build(textAnnotation.getDimensionsSet().getWidth().getValue(),
                                   textAnnotation.getDimensionsSet().getHeight().getValue(),
                                   true);
    }

    @Test
    public void testGetToolboxGlyph() {
        assertEquals(DMNSVGGlyphFactory.BUSINESS_KNOWLEDGE_MODEL_TOOLBOX,
                     shapeDef.getGlyph(BusinessKnowledgeModel.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.DECISION_TOOLBOX,
                     shapeDef.getGlyph(Decision.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.INPUT_DATA_TOOLBOX,
                     shapeDef.getGlyph(InputData.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.KNOWLEDGE_SOURCE_TOOLBOX,
                     shapeDef.getGlyph(KnowledgeSource.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.TEXT_ANNOTATION_TOOLBOX,
                     shapeDef.getGlyph(TextAnnotation.class, DEFINITION_ID));

        assertTrue(shapeDef.getGlyph(DMNViewDefinition.class, DEFINITION_ID) instanceof ShapeGlyph);
    }

    @Test
    public void testGetPaletteGlyphWithConsumer() {
        assertEquals(DMNSVGGlyphFactory.BUSINESS_KNOWLEDGE_MODEL_PALETTE,
                     shapeDef.getGlyph(BusinessKnowledgeModel.class, PaletteGlyphConsumer.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.DECISION_PALETTE,
                     shapeDef.getGlyph(Decision.class, PaletteGlyphConsumer.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.DECISION_SERVICE_PALETTE,
                     shapeDef.getGlyph(DecisionService.class, PaletteGlyphConsumer.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.INPUT_DATA_PALETTE,
                     shapeDef.getGlyph(InputData.class, PaletteGlyphConsumer.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.KNOWLEDGE_SOURCE_PALETTE,
                     shapeDef.getGlyph(KnowledgeSource.class, PaletteGlyphConsumer.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.TEXT_ANNOTATION_PALETTE,
                     shapeDef.getGlyph(TextAnnotation.class, PaletteGlyphConsumer.class, DEFINITION_ID));

        assertEquals(true, shapeDef.getGlyph(DMNViewDefinition.class, PaletteGlyphConsumer.class, DEFINITION_ID) instanceof ShapeGlyph);
    }
}