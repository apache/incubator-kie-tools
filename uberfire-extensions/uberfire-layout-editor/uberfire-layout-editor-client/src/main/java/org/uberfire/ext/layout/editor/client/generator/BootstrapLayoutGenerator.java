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
package org.uberfire.ext.layout.editor.client.generator;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.ComplexPanel;
import org.gwtbootstrap3.client.ui.Container;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.util.DragTypeBeanResolver;

/**
 * A bootstrap based layout generator
 */
@Default
@Dependent
public class BootstrapLayoutGenerator extends AbstractLayoutGenerator {

    @Inject
    private DragTypeBeanResolver dragTypeBeanResolver;

    @Override
    public ComplexPanel getLayoutContainer() {
        Container mainPanel = new Container();
        mainPanel.getElement().setId( "mainContainer" );
        return mainPanel;
    }

    @Override
    public LayoutDragComponent getLayoutDragComponent( LayoutComponent layoutComponent ) {
        return dragTypeBeanResolver.lookupDragTypeBean( layoutComponent.getDragTypeName() );
    }
}
