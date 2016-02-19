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
