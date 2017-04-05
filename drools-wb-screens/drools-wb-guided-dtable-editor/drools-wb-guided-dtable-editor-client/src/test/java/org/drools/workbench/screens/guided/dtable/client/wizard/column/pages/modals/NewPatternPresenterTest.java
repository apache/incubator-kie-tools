/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.modals;

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewPatternPresenterTest {

    @Mock
    private NewPatternPresenter.View view;

    @Mock
    private TranslationService translationService;

    @Mock
    private PatternPage patternPage;

    @Mock
    private GuidedDecisionTableView.Presenter decisionTablePresenter;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private ConditionColumnPlugin plugin;

    @Mock
    private GuidedDecisionTable52 table52;

    private NewPatternPresenter presenter;

    @Before
    public void setup() {
        when(decisionTablePresenter.getModel()).thenReturn(table52);
        when(decisionTablePresenter.getDataModelOracle()).thenReturn(oracle);
        when(patternPage.presenter()).thenReturn(decisionTablePresenter);
        when(patternPage.plugin()).thenReturn(plugin);

        this.presenter = spy(new NewPatternPresenter(view,
                                                     translationService) {{
            init(patternPage);
        }});
    }

    @Test
    public void testSetup() throws Exception {
        presenter.setup();

        verify(view).init(presenter);
    }

    @Test
    public void testShow() {
        presenter.show();

        verify(view).clear();
        verify(view).show();
    }

    @Test
    public void testHide() {
        presenter.hide();

        verify(view).hide();
    }

    @Test
    public void testCancel() throws Exception {
        presenter.cancel();

        verify(view).hide();
    }

    @Test
    public void testGetFactTypes() throws Exception {
        final String[] fakeFactTypesArray = new String[]{"factType1", "factType2", "factType3"};
        final List<String> expectedFactTypes = Arrays.asList(fakeFactTypesArray);

        when(oracle.getFactTypes()).thenReturn(fakeFactTypesArray);

        final List<String> factTypes = presenter.getFactTypes();

        assertEquals(expectedFactTypes,
                     factTypes);
    }

    @Test
    public void testAddPatternWhenPatternIsValid() throws Exception {
        final PatternWrapper pattern52 = mock(PatternWrapper.class);

        doReturn("Applicant").when(view).getSelectedFactType();
        doReturn("app").when(view).getBindingText();
        doReturn(false).when(view).isNegatePatternMatch();
        doReturn(pattern52).when(presenter).pattern52();

        presenter.addPattern();

        verify(patternPage).setEditingPattern(pattern52);
        verify(patternPage).prepareView();
        verify(view).hide();
        verify(view,
               never()).showError(any());
    }

    @Test
    public void testAddPatternWhenIsNegatePatternMatch() throws Exception {
        final PatternWrapper pattern52 = mock(PatternWrapper.class);

        doReturn("").when(view).getSelectedFactType();
        doReturn("").when(view).getBindingText();
        doReturn(true).when(view).isNegatePatternMatch();
        doReturn(pattern52).when(presenter).pattern52();

        presenter.addPattern();

        verify(patternPage).setEditingPattern(pattern52);
        verify(patternPage).prepareView();
        verify(view).hide();
        verify(view,
               never()).showError(any());
    }

    @Test
    public void testAddPatternWhenFactNameIsBlank() throws Exception {
        when(view.getSelectedFactType()).thenReturn("Applicant");
        when(view.getBindingText()).thenReturn("");
        when(view.isNegatePatternMatch()).thenReturn(false);

        presenter.addPattern();

        verify(patternPage,
               never()).setEditingPattern(any(PatternWrapper.class));
        verify(patternPage,
               never()).prepareView();
        verify(view,
               never()).hide();
        verify(view).showError(any());
        verify(translationService).format(GuidedDecisionTableErraiConstants.NewPatternPresenter_PleaseEnterANameForFact);
    }

    @Test
    public void testAddPatternWhenFactNameIsEqualsToFactType() throws Exception {
        when(view.getSelectedFactType()).thenReturn("Applicant");
        when(view.getBindingText()).thenReturn("Applicant");
        when(view.isNegatePatternMatch()).thenReturn(false);

        presenter.addPattern();

        verify(patternPage,
               never()).setEditingPattern(any(PatternWrapper.class));
        verify(patternPage,
               never()).prepareView();
        verify(view,
               never()).hide();
        verify(view).showError(any());
        verify(translationService).format(GuidedDecisionTableErraiConstants.NewPatternPresenter_PleaseEnterANameThatIsNotTheSameAsTheFactType);
    }

    @Test
    public void testAddPatternWhenFactNameIsAlreadyUsedByAnotherPattern() throws Exception {
        when(view.getSelectedFactType()).thenReturn("Applicant");
        when(view.getBindingText()).thenReturn("app");
        when(view.isNegatePatternMatch()).thenReturn(false);

        doReturn(false).when(presenter).isBindingUnique("app");

        presenter.addPattern();

        verify(patternPage,
               never()).setEditingPattern(any(PatternWrapper.class));
        verify(patternPage,
               never()).prepareView();
        verify(view,
               never()).hide();
        verify(view).showError(any());
        verify(translationService).format(GuidedDecisionTableErraiConstants.NewPatternPresenter_PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern);
    }

    @Test
    public void testPattern52() throws Exception {
        when(view.getSelectedFactType()).thenReturn("Applicant");
        when(view.getBindingText()).thenReturn("app");
        when(view.isNegatePatternMatch()).thenReturn(false);

        final PatternWrapper pattern52 = presenter.pattern52();

        assertEquals("Applicant",
                     pattern52.getFactType());
        assertEquals("app",
                     pattern52.getBoundName());
        assertEquals(false,
                     pattern52.isNegated());
    }
}
