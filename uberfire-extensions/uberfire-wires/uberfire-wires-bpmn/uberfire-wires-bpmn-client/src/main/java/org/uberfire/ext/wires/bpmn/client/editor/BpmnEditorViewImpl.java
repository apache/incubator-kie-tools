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
package org.uberfire.ext.wires.bpmn.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Label;
import org.uberfire.ext.editor.commons.client.BaseEditorViewImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.BpmnEditorContent;

@Dependent
public class BpmnEditorViewImpl extends BaseEditorViewImpl
        implements BpmnEditorView {

    interface ViewBinder
            extends
            UiBinder<Widget, BpmnEditorViewImpl> {

    }

    final private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    Label label;

    private BpmnEditorPresenter presenter;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final BpmnEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setContent( final BpmnEditorContent content,
                            final boolean isReadOnly ) {
        label.setText( content.getProcess().toString() );
    }
}