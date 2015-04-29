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
package org.uberfire.ext.widgets.common.client.common.popups;

import java.util.Iterator;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Close;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.event.ShownEvent;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for modal popup implementations. Setting the following properties by default:
 * <ul>
 * <li>setMaxHeight( ( Window.getClientHeight() * 0.75 ) + "px" );</li>
 * <li>setBackdrop( {@link BackdropType#STATIC} );</li>
 * <li>setKeyboard( true );</li>
 * <li>setAnimation( true );</li>
 * <li>setDynamicSafe( true );</li>
 * <li>setHideOthers( false );</li>
 * </ul>
 * <p/>
 * Furthermore this Modal provides:
 * <ul>
 * <li>Automatic focus to the first Focusable widget in the body</li>
 * <li>Automatic invocation of the first Button's ClickHandler where {@link ButtonType}==PRIMARY when &lt;enter&gt; is
 * pressed</li>
 * </ul>
 */
public class BaseModal extends Modal {

    public BaseModal() {
        setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );
        setHideOthers( false );
        setShowHandler();
        setKeyPressHandler();
    }

    private void setKeyPressHandler() {
        this.addDomHandler( getEnterDomHandler(), KeyDownEvent.getType() );
    }

    protected KeyDownHandler getEnterDomHandler() {
        return new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    if ( handleDefaultAction() ) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                }
            }
        };
    }

    private void setShowHandler() {
        //Setting Focus in show() doesn't work so set after Modal is shown
        addShownHandler( new ShownHandler() {
            @Override
            public void onShown( ShownEvent shownEvent ) {
                setFocus( BaseModal.this,
                          Boolean.FALSE );
            }
        } );
    }

    //Set focus on first widget. Ideally we'd only scan the body of the Modal but this is
    //not accessible from sub-classes so we ignore some Focusable elements in the Header
    protected boolean setFocus( final HasWidgets container,
                                Boolean found ) {
        final Iterator<Widget> i = container.iterator();
        while ( i.hasNext() ) {
            final Widget w = i.next();
            if ( w instanceof Close ) {
                continue;
            } else if ( w instanceof Focusable ) {
                ( (Focusable) w ).setFocus( true );
                found = true;
            } else if ( w instanceof HasWidgets ) {
                found = setFocus( ( (HasWidgets) w ),
                                  found );
            }
            if ( Boolean.TRUE.equals( found ) ) {
                break;
            }
        }
        return found;
    }

    //When <enter> is pressed look for a PRIMARY button in the ModalFooters and click it
    protected boolean handleDefaultAction() {
        for ( Widget w : getChildren() ) {
            if ( w instanceof ModalFooter ) {
                final ModalFooter footer = (ModalFooter) w;
                return handleModalFooter( footer );
            }
        }
        return false;
    }

    private boolean handleModalFooter( final ModalFooter footer ) {
        final Iterator<Widget> iterator = footer.iterator();
        while ( iterator.hasNext() ) {
            final Widget fw = iterator.next();
            //Many of our standard ModalFooters embed a ModalFooter within a ModalFooter
            if ( fw instanceof ModalFooter ) {
                return handleModalFooter( ( (ModalFooter) fw ) );
            } else if ( fw instanceof Button ) {
                final Button b = (Button) fw;
                if ( b.getType().equals( ButtonType.PRIMARY ) ) {
                    b.fireEvent( new ClickEvent() {
                    } );
                    return true;
                }
            }
        }
        return false;
    }

}
