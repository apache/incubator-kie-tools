/*
 * Copyright 2013 JBoss Inc
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
package org.kie.uberfire.plugin.client.widget.navigator;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.NavHeader;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Trigger Widget for ResourceType groups
 */
public class TriggerWidget extends Composite {

    FlexTable resourceHeader;

    public TriggerWidget( final String caption ) {
        resourceHeader = new FlexTable();
        initWidget( resourceHeader );
        resourceHeader.setWidget( 0, 0, makeNavHeader( caption ) );
        resourceHeader.setHTML( 0, 1, "&nbsp;&nbsp;" );
        resourceHeader.setWidget( 0, 2, new Icon( IconType.CARET_DOWN ) );
    }

    public TriggerWidget( final IsWidget icon,
                          final String caption ) {
        resourceHeader = new FlexTable();
        initWidget( resourceHeader );
        resourceHeader.setWidget( 0, 0, icon );
        resourceHeader.setHTML( 0, 1, "&nbsp;&nbsp;" );
        resourceHeader.setWidget( 0, 2, makeNavHeader( caption ) );
        resourceHeader.setHTML( 0, 3, "&nbsp;&nbsp;" );
        resourceHeader.setWidget( 0, 4, new Icon( IconType.CARET_DOWN ) );
    }

    private NavHeader makeNavHeader( final String caption ) {
        final NavHeader nh = new NavHeader( caption );
        nh.getElement().getStyle().setFontWeight( Style.FontWeight.BOLD );
        nh.getElement().getStyle().setColor( "#000000" );
        return nh;
    }

}
