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

package org.kie.workbench.common.screens.explorer.client.utils;

import java.util.Arrays;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClassifierTest {

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private SyncBeanDef<ClientResourceType> clientResourceTypeBeanDef;

    @Mock
    private ClientResourceType clientResourceType;

    @InjectMocks
    private Classifier classifier;

    @Before
    public void setup() {
        when( iocManager.lookupBeans( eq( ClientResourceType.class ) ) ).thenReturn( Arrays.asList( clientResourceTypeBeanDef ) );
        when( clientResourceTypeBeanDef.getInstance() ).thenReturn( clientResourceType );

        classifier.init();
    }

    @Test
    public void successfullyFindResourceTypeByPathTest() {
        final Path path = mock( Path.class );
        doReturn( true ).when( clientResourceType ).accept( path );

        final ClientResourceType resourceType = classifier.findResourceType( path );
        assertNotNull( resourceType );
        assertEquals( clientResourceType, resourceType );
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsuccessfullyFindResourceTypeByPathTest() {
        final Path path = mock( Path.class );
        doReturn( false ).when( clientResourceType ).accept( path );
        classifier.findResourceType( path );
    }

    @Test
    public void successfullyFindResourceTypeByFolderItemTest() {
        final FolderItem folderItem = mock( FolderItem.class );
        final Path path = mock( Path.class );
        doReturn( path ).when( folderItem ).getItem();
        doReturn( true ).when( clientResourceType ).accept( path );

        final ClientResourceType resourceType = classifier.findResourceType( folderItem );
        assertNotNull( resourceType );
        assertEquals( clientResourceType, resourceType );
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsuccessfullyFindResourceTypeByFolderItemTest() {
        final FolderItem folderItem = mock( FolderItem.class );
        final Path path = mock( Path.class );
        doReturn( path ).when( folderItem ).getItem();
        doReturn( false ).when( clientResourceType ).accept( path );

        classifier.findResourceType( folderItem );
    }
}
