/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class OutputClauseLiteralExpressionTest {

    private OutputClauseLiteralExpression outputClauseLiteralExpression;

    @Before
    public void setup() {
        this.outputClauseLiteralExpression = new OutputClauseLiteralExpression();
    }

    @Test
    public void testGetHasTypeRefs() {

        final List<HasTypeRef> actualHasTypeRefs = outputClauseLiteralExpression.getHasTypeRefs();
        final List<HasTypeRef> expectedHasTypeRefs = singletonList(outputClauseLiteralExpression);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }
}
