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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events.intermediate;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;

public abstract class BoundaryCatchingIntermediateEvent<T extends BaseCatchingIntermediateEvent> extends CatchingIntermediateEvent<T> {

    private static final int DEFAULT_AMOUNT_OF_INCOME_EDGES = 2;

    public BoundaryCatchingIntermediateEvent(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(getFilledTopLevelEventId(), HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptyTopLevelEventId(), HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(getFilledSubprocessLevelEventId(), HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptySubprocessLevelEventId(), HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventWithEdgesFilledProperties() throws Exception {
        checkEventMarshalling(getFilledTopLevelEventWithEdgesId(), HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventWithEdgesEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptyTopLevelEventWithEdgesId(), HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception {
        checkEventMarshalling(getFilledSubprocessLevelEventWithEdgesId(), HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventWithEdgesEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptySubprocessLevelEventWithEdgesId(), HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
    }

    @Override
    protected int getDefaultAmountOfIncomdeEdges() {
        return DEFAULT_AMOUNT_OF_INCOME_EDGES;
    }
}
