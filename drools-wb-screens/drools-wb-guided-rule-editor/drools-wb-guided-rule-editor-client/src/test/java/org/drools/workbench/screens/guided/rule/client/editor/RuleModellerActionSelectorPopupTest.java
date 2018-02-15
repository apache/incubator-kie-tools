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

import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.assertj.core.api.Assertions;
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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub({Heading.class})
@RunWith(GwtMockitoTestRunner.class)
public class RuleModellerActionSelectorPopupTest {

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

    @Captor
    private ArgumentCaptor<Command> commandArgumentCaptor;

    private RuleModel model;

    private RuleModellerActionSelectorPopup popup;

    @Before
    public void setUp() {
        GwtMockito.useProviderForType(ListBox.class, aClass -> listBox);

        this.model = spy(new RuleModel());

        when(oracle.getDSLConditions()).thenReturn(Collections.emptyList());
        when(oracle.getFactTypes()).thenReturn(new String[]{});
        when(oracle.getGlobalVariables()).thenReturn(new String[]{});

        this.popup = spy(new RuleModellerActionSelectorPopup(model,
                                                             ruleModeller,
                                                             Collections.singletonList(actionPlugin),
                                                             null,
                                                             oracle));
        reset(model);
    }

    @Test
    public void checkAddUpdateNotModifyGetsPatternBindings() {
        popup.addUpdateNotModify();

        verify(model).getLHSPatternVariables();
    }

    @Test
    public void checkAddRetractionsGetsPatternBindings() {
        popup.addRetractions();

        verify(model).getLHSPatternVariables();
    }

    @Test
    public void checkAddModifiesGetsLhsBindings() {
        popup.addModifies();

        verify(model).getAllLHSVariables();
    }

    @Test
    public void checkAddCallMethodOnGetsAllBindings() {
        popup.addCallMethodOn();

        verify(model).getAllLHSVariables();
        verify(model).getRHSBoundFacts();
    }

    @Test
    public void testActionPlugins() throws Exception {
        // reset due to calls in constructor
        reset(actionPlugin);
        final String actionDescription = "modify score";
        final String actionId = "modify score id";
        doReturn(actionDescription).when(actionPlugin).getActionAddDescription();
        doReturn(actionId).when(actionPlugin).getId();
        doReturn(iAction).when(actionPlugin).createIAction(eq(ruleModeller));

        popup.getContent();

        verify(actionPlugin).createIAction(ruleModeller);
        verify(actionPlugin).addPluginToActionList(eq(ruleModeller), commandArgumentCaptor.capture());

        // reset due to adding a lot of different items before custom action plugins
        // listbox is used as popup.choices
        reset(listBox);
        commandArgumentCaptor.getValue().execute();
        verify(listBox).addItem(eq(actionDescription), eq(actionId));
        Assertions.assertThat(popup.cmds).containsKeys(actionId);

        // reset
        // now we need listbox as popup.positionCbo
        reset(listBox);
        doReturn("123").when(listBox).getValue(anyInt());
        popup.cmds.get(actionId).execute();
        verify(model).addRhsItem(iAction, 123);
        verify(popup).hide();
    }
}