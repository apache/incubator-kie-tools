/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.screens.gadgets;

import javax.annotation.PostConstruct;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartView;

/**
 * Created by Cristiano Nicolai.
 */
public abstract class AbstractGadgetScreen {

    private final Frame frame = new Frame();
    private final ScriptElement sce = Document.get().createScriptElement();
    private final String url;

    public AbstractGadgetScreen( String url ) {
        this.url = url;
    }

    @PostConstruct
    public void init() {
        sce.setType( "text/javascript" );
        sce.setSrc( url );

        frame.setWidth( "100%" );
        frame.setHeight( "300px" );
        frame.getElement().getStyle().setBorderWidth( 0, Style.Unit.PX );

        final FrameElement fe = frame.getElement().cast();
        frame.addAttachHandler( new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach( AttachEvent event ) {
                fe.getContentDocument().getBody().appendChild( sce );
            }
        } );
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return frame;
    }
}
