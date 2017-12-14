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

package org.kie.workbench.common.dmn.client.commands.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.property.dmn.Id;

public class CommandUtilsTest {

    private DecisionRule decisionRuleOne;
    private DecisionRule decisionRuleTwo;
    private DecisionRule decisionRuleThree;

    private List<Object> allRows = new ArrayList<>();
    private List<Object> rowsToMove = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        decisionRuleOne = new DecisionRule();
        decisionRuleTwo = new DecisionRule();
        decisionRuleThree = new DecisionRule();

        decisionRuleOne.setId(new Id("1"));
        decisionRuleTwo.setId(new Id("2"));
        decisionRuleThree.setId(new Id("3"));

        allRows.clear();
        allRows.add(decisionRuleOne);
        allRows.add(decisionRuleTwo);
        allRows.add(decisionRuleThree);

        rowsToMove.clear();
    }

    @Test
    public void testMoveOneRowUp() throws Exception {
        CommandUtils.moveRows(allRows, Collections.singletonList(decisionRuleThree), 0);

        Assertions.assertThat(allRows).containsSequence(decisionRuleThree, decisionRuleOne, decisionRuleTwo);
    }

    @Test
    public void testMoveOneRowUpMiddle() throws Exception {
        CommandUtils.moveRows(allRows, Collections.singletonList(decisionRuleThree), 1);

        Assertions.assertThat(allRows).containsSequence(decisionRuleOne, decisionRuleThree, decisionRuleTwo);
    }

    @Test
    public void testMoveOneRowDown() throws Exception {
        CommandUtils.moveRows(allRows, Collections.singletonList(decisionRuleOne), 2);

        Assertions.assertThat(allRows).containsSequence(decisionRuleTwo, decisionRuleThree, decisionRuleOne);
    }

    @Test
    public void testMoveOneRowDownMiddle() throws Exception {
        CommandUtils.moveRows(allRows, Collections.singletonList(decisionRuleOne), 1);

        Assertions.assertThat(allRows).containsSequence(decisionRuleTwo, decisionRuleOne, decisionRuleThree);
    }

    @Test
    public void testMoveTwoRowsUp() throws Exception {
        CommandUtils.moveRows(allRows, Arrays.asList(decisionRuleTwo, decisionRuleThree), 0);

        Assertions.assertThat(allRows).containsSequence(decisionRuleTwo, decisionRuleThree, decisionRuleOne);
    }

    @Test
    public void testMoveTwoRowsDown() throws Exception {
        CommandUtils.moveRows(allRows, Arrays.asList(decisionRuleOne, decisionRuleTwo), 1);

        Assertions.assertThat(allRows).containsSequence(decisionRuleThree, decisionRuleOne, decisionRuleTwo);
    }
}
