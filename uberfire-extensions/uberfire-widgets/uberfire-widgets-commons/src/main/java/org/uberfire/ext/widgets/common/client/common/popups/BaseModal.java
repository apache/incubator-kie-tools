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
package org.uberfire.ext.widgets.common.client.common.popups;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.ModalShowEvent;
import org.gwtbootstrap3.client.shared.event.ModalShowHandler;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.base.button.CloseButton;
import org.gwtbootstrap3.client.ui.base.modal.ModalContent;
import org.gwtbootstrap3.client.ui.base.modal.ModalDialog;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;

/**
 * Base class for modal popup implementations. Setting the following properties by default:
 * <ul>
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


    private ModalBody body;

    public BaseModal() {
        setDataBackdrop( ModalBackdrop.STATIC );
        setDataKeyboard( true );
        setRemoveOnHide( true );
        setHideOtherModals( false );
        setShowHandler();
        setKeyPressHandler();
        getElement().setId( "panel-id" );
        addShowHandler( new ModalShowHandler() {
            @Override
            public void onShow( ModalShowEvent evt ) {
                Modal modal = evt.getModal();
                modal.getElement().setAttribute( "maxHeight", "100px" );
                modal.getElement().setAttribute( "overflowY", "scroll" );
            }
        } );
    }

    @Override
    public void show() {
        super.show();
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
        addShownHandler( new ModalShownHandler() {
            @Override
            public void onShown( ModalShownEvent shownEvent ) {
                setFocus( BaseModal.this,
                          Boolean.FALSE );
            }
        } );
    }

    //Set focus on first widget. Ideally we'd only scan the body of the Modal but this is
    //not accessible from sub-classes so we ignore some Focusable elements in the Header
    protected boolean setFocus( final HasWidgets container,
                                Boolean found ) {
        for ( final Widget w : container ) {
            if ( w instanceof CloseButton ) {
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
        return handleDefaultAction( this );
    }

    protected <T extends ComplexPanel> boolean handleDefaultAction( final T panel ) {
        for ( int i = 0; i < panel.getWidgetCount(); i++ ) {
            final Widget w = panel.getWidget( i );
            if ( w instanceof ModalFooter ) {
                return handleModalFooter( (ModalFooter) w );
            } else if ( w instanceof ModalDialog ) {
                return handleDefaultAction( (ModalDialog) w );
            } else if ( w instanceof ModalContent ) {
                return handleDefaultAction( (ModalContent) w );
            }
        }
        return false;
    }

    private boolean handleModalFooter( final ModalFooter footer ) {
        for ( final Widget fw : footer ) {
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

    public void setBody( final Widget widget ){
        final ModalBody body = new ModalBody();
        body.add( widget );
        this.add( body );
    }

}
