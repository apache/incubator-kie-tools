/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import org.junit.Test;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapperHelper.ContextSection;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapperHelper.getSection;

public class ContextUIModelMapperHelperTest {

    @Test
    public void testGetSectionNone() {
        assertEquals(ContextSection.NONE,
                     getSection(ContextUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT +
                                        ContextUIModelMapperHelper.NAME_COLUMN_COUNT +
                                        ContextUIModelMapperHelper.EXPRESSION_COLUMN_COUNT + 1));
    }

    @Test
    public void testGetSectionRowNumberColumn() {
        assertEquals(ContextSection.ROW_INDEX,
                     getSection(0));
    }

    @Test
    public void testGetSectionNameColumn() {
        assertEquals(ContextSection.NAME,
                     getSection(ContextUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT));
    }

    @Test
    public void testGetSectionExpressionColumn() {
        assertEquals(ContextSection.EXPRESSION,
                     getSection(ContextUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT +
                                        ContextUIModelMapperHelper.NAME_COLUMN_COUNT));
    }
}
