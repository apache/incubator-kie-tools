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
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.client.resources.DMNSVGGlyphFactory;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class DMNConnectorShapeDefImplTest {

    private static final String DEFINITION_ID = "definition-id";

    private DMNConnectorShapeDefImpl connectorShapeDef;

    @Before
    public void setup() {
        this.connectorShapeDef = new DMNConnectorShapeDefImpl();
    }

    @Test
    public void testGetGlyph() {
        assertEquals(DMNSVGGlyphFactory.ASSOCIATION_TOOLBOX,
                     connectorShapeDef.getGlyph(Association.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.AUTHORITY_REQUIREMENT_TOOLBOX,
                     connectorShapeDef.getGlyph(AuthorityRequirement.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.INFORMATION_REQUIREMENT_TOOLBOX,
                     connectorShapeDef.getGlyph(InformationRequirement.class, DEFINITION_ID));
        assertEquals(DMNSVGGlyphFactory.KNOWLEDGE_REQUIREMENT_TOOLBOX,
                     connectorShapeDef.getGlyph(KnowledgeRequirement.class, DEFINITION_ID));

        assertTrue(connectorShapeDef.getGlyph(Decision.class, DEFINITION_ID) instanceof ShapeGlyph);
    }
}
