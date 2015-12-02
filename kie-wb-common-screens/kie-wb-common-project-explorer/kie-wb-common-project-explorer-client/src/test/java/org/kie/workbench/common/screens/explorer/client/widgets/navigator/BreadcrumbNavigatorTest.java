/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.DotResourceTypeDefinition;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class BreadcrumbNavigatorTest {

    @Mock
    private Path path;

    @Mock
    private DotResourceTypeDefinition hiddenTypeDef;

    @Mock
    private User user;

    @Test
    public void testLoadContentEmpty() {
        final BreadcrumbNavigator navigator = spy( new BreadcrumbNavigator( hiddenTypeDef,
                                                                            user ) );
        final FolderItem item = new FolderItem( path,
                                                "item1",
                                                FolderItemType.FILE );
        final List<FolderItem> content = new ArrayList<FolderItem>();
        final List<FolderItem> segments = new ArrayList<FolderItem>();
        final FolderListing listing = new FolderListing( item,
                                                         content,
                                                         segments );
        navigator.loadContent( listing );

        verify( navigator ).setupBreadcrumb( listing );
        verify( navigator ).setupUpFolder( listing );
        verify( navigator ).setupContent( listing );

        verify( navigator ).hideNavigatorPanel();
    }

    @Test
    public void testLoadContentOnlyFile() {
        final BreadcrumbNavigator navigator = spy( new BreadcrumbNavigator( hiddenTypeDef,
                                                                            user ) );
        final FolderItem item = new FolderItem( path,
                                                "item1",
                                                FolderItemType.FILE );
        final List<FolderItem> content = new ArrayList<FolderItem>() {{
            add( new FolderItem( mock( Path.class ),
                                 "File1",
                                 FolderItemType.FILE ) );
        }};
        final List<FolderItem> segments = new ArrayList<FolderItem>();

        final FolderListing listing = new FolderListing( item,
                                                         content,
                                                         segments );
        navigator.loadContent( listing );

        verify( navigator ).setupBreadcrumb( listing );
        verify( navigator ).setupUpFolder( listing );
        verify( navigator ).setupContent( listing );

        verify( navigator ).hideNavigatorPanel();
    }

    @Test
    public void testLoadContentOnlyFolder() {
        final BreadcrumbNavigator navigator = spy( new BreadcrumbNavigator( hiddenTypeDef,
                                                                            user ) );
        final FolderItem item = new FolderItem( path,
                                                "item1",
                                                FolderItemType.FILE );
        final List<FolderItem> content = new ArrayList<FolderItem>() {{
            add( new FolderItem( mock( Path.class ),
                                 "Folder1",
                                 FolderItemType.FOLDER ) );
        }};
        final List<FolderItem> segments = new ArrayList<FolderItem>();

        final FolderListing listing = new FolderListing( item,
                                                         content,
                                                         segments );
        navigator.loadContent( listing );

        verify( navigator ).setupBreadcrumb( listing );
        verify( navigator ).setupUpFolder( listing );
        verify( navigator ).setupContent( listing );

        verify( navigator ).showNavigatorPanel();
    }

    @Test
    public void testLoadContentFileAndFolder() {
        final BreadcrumbNavigator navigator = spy( new BreadcrumbNavigator( hiddenTypeDef,
                                                                            user ) );
        final FolderItem item = new FolderItem( path,
                                                "item1",
                                                FolderItemType.FILE );
        final List<FolderItem> content = new ArrayList<FolderItem>() {{
            add( new FolderItem( mock( Path.class ),
                                 "File1",
                                 FolderItemType.FILE ) );
            add( new FolderItem( mock( Path.class ),
                                 "Folder1",
                                 FolderItemType.FOLDER ) );
        }};
        final List<FolderItem> segments = new ArrayList<FolderItem>();

        final FolderListing listing = new FolderListing( item,
                                                         content,
                                                         segments );
        navigator.loadContent( listing );

        verify( navigator ).setupBreadcrumb( listing );
        verify( navigator ).setupUpFolder( listing );
        verify( navigator ).setupContent( listing );

        verify( navigator ).showNavigatorPanel();
    }

}
