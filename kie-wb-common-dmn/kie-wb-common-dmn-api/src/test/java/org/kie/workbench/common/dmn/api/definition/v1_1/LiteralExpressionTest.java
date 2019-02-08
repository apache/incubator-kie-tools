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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.v1_1.common.HasTypeRefHelper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HasTypeRefHelper.class})
public class LiteralExpressionTest {

    private LiteralExpression literalExpression;

    @Before
    public void setup() {
        this.literalExpression = new LiteralExpression();
    }

    @Test
    public void testGetHasTypeRefs() {
        final java.util.List<HasTypeRef> actualHasTypeRefs = literalExpression.getHasTypeRefs();
        final java.util.List<HasTypeRef> expectedHasTypeRefs = singletonList(literalExpression);

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }

    @Test
    public void testComponentWidths() {
        assertEquals(literalExpression.getRequiredComponentWidthCount(),
                     literalExpression.getComponentWidths().size());
        literalExpression.getComponentWidths().forEach(Assert::assertNull);
    }
}
