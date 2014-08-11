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

package org.kie.uberfire.client.wizards;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.uberfire.client.common.popups.KieBaseModal;

/**
 * The generic Wizard view implementation
 */
@Dependent
public class WizardViewImpl extends KieBaseModal
        implements
        WizardView {

    @Inject
    private SyncBeanManager iocBeanManager;

    @UiField
    protected VerticalPanel sideBar;

    @UiField
    protected SimplePanel sideBarContainer;

    @UiField
    protected SimplePanel body;

    @UiField
    protected ScrollPanel bodyContainer;

    protected WizardPopupFooter footer;

    private List<WizardPageTitle> pageTitleWidgets = new ArrayList<WizardPageTitle>();

    private int pageNumber;
    private int pageNumberTotal;

    private AbstractWizard presenter;

    interface WizardActivityViewImplBinder
            extends
            UiBinder<Widget, WizardViewImpl> {

    }

    private static WizardActivityViewImplBinder uiBinder = GWT.create( WizardActivityViewImplBinder.class );

    public WizardViewImpl() {
        footer = new WizardPopupFooter(
                new Command() {
                    @Override
                    public void execute() {
                        if ( pageNumber == 0 ) {
                            return;
                        }
                        selectPage( pageNumber - 1 );
                        footer.setPreviousButtonFocus( false );
                    }
                },
                new Command() {
                    @Override
                    public void execute() {
                        if ( pageNumber == pageNumberTotal - 1 ) {
                            return;
                        }
                        selectPage( pageNumber + 1 );
                        footer.setNextButtonFocus( false );
                    }
                },
                new Command() {
                    @Override
                    public void execute() {
                        presenter.close();
                    }
                },
                new Command() {
                    @Override
                    public void execute() {
                        presenter.complete();
                    }
                }
        );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );
    }

    @Override
    public void init( final AbstractWizard presenter ) {
        this.presenter = presenter;
    }

    public void setPageTitles( final List<WizardPage> pages ) {
        //Clear existing titles
        releaseWizardPageTitles();
        sideBar.clear();

        //Add new titles for pages
        this.pageNumberTotal = pages.size();
        for ( WizardPage page : pages ) {
            final WizardPageTitle wpt = makeWizardPageTitle( page );
            pageTitleWidgets.add( wpt );
            sideBar.add( wpt );
        }
    }

    private void releaseWizardPageTitles() {
        for ( WizardPageTitle wpt : pageTitleWidgets ) {
            iocBeanManager.destroyBean( wpt );
        }
        pageTitleWidgets.clear();
    }

    private WizardPageTitle makeWizardPageTitle( final WizardPage page ) {
        final IOCBeanDef<WizardPageTitle> beanDefinition = iocBeanManager.lookupBean( WizardPageTitle.class );
        final WizardPageTitle bean = beanDefinition.getInstance();
        bean.setContent( page );
        return bean;
    }

    public void selectPage( final int pageNumber ) {
        if ( pageNumber < 0 || pageNumber > pageNumberTotal - 1 ) {
            return;
        }
        this.pageNumber = pageNumber;
        for ( int i = 0; i < this.pageTitleWidgets.size(); i++ ) {
            final WizardPageTitle wpt = this.pageTitleWidgets.get( i );
            wpt.setPageSelected( i == pageNumber );
        }
        footer.enableNextButton( pageNumber < pageNumberTotal - 1 );
        footer.enablePreviousButton( pageNumber > 0 );
        presenter.pageSelected( pageNumber );
    }

    public void setBodyWidget( final Widget w ) {
        body.setWidget( w );
    }

    public void setPreferredHeight( final int height ) {
        sideBarContainer.setHeight( height + "px" );
        bodyContainer.setHeight( height + "px" );
    }

    public void setPreferredWidth( final int width ) {
        bodyContainer.setWidth( width + "px" );
        //Sidebar is 200px and GWT-Bootstraps Modal has padding of 15px (left and right)
        setWidth( width + 230 );
    }

    public void setPageCompletionState( final int pageIndex,
                                        final boolean isComplete ) {
        final WizardPageTitle wpt = this.pageTitleWidgets.get( pageIndex );
        wpt.setComplete( isComplete );
    }

    public void setCompletionStatus( final boolean isComplete ) {
        footer.enableFinishButton( isComplete );
    }

    @Override
    public void show() {
        super.show();
        centerHorizontally( getElement() );
    }

    /**
     * Centers fixed positioned element horizontally.
     * @param e Element to center horizontally
     */
    private native void centerHorizontally( Element e ) /*-{
        $wnd.jQuery(e).css("margin-left", (-1 * $wnd.jQuery(e).outerWidth() / 2) + "px");
    }-*/;
}
