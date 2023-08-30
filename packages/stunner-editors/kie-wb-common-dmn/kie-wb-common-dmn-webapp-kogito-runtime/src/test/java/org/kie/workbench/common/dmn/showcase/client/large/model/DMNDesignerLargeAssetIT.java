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

package org.kie.workbench.common.dmn.showcase.client.large.model;

import org.junit.Test;
import org.kie.workbench.common.dmn.showcase.client.common.DMNDesignerBaseIT;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.DecisionNavigatorXPathLocator;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNDesignerLargeAssetIT extends DMNDesignerBaseIT {

    @Test
    public void testCopiesOfTheSameNode() throws Exception {
        final String expected = loadResource("large-model-nodes-226-copies.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("Decision-1"));
    }

    @Test
    public void testUniqueNodesWithoutLayout() throws Exception {
        final String expected = loadResource("large-model-nodes-50-unique-no-layout.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        for (int i = 1; i <= 50; i++) {
            decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("Decision " + i));
        }
    }

    @Test
    public void testUniqueNodesWithLayout() throws Exception {
        final String expected = loadResource("large-model-nodes-38-unique-layout.xml");
        setContent(expected);

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        decisionNavigator.assertItemsMatch(DecisionNavigatorXPathLocator.node("anonym_"),
                                           39); // 38 nodes + 1 diagram root
    }
}
