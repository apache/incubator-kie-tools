/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.fileexplorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.client.resources.i18n.CommonConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.core.client.tree.FSTreeItem;
import org.uberfire.ext.widgets.core.client.tree.Tree;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FileExplorerViewImplTest {

    @Mock
    private FileExplorerPresenter presenter;

    @Mock
    private Tree<FSTreeItem> tree;

    private FSTreeItem item;
    private FileExplorerViewImpl view;

    @Before
    public void setUp() {
        view = new FileExplorerViewImpl(tree);
        doAnswer(invocationOnMock -> {
            FSTreeItem item1 = (FSTreeItem) invocationOnMock.getArguments()[0];
            return item1;
        }).when(tree).addItem(any(FSTreeItem.class));
        view.init(presenter);
    }

    @Test
    public void checkItemsAreNotLazyLoaded() {
        item = newTreeItem(new TreeItemData(FSTreeItem.FSType.ITEM,
                                            "file",
                                            mock(Path.class)));
        assertFalse(view.needsLoading(item));
    }

    @Test
    public void checkFoldersWithNoChildrenAreNotLazyLoaded() {
        item = newTreeItem(new TreeItemData(FSTreeItem.FSType.FOLDER,
                                            "folder",
                                            mock(Path.class)));
        assertFalse(view.needsLoading(item));
    }

    @Test
    public void checkFoldersWithExistingChildrenAreNotLazyLoaded() {
        item = newTreeItem(new TreeItemData(FSTreeItem.FSType.FOLDER,
                                            "folder",
                                            mock(Path.class)),
                           new TreeItemData(FSTreeItem.FSType.ITEM,
                                            "file1",
                                            mock(Path.class)),
                           new TreeItemData(FSTreeItem.FSType.ITEM,
                                            "file2",
                                            mock(Path.class)));
        assertFalse(view.needsLoading(item));
    }

    @Test
    public void checkFoldersWithLazyFlagAreLazyLoaded() {
        item = spy(newTreeItem(new TreeItemData(FSTreeItem.FSType.FOLDER,
                                                "folder",
                                                mock(Path.class)),
                               new TreeItemData(FSTreeItem.FSType.ITEM,
                                                CommonConstants.INSTANCE.Loading(),
                                                mock(Path.class))));
        final FSTreeItem child = mock(FSTreeItem.class);
        when(item.getChild(eq(0))).thenReturn(child);
        when(child.getText()).thenReturn(CommonConstants.INSTANCE.Loading());
        assertTrue(view.needsLoading(item));
    }

    private FSTreeItem newTreeItem(TreeItemData parent,
                                   TreeItemData... children) {
        final List<FSTreeItem> cti = new ArrayList<>();

        final FSTreeItem item = new FSTreeItem(parent.type,
                                               parent.value) {

            @Override
            public int getChildCount() {
                return cti.size();
            }

            @Override
            public FSTreeItem getChild(int i) {
                return cti.get(i);
            }

            protected FSTreeItem makeChild(final FSType type,
                                           final String value) {
                return new FSTreeItem(type,
                                      value) {
                    @Override
                    public String getText() {
                        return value;
                    }
                };
            }
        };
        item.setUserObject(parent.path);

        Arrays.asList(children).stream().forEach((c) -> {
            final FSTreeItem ti = item.addItem(c.type,
                                               c.value);
            ti.setUserObject(c.path);
            cti.add(ti);
        });

        return item;
    }

    private class TreeItemData {

        FSTreeItem.FSType type;
        String value;
        Path path;

        TreeItemData(final FSTreeItem.FSType type,
                     final String value,
                     final Path path) {
            this.type = type;
            this.value = value;
            this.path = path;
        }
    }
}