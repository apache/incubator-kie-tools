/*
 *
 *  * Copyright 2015 JBoss Inc
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

package org.uberfire.client.views.pfly.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Popover;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Placement;

/**
 * Created by Cristiano Nicolai.
 */
public class HelpIcon extends Composite {

    private final Icon icon = new Icon( IconType.INFO_CIRCLE );
    private final SimplePanel panel = new SimplePanel();
    private String title;
    private String content;

    public HelpIcon() {
        initWidget( panel );
        addStyleName( "uf-help-icon" );
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if ( title != null || content != null ) {
            final Popover popover = new Popover( icon );
            popover.setContent( content );
            popover.setTitle( title );
            popover.setContainer( "body" );
            popover.setIsHtml( true );
            popover.setPlacement( Placement.AUTO );
            panel.setWidget( popover );
        }
    }

    public void setHelpTitle( final String title ) {
        this.title = title;
    }

    public void setHelpContent( final String content ) {
        this.content = content;
    }

}
