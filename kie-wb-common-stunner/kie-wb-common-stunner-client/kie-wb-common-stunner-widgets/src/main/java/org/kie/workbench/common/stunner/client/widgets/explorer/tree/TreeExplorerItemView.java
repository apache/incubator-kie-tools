/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.explorer.tree;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Heading;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

import javax.enterprise.context.Dependent;

@Dependent
public class TreeExplorerItemView extends Composite implements TreeExplorerItem.View {

    interface ViewBinder extends UiBinder<Widget, TreeExplorerItemView> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    private TreeExplorerItem presenter;
    private LienzoPanel lienzoPanel;
    private final Layer lienzoLayer = new Layer();

    @UiField
    SimplePanel glyphPanel;

    @UiField
    Heading name;

    @UiField
    HTML uuid;

    @Override
    public void init( final TreeExplorerItem presenter ) {
        this.presenter = presenter;
        initWidget( uiBinder.createAndBindUi( this ) );
        lienzoLayer.setTransformable( true );
    }

    @Override
    public TreeExplorerItem.View setUUID( final String uuid ) {
        final String t = "[" + uuid + "]";
        this.uuid.setText( t );
        this.uuid.setTitle( uuid );
        return this;
    }

    @Override
    public TreeExplorerItem.View setName( final String name ) {
        this.name.setText( name );
        this.name.setTitle( name );
        return this;
    }

    @Override
    public TreeExplorerItem.View setGlyph( final Glyph<Group> glyph ) {
        initLienzoPanel( glyph.getWidth(), glyph.getHeight() );
        lienzoLayer.add( glyph.getGroup() );
        return this;
    }

    private void initLienzoPanel( final double width, final double height ) {
        lienzoPanel = new LienzoPanel( ( int ) width, ( int ) height );
        lienzoPanel.add( lienzoLayer );
        glyphPanel.add( lienzoPanel );
    }

}
