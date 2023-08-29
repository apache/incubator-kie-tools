/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.types.imported.treelist;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TreeListViewTest {

    private TreeListView listView;

    @Mock
    private HTMLDivElement treeItemsContainer;

    @Before
    public void setup() {
        listView = new TreeListView(treeItemsContainer);
    }

    @Test
    public void testAdd() {

        final TreeListItem item = mock(TreeListItem.class);
        final Node element = mock(Node.class);
        when(item.getElement()).thenReturn(element);

        listView.add(item);

        verify(treeItemsContainer).appendChild(element);
    }
}