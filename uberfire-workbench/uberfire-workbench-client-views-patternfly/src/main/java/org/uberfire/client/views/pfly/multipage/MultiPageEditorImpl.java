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

package org.uberfire.client.views.pfly.multipage;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import org.uberfire.client.workbench.widgets.multipage.Multiple;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditorView;
import org.uberfire.client.workbench.widgets.multipage.Page;

@Dependent
public class MultiPageEditorImpl implements MultiPageEditor {

    @Inject @Multiple
    private MultiPageEditorViewImpl view;

    public void addPage( final Page page ) {
        view.addPage( page );
    }

    public void selectPage( final int index ) {
        view.selectPage( index );
    }

    public int selectedPage() {
        return view.selectedPage();
    }

    @Override
    public void clear() {
        view.clear();
    }

    @Override
    public MultiPageEditorView getView() {
        return view;
    }

    @Override
    public void addWidget( final IsWidget widget, final String label ) {
        view.addPage( new PageImpl( widget, label ) );
    }

    @Override
    public Widget asWidget() {
        return view;
    }
}
