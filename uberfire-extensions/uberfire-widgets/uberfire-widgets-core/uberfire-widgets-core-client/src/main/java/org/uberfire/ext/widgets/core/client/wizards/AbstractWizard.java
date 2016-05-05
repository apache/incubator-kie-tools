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

package org.uberfire.ext.widgets.core.client.wizards;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.callbacks.Callback;

/**
 * The generic "Wizard" container, providing a left-hand side list of Page
 * titles, buttons to navigate the Wizard pages and a mechanism to display
 * different pages of the Wizard.
 */
public abstract class AbstractWizard implements
                                     Wizard {

    @Inject
    //The generic view
    protected WizardView view;
    protected boolean isStarted = false;

    @PostConstruct
    public void setup() {
        view.init( this );
    }

    //Update the status of each belonging to this Wizard
    public void onStatusChange( final @Observes WizardPageStatusChangeEvent event ) {
        //Ignore events until the Wizard has been started
        if ( !isStarted ) {
            return;
        }
        //Ensure event belongs to this Wizard
        final List<WizardPage> wps = getPages();
        if ( !wps.contains( event.getPage() ) ) {
            return;
        }

        checkPagesState();
    }

    protected void checkPagesState() {
        final List<WizardPage> wps = getPages();
        for ( WizardPage wp : wps ) {
            final int index = wps.indexOf( wp );
            wp.isComplete( new Callback<Boolean>() {
                @Override
                public void callback( final Boolean result ) {
                    view.setPageCompletionState( index,
                                                 Boolean.TRUE.equals( result ) );
                }
            } );
        }

        //Update the status of this Wizard
        isComplete( new Callback<Boolean>() {
            @Override
            public void callback( final Boolean result ) {
                view.setCompletionStatus( Boolean.TRUE.equals( result ) );
            }
        } );

    }

    public void onPageSelected( final @Observes WizardPageSelectedEvent event ) {
        //Ignore events until the Wizard has been started
        if ( !isStarted ) {
            return;
        }
        final WizardPage page = event.getSelectedPage();
        final int index = getPages().indexOf( page );
        view.selectPage( index );
    }

    @Override
    public void start() {
        //Go, Go gadget Wizard!
        isStarted = true;
        view.setTitle( getTitle() );
        view.setPreferredHeight( getPreferredHeight() );
        view.setPreferredWidth( getPreferredWidth() );
        view.setPageTitles( getPages() );

        //Ensure Wizard's generic Cancel/Finish buttons are set correctly
        checkPagesState();

        view.selectPage( 0 );
        view.show();
    }

    @Override
    public void pageSelected( final int pageNumber ) {
        final Widget w = getPageWidget( pageNumber );
        view.setBodyWidget( w );
    }

    @Override
    public void close() {
        view.hide();
    }

    @Override
    public void complete() {
        view.hide();
    }

}
