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

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.common.Popup;

/**
 * The generic Wizard view implementation
 */
@Dependent
public class WizardViewImpl extends Popup
        implements
        WizardView {

    @Inject
    private SyncBeanManager iocBeanManager;

    @UiField
    protected VerticalPanel sideBar;

    @UiField
    protected SimplePanel sideBarContainer;

    @UiField
    ScrollPanel bodyContainer;

    @UiField
    protected SimplePanel body;

    @UiField
    protected Button btnNext;

    @UiField
    protected Button btnPrevious;

    @UiField
    protected Button btnFinish;

    private Widget content;
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
        content = uiBinder.createAndBindUi( this );
    }

    @Override
    public void init( final AbstractWizard presenter ) {
        this.presenter = presenter;
    }

    @Override
    public Widget getContent() {
        return this.content;
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

    @UiHandler(value = "btnCancel")
    public void btnCancelClick( final ClickEvent event ) {
        presenter.close();
    }

    @UiHandler(value = "btnFinish")
    public void btnFinishClick( final ClickEvent event ) {
        presenter.complete();
    }

    @UiHandler(value = "btnNext")
    public void btnNextClick( final ClickEvent event ) {
        if ( pageNumber == pageNumberTotal - 1 ) {
            return;
        }
        selectPage( pageNumber + 1 );
        btnNext.setFocus( false );
    }

    @UiHandler(value = "btnPrevious")
    public void btnPreviousClick( final ClickEvent event ) {
        if ( pageNumber == 0 ) {
            return;
        }
        selectPage( pageNumber - 1 );
        btnPrevious.setFocus( false );
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
        btnNext.setEnabled( pageNumber < pageNumberTotal - 1 );
        btnPrevious.setEnabled( pageNumber > 0 );
        presenter.pageSelected( pageNumber );
    }

    public void setBodyWidget( final Widget w ) {
        body.setWidget( w );
        center();
    }

    public void setPreferredHeight( final int height ) {
        bodyContainer.setHeight( height + "px" );
        sideBarContainer.setHeight( height + "px" );
    }

    public void setPreferredWidth( final int width ) {
        bodyContainer.setWidth( width + "px" );
    }

    public void setPageCompletionState( final int pageIndex,
                                        final boolean isComplete ) {
        final WizardPageTitle wpt = this.pageTitleWidgets.get( pageIndex );
        wpt.setComplete( isComplete );
    }

    public void setCompletionStatus( final boolean isComplete ) {
        btnFinish.setEnabled( isComplete );
    }

}
