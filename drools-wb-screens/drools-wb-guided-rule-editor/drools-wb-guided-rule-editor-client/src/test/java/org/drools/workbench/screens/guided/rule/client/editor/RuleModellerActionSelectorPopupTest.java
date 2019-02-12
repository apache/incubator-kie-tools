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
package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.assertj.core.api.Assertions;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.editor.plugin.RuleModellerActionPlugin;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub({Heading.class})
@RunWith(GwtMockitoTestRunner.class)
public class RuleModellerActionSelectorPopupTest {

    private static final String ACTION_ID = "modify score id";

    private static final String ACTION_DESCRIPTION = "modify score";

    @Mock
    private RuleModeller ruleModeller;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private RuleModellerActionPlugin actionPlugin;

    @Mock
    private IAction iAction;

    @Mock
    private ListBox listBox;

    @GwtMock
    private RuleModellerSelectorFilter filterWidget;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<Command> commandArgumentCaptor;

    private RuleModel model;

    private RuleModellerActionSelectorPopup popup;

    @Before
    public void setUp() {
        GwtMockito.useProviderForType(ListBox.class, aClass -> listBox);

        this.model = spy(new RuleModel());

        when(oracle.getDSLActions()).thenReturn(Collections.singletonList(new DSLSentence() {{
            setDefinition("dslSentence");
        }}));
        when(oracle.getFactTypes()).thenReturn(new String[]{"Applicant"});
        when(oracle.getGlobalVariables()).thenReturn(new String[]{"$global"});
        when(ruleModeller.isDSLEnabled()).thenReturn(true);
        when(actionPlugin.getId()).thenReturn(ACTION_ID);
        when(actionPlugin.getActionAddDescription()).thenReturn(ACTION_DESCRIPTION);

        this.popup = spy(new RuleModellerActionSelectorPopup(model,
                                                             ruleModeller,
                                                             Collections.singletonList(actionPlugin),
                                                             0,
                                                             oracle));
        reset(model);
    }

    @Test
    public void checkAddUpdateNotModifyGetsPatternBindings() {
        popup.addUpdateNotModify(false);

        verify(model).getLHSPatternVariables();
    }

    @Test
    public void checkAddRetractionsGetsPatternBindings() {
        popup.addRetractions(false);

        verify(model).getLHSPatternVariables();
    }

    @Test
    public void checkAddModifiesGetsLhsBindings() {
        popup.addModifies(false);

        verify(model).getAllLHSVariables();
    }

    @Test
    public void checkAddCallMethodOnGetsAllBindings() {
        popup.addCallMethodOn(false);

        verify(model).getAllLHSVariables();
        verify(model).getRHSBoundFacts();
    }

    @Test
    public void testActionPlugins() throws Exception {
        // reset due to calls in constructor
        reset(actionPlugin);
        doReturn(ACTION_DESCRIPTION).when(actionPlugin).getActionAddDescription();
        doReturn(ACTION_ID).when(actionPlugin).getId();
        doReturn(iAction).when(actionPlugin).createIAction(eq(ruleModeller));

        popup.getContent();

        verify(actionPlugin).createIAction(ruleModeller);
        verify(actionPlugin).addPluginToActionList(eq(ruleModeller), commandArgumentCaptor.capture());

        // reset due to adding a lot of different items before custom action plugins
        // listbox is used as popup.choices
        reset(listBox);
        commandArgumentCaptor.getValue().execute();
        verify(listBox).addItem(eq(ACTION_DESCRIPTION), eq(ACTION_ID));
        Assertions.assertThat(popup.cmds).containsKeys(ACTION_ID);

        // reset
        // now we need listbox as popup.positionCbo
        reset(listBox);
        doReturn("123").when(listBox).getValue(anyInt());
        popup.cmds.get(ACTION_ID).execute();
        verify(model).addRhsItem(iAction, 123);
        verify(popup).hide();
    }

    @Test
    public void testLoadContentFiltered() throws Exception {
        reset(listBox, actionPlugin);
        when(actionPlugin.getId()).thenReturn(ACTION_ID);
        when(actionPlugin.getActionAddDescription()).thenReturn(ACTION_DESCRIPTION);

        when(filterWidget.getFilterText()).thenReturn("cheese");

        popup = new RuleModellerActionSelectorPopup(model,
                                                    ruleModeller,
                                                    Collections.singletonList(actionPlugin),
                                                    0,
                                                    oracle);

        verify(actionPlugin).addPluginToActionList(eq(ruleModeller), commandArgumentCaptor.capture());
        commandArgumentCaptor.getValue().execute();

        verify(listBox, atLeastOnce()).addItem(keyCaptor.capture(), anyString());

        final List<String> keys = keyCaptor.getAllValues();

        assertThat(keys).containsExactly("ChangeFieldValuesOf0($global)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "AddFreeFormDrl",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "CallMethodOn0($global)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         ACTION_DESCRIPTION);
    }

