/*
 * Copyright 2015 JBoss Inc
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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

@Dependent
public class ProjectClassList
        implements IsWidget,
                    ProjectClassListView.Presenter {

    private ProjectClassListView view;

    private AsyncDataProvider<ClassRow> dataProvider;

    private List<ClassRow> classes;

    private ProjectClassListView.LoadClassesHandler loadClassesHandler;

    public ProjectClassList() {
    }

    @Inject
    public ProjectClassList( ProjectClassListView view ) {
        this.view = view;
        view.setPresenter( this );
        dataProvider = new AsyncDataProvider<ClassRow>() {
            @Override
            protected void onRangeChanged( HasData<ClassRow> display ) {
                if ( classes != null ) {
                    updateRowCount( classes.size(), true );
                    updateRowData( 0, classes );
                } else {
                    updateRowCount( 0, true );
                    updateRowData( 0, new ArrayList<ClassRow>(  ) );
                }
            }
        };
        view.init( dataProvider );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void fillList( List<ClassRow> classes ) {
        this.classes = classes;
        if ( classes != null ) {
            dataProvider.updateRowCount( classes.size(), true );
            dataProvider.updateRowData( 0, classes );
        } else {
            dataProvider.updateRowCount( 0, true );
            dataProvider.updateRowData( 0, new ArrayList<ClassRow>(  ) );
        }

        view.redraw();
    }

    public List<ClassRow> getClasses() {
        return classes;
    }

    @Override
    public void onLoadClasses() {
        if ( loadClassesHandler != null ) {
            loadClassesHandler.onLoadClasses();
        }
    }

    @Override
    public void onRemoveClass( ClassRow classRow ) {
        classes.remove( classRow );
        fillList( classes );
    }

    @Override
    public void addLoadClassesHandler( ProjectClassListView.LoadClassesHandler loadClassesHandler ) {
        this.loadClassesHandler = loadClassesHandler;
    }

    public void setReadOnly( boolean readOnly ) {
        view.setReadOnly( readOnly );
    }

    public void redraw() {
        view.redraw();
    }
}