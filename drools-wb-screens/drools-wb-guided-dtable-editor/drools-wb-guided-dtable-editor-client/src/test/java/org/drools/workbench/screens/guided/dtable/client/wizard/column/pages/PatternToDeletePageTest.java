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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionRetractFactPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PatternToDeletePageTest {

    @Mock
    private PatternToDeletePage.View view;

    @Mock
    private ActionRetractFactPlugin plugin;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private TranslationService translationService;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private SimplePanel content;

    @InjectMocks
    private PatternToDeletePage page = spy(new PatternToDeletePage(view,
                                                                   translationService));

    @BeforeClass
    public static void staticSetup() {
        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();
    }

    @Test
    public void testIsComplete() throws Exception {
        doReturn(true).when(plugin).isPatternToDeletePageCompleted();

        page.isComplete(Assert::assertTrue);

        verify(plugin).isPatternToDeletePageCompleted();
    }

    @Test
    public void testPrepareView() throws Exception {
        doReturn(new ArrayList<String>()).when(page).getLHSBoundFacts();

        page.prepareView();

        verify(view).init(page);
        verify(page).markAsViewed();
    }

    @Test
    public void testLoadPatterns() throws Exception {
        final ArrayList<String> lhsBoundFacts = new ArrayList<String>() {{
            add("boundFact1");
            add("");
            add("boundFact3");
        }};

        final ArrayList<String> expectedPatterns = new ArrayList<String>() {{
            add("boundFact1");
            add("boundFact3");
        }};

        doReturn(lhsBoundFacts).when(page).getLHSBoundFacts();

        final List<String> patterns = page.loadPatterns();

        assertEquals(expectedPatterns,
                     patterns);
    }

    @Test
    public void testBinding() throws Exception {
        page.binding();

        verify(plugin).getEditingColStringValue();
    }

    @Test
    public void testGetLHSBoundFacts() throws Exception {
        final ArrayList<Pattern52> patterns = new ArrayList<Pattern52>() {{
            add(pattern("pattern1"));
            add(pattern("pattern2"));
        }};
        final ArrayList<String> expectedLHSBoundFacts = new ArrayList<String>() {{
            add("pattern1");
            add("pattern2");
        }};

        doReturn(model).when(presenter).getModel();
        doReturn(patterns).when(model).getConditions();

        final List<String> lhsBoundFacts = page.getLHSBoundFacts();

        assertEquals(expectedLHSBoundFacts,
                     lhsBoundFacts);
    }

    @Test
    public void testSetTheSelectedPattern() throws Exception {
        page.setTheSelectedPattern();

        verify(plugin).setEditingColStringValue(view.selectedPattern());
    }

    @Test
    public void testMarkAsViewed() throws Exception {
        page.markAsViewed();

        verify(plugin).setPatternToDeletePageAsCompleted();
    }

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.PatternToDeletePage_Pattern;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = page.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testAsWidget() {
        final Widget contentWidget = page.asWidget();

        assertEquals(contentWidget,
                     content);
    }

    private Pattern52 pattern(final String pattern1) {
        return new Pattern52() {{
            setBoundName(pattern1);
        }};
    }
}
