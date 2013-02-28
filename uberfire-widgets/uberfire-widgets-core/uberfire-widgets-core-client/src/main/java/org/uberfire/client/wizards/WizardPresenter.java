/*
 * Copyright 2011 JBoss Inc
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

package org.uberfire.client.wizards;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;

/**
 * The generic "Wizard" container, providing a left-hand side list of Page
 * titles, buttons to navigate the Wizard pages and a mechanism to display
 * different pages of the Wizard.
 */
@ApplicationScoped
public class WizardPresenter implements
                             WizardView.Presenter {

    @Inject
    //The generic view
    private WizardView view;

    //The specific "page factory" for a particular Wizard
    private Wizard<? extends WizardContext> wizard;

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    //Update the status of each belonging to this Wizard
    public void onStatusChange( final @Observes WizardPageStatusChangeEvent event ) {
        //It is possible that this event is raised by the Wizard implementation before the start method has been called
        if ( wizard == null ) {
            return;
        }
        for ( WizardPage wp : wizard.getPages() ) {
            final int index = wizard.getPages().indexOf( wp );
            view.setPageCompletionState( index,
                                         wp.isComplete() );
        }

        //Update the status of this Wizard
        view.setCompletionStatus(wizard.isComplete());
    }

    public void onPageSelected( final @Observes WizardPageSelectedEvent event ) {
        //It is possible that this event is raised by the Wizard implementation before the start method has been called
        if ( wizard == null ) {
            return;
        }
        final WizardPage page = event.getSelectedPage();
        final int index = wizard.getPages().indexOf( page );
        view.selectPage(index);
    }

    public void start( final Wizard<? extends WizardContext> wizard ) {

        this.wizard = wizard;

        //Go, Go gadget Wizard!
        view.setTitle( wizard.getTitle() );
        view.setPreferredHeight( wizard.getPreferredHeight() );
        view.setPreferredWidth( wizard.getPreferredWidth() );
        view.setPageTitles( wizard.getPages() );
        view.show();
        view.selectPage(0);
    }

    public void pageSelected( final int pageNumber ) {
        final Widget w = wizard.getPageWidget( pageNumber );
        view.setBodyWidget(w);
    }

    public void complete() {
        wizard.complete();
    }

    public void hide() {
        view.hide();
    }

}
