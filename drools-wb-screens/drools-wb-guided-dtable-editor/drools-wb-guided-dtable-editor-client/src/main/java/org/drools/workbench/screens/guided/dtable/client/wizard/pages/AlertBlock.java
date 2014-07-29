/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.wizard.pages;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.base.AlertBase;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style;

/**
 * An AlertBlock that can show an icon beside the text
 */
public class AlertBlock extends AlertBase {

    private Icon icon = new Icon();

    public AlertBlock() {
        super();
        setup();
    }

    public AlertBlock( final String html ) {
        super( html );
        setup();
    }

    public AlertBlock( final String html,
                       final boolean hasClose ) {
        super( html,
               hasClose );
        setup();
    }

    public AlertBlock( final AlertType type ) {
        super( type );
        setup();
    }

    private void setup() {
        super.addStyleName( Constants.ALERT_BLOCK );
        getHeadingContainer().add( icon );
        icon.getElement().getStyle().setMarginRight( 5,
                                                     Style.Unit.PX );
    }

    public void setIconType( final IconType type ) {
        icon.setType( type );
    }

    public void setIconSize( final IconSize size ) {
        icon.setIconSize( size );
    }

}
