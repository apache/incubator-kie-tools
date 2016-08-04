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

package org.uberfire.ext.layout.editor.client;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;


@Templated
@Dependent
public class LayoutEditorView
        implements UberElement<LayoutEditorPresenter>,
        LayoutEditorPresenter.View, IsElement {


    @Inject
    @DataField
    Div container;


    @Inject
    @DataField
    Div components;

    private LayoutEditorPresenter presenter;

    @Override
    public void init( LayoutEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupContainer( UberElement<Container> container ) {
        this.container.appendChild( container.getElement() );
    }

    @Override
    public void addDraggableComponentGroup( UberElement<LayoutDragComponentGroupPresenter> group ) {
        components.appendChild( group.getElement() );
    }


    @Override
    public void removeDraggableComponentGroup( UberElement<LayoutDragComponentGroupPresenter> group ) {
        components.removeChild( group.getElement() );
    }

}