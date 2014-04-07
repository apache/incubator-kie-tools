/*
 * Copyright 2014 JBoss Inc
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

package org.drools.workbench.screens.drltext.client.widget;

import java.util.List;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.screens.drltext.client.editor.DRLEditorPresenter;
import org.drools.workbench.screens.drltext.client.resources.DRLTextEditorResources;
import org.drools.workbench.screens.drltext.client.resources.i18n.DRLTextEditorConstants;
import org.uberfire.client.common.ClickableLabel;
import org.uberfire.client.common.SmallLabel;
import org.uberfire.client.mvp.UberView;

public class DSLSentenceBrowserWidget
        extends Composite implements UberView<DRLEditorPresenter> {

    private DRLEditorPresenter presenter;

    private final Tree tree = new Tree();

    public DSLSentenceBrowserWidget( final ClickEvent ev,
                                     final String showDSLCaption,
                                     final String dslCaption ) {
        final VerticalPanel container = new VerticalPanel();
        final HorizontalPanel hpDSLSentencesAndHide = new HorizontalPanel();
        final HorizontalPanel hpShow = new HorizontalPanel();

        hpShow.add( new ClickableLabel( showDSLCaption,
                                        new ClickHandler() {
                                            public void onClick( com.google.gwt.event.dom.client.ClickEvent event ) {
                                                hpShow.setVisible( false );
                                                hpDSLSentencesAndHide.setVisible( true );
                                                tree.setVisible( true );
                                            }
                                        } ) );
        container.add( hpShow );

        hpDSLSentencesAndHide.add( new SmallLabel( dslCaption ) );
        hpDSLSentencesAndHide.add( new ClickableLabel( DRLTextEditorConstants.INSTANCE.hide(),
                                                       new ClickHandler() {
                                                           public void onClick( com.google.gwt.event.dom.client.ClickEvent event ) {
                                                               hpShow.setVisible( true );
                                                               hpDSLSentencesAndHide.setVisible( false );
                                                               tree.setVisible( false );
                                                           }
                                                       } ) );
        container.add( hpDSLSentencesAndHide );

        container.add( tree );

        tree.setStyleName( DRLTextEditorResources.INSTANCE.CSS().categoryExplorerTree() );
        tree.addSelectionHandler( new SelectionHandler<TreeItem>() {
            public void onSelection( SelectionEvent<TreeItem> event ) {
                Object o = event.getSelectedItem().getUserObject();
                ev.selected( o.toString() );
            }
        } );

        tree.setVisible( true );
        hpDSLSentencesAndHide.setVisible( true );
        hpShow.setVisible( false );

        initWidget( container );
    }

    @Override
    public void init( final DRLEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    public void setDSLSentences( final List<DSLSentence> dslSentences ) {
        if ( tree.getItem( 0 ) != null ) {
            tree.clear();
        }

        if ( dslSentences != null ) {
            for ( DSLSentence dslSentence : dslSentences ) {
                final TreeItem it = new TreeItem();
                it.setHTML( "<small>" + dslSentence.toString() + "</small>" );
                it.setUserObject( dslSentence );
                tree.addItem( it );
            }
        }
    }

}
