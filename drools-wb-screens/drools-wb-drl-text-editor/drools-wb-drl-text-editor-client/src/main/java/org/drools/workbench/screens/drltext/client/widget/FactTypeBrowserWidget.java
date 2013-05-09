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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.screens.drltext.client.resources.Resources;
import org.drools.workbench.screens.drltext.client.resources.i18n.DRLTextEditorConstants;
import org.drools.workbench.screens.drltext.client.resources.images.ImageResources;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.uberfire.client.common.ClickableLabel;
import org.uberfire.client.common.SmallLabel;

public class FactTypeBrowserWidget
        extends Composite {

    private final Tree tree;

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

        tree.setStyleName( Resources.INSTANCE.CSS().categoryExplorerTree() );
        tree.addSelectionHandler( new SelectionHandler<TreeItem>() {
            public void onSelection( SelectionEvent<TreeItem> event ) {
                Object o = event.getSelectedItem().getUserObject();
                if ( o instanceof String ) {
                    ev.selected( (String) o );
                }
            }
        } );

        tree.setVisible( false );
        hpFactsAndHide.setVisible( false );
        hpShow.setVisible( true );

        initWidget( panel );
    }

    public void setDataModel( final PackageDataModelOracle dataModel ) {
        if ( tree.getItem( 0 ) != null ) {
            tree.clear();
        }

        if ( dataModel.getFactTypes() != null ) {
            for ( String type : dataModel.getFactTypes() ) {
                TreeItem it = new TreeItem();
                it.setHTML( AbstractImagePrototype.create( ImageResources.INSTANCE.classImage() ).getHTML()
                                    + "<small>"
                                    + type + "</small>" );
                it.setUserObject( type + "( )" );
                tree.addItem( it );

                String[] fields = dataModel.getFieldCompletions( type );
                if ( fields != null ) {
                    for ( String field : fields ) {
                        TreeItem fi = new TreeItem();
                        fi.setHTML( AbstractImagePrototype.create( ImageResources.INSTANCE.field() ).getHTML()
                                            + "<small>"
                                            + field + "</small>" );
                        fi.setUserObject( field );
                        it.addItem( fi );
                    }
                }
            }
        }
    }

    public static interface ClickEvent {

        public void selected( String text );
    }

}
