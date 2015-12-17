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

package org.kie.workbench.common.screens.datamodeller.client.pdescriptor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.view.client.AsyncDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith( MockitoJUnitRunner.class )
public class ProjectClassListTest {

    @Mock
    private ProjectClassListView view;

    @Mock
    private ProjectClassListView.LoadClassesHandler mockLoadClassesHandler;

    private ProjectClassList presenter;

    ClassRow classRow1 = new ClassRow() {
        @Override public String getClassName() {
            return "Class1";
        }

        @Override public void setClassName( String className ) {

        }
    };

    ClassRow classRow2 = new ClassRow() {
        @Override public String getClassName() {
            return "Class2";
        }

        @Override public void setClassName( String className ) {

        }
    };

    @Before
    public void setup() {
        presenter = new ProjectClassList( view );
        presenter.addLoadClassesHandler( mockLoadClassesHandler );
        verify( view, times( 1 ) ).setPresenter( any( ProjectClassList.class ) );
        verify( view, times( 1 ) ).setDataProvider( any( AsyncDataProvider.class ) );
    }

    @Test
    public void testOnLoadClassesWithNoResult() {
        verify( mockLoadClassesHandler, times( 0 ) ).onLoadClasses();
        presenter.onLoadClasses();
        verify( mockLoadClassesHandler, times( 1 ) ).onLoadClasses();
        verify( view, times( 0 ) ).redraw();
    }

    @Test
    public void testOnLoadClassesWithResult() {

        final ProjectClassListView.LoadClassesHandler handler = new ProjectClassListView.LoadClassesHandler() {
            @Override
            public void onLoadClasses() {
                List<ClassRow> classes = new ArrayList<ClassRow>();
                classes.add( classRow1 );
                classes.add( classRow2 );
                presenter.setClasses( classes );
            }

            @Override
            public void onLoadClass( String className ) {

            }
        };
        presenter.addLoadClassesHandler( handler );
        presenter.onLoadClasses();

        verify( view, times( 1 ) ).redraw();

        assertEquals( presenter.getClasses().size(), 2 );
    }

    @Test
    public void testOnRemoveClass() {

        final ProjectClassListView.LoadClassesHandler handler = new ProjectClassListView.LoadClassesHandler() {
            @Override
            public void onLoadClasses() {
                List<ClassRow> classes = new ArrayList<ClassRow>();
                classes.add( classRow1 );
                classes.add( classRow2 );
                presenter.setClasses( classes );
            }

            @Override
            public void onLoadClass( String className ) {

            }
        };
        presenter.addLoadClassesHandler( handler );
        presenter.onLoadClasses();

        verify( view, times( 1 ) ).redraw();

        assertEquals( presenter.getClasses().size(), 2 );

        presenter.onRemoveClass( classRow1 );
        verify( view, times( 2 ) ).redraw();
        assertEquals( presenter.getClasses().size(), 1 );
        assertEquals( false, presenter.getClasses().contains( classRow1 ) );

        presenter.onRemoveClass( classRow2 );
        verify( view, times( 3 ) ).redraw();
        assertEquals( presenter.getClasses().size(), 0 );
        assertEquals( false, presenter.getClasses().contains( classRow2 ) );

    }

}
