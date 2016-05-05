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
package org.uberfire.ext.widgets.common.client.accordion;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.HiddenEvent;
import org.gwtbootstrap3.client.shared.event.HiddenHandler;
import org.gwtbootstrap3.client.shared.event.ShowEvent;
import org.gwtbootstrap3.client.shared.event.ShowHandler;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.base.AbstractTextWidget;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Strong;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

/**
 * Trigger Widget for ResourceType groups
 */
public class TriggerWidget extends PanelHeader {

    private final FlexTable table = new FlexTable();
    private final Icon caret;

    public TriggerWidget( final String description ) {
        this( description, true );
    }

    public TriggerWidget( final String description,
                          final boolean isCaretDown ) {
        getElement().getStyle().setCursor( Style.Cursor.POINTER );
        caret = makeIcon( isCaretDown ? IconType.CARET_DOWN : IconType.CARET_UP, CommonConstants.INSTANCE.ClickToDisplay0( description ) );
        table.setWidget( 0, 0, buildDescription( description ) );
        table.setHTML( 0, 1, "&nbsp;&nbsp;" );
        table.setWidget( 0, 2, caret );
        insert( table, 0 );
    }

    public TriggerWidget( final IsWidget icon,
                          final String description ) {
        this( icon, description, true );
    }

    public TriggerWidget( final IsWidget icon,
                          final String description,
                          boolean isCaretDown ) {
        getElement().getStyle().setCursor( Style.Cursor.POINTER );
        caret = makeIcon( isCaretDown ? IconType.CARET_DOWN : IconType.CARET_UP, CommonConstants.INSTANCE.ClickToDisplay0( description ) );
        if ( icon == null ) {
            table.setWidget( 0, 0, buildDescription( description ) );
            table.setHTML( 0, 1, "&nbsp;&nbsp;" );
            table.setWidget( 0, 2, caret );
        } else {
            table.setWidget( 0, 0, icon );
            table.setHTML( 0, 1, "&nbsp;&nbsp;" );
            table.setWidget( 0, 2, buildDescription( description ) );
            table.setHTML( 0, 3, "&nbsp;&nbsp;" );
            table.setWidget( 0, 4, caret );
        }
        insert( table, 0 );
    }

    @Override
    public void setDataTargetWidget( final Widget widget ) {
        super.setDataTargetWidget( widget );
        if ( widget instanceof PanelCollapse ) {
            final PanelCollapse collapse = (PanelCollapse) widget;
            collapse.addShowHandler( new ShowHandler() {
                @Override
                public void onShow( ShowEvent showEvent ) {
                    caret.setType( IconType.CARET_UP );
                }
            } );
            collapse.addHiddenHandler( new HiddenHandler() {
                @Override
                public void onHidden( HiddenEvent event ) {
                    caret.setType( IconType.CARET_DOWN );
                }
            } );
        }
    }

    private Icon makeIcon( final IconType iconType,
                           final String tooltip ) {
        return new Icon( iconType ) {{
            setTitle( tooltip );
        }};
    }

    private Widget buildDescription( final String caption ) {
        return new AbstractTextWidget( Document.get().createSpanElement() ) {{
            addStyleName( "text-uppercase" );
            setTitle( CommonConstants.INSTANCE.ClickToDisplay0( caption ) );
            setHTML( new Strong( caption ).getElement().getString() );
        }};
    }

}
