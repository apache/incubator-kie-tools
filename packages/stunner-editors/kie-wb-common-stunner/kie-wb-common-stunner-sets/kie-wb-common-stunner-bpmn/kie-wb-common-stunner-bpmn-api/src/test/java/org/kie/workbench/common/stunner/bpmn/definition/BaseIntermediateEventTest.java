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

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class BaseIntermediateEventTest {

    private static final String FAKE_LABEL = "fake_label";
    private static final String ANOTHER_LABEL = "another_label";

    private BPMNGeneralSet generalSet;
    private BackgroundSet backgroundSet;
    private FontSet fontSet;
    private CircleDimensionSet dimensionsSet;
    private DataIOSet dataIOSet;

    private FakeBaseIntermediateEventTest tested;

    @Before
    public void setUp() {
        generalSet = mock(BPMNGeneralSet.class);
        backgroundSet = mock(BackgroundSet.class);
        fontSet = mock(FontSet.class);
        dimensionsSet = mock(CircleDimensionSet.class);
        dataIOSet = mock(DataIOSet.class);
        tested = spy(new FakeBaseIntermediateEventTest());
    }

    @Test
    public void initLabels() {
        tested.initLabels();
        assertTrue(tested.labels.contains(FAKE_LABEL));
    }

    @Test
    public void getGeneral() {
        tested.general = generalSet;
        assertEquals(generalSet, tested.getGeneral());
    }

    @Test
    public void setGeneral() {
        tested.general = null;
        tested.setGeneral(generalSet);
        assertEquals(generalSet, tested.general);
    }

    @Test
    public void getBackgroundSet() {
        tested.backgroundSet = backgroundSet;
        assertEquals(backgroundSet, tested.getBackgroundSet());
    }

    @Test
    public void setBackgroundSet() {
        tested.backgroundSet = null;
        tested.setBackgroundSet(backgroundSet);
        assertEquals(backgroundSet, tested.backgroundSet);
    }

    @Test
    public void getFontSet() {
        tested.fontSet = fontSet;
        assertEquals(fontSet, tested.getFontSet());
    }

    @Test
    public void setFontSet() {
        tested.fontSet = null;
        tested.setFontSet(fontSet);
        assertEquals(fontSet, tested.fontSet);
    }

    @Test
    public void getDimensionsSet() {
        tested.dimensionsSet = dimensionsSet;
        assertEquals(dimensionsSet, tested.getDimensionsSet());
    }

    @Test
    public void setDimensionsSet() {
        tested.dimensionsSet = null;
        tested.setDimensionsSet(dimensionsSet);
        assertEquals(dimensionsSet, tested.dimensionsSet);
    }

    @Test
    public void getDataIOSet() {
        tested.dataIOSet = dataIOSet;
        assertEquals(dataIOSet, tested.getDataIOSet());
    }

    @Test
    public void setDataIOSet() {
        tested.dataIOSet = null;
        tested.setDataIOSet(dataIOSet);
        assertEquals(dataIOSet, tested.dataIOSet);
    }

    @Test
    public void getLabels() {
        assertEquals(tested.labels, tested.getLabels());
    }

    @Test
    public void testEquals() {
        tested.general = mock(BPMNGeneralSet.class);
        tested.backgroundSet = mock(BackgroundSet.class);
        tested.fontSet = mock(FontSet.class);
        tested.dimensionsSet = mock(CircleDimensionSet.class);
        tested.labels.clear();
        tested.labels.add(FAKE_LABEL);

        IntermediateTimerEvent compare1 = new IntermediateTimerEvent();

        FakeBaseIntermediateEventTest compare2 = new FakeBaseIntermediateEventTest();
        compare2.general = mock(BPMNGeneralSet.class);
        compare2.backgroundSet = mock(BackgroundSet.class);
        compare2.fontSet = mock(FontSet.class);
        compare2.dimensionsSet = mock(CircleDimensionSet.class);
        compare2.labels.add(ANOTHER_LABEL);

        FakeBaseIntermediateEventTest compare3 = new FakeBaseIntermediateEventTest();
        compare3.general = generalSet;
        compare3.backgroundSet = backgroundSet;
        compare3.fontSet = fontSet;
        compare3.dimensionsSet = dimensionsSet;
        compare3.labels.add(FAKE_LABEL);

        assertFalse(tested.equals(compare1));
        assertFalse(tested.equals(compare2));
        assertFalse(tested.equals(compare3));
        assertTrue(tested.equals(tested));
    }

    private class FakeBaseIntermediateEventTest extends BaseIntermediateEvent {

        @Override
        protected void initLabels() {
            labels.add(FAKE_LABEL);
        }
    }
}