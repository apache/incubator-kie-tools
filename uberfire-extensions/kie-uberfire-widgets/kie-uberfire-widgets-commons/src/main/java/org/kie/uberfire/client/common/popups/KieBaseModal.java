/*
 * Copyright 2012 JBoss Inc
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
package org.kie.uberfire.client.common.popups;

import java.util.Iterator;

import com.github.gwtbootstrap.client.ui.Close;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base Modal for all KIE Workbench use. Setting the following properties by default:
 * - setMaxHeight( ( Window.getClientHeight() * 0.75 ) + "px" );
 * - setBackdrop( BackdropType.STATIC );
 * - setKeyboard( true );
 * - setAnimation( true );
 * - setDynamicSafe( true );
 * - setHideOthers( false );
 */
public class KieBaseModal extends Modal {

    public KieBaseModal() {
        setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );
        setHideOthers( false );
    }

    @Override
    public void show() {
        super.show();
        setFocus( this );
    }

    //Set focus on first widget
    private boolean setFocus( final HasWidgets container ) {
        final Iterator<Widget> i = container.iterator();
        while ( i.hasNext() ) {
            final Widget w = i.next();
            if ( w instanceof Close ) {
                continue;
            } else if ( w instanceof Focusable ) {
                ( (Focusable) w ).setFocus( true );
                return true;
            } else if ( w instanceof HasWidgets ) {
                return setFocus( ( (HasWidgets) w ) );
            }
        }
        return false;
    }
}
