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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import java.util.Arrays;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiCell;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SystemControlledColumnValuesSynchronizerTest extends BaseSynchronizerTest {

    private MetadataCol52 resolvedHitMetadata;
    private AttributeCol52 salienceAttribute;
    private int rowsCount;

    @Before
    public void setUp() throws Exception {
        salienceAttribute = new AttributeCol52();
        salienceAttribute.setAttribute(Attribute.SALIENCE.getAttributeName());

        resolvedHitMetadata = new MetadataCol52();
        resolvedHitMetadata.setMetadata(GuidedDecisionTable52.HitPolicy.RESOLVED_HIT_METADATA_NAME);

        model.setHitPolicy(GuidedDecisionTable52.HitPolicy.RESOLVED_HIT);
        modelSynchronizer.appendColumn(salienceAttribute);
        modelSynchronizer.appendColumn(resolvedHitMetadata);

        rowsCount = 0;
    }

    @Test
    public void testPrioritiesOverSameRowMoveUp() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("2");

        uiModel.moveRowsTo(0,
                           Arrays.asList(uiModel.getRow(1)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "1",
                                       "1"));
    }

    @Test
    public void testPrioritiesOverSameRowMoveDown() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("");

        uiModel.moveRowsTo(3,
                           Arrays.asList(uiModel.getRow(0)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "",
                                       ""));
    }

    @Test
    public void testTransitivePrioritiesMoveUp() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("3");
        addRowWithPriorityOver("");

        uiModel.moveRowsTo(0,
                           Arrays.asList(uiModel.getRow(4)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "2",
                                       "3",
                                       "4"));
    }

    @Test
    public void testTransitivePrioritiesMoveDown() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("3");
        addRowWithPriorityOver("");

        uiModel.moveRowsTo(4,
                           Arrays.asList(uiModel.getRow(0)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "",
                                       "",
                                       ""));
    }

    @Test
    public void testTransitivePrioritiesMoveFirstUp() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("3");

        uiModel.moveRowsTo(0,
                           Arrays.asList(uiModel.getRow(1)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "1",
                                       "3"));
    }

    @Test
    public void testTransitivePrioritiesMoveFirstDown() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("3");

        uiModel.moveRowsTo(2,
                           Arrays.asList(uiModel.getRow(1)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "",
                                       "2"));
    }

    @Test
    public void testTransitivePrioritiesMoveMiddleUp() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("3");

        uiModel.moveRowsTo(0,
                           Arrays.asList(uiModel.getRow(2)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "",
                                       "1"));
    }

    @Test
    public void testTransitivePrioritiesMoveMiddleDown() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("");
        addRowWithPriorityOver("2");

        uiModel.moveRowsTo(2,
                           Arrays.asList(uiModel.getRow(1)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "1",
                                       "3"));
    }

    @Test
    public void testTransitivePrioritiesMoveLastUp() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("3");

        uiModel.moveRowsTo(0,
                           Arrays.asList(uiModel.getRow(3)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "",
                                       "3"));
    }

    @Test
    public void testTransitivePrioritiesMoveLastDown() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("");

        uiModel.moveRowsTo(3,
                           Arrays.asList(uiModel.getRow(2)));

        assertPriorities(Arrays.asList("",
                                       "1",
                                       "",
                                       "2"));
    }

    @Test
    public void testDeletionOfRow() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("4");

        modelSynchronizer.deleteRow(2);

        assertPriorities(Arrays.asList("",
                                       "1",
                                       "2",
                                       "3"));
    }

    @Test
    public void testDeletionOfExplicitlyUsedRow() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("4");

        modelSynchronizer.deleteRow(0);

        assertPriorities(Arrays.asList("0",
                                       "0",
                                       "1",
                                       "3"));
    }

    @Test
    public void testInsertionAtBeginning() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("4");

        modelSynchronizer.insertRow(0);
        uiModel.setCellValue(0,
                             3,
                             new GuidedDecisionTableUiCell<>(""));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "2",
                                       "2",
                                       "3",
                                       "5"));
    }

    @Test
    public void testInsertionIntoMiddle() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("4");

        modelSynchronizer.insertRow(3);
        uiModel.setCellValue(3,
                             3,
                             new GuidedDecisionTableUiCell<>(""));

        assertPriorities(Arrays.asList("",
                                       "1",
                                       "1",
                                       "",
                                       "2",
                                       "5"));
    }

    @Test
    public void testInsertionOnPlaceOfUsedRows() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("4");

        modelSynchronizer.insertRow(4);
        uiModel.setCellValue(4,
                             3,
                             new GuidedDecisionTableUiCell<>(""));

        assertPriorities(Arrays.asList("",
                                       "1",
                                       "1",
                                       "2",
                                       "",
                                       "5"));
    }

    @Test
    public void testMoveIntoGroup() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("3");

        uiModel.moveRowsTo(2,
                           Arrays.asList(uiModel.getRow(0)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "",
                                       "2"));
    }

    @Test
    public void testMoveRowWithPriority() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("2");

        uiModel.moveRowsTo(0,
                           Arrays.asList(uiModel.getRow(2)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "",
                                       "3"));
    }

    @Test
    public void testMoveMultipleRows() throws Exception {
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("2");
        addRowWithPriorityOver("");
        addRowWithPriorityOver("1");
        addRowWithPriorityOver("5");

        uiModel.moveRowsTo(3,
                           Arrays.asList(uiModel.getRow(0)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "",
                                       "",
                                       "4",
                                       "5"));
        uiModel.moveRowsTo(2,
                           Arrays.asList(uiModel.getRow(3)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "",
                                       "",
                                       "3",
                                       "5"));

        uiModel.moveRowsTo(1,
                           Arrays.asList(uiModel.getRow(4)));

        assertPriorities(Arrays.asList("",
                                       "",
                                       "",
                                       "",
                                       "",
                                       "2"));
    }

    private void addRowWithPriorityOver(String priorityOverRow) throws Exception {
        modelSynchronizer.appendRow();
        uiModel.setCellValue(rowsCount,
                             0,
                             new GuidedDecisionTableUiCell<>(rowsCount + 1));
        uiModel.setCellValue(rowsCount,
                             3,
                             new GuidedDecisionTableUiCell<>(priorityOverRow));
        rowsCount++;
    }

    private void assertPriorities(List<String> priorities) {
        for (int rowIndex = 0; rowIndex < priorities.size(); rowIndex++) {
            assertEquals(priorities.get(rowIndex),
                         uiModel.getCell(rowIndex,
                                         3).getValue().getValue());
        }
    }
}
