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
package org.uberfire.client.workbench.panels;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;

public class UFFlowPanel extends FlowPanel {

    /**
     * Creates a panel with relative positioning that fills its nearest relative or absolute positioned parent.
     */
    public UFFlowPanel() {
        getElement().getStyle().setPosition( Position.RELATIVE );
        getElement().getStyle().setWidth( 100, Unit.PCT );
        getElement().getStyle().setHeight( 100, Unit.PCT );
    }

    /**
     * Creates a panel with relative positioning that has the given fixed height in pixels, and fills the width of its
     * nearest relative or absolute positioned parent.
     */
    public UFFlowPanel( int height ) {
        getElement().getStyle().setPosition( Position.RELATIVE );
        getElement().getStyle().setWidth( 100, Unit.PCT );
        getElement().getStyle().setHeight( height, Unit.PX );
    }

    /**
     * Creates a panel with relative positioning that has the given fixed width and height in pixels.
     */
    public UFFlowPanel( int width,
                        int height ) {
        getElement().getStyle().setPosition( Position.RELATIVE );
        getElement().getStyle().setWidth( width, Unit.PX );
        getElement().getStyle().setHeight( height, Unit.PX );
    }

}