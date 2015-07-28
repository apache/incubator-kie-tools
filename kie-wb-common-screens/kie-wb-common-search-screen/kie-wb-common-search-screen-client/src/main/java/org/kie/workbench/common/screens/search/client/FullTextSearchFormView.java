/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.screens.search.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.screens.search.client.widgets.SearchResultTable;
import org.kie.workbench.common.screens.search.model.SearchTermPageRequest;

@Dependent
public class FullTextSearchFormView
        extends Composite
        implements FullTextSearchFormPresenter.View {

    interface FullTextSearchFormBinder
            extends
            UiBinder<Widget, FullTextSearchFormView> {

    }

    private static FullTextSearchFormBinder uiBinder = GWT.create( FullTextSearchFormBinder.class );

    @UiField
    SimplePanel resultPanel;

    @UiField
    TextBox termTextBox;

    private FullTextSearchFormPresenter presenter;

    private String text = null;

    @Override
    public void init( final FullTextSearchFormPresenter presenter ) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setSearchTerm( final String term ) {
        termTextBox.setText( term );
        search();
    }

    @UiHandler("search")
    public void onSearchClick( final ClickEvent e ) {
        search();
    }

    private void search() {
        presenter.setTitle( termTextBox.getText().trim() );

        resultPanel.clear();

        final String term = termTextBox.getText().trim();
        if ( term.isEmpty() ) {
            resultPanel.add( new SearchResultTable() );
            return;
        }

        resultPanel.add( new SearchResultTable( new SearchTermPageRequest( term, 0, null ) ) );
    }
}
