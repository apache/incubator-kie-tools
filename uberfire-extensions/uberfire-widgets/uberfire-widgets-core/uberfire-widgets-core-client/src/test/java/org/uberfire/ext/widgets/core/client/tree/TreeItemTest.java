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

package org.uberfire.ext.widgets.core.client.tree;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TreeItemTest {

    @GwtMock
    FlowPanel container;

    ClickHandler clickHandler;

    @Test
    public void testItemEvents() {
        when( container.addDomHandler( any( ClickHandler.class ),
                                       any( ClickEvent.getType().getClass() ) ) ).thenAnswer( new Answer() {
            @Override
            public Object answer( InvocationOnMock aInvocation ) throws Throwable {
                clickHandler = (ClickHandler) aInvocation.getArguments()[ 0 ];
                return null;
            }
        } );

        final Tree tree = mock( Tree.class );
        final TreeItem treeItem = new TreeItem( TreeItem.Type.ITEM,
                                                "item" );
        treeItem.setTree( tree );

        //Check items are selected
        clickHandler.onClick( new ClickEvent() {
        } );

        verify( tree,
                times( 1 ) ).onSelection( eq( treeItem ),
                                          eq( true ) );
        verify( tree,
                never() ).fireStateChanged( eq( treeItem ),
                                            eq( TreeItem.State.OPEN ) );
    }

    @Test
    public void testFolderEvents() {
        when( container.addDomHandler( any( ClickHandler.class ),
                                       any( ClickEvent.getType().getClass() ) ) ).thenAnswer( new Answer() {
            @Override
            public Object answer( InvocationOnMock aInvocation ) throws Throwable {
                clickHandler = (ClickHandler) aInvocation.getArguments()[ 0 ];
                return null;
            }
        } );

        final Tree tree = mock( Tree.class );
        final TreeItem treeItem = new TreeItem( TreeItem.Type.FOLDER,
                                                "folder" );
        treeItem.setTree( tree );

        //Check folders are selected and opened
        clickHandler.onClick( new ClickEvent() {
        } );

        verify( tree,
                times( 1 ) ).onSelection( eq( treeItem ),
                                          eq( true ) );
        verify( tree,
                times( 1 ) ).fireStateChanged( eq( treeItem ),
                                               eq( TreeItem.State.OPEN ) );

        //Check folders are closed when clicked again
        clickHandler.onClick( new ClickEvent() {
        } );

        verify( tree,
                times( 2 ) ).onSelection( eq( treeItem ),
                                          eq( true ) );
        verify( tree,
                times( 1 ) ).fireStateChanged( eq( treeItem ),
                                               eq( TreeItem.State.CLOSE ) );
    }

}
