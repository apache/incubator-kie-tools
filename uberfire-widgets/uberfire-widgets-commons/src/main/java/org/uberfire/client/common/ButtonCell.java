/*
 * Copyright 2014 JBoss Inc
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
package org.uberfire.client.common;

import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class ButtonCell extends com.github.gwtbootstrap.client.ui.ButtonCell {

    private boolean isEnabled;
    private String disabledString = "";

    public ButtonCell() {
    }

    public ButtonCell( final ButtonType type ) {
        super( type );
    }

    public ButtonCell( final IconType icon ) {
        super( icon );
    }

    public ButtonCell( final ButtonSize size ) {
        super( size );
    }

    public ButtonCell( final IconType icon,
                       final ButtonType type ) {
        super( icon,
               type );
    }

    public ButtonCell( final IconType icon,
                       final ButtonSize size ) {
        super( icon,
               size );
    }

    public ButtonCell( final ButtonType type,
                       final ButtonSize size ) {
        super( type,
               size );
    }

    public ButtonCell( final IconType icon,
                       final ButtonType type,
                       final ButtonSize size ) {
        super( icon,
               type,
               size );
    }

    @Override
    public void render( final Context context,
                        final SafeHtml data,
                        final SafeHtmlBuilder sb ) {
        final ButtonType type = getType();
        final ButtonSize size = getSize();
        final IconType icon = getIcon();
        sb.appendHtmlConstant( "<button type=\"button\" class=\"btn " + ( type != null ? type.get() : "" ) + ( size != null ? " " + size.get() : "" ) + "\" tabindex=\"-1\"" + disabledString + ">" );
        if ( data != null ) {
            if ( icon != null ) {
                sb.appendHtmlConstant( "<i class=\"" + icon.get() + "\"></i> " );
            }
            sb.append( data );
        }
        sb.appendHtmlConstant( "</button>" );
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled( final boolean isEnabled ) {
        this.isEnabled = isEnabled;
        if ( isEnabled ) {
            disabledString = "";
        } else {
            disabledString = "disabled=\"disabled\"";
        }
    }

}
