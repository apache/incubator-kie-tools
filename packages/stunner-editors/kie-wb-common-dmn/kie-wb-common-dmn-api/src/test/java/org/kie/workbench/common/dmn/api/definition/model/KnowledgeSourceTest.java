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
import org.kie.workbench.common.dmn.api.property.dmn.KnowledgeSourceType;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.styling.FontSize;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class KnowledgeSourceTest {

    @Test
    public void testDifferentStylingSet() {

        final KnowledgeSource modelOne = new KnowledgeSource(new Id("123"),
                                                             new Description(),
                                                             new Name(),
                                                             new KnowledgeSourceType(),
                                                             new LocationURI(),
                                                             new StylingSet(),
                                                             new GeneralRectangleDimensionsSet());

        final KnowledgeSource modelTwo = new KnowledgeSource(new Id("123"),
                                                             new Description(),
                                                             new Name(),
                                                             new KnowledgeSourceType(),
                                                             new LocationURI(),
                                                             new StylingSet(),
                                                             new GeneralRectangleDimensionsSet());

        assertEquals(modelOne, modelTwo);

        modelOne.getStylingSet().setFontSize(new FontSize(10.0));
        modelTwo.getStylingSet().setFontSize(new FontSize(11.0));

        assertNotEquals(modelOne, modelTwo);
    }
}
