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


package org.kie.workbench.common.widgets.client.search.common;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.kie.workbench.common.widgets.client.search.common.JavaStreamHelper.Tuple;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.widgets.client.search.common.JavaStreamHelper.indexedStream;

public class JavaStreamHelperTest {

    @Test
    public void testFilterElements() {

        final List<String> companies = asList("Red Hat", "Github", "IBM", "Microsoft");
        final List<String> expectedElements = asList("Red Hat", "IBM");

        final List<String> actualElements =
                indexedStream(companies)
                        .filter((index, value) -> index % 2 == 0)
                        .map(Tuple::getElement)
                        .collect(Collectors.toList());

        assertEquals(expectedElements, actualElements);
    }

    @Test
    public void testFilterIndexes() {

        final List<String> companies = asList("Red Hat", "Github", "IBM", "Microsoft");
        final List<Integer> expectedIndexes = asList(0, 2);

        final List<Integer> actualIndexes =
                indexedStream(companies)
                        .filter((index, value) -> index % 2 == 0)
                        .map(Tuple::getIndex)
                        .collect(Collectors.toList());

        assertEquals(expectedIndexes, actualIndexes);
    }
}
