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

package org.kie.workbench.common.dmn.api.definition.model;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RuleAnnotationClauseTest {

    private static final String RULE_NAME = "RULE-NAME";

    @Test
    public void testCopy() {
        final RuleAnnotationClause source = new RuleAnnotationClause();
        source.setName(new Name(RULE_NAME));

        final RuleAnnotationClause target = source.copy();

        assertNotNull(target);
        assertEquals(RULE_NAME, target.getName().getValue());
    }
}
