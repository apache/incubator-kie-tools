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

package org.drools.workbench.screens.guided.dtable.client.wizard.table.pages;

import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ActionSetFieldsPageTest {

    private GuidedDecisionTable52 model;

    @Captor
    private ArgumentCaptor<List<Pattern52>> patternsCaptor;

    @Mock
    private ActionSetFieldsPageView actionSetFieldsPageView;

    @InjectMocks
    private ActionSetFieldsPage actionSetFieldsPage;

    @Before
    public void setUp() throws Exception {
        model = new GuidedDecisionTable52();
        actionSetFieldsPage.model = model;
    }

    @Test
    public void testPrepareViewNoPatterns() throws Exception {
        actionSetFieldsPage.prepareView();
        verify(actionSetFieldsPageView).setAvailablePatterns(Collections.emptyList());
    }

    @Test
    public void testPrepareViewPatternsAvailableButNoConditionSpecified() throws Exception {
        model.setConditionPatterns(Collections.singletonList(new Pattern52()));

        actionSetFieldsPage.prepareView();
        verify(actionSetFieldsPageView).setAvailablePatterns(Collections.emptyList());
    }

    @Test
    public void testPrepareView() throws Exception {
        final Pattern52 pattern = new Pattern52() {{
            setFactType("Person");
            setBoundName("p");
            setChildColumns(Collections.singletonList(new ConditionCol52()));
        }};
        model.setConditionPatterns(Collections.singletonList(pattern));

        actionSetFieldsPage.prepareView();
        verify(actionSetFieldsPageView).setAvailablePatterns(patternsCaptor.capture());

        assertEquals(1, patternsCaptor.getValue().size());
        assertEquals(pattern, patternsCaptor.getValue().get(0));
    }
}
