/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.widgets.client.source;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class ViewDRLSourceWidget
        extends FlowPanel
        implements RequiresResize {

    private final DrlEditor drlEditor = new DrlEditor();

    public ViewDRLSourceWidget() {
        add( drlEditor );
        drlEditor.setReadOnly( true );
        setWidth( "100%" );
        setHeight( "100%" );
    }

    public void setContent( final String content ) {
        clearContent();
        drlEditor.setText( content );
        onResize();
    }

    public void clearContent() {
        drlEditor.setText( "" );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width,
                      height );
        drlEditor.onResize();
    }

}
