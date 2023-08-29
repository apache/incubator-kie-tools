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

import static org.assertj.core.api.Assertions.assertThat;

public class RelationDefaultValueUtilitiesTest {

    private Relation relation;

    @Before
    public void setup() {
        this.relation = new Relation();
    }

    @Test
    public void testGetNewColumnName() {
        final InformationItem informationItem1 = new InformationItem();
        relation.getColumn().add(informationItem1);
        informationItem1.getName().setValue(RelationDefaultValueUtilities.getNewColumnName(relation));
        assertThat(informationItem1.getName().getValue()).isEqualTo(RelationDefaultValueUtilities.PREFIX + "1");

        final InformationItem informationItem2 = new InformationItem();
        relation.getColumn().add(informationItem2);
        informationItem2.getName().setValue(RelationDefaultValueUtilities.getNewColumnName(relation));
        assertThat(informationItem2.getName().getValue()).isEqualTo(RelationDefaultValueUtilities.PREFIX + "2");
    }

    @Test
    public void testGetNewColumnNameWithExistingColumns() {
        final InformationItem informationItem1 = new InformationItem();
        relation.getColumn().add(informationItem1);
        informationItem1.getName().setValue("column");

        final InformationItem informationItem2 = new InformationItem();
        relation.getColumn().add(informationItem2);
        informationItem2.getName().setValue(RelationDefaultValueUtilities.getNewColumnName(relation));
        assertThat(informationItem2.getName().getValue()).isEqualTo(RelationDefaultValueUtilities.PREFIX + "1");
    }

    @Test
    public void testGetNewColumnNameWithDeletion() {
        final InformationItem informationItem1 = new InformationItem();
        relation.getColumn().add(informationItem1);
        informationItem1.getName().setValue(RelationDefaultValueUtilities.getNewColumnName(relation));
        assertThat(informationItem1.getName().getValue()).isEqualTo(RelationDefaultValueUtilities.PREFIX + "1");

        final InformationItem informationItem2 = new InformationItem();
        relation.getColumn().add(informationItem2);
        informationItem2.getName().setValue(RelationDefaultValueUtilities.getNewColumnName(relation));
        assertThat(informationItem2.getName().getValue()).isEqualTo(RelationDefaultValueUtilities.PREFIX + "2");

        relation.getColumn().remove(informationItem1);

        final InformationItem informationItem3 = new InformationItem();
        relation.getColumn().add(informationItem3);
        informationItem3.getName().setValue(RelationDefaultValueUtilities.getNewColumnName(relation));
        assertThat(informationItem3.getName().getValue()).isEqualTo(RelationDefaultValueUtilities.PREFIX + "3");
    }
}
