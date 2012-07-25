/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.moshpit;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * An example editor with a fixed size that shows it's size
 */
public class DebugLabel extends VerticalPanel
    implements
    RequiresResize {

    private Label label = new Label();

    public DebugLabel() {
        setPixelSize( 200,
                      200 );
        getElement().getStyle().setBackgroundColor( "#90f0e0" );
        add( label );
    }

    @Override
    public void onResize() {
        label.setText( "(w:" + getParent().getOffsetWidth() + ", h:" + getParent().getOffsetHeight() + ")" );
    }

}
