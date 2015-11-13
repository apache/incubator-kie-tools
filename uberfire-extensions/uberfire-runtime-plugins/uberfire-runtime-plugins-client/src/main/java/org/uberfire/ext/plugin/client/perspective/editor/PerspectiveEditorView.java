/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.perspective.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.editor.commons.client.BaseEditorViewImpl;

@Dependent
public class PerspectiveEditorView
        extends BaseEditorViewImpl
        implements UberView<PerspectiveEditorPresenter>,
                   PerspectiveEditorPresenter.View {

    interface ViewBinder
            extends
            UiBinder<Widget, PerspectiveEditorView> {

    }

    interface PerspectiveEditorViewBinder
            extends
            UiBinder<Widget, PerspectiveEditorView> {

    }

    private static PerspectiveEditorViewBinder uiBinder = GWT.create( PerspectiveEditorViewBinder.class );

    @UiField
    FlowPanel layoutEditor;

    private PerspectiveEditorPresenter presenter;

    @PostConstruct
    public void setup() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final PerspectiveEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupLayoutEditor( Widget widget ) {
        this.layoutEditor.add( widget );
    }
}