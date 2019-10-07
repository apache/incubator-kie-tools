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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.AttributeColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AttributeColumnPageTest {

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private AttributeColumnPage.View view;

    @Mock
    private TranslationService translationService;

    @Mock
    private SimplePanel content;

    @Mock
    private AttributeColumnPlugin plugin;

    @InjectMocks
    private AttributeColumnPage page = spy(new AttributeColumnPage(view,
                                                                   translationService));

    @BeforeClass
    public static void setupPreferences() {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd/mm/yyyy");
        }};

        ApplicationPreferences.setUp(preferences);

        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();
    }

    @Test
    public void testGetAttributesWhenThereAreReservedAttributes() {
        reservedAttributeNamesMock(Attribute.SALIENCE.getAttributeName(),
                                   Attribute.AGENDA_GROUP.getAttributeName());

        final List<String> result = page.getAttributes();
        final List<String> expected = attributesList();
        expected.remove(Attribute.AGENDA_GROUP.getAttributeName());
        expected.remove(Attribute.SALIENCE.getAttributeName());

        assertEquals(expected,
                     result);
    }

    @Test
    public void testGetAttributesWhenThereIsNoReservedAttribute() {
        reservedAttributeNamesMock();

        final List<String> result = page.getAttributes();
        final List<String> expected = attributesList();

        assertEquals(expected,
                     result);
    }

    @Test
    public void testIsCompleteWhenAttributeIsNull() {
        when(plugin.getAttribute()).thenReturn(null);

        page.isComplete(Assert::assertFalse);
    }

    @Test
    public void testIsCompleteWhenAttributeIsBlank() {
        when(plugin.getAttribute()).thenReturn(null);

        page.isComplete(Assert::assertFalse);
    }

    @Test
    public void testIsCompleteWhenAttributeIsNotNull() {
        when(plugin.getAttribute()).thenReturn(Attribute.SALIENCE.getAttributeName());

        page.isComplete(Assert::assertTrue);
    }

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.AttributeColumnPage_AddNewAttribute;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = page.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testPrepareView() throws Exception {
        page.prepareView();

        verify(view).init(page);
    }

    @Test
    public void testAsWidget() {
        final Widget contentWidget = page.asWidget();

        assertEquals(contentWidget,
                     content);
    }

    @Test
    public void testSelectItem() {
        final String item = "attributeMock";

        page.selectItem(item);

        verify(plugin).setAttribute(item);
    }

    @Test
    public void testSelectedAttribute() {
        page.selectedAttribute();

        verify(plugin).getAttribute();
    }

    private void reservedAttributeNamesMock(final String... attributes) {
        final Set<String> reservedAttributeNames = new HashSet<String>() {{
            for (String attribute : attributes) {
                add(attribute);
            }
        }};

        when(presenter.getReservedAttributeNames()).thenReturn(reservedAttributeNames);
    }

    private List<String> attributesList() {
        return Stream.of(Attribute.values()).map(Attribute::getAttributeName).collect(Collectors.toList());
    }
}
