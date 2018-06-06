/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.widget.attribute;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(GuidedRuleEditorImages508.class)
public class AddAttributeWidgetTest {

    @Mock
    private Image addImage;

    @Mock
    private RuleModeller ruleModeller;

    @Mock
    private GuidedRuleAttributeSelectorPopup guidedRuleAttributeSelectorPopup;

    @Captor
    private ArgumentCaptor<ClickHandler> clickHandlerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Command> commandArgumentCaptor;

    @Spy
    private AddAttributeWidget addAttributeWidget;

    @Before
    public void setUp() throws Exception {
        GwtMockito.useProviderForType(GuidedRuleAttributeSelectorPopup.class,
                                      (aClass) -> guidedRuleAttributeSelectorPopup);

        doReturn(addImage).when(addAttributeWidget).asWidget();
    }

    @Test
    public void testInit() {
        addAttributeWidget.init(ruleModeller);

        verify(addImage).addClickHandler(clickHandlerArgumentCaptor.capture());

        clickHandlerArgumentCaptor.getValue().onClick(null);

        verify(addAttributeWidget).showAttributeSelectorPopup();
    }

    @Test
    public void testShowAttributeSelectorPopup() {
        final RuleModel model = mock(RuleModel.class);
        final boolean lockLHS = false;
        final boolean lockRHS = true;
        doReturn(model).when(ruleModeller).getModel();
        doReturn(lockLHS).when(ruleModeller).lockLHS();
        doReturn(lockRHS).when(ruleModeller).lockRHS();

        addAttributeWidget.init(ruleModeller);
        addAttributeWidget.showAttributeSelectorPopup();

        verify(guidedRuleAttributeSelectorPopup).init(eq(model),
                                                      eq(lockLHS),
                                                      eq(lockRHS),
                                                      commandArgumentCaptor.capture());

        commandArgumentCaptor.getValue().execute();
        verify(ruleModeller).refreshWidget();
    }
}
