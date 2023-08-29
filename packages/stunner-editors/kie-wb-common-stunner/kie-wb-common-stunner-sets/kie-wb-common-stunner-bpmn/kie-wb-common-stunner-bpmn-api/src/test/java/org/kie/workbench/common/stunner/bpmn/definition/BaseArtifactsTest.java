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


package org.kie.workbench.common.stunner.bpmn.definition;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BgColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Height;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BaseArtifactsTest {

    private BaseArtifacts tested = new FakeBaseArtifacts(new BackgroundSet(),
                                                         new FontSet(),
                                                         new RectangleDimensionsSet(),
                                                         new AdvancedData());

    @Test
    public void getCategory() {
        assertNotNull(tested.getCategory());
    }

    @Test
    public void getBackgroundSet() {
        assertNotNull(tested.getBackgroundSet());
    }

    @Test
    public void getFontSet() {
        assertNotNull(tested.getFontSet());
    }

    @Test
    public void getDimensionsSet() {
        assertNotNull(tested.getDimensionsSet());
    }

    @Test
    public void setBackgroundSet() {
        BackgroundSet backgroundSet = new BackgroundSet();
        tested.setBackgroundSet(backgroundSet);
        assertEquals(backgroundSet, tested.getBackgroundSet());
    }

    @Test
    public void setFontSet() {
        FontSet fontSet = new FontSet();
        tested.setFontSet(fontSet);
        assertEquals(fontSet, tested.getFontSet());
    }

    @Test
    public void setDimensionsSet() {
        RectangleDimensionsSet dimensionsSet = new RectangleDimensionsSet();
        tested.setDimensionsSet(dimensionsSet);
        assertEquals(dimensionsSet, tested.getDimensionsSet());
    }

    @Test
    public void testHashCode() {
        assertNotEquals(new DataObject().hashCode(), tested.hashCode());
        assertNotEquals(new TextAnnotation().hashCode(), tested.hashCode());
    }

    @Test
    public void testEquals() {
        assertTrue(tested.equals(new DataObject()));
        assertFalse(tested.equals(""));
        final DataObject dataObject = new DataObject();
        BgColor color = new BgColor();
        color.setValue("Black");
        dataObject.getBackgroundSet().setBgColor(color);
        assertFalse(tested.equals(dataObject));
        tested.getBackgroundSet().setBgColor(color);
        assertTrue(tested.equals(dataObject));
        dataObject.getFontSet().setFontSize(new FontSize(11.0));
        assertFalse(tested.equals(dataObject));
        tested.getFontSet().setFontSize(new FontSize(11.0));
        assertTrue(tested.equals(dataObject));
        dataObject.getDimensionsSet().setHeight(new Height(11.0));
        assertFalse(tested.equals(dataObject));
        tested.getDimensionsSet().setHeight(new Height(11.0));
        assertTrue(tested.equals(dataObject));
    }

    private static class FakeBaseArtifacts extends BaseArtifacts {

        public FakeBaseArtifacts(BackgroundSet backgroundSet,
                                 FontSet fontSet,
                                 RectangleDimensionsSet dimensionsSet,
                                 AdvancedData advancedData) {
            super(backgroundSet, fontSet, dimensionsSet, advancedData);
        }

        @Override
        public BPMNBaseInfo getGeneral() {
            return null;
        }
    }
}