/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.shapes;

import org.junit.Test;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.FOR_EACH_BATCH_SIZE;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.INPUT_COLLECTION;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.ITERATION_PARAMETER;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.OUTPUT_COLLECTION;

public class HasCollectionsTest extends HasTranslationGeneralTest {

    private final HasCollections hasCollections = HasCollectionsTest.super::getTranslation;
    private final ForEachState state = new ForEachState();
    private final String LONG_VALUE = "some long long long value of the parameter";
    private final String TRUNCATE_VALUE = "some long long long value of t...";
    private final String SHORT_VALUE = "{some_value}";

    @Test
    public void testGetCollectionsAllNull() {
        assertTranslations(TEST_STRING + ": null\r\n" + TEST_STRING + ": ∞",
                           hasCollections.getCollections(state),
                           INPUT_COLLECTION,
                           FOR_EACH_BATCH_SIZE);
    }

    @Test
    public void testGetCollectionsLongInputCollection() {
        state.setInputCollection(LONG_VALUE);
        assertTranslations(TEST_STRING + ": " + TRUNCATE_VALUE + "\r\n" + TEST_STRING + ": ∞",
                           hasCollections.getCollections(state),
                           INPUT_COLLECTION,
                           FOR_EACH_BATCH_SIZE);
    }

    @Test
    public void testGetCollectionsShortInputCollection() {
        state.setInputCollection(SHORT_VALUE);
        assertTranslations(TEST_STRING + ": " + SHORT_VALUE + "\r\n" + TEST_STRING + ": ∞",
                           hasCollections.getCollections(state),
                           INPUT_COLLECTION,
                           FOR_EACH_BATCH_SIZE);
    }

    @Test
    public void testGetCollectionsShortOutputCollection() {
        state.setOutputCollection(SHORT_VALUE);
        assertTranslations(TEST_STRING + ": null\r\n" + TEST_STRING + ": " + SHORT_VALUE + "\r\n" + TEST_STRING + ": ∞",
                           hasCollections.getCollections(state),
                           INPUT_COLLECTION,
                           OUTPUT_COLLECTION,
                           FOR_EACH_BATCH_SIZE);
    }

    @Test
    public void testGetCollectionsLongOutputCollection() {
        state.setOutputCollection(LONG_VALUE);
        assertTranslations(TEST_STRING + ": null\r\n" + TEST_STRING + ": " + TRUNCATE_VALUE + "\r\n" + TEST_STRING + ": ∞",
                           hasCollections.getCollections(state),
                           INPUT_COLLECTION,
                           OUTPUT_COLLECTION,
                           FOR_EACH_BATCH_SIZE);
    }

    @Test
    public void testGetCollectionsIterationParam() {
        state.setIterationParam(SHORT_VALUE);
        assertTranslations(TEST_STRING + ": null\r\n" + TEST_STRING + ": " + SHORT_VALUE + "\r\n" + TEST_STRING + ": ∞",
                           hasCollections.getCollections(state),
                           INPUT_COLLECTION,
                           ITERATION_PARAMETER,
                           FOR_EACH_BATCH_SIZE);
    }

    @Test
    public void testGetCollectionsLongIterationParam() {
        state.setIterationParam(LONG_VALUE);
        assertTranslations(TEST_STRING + ": null\r\n" + TEST_STRING + ": " + TRUNCATE_VALUE + "\r\n" + TEST_STRING + ": ∞",
                           hasCollections.getCollections(state),
                           INPUT_COLLECTION,
                           ITERATION_PARAMETER,
                           FOR_EACH_BATCH_SIZE);
    }

    @Test
    public void testIsDefaultBatchSizeInteger() {
        state.setBatchSize(Integer.valueOf(5));
        assertTranslations(TEST_STRING + ": null\r\n" + TEST_STRING + ": 5",
                           hasCollections.getCollections(state),
                           INPUT_COLLECTION,
                           FOR_EACH_BATCH_SIZE);
    }

    @Test
    public void testIsDefaultBatchSizeString() {
        state.setBatchSize("7");
        assertTranslations(TEST_STRING + ": null\r\n" + TEST_STRING + ": 7",
                           hasCollections.getCollections(state),
                           INPUT_COLLECTION,
                           FOR_EACH_BATCH_SIZE);
    }

    @Test
    public void testIsDefaultModeIncorrect() {
        boolean actual = hasCollections.isDefaultMode("test string");
        assertTrue(actual);
    }

    @Test
    public void testIsDefaultModeParallel() {
        boolean actual = hasCollections.isDefaultMode("parallel");
        assertTrue(actual);
    }

    @Test
    public void testIsDefaultModeSequential() {
        boolean actual = hasCollections.isDefaultMode("sequential");
        assertFalse(actual);
    }
}
