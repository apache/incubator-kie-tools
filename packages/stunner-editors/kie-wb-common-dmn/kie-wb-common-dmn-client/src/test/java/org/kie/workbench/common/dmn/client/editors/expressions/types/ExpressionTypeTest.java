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

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpressionTypeTest {

    @Test
    public void testGetTypeByText_whenTextIsOk_thenTypeIsReturned() {
        assertThat(ExpressionType.getTypeByText("Relation")).isEqualTo(ExpressionType.RELATION);
    }

    @Test
    public void testGetTypeByText_whenTextIsOkButWithLeadingOrTrailingSpaces_thenTypeIsReturned() {
        assertThat(ExpressionType.getTypeByText(" Relation  ")).isEqualTo(ExpressionType.RELATION);
    }

    @Test
    public void testGetTypeByText_whenTextIsOkButWithWrongCase_thenTypeIsReturned() {
        assertThat(ExpressionType.getTypeByText("RELATIoN")).isEqualTo(ExpressionType.RELATION);
    }

    @Test
    public void testGetTypeByText_whenTextIsWrong_thenTypeIsUndefined() {
        assertThat(ExpressionType.getTypeByText("Rellllation")).isEqualTo(ExpressionType.UNDEFINED);
    }

    @Test
    public void testGetTypeByText_whenTextIsEmpty_thenTypeIsUndefined() {
        assertThat(ExpressionType.getTypeByText("")).isEqualTo(ExpressionType.UNDEFINED);
    }

    @Test
    public void testGetTypeByText_whenTextIsNull_thenTypeIsUndefined() {
        assertThat(ExpressionType.getTypeByText(null)).isEqualTo(ExpressionType.UNDEFINED);
    }
}
