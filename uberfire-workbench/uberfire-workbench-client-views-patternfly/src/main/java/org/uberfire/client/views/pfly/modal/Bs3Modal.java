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

package org.uberfire.client.views.pfly.modal;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.Attributes;
import org.gwtbootstrap3.client.ui.constants.ButtonDismiss;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * A modal dialog that floats above the workbench. Each instance can only be shown once.
 */
@Dependent
public class Bs3Modal extends Modal {

    private final ModalBody body = GWT.create(  ModalBody.class );

    /**
     * Used for enforcing the "only show one time" rule.
     */
    boolean hasBeenShown;

    public Bs3Modal() {
        this.add( body );
        this.setDataBackdrop( ModalBackdrop.STATIC );
        this.setFade( true );
        this.getElement().setAttribute( Attributes.ROLE, "dialog" );
        this.getElement().setAttribute( Attributes.TABINDEX, "-1" );
        this.addStyleName( WorkbenchResources.INSTANCE.CSS().modal() );
        this.setId( DOM.createUniqueId() );
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        initFooter( this.getId() );
    }

    private native void initFooter( final String id ) /*-{
        var footer = $wnd.jQuery( '#' + id + ' .modal-footer' );
        if( footer.length == 0 ){
            this.@org.uberfire.client.views.pfly.modal.Bs3Modal::addDefaultFooter()();
        }
    }-*/;


    /**
     * Shows this modal dialog above the current workbench.
     * @param afterShown the action to perform once the dialog has been shown. Not null. Use {@link Commands#DO_NOTHING} if you don't have an "after show" action.
     * @param afterClosed the action to perform once the dialog has been dismissed. Not null. Use {@link Commands#DO_NOTHING} if you don't have an "after close" action.
     */
    public void show( final Command afterShown,
                      final Command afterClosed ) {

        checkNotNull( "afterShown", afterShown );
        checkNotNull( "afterClosed", afterClosed );
        this.addShownHandler( new ModalShownHandler() {
            @Override
            public void onShown( final ModalShownEvent showEvent ) {
                if ( afterShown != null ) {
                    afterShown.execute();
                }
            }
        } );
        this.addHiddenHandler( new ModalHiddenHandler() {
            @Override
            public void onHidden( final ModalHiddenEvent hiddenEvent ) {
                if ( afterClosed != null ) {
                    afterClosed.execute();
                }
            }
        } );
        this.show();
    }

    @Override
    public void show() {
        if ( hasBeenShown ) {
            throw new IllegalStateException( "This modal has already been shown. Create a new instance if you want to show another modal." );
        }
        super.show();
    }

    /**
     * Replaces the contents within the main body area of the modal. By default, the main body area is empty.
     * @param content the new content for the main body area.
     */
    public void setContent( IsWidget content ) {
        body.clear();
        body.add( content );
    }

    protected void addDefaultFooter(){
        final Button close = GWT.create( Button.class );
        close.setText( "OK" );
        close.setDataDismiss( ButtonDismiss.MODAL );
        close.setType( ButtonType.PRIMARY );
        setFooterContent( close );
    }

    public void setModalTitle( final String title ) {
        this.setTitle( SafeHtmlUtils.htmlEscape( title ) );
    }

    /**
     * Replaces the current contents of the footer area with the given widget. By default (if you do not call this
     * method), the footer contains an OK button that dismisses the dialog when clicked.
     * @param content the new content for the footer area.
     */
    public void setFooterContent( IsWidget content ) {
        final ModalFooter footer = GWT.create(  ModalFooter.class );
        this.add( footer );
        footer.add( content );
    }

    /**
     * Sets the pixel height of the main content container.
     */
    public void setBodyHeight( int height ) {
        body.setHeight( height + "px" );
    }

}
