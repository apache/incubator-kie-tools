/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationwizard;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;

@Dependent
public class SearchAnnotationPageViewImpl
        extends Composite
        implements SearchAnnotationPageView {

    interface SearchAnnotationPageViewImplBinder extends UiBinder<Widget, SearchAnnotationPageViewImpl> {

    }

    private static SearchAnnotationPageViewImplBinder uiBinder = GWT.create( SearchAnnotationPageViewImplBinder.class );

    private Presenter presenter;

    @UiField
    TextBox annotationClassName;

    @UiField
    HelpBlock annotationClassNameHelpInline;

    @UiField
    Button searchAnnotationButton;

    public SearchAnnotationPageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        searchAnnotationButton.setTitle( Constants.INSTANCE.advanced_domain_wizard_search_page_search_button_tooltip() );
        annotationClassName.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                presenter.onSearchClassChanged();
            }
        } );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getClassName() {
        return annotationClassName.getText();
    }

    @Override
    public void setClassName( String className ) {
        annotationClassName.setText( className );
    }

    @Override
    public void clearHelpMessage() {
        annotationClassNameHelpInline.setText( null );
    }

    @Override
    public void setHelpMessage( String helpMessage ) {
        annotationClassNameHelpInline.setText( helpMessage );
    }

    @UiHandler("searchAnnotationButton")
    void onSearchAnnotationClicked( ClickEvent event ) {
        presenter.onSearchClass();
    }
}
