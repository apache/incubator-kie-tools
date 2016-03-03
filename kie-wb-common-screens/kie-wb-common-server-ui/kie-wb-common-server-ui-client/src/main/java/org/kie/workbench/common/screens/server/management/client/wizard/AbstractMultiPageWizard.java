/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client.wizard;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

public abstract class AbstractMultiPageWizard extends AbstractWizard {

    protected final ArrayList<WizardPage> pages = new ArrayList<WizardPage>();
    private int selectedPage = 0;

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        final int[] unCompletedPages = { this.pages.size() };

        //only when all pages are complete we can say the wizard is complete.
        for ( WizardPage page : this.pages ) {
            page.isComplete( new Callback<Boolean>() {
                @Override
                public void callback( final Boolean result ) {
                    if ( Boolean.TRUE.equals( result ) ) {
                        unCompletedPages[ 0 ]--;
                        if ( unCompletedPages[ 0 ] == 0 ) {
                            callback.callback( true );
                        }
                    } else {
                        callback.callback( false );
                    }
                }
            } );
        }
    }

    @Override
    public List<WizardPage> getPages() {
        return pages;
    }

    @Override
    public Widget getPageWidget( final int pageNumber ) {
        return pages.get( pageNumber ).asWidget();
    }

    @Override
    public void pageSelected( final int pageNumber ) {
        this.selectedPage = pageNumber;
        super.pageSelected( pageNumber );
    }

    public int getSelectedPage() {
        return selectedPage;
    }

}
