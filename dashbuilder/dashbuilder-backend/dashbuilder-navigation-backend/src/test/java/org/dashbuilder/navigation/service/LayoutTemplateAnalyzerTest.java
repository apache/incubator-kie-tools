/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.navigation.service;

import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.navigation.layout.LayoutNavigationRef;
import org.dashbuilder.navigation.layout.LayoutRecursionIssue;
import org.dashbuilder.navigation.layout.LayoutTemplateContext;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.dashbuilder.navigation.layout.LayoutNavigationRefType.*;
import static org.dashbuilder.navigation.layout.NavDragComponentSettings.*;
import static org.dashbuilder.navigation.layout.NavDragComponentType.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LayoutTemplateAnalyzerTest {

    @Mock
    PerspectivePluginServicesImpl pluginServices;

    @Mock
    NavigationServicesImpl navigationServices;

    @InjectMocks
    LayoutTemplateAnalyzer layoutTemplateAnalyzer;

    LayoutTemplate layoutA = new LayoutTemplate("A");
    LayoutTemplate layoutB = new LayoutTemplate("B");
    LayoutTemplate layoutC = new LayoutTemplate("C");
    LayoutTemplate layoutD = new LayoutTemplate("D");
    LayoutTemplate layoutE = new LayoutTemplate("D");
    LayoutColumn layoutColumnA = new LayoutColumn("12");
    LayoutColumn layoutColumnB = new LayoutColumn("12");
    LayoutColumn layoutColumnD = new LayoutColumn("12");
    LayoutComponent layoutComponentA = new LayoutComponent(CAROUSEL.getFQClassName());
    LayoutComponent layoutComponentB = new LayoutComponent(CAROUSEL.getFQClassName());
    LayoutComponent layoutComponentC1 = new LayoutComponent(TABLIST.getFQClassName());
    LayoutComponent layoutComponentC2 = new LayoutComponent(TABLIST.getFQClassName());
    LayoutComponent layoutComponentD = new LayoutComponent(TREE.getFQClassName());
    LayoutComponent layoutComponentE = new LayoutComponent();


    @Before
    public void setUp() throws Exception {
        LayoutRow layoutRowA = new LayoutRow();
        layoutComponentA.addProperty(NAV_GROUP_ID, "groupA");
        layoutRowA.add(layoutColumnA);
        layoutColumnA.add(layoutComponentA);
        layoutA.addRow(layoutRowA);

        LayoutRow layoutRowB = new LayoutRow();
        layoutComponentB.addProperty(NAV_GROUP_ID, "groupB");
        layoutRowB.add(layoutColumnB);
        layoutColumnB.add(layoutComponentB);
        layoutB.addRow(layoutRowB);

        LayoutRow layoutRowC = new LayoutRow();
        LayoutColumn layoutColumnC1 = new LayoutColumn("6");
        LayoutColumn layoutColumnC2 = new LayoutColumn("6");
        layoutComponentC1.addProperty(NAV_GROUP_ID, "groupC");
        layoutComponentC2.addProperty(NAV_GROUP_ID, "groupC");
        layoutRowC.add(layoutColumnC1);
        layoutRowC.add(layoutColumnC2);
        layoutColumnC1.add(layoutComponentC1);
        layoutColumnC2.add(layoutComponentC2);
        layoutC.addRow(layoutRowC);

        LayoutRow layoutRowD = new LayoutRow();
        layoutComponentD.addProperty(NAV_GROUP_ID, "groupD");
        layoutRowD.add(layoutColumnD);
        layoutColumnD.add(layoutComponentD);
        layoutD.addRow(layoutRowD);

        LayoutRow layoutRowE = new LayoutRow();
        LayoutColumn layoutColumnE = new LayoutColumn("12");
        layoutRowE.add(layoutColumnE);
        layoutColumnE.add(layoutComponentE);
        layoutE.addRow(layoutRowE);

        when(pluginServices.getLayoutTemplate("A")).thenReturn(layoutA);
        when(pluginServices.getLayoutTemplate("B")).thenReturn(layoutB);
        when(pluginServices.getLayoutTemplate("C")).thenReturn(layoutC);
        when(pluginServices.getLayoutTemplate("D")).thenReturn(layoutD);
        when(pluginServices.getLayoutTemplate("E")).thenReturn(layoutE);
    }

    @Test
    public void testNavTreeNotDefined() throws Exception {
        when(navigationServices.loadNavTree()).thenReturn(null);

        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutA);
        assertTrue(info.isEmpty());
    }

    @Test
    public void testOneLevelNoRecursiveIssue() throws Exception {
        when(navigationServices.loadNavTree()).thenReturn(new NavTreeBuilder()
                .group("groupA", "", "", true)
                    .item("layout", "", "", true, NavWorkbenchCtx.perspective("B"))
                    .endGroup()
                .build());

        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutA);
        assertTrue(info.isEmpty());
    }

    @Test
    public void testPerspectiveReuseNoRecursiveIssue() throws Exception {
        when(navigationServices.loadNavTree()).thenReturn(new NavTreeBuilder()
                .group("groupC", "", "", true)
                    .item("layout", "", "", true, NavWorkbenchCtx.perspective("B"))
                    .endGroup()
                .build());

        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutC);
        assertTrue(info.isEmpty());
    }

    @Test
    public void testSimpleRecursion() throws Exception {
        when(navigationServices.loadNavTree()).thenReturn(new NavTreeBuilder()
                .group("groupA", "", "", true)
                    .item("layout", "", "", true, NavWorkbenchCtx.perspective("A"))
                    .endGroup()
                .build());

        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutA);
        assertEquals(info.getRefList().size(), 4);
        assertEquals(info.getRefList().get(0), new LayoutNavigationRef(PERSPECTIVE, "A"));
        assertEquals(info.getRefList().get(1), new LayoutNavigationRef(NAV_COMPONENT, CAROUSEL.getFQClassName()));
        assertEquals(info.getRefList().get(2), new LayoutNavigationRef(NAV_GROUP_DEFINED, "groupA"));
        assertEquals(info.getRefList().get(3), new LayoutNavigationRef(PERSPECTIVE, "A"));
    }

    @Test
    public void testPerspectiveComponent() throws Exception {
        LayoutComponent layoutComponentB = new LayoutComponent("PerspectiveDrag");
        layoutComponentB.addProperty(PERSPECTIVE_ID, "B");
        layoutColumnA.add(layoutComponentB);

        when(navigationServices.loadNavTree()).thenReturn(new NavTreeBuilder().build());
        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutA);
        assertTrue(info.isEmpty());
    }

    @Test
    public void testPerspectiveRecursiveIssue() throws Exception {
        LayoutComponent layoutComponentB = new LayoutComponent("PerspectiveDrag");
        layoutComponentB.addProperty(PERSPECTIVE_ID, "B");
        layoutColumnA.add(layoutComponentB);
        LayoutComponent layoutComponentA = new LayoutComponent("PerspectiveDrag");
        layoutComponentB.addProperty(PERSPECTIVE_ID, "A");
        layoutColumnB.add(layoutComponentA);

        when(navigationServices.loadNavTree()).thenReturn(new NavTreeBuilder().build());
        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutA);
        assertEquals(info.getRefList().size(), 3);
        assertEquals(info.getRefList().get(0), new LayoutNavigationRef(PERSPECTIVE, "A"));
        assertEquals(info.getRefList().get(1), new LayoutNavigationRef(NAV_COMPONENT, "PerspectiveDrag"));
        assertEquals(info.getRefList().get(2), new LayoutNavigationRef(PERSPECTIVE, "A"));
    }

    @Test
    public void testIndirectRecursiveIssue() throws Exception {
        when(navigationServices.loadNavTree()).thenReturn(new NavTreeBuilder()
                .group("groupA", "", "", true)
                    .item("layout", "", "", true, NavWorkbenchCtx.perspective("B"))
                    .endGroup()
                .group("groupB", "", "", true)
                    .item("layout", "", "", true, NavWorkbenchCtx.perspective("A"))
                    .endGroup()
                .build());

        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutA);
        assertEquals(info.getRefList().size(), 7);
        assertEquals(info.getRefList().get(0), new LayoutNavigationRef(PERSPECTIVE, "A"));
        assertEquals(info.getRefList().get(1), new LayoutNavigationRef(NAV_COMPONENT, CAROUSEL.getFQClassName()));
        assertEquals(info.getRefList().get(2), new LayoutNavigationRef(NAV_GROUP_DEFINED, "groupA"));
        assertEquals(info.getRefList().get(3), new LayoutNavigationRef(PERSPECTIVE, "B"));
        assertEquals(info.getRefList().get(4), new LayoutNavigationRef(NAV_COMPONENT, CAROUSEL.getFQClassName()));
        assertEquals(info.getRefList().get(5), new LayoutNavigationRef(NAV_GROUP_DEFINED, "groupB"));
        assertEquals(info.getRefList().get(6), new LayoutNavigationRef(PERSPECTIVE, "A"));
    }

    @Test
    public void testDefaultItemNoRecursiveIssue() throws Exception {
        layoutComponentC1.addProperty(NAV_DEFAULT_ID, "item2");
        layoutComponentC2.addProperty(NAV_DEFAULT_ID, "item2");
        when(navigationServices.loadNavTree()).thenReturn(new NavTreeBuilder()
                .group("groupC", "", "", true)
                .item("item1", "", "", true, NavWorkbenchCtx.perspective("C"))
                .item("item2", "", "", true, NavWorkbenchCtx.perspective("B"))
                .endGroup()
                .build());

        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutC);
        assertTrue(info.isEmpty());
    }

    @Test
    public void testIndirectNavGroupRecursiveIssue() throws Exception {
        layoutComponentC1.addProperty(NAV_DEFAULT_ID, "item2");
        layoutComponentD.addProperty(NAV_DEFAULT_ID, "item4");
        when(navigationServices.loadNavTree()).thenReturn(new NavTreeBuilder()
                .group("groupC", "", "", true)
                .item("item1", "", "", true, NavWorkbenchCtx.perspective("A"))
                .item("item2", "", "", true, NavWorkbenchCtx.perspective("D").setNavGroupId("groupX"))
                .endGroup()
                .group("groupX", "", "", true)
                .item("item3", "", "", true, NavWorkbenchCtx.perspective("D").setNavGroupId("groupX"))
                .item("item4", "", "", true, NavWorkbenchCtx.perspective("A"))
                .endGroup()
                .build());

        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutC);
        assertEquals(info.getRefList().size(), 9);
        assertEquals(info.getRefList().get(0), new LayoutNavigationRef(PERSPECTIVE, "C"));
        assertEquals(info.getRefList().get(1), new LayoutNavigationRef(NAV_COMPONENT, TABLIST.getFQClassName()));
        assertEquals(info.getRefList().get(2), new LayoutNavigationRef(NAV_GROUP_DEFINED, "groupC"));
        assertEquals(info.getRefList().get(3), new LayoutNavigationRef(DEFAULT_ITEM_DEFINED, "item2"));
        assertEquals(info.getRefList().get(4), new LayoutNavigationRef(PERSPECTIVE, "D"));
        assertEquals(info.getRefList().get(5), new LayoutNavigationRef(NAV_COMPONENT, TREE.getFQClassName()));
        assertEquals(info.getRefList().get(6), new LayoutNavigationRef(NAV_GROUP_CONTEXT, "groupX"));
        assertEquals(info.getRefList().get(7), new LayoutNavigationRef(DEFAULT_ITEM_FOUND, "item3"));
        assertEquals(info.getRefList().get(8), new LayoutNavigationRef(PERSPECTIVE, "D"));
    }

    @Test
    public void testDefaultItemRecursiveIssue() throws Exception {
        layoutComponentC1.addProperty(NAV_DEFAULT_ID, "item1");
        layoutComponentC2.addProperty(NAV_DEFAULT_ID, "item1");
        when(navigationServices.loadNavTree()).thenReturn(new NavTreeBuilder()
                .group("groupC", "", "", true)
                .item("item1", "", "", true, NavWorkbenchCtx.perspective("C"))
                .item("item2", "", "", true, NavWorkbenchCtx.perspective("B"))
                .endGroup()
                .build());

        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutC);
        assertEquals(info.getRefList().size(), 5);
        assertEquals(info.getRefList().get(0), new LayoutNavigationRef(PERSPECTIVE, "C"));
        assertEquals(info.getRefList().get(1), new LayoutNavigationRef(NAV_COMPONENT, TABLIST.getFQClassName()));
        assertEquals(info.getRefList().get(2), new LayoutNavigationRef(NAV_GROUP_DEFINED, "groupC"));
        assertEquals(info.getRefList().get(3), new LayoutNavigationRef(DEFAULT_ITEM_DEFINED, "item1"));
        assertEquals(info.getRefList().get(4), new LayoutNavigationRef(PERSPECTIVE, "C"));
    }

    @Test
    public void testFirstItemIndirectRecursiveIssue() throws Exception {
        when(navigationServices.loadNavTree()).thenReturn(new NavTreeBuilder()
                .group("groupA", "", "", true)
                .item("itemA", "", "", true, NavWorkbenchCtx.perspective("C"))
                .endGroup()
                .group("groupC", "", "", true)
                .item("itemC", "", "", true, NavWorkbenchCtx.perspective("A"))
                .endGroup()
                .build());

        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutC);
        assertEquals(info.getRefList().size(), 8);
        assertEquals(info.getRefList().get(0), new LayoutNavigationRef(PERSPECTIVE, "C"));
        assertEquals(info.getRefList().get(1), new LayoutNavigationRef(NAV_COMPONENT, TABLIST.getFQClassName()));
        assertEquals(info.getRefList().get(2), new LayoutNavigationRef(NAV_GROUP_DEFINED, "groupC"));
        assertEquals(info.getRefList().get(3), new LayoutNavigationRef(DEFAULT_ITEM_FOUND, "itemC"));
        assertEquals(info.getRefList().get(4), new LayoutNavigationRef(PERSPECTIVE, "A"));
        assertEquals(info.getRefList().get(5), new LayoutNavigationRef(NAV_COMPONENT, CAROUSEL.getFQClassName()));
        assertEquals(info.getRefList().get(6), new LayoutNavigationRef(NAV_GROUP_DEFINED, "groupA"));
        assertEquals(info.getRefList().get(7), new LayoutNavigationRef(PERSPECTIVE, "C"));
    }

    @Test
    public void testNavGroupFromContext() throws Exception {
        when(navigationServices.loadNavTree()).thenReturn(new NavTreeBuilder()
                .group("groupC", "", "", true)
                .item("item1", "", "", true, NavWorkbenchCtx.perspective("A"))
                .endGroup()
                .group("groupX", "", "", true)
                .item("item2", "", "", true, NavWorkbenchCtx.perspective("C"))
                .endGroup()
                .build());

        LayoutRecursionIssue info = layoutTemplateAnalyzer.analyzeRecursion(layoutC, new LayoutTemplateContext("groupX"));
        assertEquals(info.getRefList().size(), 5);
        assertEquals(info.getRefList().get(0), new LayoutNavigationRef(PERSPECTIVE, "C"));
        assertEquals(info.getRefList().get(1), new LayoutNavigationRef(NAV_COMPONENT, TABLIST.getFQClassName()));
        assertEquals(info.getRefList().get(2), new LayoutNavigationRef(NAV_GROUP_CONTEXT, "groupX"));
        assertEquals(info.getRefList().get(3), new LayoutNavigationRef(DEFAULT_ITEM_FOUND, "item2"));
        assertEquals(info.getRefList().get(4), new LayoutNavigationRef(PERSPECTIVE, "C"));
    }

    @Test
    public void testHasNavigationComponents() throws Exception {
        assertTrue(layoutTemplateAnalyzer.hasNavigationComponents(layoutA));
        assertTrue(layoutTemplateAnalyzer.hasNavigationComponents(layoutB));
        assertTrue(layoutTemplateAnalyzer.hasNavigationComponents(layoutC));
        assertTrue(layoutTemplateAnalyzer.hasNavigationComponents(layoutD));
        assertFalse(layoutTemplateAnalyzer.hasNavigationComponents(layoutE));
    }
}
