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
import org.kie.workbench.common.dmn.showcase.client.selenium.component.DataTypesEditor;
import org.kie.workbench.common.dmn.showcase.client.selenium.component.DataTypesEditor.DataTypeEntry;

/**
 * Selenium test DMN Data Types PopOver
 *
 * This test check details pop over contains entries for particular fields
 * Then check user can highlight/navigate to the root definition
 *
 * For more details see https://issues.redhat.com/browse/KOGITO-4179
 */
public class DMNDesignerDataTypesPopOverIT extends DMNDesignerBaseIT {

    @Test
    public void testNumberSuggestions() throws Exception {
        final String expected = loadResource("complex-data-types.xml");
        setContent(expected);

        final DataTypesEditor dataTypesEditor = DataTypesEditor.open(waitUtils);
        dataTypesEditor
                .expandAll()
                .viewDetails(new DataTypeEntry("Records", "tRecord"))
                .assertDetailEntries(
                        new DataTypeEntry("Question", "tQuestion"),
                        new DataTypeEntry("Respondent", "Structure")
                )
                .viewThisDataType();

        dataTypesEditor.assertHighlightedRootDataType(new DataTypeEntry("tRecord", "Structure"));
    }
}
