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

package org.kie.workbench.common.dmn.showcase.client.selenium;

import org.junit.Test;
import org.kie.workbench.common.dmn.showcase.client.common.DMNDesignerBaseIT;
import org.kie.workbench.common.dmn.showcase.client.selenium.component.DrdNameEditor;
import org.kie.workbench.common.dmn.showcase.client.selenium.locator.DecisionNavigatorXPathLocator;
import org.xmlunit.assertj.XmlAssert;

/**
 * Selenium test to rename one DRD of the DMN diagram
 *
 * For more details see https://issues.redhat.com/browse/KOGITO-6021
 */
public class DMNRenameDrdIT extends DMNDesignerBaseIT {

    @Test
    public void testRenameIsPropagated() throws Exception {
        final String expected = loadResource("single-diagram-with-two-drd.xml");
        setContent(expected);

        decisionNavigator.selectItem(DecisionNavigatorXPathLocator.node("drd-one"));

        final DrdNameEditor drdNameEditor = DrdNameEditor.initialize(waitUtils);
        drdNameEditor.assertValueEqualTo("drd-one");

        drdNameEditor.appendText("xxx");
        drdNameEditor.assertValueEqualTo("drd-onexxx");

        decisionNavigator.assertItemIsPresent(DecisionNavigatorXPathLocator.node("drd-onexxx"));

        final String actualDiagramContent = getContent();
        XmlAssert.assertThat(actualDiagramContent)
                .withNamespaceContext(NAMESPACES)
                .hasXPath("/dmn:definitions" +
                                  "/dmndi:DMNDI" +
                                  "/dmndi:DMNDiagram[@name='drd-onexxx']");
    }
}
