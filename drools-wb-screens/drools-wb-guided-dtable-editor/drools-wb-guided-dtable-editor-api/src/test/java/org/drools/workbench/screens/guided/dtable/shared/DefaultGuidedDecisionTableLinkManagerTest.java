/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.shared;

import java.util.ArrayList;

import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class DefaultGuidedDecisionTableLinkManagerTest {

    private DefaultGuidedDecisionTableLinkManager manager;

    @Before
    public void setup() {
        final DefaultGuidedDecisionTableLinkManager wrapped = new DefaultGuidedDecisionTableLinkManager();
        manager = spy(wrapped);
    }

    @Test
    public void onlyOneDecisionTableThereforeNoLinks() {
        manager.link(new GuidedDecisionTable52(),
                     null,
                     (s, t) -> fail("There should be no links"));
    }

    @Test
    public void fieldConstraintLinksToActionUpdateField() {
        //Columns: Row#[0], Description[1], Condition[2], Action[3]
        final GuidedDecisionTable52 dt1 = new GuidedDecisionTable52();
        final Pattern52 p1 = new Pattern52();
        p1.setBoundName("$f");
        p1.setFactType("Fact");
        final ConditionCol52 p1c1 = new ConditionCol52();
        p1c1.setFactField("field");
        p1.getChildColumns().add(p1c1);
        dt1.getConditions().add(p1);
        final ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName("$f");
        asf.setFactField("field");
        dt1.getActionCols().add(asf);

        //Columns: Row#[0], Description[1], Condition[2]
        final GuidedDecisionTable52 dt2 = new GuidedDecisionTable52();
        final Pattern52 p2 = new Pattern52();
        p2.setBoundName("$f");
        p2.setFactType("Fact");
        final ConditionCol52 p2c1 = new ConditionCol52();
        p2c1.setFactField("field");
        p2.getChildColumns().add(p2c1);
        dt2.getConditions().add(p2);

        manager.link(dt1,
                     dt2,
                     (s, t) -> {
                         assertEquals(4,
                                      s);
                         assertEquals(3,
                                      t);
                     });
    }

    @Test
    public void fieldConstraintLinksToActionInsertFactField() {
        //Columns: Row#[0], Description[1], Action[2]
        final GuidedDecisionTable52 dt1 = new GuidedDecisionTable52();
        final ActionInsertFactCol52 aif = new ActionInsertFactCol52();
        aif.setFactType("Fact");
        aif.setFactField("field");
        dt1.getActionCols().add(aif);

        //Columns: Row#[0], Description[1], Condition[2]
        final GuidedDecisionTable52 dt2 = new GuidedDecisionTable52();
        final Pattern52 p2 = new Pattern52();
        p2.setBoundName("$f");
        p2.setFactType("Fact");
        final ConditionCol52 p2c1 = new ConditionCol52();
        p2c1.setFactField("field");
        p2.getChildColumns().add(p2c1);
        dt2.getConditions().add(p2);

        manager.link(dt1,
                     dt2,
                     (s, t) -> {
                         assertEquals(3,
                                      s);
                         assertEquals(3,
                                      t);
                     });
    }

    @Test
    public void fieldConstraintWithActionBRLFragmentFieldWithoutTemplateKey() {
        //Columns: Row#[0], Description[1], Action[2]
        final GuidedDecisionTable52 dt1 = new GuidedDecisionTable52();
        final BRLActionColumn brl = new BRLActionColumn();
        final ActionInsertFact aif = new ActionInsertFact();
        aif.setFactType("Fact");
        aif.addFieldValue(new ActionFieldValue() {{
            setField("field");
            setValue("10");
            setNature(FieldNatureType.TYPE_LITERAL);
        }});
        brl.setDefinition(new ArrayList<IAction>() {{
            add(aif);
        }});
        brl.getChildColumns().add(new BRLActionVariableColumn("",
                                                              DataType.TYPE_BOOLEAN));

        dt1.getActionCols().add(brl);

        //Columns: Row#[0], Description[1], Condition[2]
        final GuidedDecisionTable52 dt2 = new GuidedDecisionTable52();
        final Pattern52 p2 = new Pattern52();
        p2.setBoundName("$f");
        p2.setFactType("Fact");
        final ConditionCol52 p2c1 = new ConditionCol52();
        p2c1.setFactField("field");
        p2.getChildColumns().add(p2c1);
        dt2.getConditions().add(p2);

        manager.link(dt1,
                     dt2,
                     (s, t) -> {
                         assertEquals(3,
                                      s);
                         assertEquals(3,
                                      t);
                     });
    }

    @Test
    public void fieldConstraintWithActionBRLFragmentFieldWithTemplateKey() {
        //Columns: Row#[0], Description[1], Action[2]
        final GuidedDecisionTable52 dt1 = new GuidedDecisionTable52();
        final BRLActionColumn brl = new BRLActionColumn();
        final ActionInsertFact aif = new ActionInsertFact("Fact");
        aif.addFieldValue(new ActionFieldValue() {{
            setField("field");
            setValue("10");
            setType(DataType.TYPE_STRING);
            setNature(FieldNatureType.TYPE_TEMPLATE);
        }});
        brl.setDefinition(new ArrayList<IAction>() {{
            add(aif);
        }});
        brl.getChildColumns().add(new BRLActionVariableColumn("$f",
                                                              DataType.TYPE_STRING,
                                                              "Fact",
                                                              "field"));

        dt1.getActionCols().add(brl);

        //Columns: Row#[0], Description[1], Condition[2]
        final GuidedDecisionTable52 dt2 = new GuidedDecisionTable52();
        final Pattern52 p2 = new Pattern52();
        p2.setBoundName("$f");
        p2.setFactType("Fact");
        final ConditionCol52 p2c1 = new ConditionCol52();
        p2c1.setFactField("field");
        p2.getChildColumns().add(p2c1);
        dt2.getConditions().add(p2);

        manager.link(dt1,
                     dt2,
                     (s, t) -> {
                         assertEquals(3,
                                      s);
                         assertEquals(3,
                                      t);
                     });
    }
}
