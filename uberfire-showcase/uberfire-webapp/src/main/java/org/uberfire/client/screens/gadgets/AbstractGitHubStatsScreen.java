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

package org.uberfire.client.screens.gadgets;

import javax.annotation.PostConstruct;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.html.Div;
import org.uberfire.client.annotations.WorkbenchPartView;

/**
 * Created by Cristiano Nicolai.
 */
public abstract class AbstractGitHubStatsScreen extends Composite implements RequiresResize {

    private static final int PADDING = 30;

    protected Div panel = new Div();

    protected JavaScriptObject graph;

    protected boolean graphInitialized = false;

    @PostConstruct
    protected void setup() {
        final String id = Document.get().createUniqueId();
        panel.setId( id );
        panel.addStyleName( "text-center" );
        initWidget( panel );
        panel.addAttachHandler( new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach( final AttachEvent event ) {
                if ( event.isAttached() && graph == null ) {
                    graph = generateGraph( panel.getId() );
                }
            }
        } );
    }

    protected int getPadding() {
        return PADDING;
    }

    public void initGraph() {
        graphInitialized = true;
        onResize();
    }

    @WorkbenchPartView
    public Widget getView() {
        return this;
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width, height );
        final int padding = getPadding();
        int graphHeight = height < padding ? height : height - padding;
        int graphWidth = width < padding ? width : width - padding;
        if ( graphInitialized && graph != null ) {
            resizeGraph( graph, graphHeight, graphWidth );
        }
    }

    public native void resizeGraph( JavaScriptObject graph, int graphHeight, int graphWidth )/*-{
        graph.resize({
            height: graphHeight,
            width: graphWidth
        });
    }-*/;

    public abstract JavaScriptObject generateGraph( final String id );
}