    @Test
    public void testLoadContentFilteredFactMatched() throws Exception {
        reset(listBox, actionPlugin);
        when(actionPlugin.getId()).thenReturn(ACTION_ID);
        when(actionPlugin.getActionAddDescription()).thenReturn(ACTION_DESCRIPTION);

        when(filterWidget.getFilterText()).thenReturn("applicant");

        popup = new RuleModellerActionSelectorPopup(model,
                                                    ruleModeller,
                                                    Collections.singletonList(actionPlugin),
                                                    0,
                                                    oracle);

        verify(actionPlugin).addPluginToActionList(eq(ruleModeller), commandArgumentCaptor.capture());
        commandArgumentCaptor.getValue().execute();

        verify(listBox, atLeastOnce()).addItem(keyCaptor.capture(), anyString());

        final List<String> keys = keyCaptor.getAllValues();

        assertThat(keys).containsExactly("ChangeFieldValuesOf0($global)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "InsertFact0(Applicant)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "LogicallyInsertFact0(Applicant)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "AddFreeFormDrl",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "CallMethodOn0($global)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         ACTION_DESCRIPTION);
    }

    @Test
    public void testLoadContentDslSentenceMatched() throws Exception {
        reset(listBox, actionPlugin);
        when(actionPlugin.getId()).thenReturn(ACTION_ID);
        when(actionPlugin.getActionAddDescription()).thenReturn(ACTION_DESCRIPTION);

        when(filterWidget.getFilterText()).thenReturn("dsl");

        popup = new RuleModellerActionSelectorPopup(model,
                                                    ruleModeller,
                                                    Collections.singletonList(actionPlugin),
                                                    0,
                                                    oracle);

        verify(actionPlugin).addPluginToActionList(eq(ruleModeller), commandArgumentCaptor.capture());
        commandArgumentCaptor.getValue().execute();

        verify(listBox, atLeastOnce()).addItem(keyCaptor.capture(), anyString());

        final List<String> keys = keyCaptor.getAllValues();

        assertThat(keys).containsExactly("dslSentence",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "ChangeFieldValuesOf0($global)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "AddFreeFormDrl",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "CallMethodOn0($global)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         ACTION_DESCRIPTION);
    }

    @Test
    public void testLoadContentBothDslSentenceAndFactMatched() throws Exception {
        reset(listBox, actionPlugin);
        when(actionPlugin.getId()).thenReturn(ACTION_ID);
        when(actionPlugin.getActionAddDescription()).thenReturn(ACTION_DESCRIPTION);

        // ds[l], app[l]icant
        when(filterWidget.getFilterText()).thenReturn("l");

        popup = new RuleModellerActionSelectorPopup(model,
                                                    ruleModeller,
                                                    Collections.singletonList(actionPlugin),
                                                    0,
                                                    oracle);

        verify(actionPlugin).addPluginToActionList(eq(ruleModeller), commandArgumentCaptor.capture());
        commandArgumentCaptor.getValue().execute();

        verify(listBox, atLeastOnce()).addItem(keyCaptor.capture(), anyString());

        final List<String> keys = keyCaptor.getAllValues();

        assertThat(keys).containsExactly("dslSentence",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "ChangeFieldValuesOf0($global)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "InsertFact0(Applicant)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "LogicallyInsertFact0(Applicant)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "AddFreeFormDrl",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "CallMethodOn0($global)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         ACTION_DESCRIPTION);
    }

    @Test
    public void testLoadContentUnfiltered() throws Exception {
        verify(actionPlugin).addPluginToActionList(eq(ruleModeller), commandArgumentCaptor.capture());
        commandArgumentCaptor.getValue().execute();

        verify(listBox, atLeastOnce()).addItem(keyCaptor.capture(), anyString());

        final List<String> keys = keyCaptor.getAllValues();

        assertThat(keys).containsExactly("dslSentence",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "ChangeFieldValuesOf0($global)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "InsertFact0(Applicant)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "LogicallyInsertFact0(Applicant)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "AddFreeFormDrl",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         "CallMethodOn0($global)",
                                         AbstractRuleModellerSelectorPopup.SECTION_SEPARATOR,
                                         ACTION_DESCRIPTION);
    }
}