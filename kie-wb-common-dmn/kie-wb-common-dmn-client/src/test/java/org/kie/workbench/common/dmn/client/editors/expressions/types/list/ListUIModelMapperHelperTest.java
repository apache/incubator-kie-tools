/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.editors.expressions.types.list;

import org.junit.Test;
import org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListUIModelMapperHelper.ListSection;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListUIModelMapperHelper.getSection;

public class ListUIModelMapperHelperTest {

    @Test
    public void testGetSectionNoneLessThanMinimum() {
        assertEquals(ListSection.NONE,
                     getSection(ListUIModelMapperHelper.ROW_COLUMN_INDEX - 1));
    }

    @Test
    public void testGetSectionNoneMoreThanMaximum() {
        assertEquals(ListSection.NONE,
                     getSection(ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX + 1));
    }

    @Test
    public void testGetSectionRowNumberColumn() {
        assertEquals(ListSection.ROW_INDEX,
                     getSection(ListUIModelMapperHelper.ROW_COLUMN_INDEX));
    }

    @Test
    public void testGetSectionExpressionColumn() {
        assertEquals(ListSection.EXPRESSION,
                     getSection(ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX));
    }
}
