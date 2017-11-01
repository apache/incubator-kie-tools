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

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.gwtbootstrap3.client.ui.Heading;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;

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

    private RuleModel model;

    private RuleModellerActionSelectorPopup popup;

    @Before
    public void setUp() {
        this.model = spy(new RuleModel());

        when(oracle.getDSLConditions()).thenReturn(Collections.emptyList());
        when(oracle.getFactTypes()).thenReturn(new String[]{});
        when(oracle.getGlobalVariables()).thenReturn(new String[]{});

        this.popup = new RuleModellerActionSelectorPopup(model,
                                                         ruleModeller,
                                                         Collections.emptyList(),
                                                         null,
                                                         oracle);
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
}