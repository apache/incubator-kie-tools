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

package org.kie.workbench.common.dmn.api.definition.model;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.styling.FontSize;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;

public class BusinessKnowledgeModelTest {

    @Test
    public void testConstructor() {
        final Id id = mock(Id.class);
        final Description description = mock(Description.class);
        final Name name = mock(Name.class);
        final FunctionDefinition functionDefinition = mock(FunctionDefinition.class);
        final StylingSet stylingSet = mock(StylingSet.class);
        final GeneralRectangleDimensionsSet dimensionsSet = mock(GeneralRectangleDimensionsSet.class);

        final InformationItemPrimary variable = new InformationItemPrimary();
        final BusinessKnowledgeModel expectedParent = new BusinessKnowledgeModel(id,
                                                                                 description,
                                                                                 name,
                                                                                 variable,
                                                                                 functionDefinition,
                                                                                 stylingSet,
                                                                                 dimensionsSet);

        final DMNModelInstrumentedBase actualParent = variable.getParent();

        assertEquals(expectedParent, actualParent);
    }

    @Test
    public void testDifferentStylingSet() {
        final BusinessKnowledgeModel modelOne = new BusinessKnowledgeModel(new Id("123"),
                                                                           new Description(),
                                                                           new Name(),
                                                                           new InformationItemPrimary(new Id("346"),
                                                                                                      new Name(),
                                                                                                      new QName()),
                                                                           new FunctionDefinition(new Id("789"),
                                                                                                  new Description(),
                                                                                                  new QName(),
                                                                                                  null),
                                                                           new StylingSet(),
                                                                           new GeneralRectangleDimensionsSet());

        final BusinessKnowledgeModel modelTwo = new BusinessKnowledgeModel(new Id("123"),
                                                                           new Description(),
                                                                           new Name(),
                                                                           new InformationItemPrimary(new Id("346"),
                                                                                                      new Name(),
                                                                                                      new QName()),
                                                                           new FunctionDefinition(new Id("789"),
                                                                                                  new Description(),
                                                                                                  new QName(),
                                                                                                  null),
                                                                           new StylingSet(),
                                                                           new GeneralRectangleDimensionsSet());

        assertEquals(modelOne, modelTwo);

        modelOne.getStylingSet().setFontSize(new FontSize(10.0));
        modelTwo.getStylingSet().setFontSize(new FontSize(11.0));

        assertNotEquals(modelOne, modelTwo);
    }
}
