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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapperHelper.RelationSection;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapperHelper.getInformationItemIndex;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapperHelper.getSection;

public class RelationUIModelMapperHelperTest {

    private Relation relation;

    @Before
    public void setup() {
        this.relation = new Relation();
    }

    @Test
    public void testGetSectionNone() {
        assertEquals(RelationSection.NONE,
                     getSection(relation,
                                RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT + 1));
    }

    @Test
    public void testGetSectionRowNumberColumn() {
        assertEquals(RelationSection.ROW_INDEX,
                     getSection(relation,
                                0));
    }

    @Test
    public void testGetSectionRowNumberColumnWhenInformationItemPresent() {
        relation.getColumn().add(new InformationItem());

        assertEquals(RelationSection.ROW_INDEX,
                     getSection(relation,
                                0));
    }

    @Test
    public void testGetSectionNonExistingNegativeIndex() {
        relation.getColumn().add(new InformationItem());

        assertEquals(RelationSection.NONE,
                     getSection(relation,
                                -1));
    }

    @Test
    public void testGetSectionNonExistingIndexEqualToColumnCount() {
        relation.getColumn().add(new InformationItem());

        assertEquals(RelationSection.NONE,
                     getSection(relation,
                                relation.getColumn().size() + 1));
    }

    @Test
    public void testGetSectionInformationItemColumn() {
        relation.getColumn().add(new InformationItem());

        assertEquals(RelationSection.INFORMATION_ITEM,
                     getSection(relation,
                                1));
    }

    @Test
    public void testGetInformationItemIndex() {
        relation.getColumn().add(new InformationItem());
        relation.getColumn().add(new InformationItem());

        assertEquals(0,
                     getInformationItemIndex(relation,
                                             1));
        assertEquals(1,
                     getInformationItemIndex(relation,
                                             2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSectionInformationItemColumnNegativeIndex() {
        relation.getColumn().add(new InformationItem());

        assertEquals(RelationSection.INFORMATION_ITEM.ordinal(),
                     getInformationItemIndex(relation,
                                             -1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSectionInformationItemColumnIndexEqualToColumnCount() {
        relation.getColumn().add(new InformationItem());

        assertEquals(RelationSection.INFORMATION_ITEM.ordinal(),
                     getInformationItemIndex(relation,
                                             relation.getColumn().size() + 1));
    }
}
