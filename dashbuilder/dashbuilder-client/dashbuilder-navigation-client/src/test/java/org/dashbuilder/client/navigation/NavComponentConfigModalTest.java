/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.navigation;

import java.util.Arrays;
import java.util.List;

import org.dashbuilder.client.navigation.widget.NavComponentConfigModal;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NavComponentConfigModalTest {

    @Mock
    NavComponentConfigModal.View view;

    @Mock
    Command onOk;

    NavTree tree;
    NavComponentConfigModal presenter;

    @Before
    public void setUp() throws Exception {
        tree = new NavTreeBuilder()
                .item("H1", "H1", null, false)
                .divider()
                .group("A", "A", null, false)
                .item("A1", "A1", null, false)
                .item("A2", "A2", null, false)
                .group("B", "B", null, false)
                .group("C", "C", null, false)
                .build();

        presenter = new NavComponentConfigModal(view);
        presenter.setOnOk(onOk);
    }

    @Test
    public void testInitDefault() {
        presenter.setNavGroup(tree.getRootItems(), null);
        presenter.show();

        verify(view).init(presenter);
        verify(view).clearNavGroupItems();
        verify(view).setNavGroupSelection(eq("A"), any());
        verify(view).addNavGroupItem(eq("A>B"), any());
        verify(view).addNavGroupItem(eq("A>B>C"), any());
        verify(view).show();
    }

    @Test
    public void testInitSelected() {
        presenter.setNavGroup(tree.getRootItems(), "B");
        presenter.show();

        verify(view).init(presenter);
        verify(view).clearNavGroupItems();
        verify(view).setNavGroupSelection(eq("A>B"), any());
        verify(view).addNavGroupItem(eq("A"), any());
        verify(view).addNavGroupItem(eq("A>B>C"), any());
        verify(view).show();
    }

    @Test
    public void testSelectItem() {
        presenter.setNavGroup(tree.getRootItems(), "A");
        presenter.show();

        reset(view);
        presenter.onGroupSelected("B");

        assertEquals(presenter.getGroupId(), "B");
        verify(view).clearNavGroupItems();
        verify(view).setNavGroupSelection(eq("A>B"), any());
        verify(view).addNavGroupItem(eq("A"), any());
        verify(view).addNavGroupItem(eq("A>B>C"), any());
    }

    @Test
    public void testDefaultItemsPerGroup() {
        presenter.setNavGroup(tree.getRootItems(), "A");
        presenter.show();

        verify(view).setDefaultNavItemEnabled(true);
        verify(view).clearDefaultItems();
        verify(view).addDefaultItem(eq("A>A1"), any());
        verify(view).addDefaultItem(eq("A>A2"), any());
        verify(view, never()).addDefaultItem(eq("A>B"), any());
        verify(view, never()).addDefaultItem(eq("C"), any());

        reset(view);
        presenter.onGroupSelected("C");
        verify(view).setDefaultNavItemEnabled(true);
        verify(view).clearDefaultItems();
        verify(view, never()).addDefaultItem(anyString(), any());
    }

    @Test
    public void testTargetDivList() {
        List<String> targetDivIdList = Arrays.asList("div1", "div2");
        presenter.setTargetDivIdList(targetDivIdList);
        presenter.show();

        verify(view).clearTargetDivItems();
        verify(view).setTargetDivSelection(eq("div1"), any());
        verify(view).addTargetDivItem(eq("div2"), any());
        assertEquals(presenter.getTargetDivId(), "div1");

        reset(view);
        presenter.setTargetDiv("t");
        presenter.show();
        verify(view).clearTargetDivItems();
        verify(view).setTargetDivSelection(eq("div1"), any());
        verify(view).addTargetDivItem(eq("div2"), any());
        assertEquals(presenter.getTargetDivId(), "div1");
    }
}