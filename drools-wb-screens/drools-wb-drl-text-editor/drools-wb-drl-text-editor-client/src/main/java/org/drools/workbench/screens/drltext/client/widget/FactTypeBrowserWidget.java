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

package org.drools.workbench.screens.drltext.client.widget;

import java.util.List;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.screens.drltext.client.editor.DRLEditorPresenter;
import org.drools.workbench.screens.drltext.client.resources.DRLTextEditorResources;
import org.drools.workbench.screens.drltext.client.resources.i18n.DRLTextEditorConstants;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.common.ClickableLabel;
import org.uberfire.client.common.SmallLabel;
import org.uberfire.client.common.Util;
import org.uberfire.client.mvp.UberView;

public class FactTypeBrowserWidget
        extends Composite implements UberView<DRLEditorPresenter> {

    private static final String LAZY_LOAD = CommonConstants.INSTANCE.Loading();

    private DRLEditorPresenter presenter;

    private final Tree tree;

    private boolean isDSLR;

    public FactTypeBrowserWidget( final ClickEvent ev ) {
        this.tree = new Tree();

        final VerticalPanel panel = new VerticalPanel();
        final HorizontalPanel hpFactsAndHide = new HorizontalPanel();
        final HorizontalPanel hpShow = new HorizontalPanel();

        hpShow.add( new ClickableLabel( DRLTextEditorConstants.INSTANCE.ShowFactTypes(),
                                        new ClickHandler() {
                                            public void onClick( com.google.gwt.event.dom.client.ClickEvent event ) {
                                                hpShow.setVisible( false );
                                                hpFactsAndHide.setVisible( true );
                                                tree.setVisible( true );
                                            }
                                        } ) );
        panel.add( hpShow );

        hpFactsAndHide.add( new SmallLabel( DRLTextEditorConstants.INSTANCE.FactTypes() ) );
        hpFactsAndHide.add( new ClickableLabel( DRLTextEditorConstants.INSTANCE.hide(),
                                                new ClickHandler() {
                                                    public void onClick( com.google.gwt.event.dom.client.ClickEvent event ) {
                                                        hpShow.setVisible( true );
                                                        hpFactsAndHide.setVisible( false );
                                                        tree.setVisible( false );
                                                    }
                                                } ) );
        panel.add( hpFactsAndHide );

        panel.add( tree );

        tree.setStyleName( DRLTextEditorResources.INSTANCE.CSS().categoryExplorerTree() );
        tree.addSelectionHandler( new SelectionHandler<TreeItem>() {
            public void onSelection( SelectionEvent<TreeItem> event ) {
                Object o = event.getSelectedItem().getUserObject();
                if ( o instanceof ClassUserObject ) {
                    final String text = ( (ClassUserObject) o ).textToInsert;
                    ev.selected( isDSLR ? ">" + text : text );
                } else if ( o instanceof String ) {
                    ev.selected( (String) o );
                }
            }
        } );

        tree.addOpenHandler( new OpenHandler<TreeItem>() {
            @Override
            public void onOpen( final OpenEvent<TreeItem> event ) {
                final TreeItem item = event.getTarget();
                if ( needsLoading( item ) ) {
                    final Object userObject = event.getTarget().getUserObject();
                    presenter.loadClassFields( ( (ClassUserObject) userObject ).fullyQualifiedClassName,
                                               new Callback<List<String>>() {
                                                   @Override
                                                   public void callback( final List<String> fields ) {
                                                       item.getChild( 0 ).remove();
                                                       if ( fields != null ) {
                                                           for ( String field : fields ) {
                                                               final TreeItem fi = new TreeItem();
                                                               fi.setHTML( AbstractImagePrototype.create( DRLTextEditorResources.INSTANCE.images().fieldImage() ).getHTML()
                                                                                   + "<small>"
                                                                                   + field + "</small>" );
                                                               fi.setUserObject( field );
                                                               item.addItem( fi );
                                                           }
                                                       }
                                                   }
                                               } );
                }
            }
        } );

        tree.setVisible( true );
        hpFactsAndHide.setVisible( true );
        hpShow.setVisible( false );

        initWidget( panel );
    }

    @Override
    public void init( final DRLEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    public void setDSLR( final boolean isDSLR ) {
        this.isDSLR = isDSLR;
    }

    public void setFullyQualifiedClassNames( final List<String> fullyQualifiedClassNames ) {
        if ( tree.getItem( 0 ) != null ) {
            tree.clear();
        }

        if ( fullyQualifiedClassNames != null ) {
            for ( String type : fullyQualifiedClassNames ) {
                final TreeItem it = new TreeItem();
                it.setHTML( AbstractImagePrototype.create( DRLTextEditorResources.INSTANCE.images().classImage() ).getHTML()
                                    + "<small>"
                                    + type + "</small>" );
                it.setUserObject( new ClassUserObject( type + "( )",
                                                       type ) );
                tree.addItem( it );
                it.addItem( Util.toSafeHtml( LAZY_LOAD ) );
            }
        }
    }

    private boolean needsLoading( final TreeItem item ) {
        return item.getChildCount() == 1 && LAZY_LOAD.equals( item.getChild( 0 ).getText() );
    }

    private static class ClassUserObject {

        private String textToInsert;
        private String fullyQualifiedClassName;

        ClassUserObject( final String textToInsert,
                         final String fullyQualifiedClassName ) {
            this.textToInsert = textToInsert;
            this.fullyQualifiedClassName = fullyQualifiedClassName;
        }

    }

}
