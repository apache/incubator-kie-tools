/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.services.verifier.plugin.client.api;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SortTableTest {

    @Test
    public void testEmpty() {
        final ArrayList<Integer> rowOrder = new ArrayList<>();
        final SortTable sortTable = new SortTable(rowOrder);
        assertTrue(sortTable.getRowOrder().isEmpty());
    }

    @Test
    public void testFilled() {
        final ArrayList<Integer> rowOrder = new ArrayList<>();
        rowOrder.add(2);
        rowOrder.add(1);

        final SortTable sortTable = new SortTable(rowOrder);
        Assertions.assertThat(sortTable.getRowOrder()).containsExactly(2, 1);
    }
}