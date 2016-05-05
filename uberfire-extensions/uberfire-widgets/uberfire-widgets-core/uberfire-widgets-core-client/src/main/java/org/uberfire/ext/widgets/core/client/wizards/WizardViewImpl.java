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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.NavPills;
import org.gwtbootstrap3.client.ui.base.modal.ModalDialog;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

/**
 * The generic Wizard view implementation
 */
@Dependent
public class WizardViewImpl extends BaseModal
        implements
        WizardView {

    @Inject
    private SyncBeanManager iocBeanManager;

    @UiField
    protected NavPills sideBar;

    @UiField
    protected Column body;

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

        setBody( uiBinder.createAndBindUi( WizardViewImpl.this ) );

        add( footer );
    }

    @Override
    public void init( final AbstractWizard presenter ) {
        this.presenter = presenter;
    }

    @Override
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
        final SyncBeanDef<WizardPageTitle> beanDefinition = iocBeanManager.lookupBean( WizardPageTitle.class );
        final WizardPageTitle bean = beanDefinition.getInstance();
        bean.setContent( page );
        return bean;
    }

    @Override
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

    @Override
    public void setBodyWidget( final Widget w ) {
        body.clear();
        body.add( w );
    }

    @Override
    public void setPreferredHeight( final int height ) {
        if( getWidgetCount() == 1 && getWidget( 0 ) instanceof ModalDialog ){
            this.getWidget( 0 ).setHeight( height + "px" );
        }
    }

    @Override
    public void setPreferredWidth( final int width ) {
        setWidth( width + "px" );
    }

    @Override
    public void setPageCompletionState( final int pageIndex,
                                        final boolean isComplete ) {
        final WizardPageTitle wpt = this.pageTitleWidgets.get( pageIndex );
        wpt.setComplete( isComplete );
    }

    @Override
    public void setCompletionStatus( final boolean isComplete ) {
        footer.enableFinishButton( isComplete );
    }

    @Override
    public void show() {
        super.show();
    }

}
