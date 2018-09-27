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
package org.kie.workbench.common.services.verifier.reporting.client.reporting;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class ExplanationTest {

    @Test
    public void testTest() throws Exception {

        Explanation explanation = new Explanation()
                .addParagraph("Para 1.")
                .startExampleTable()
                .startHeader()
                .headerConditions("Salary", "Savings")
                .headerActions("Approve loan")
                .end()
                .startRow()
                .addConditions("--", "100 000").addActions("true").end()
                .startRow()
                .addConditions("30 000", "--").addActions("false")
                .end()
                .end()
                .addParagraph("Para 2.");

        assertEquals("<p>Para 1.</p>" +
                             "<table class='exampleTable'>" +
                             "<tr>" +
                             "<th class='exampleTableHeaderConditions'>Salary</th>" +
                             "<th class='exampleTableHeaderConditions'>Savings</th>" +
                             "<th class='exampleTableHeaderActions'>Approve loan</th>" +
                             "</tr>" +
                             "<tr>" +
                             "<td class='oddConditionCell'>--</td>" +
                             "<td class='oddConditionCell'>100 000</td>" +
                             "<td class='oddActionCell'>true</td>" +
                             "</tr>" +
                             "<tr>" +
                             "<td class='evenConditionCell'>30 000</td>" +
                             "<td class='evenConditionCell'>--</td>" +
                             "<td class='evenActionCell'>false</td>" +
                             "</tr>" +
                             "</table>" +
                             "<p>Para 2.</p>",
                     explanation.toHTML().asString());
    }
}