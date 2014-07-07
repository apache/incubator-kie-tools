/*
 * Copyright 2005 JBoss Inc
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

package org.kie.workbench.common.widgets.metadata.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.widgets.metadata.client.resources.Images;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.MetadataConstants;
import org.kie.uberfire.client.common.DirtyableComposite;
import org.kie.uberfire.client.common.DirtyableFlexTable;
import org.kie.uberfire.client.common.FormStylePopup;
import org.kie.uberfire.client.common.SmallLabel;

/**
 * This is a viewer/selector for categories.
 * It will show a list of categories currently applicable, and allow you to
 * remove/add to them.
 * <p/>
 * It is intended to work with the meta data form.
 */
public class CategorySelectorWidget
        extends DirtyableComposite {

    private Metadata data;
    private DirtyableFlexTable layout = new DirtyableFlexTable();
    private FlexTable list;
    private boolean readOnly;

    /**
     * @param d The meta data.
     * @param readOnly If it is to be non editable.
     */
    public CategorySelectorWidget( ) {

        initWidget( layout );
    }

    public void setContent(Metadata d, boolean readOnly) {
        this.data = d;

        list = new FlexTable();
        this.readOnly = readOnly;
        loadData( list );
        list.setStyleName( "rule-List" );
        layout.setWidget( 0, 0, list );

        if ( !readOnly ) {
            doActions();
        }
    }

    private void doActions() {
        final VerticalPanel actions = new VerticalPanel();
        final Image add = Images.INSTANCE.NewItem();
        add.setAltText( MetadataConstants.INSTANCE.AssetCategoryEditorAddNewCategory() );
        add.setTitle( MetadataConstants.INSTANCE.AddANewCategory() );

        add.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                doOKClick();
            }
        } );

        actions.add( add );
        layout.setWidget( 0, 1, actions );

    }

    protected void removeCategory( int idx ) {
        data.removeCategory( idx );
        makeDirty();
        resetBox();
    }

    private void resetBox() {
        list = new FlexTable();
        list.setStyleName( "rule-List" );
        layout.setWidget( 0,
                          0,
                          list );
        loadData( list );
        makeDirty();
    }

    private void loadData( final FlexTable list ) {

        for ( int i = 0; i < data.getCategories().size(); i++ ) {
            final int idx = i;
            final String categoryPath = data.getCategories().get( idx );

            list.setWidget( i, 0, new SmallLabel( categoryPath ) );
            if ( !readOnly ) {
                final Image del = Images.INSTANCE.Trash();
                del.setTitle( MetadataConstants.INSTANCE.RemoveThisCategory() );
                del.addClickHandler( new ClickHandler() {
                    public void onClick( final ClickEvent event ) {
                        removeCategory( idx );
                    }
                } );
                list.setWidget( i, 1, del );
            }
        }
    }

    /**
     * Handles the OK click on the selector popup
     */
    private void doOKClick() {
        final CategorySelector sel = new CategorySelector();
        sel.show();
    }

    /**
     * Appy the change (selected path to be added).
     */
    public void addToCategory( final String category ) {
        data.addCategory( category );
        resetBox();
    }

    /**
     * This is a popup that allows you to select a category to add to the asset.
     */
    class CategorySelector extends FormStylePopup {

        public Button ok = new Button( MetadataConstants.INSTANCE.OK() );
        private CategoryExplorerWidget selector;
        public String selectedPath;

        public CategorySelector() {
            setTitle( MetadataConstants.INSTANCE.SelectCategoryToAdd() );
            final VerticalPanel vert = new VerticalPanel();

            selector = new CategoryExplorerWidget( data.getPath(),
                                                   new CategorySelectHandler() {
                                                       public void selected( final String sel ) {
                                                           selectedPath = sel;
                                                       }
                                                   } );

            vert.add( selector );
            vert.add( ok );

            addRow( vert );

            ok.addClickHandler( new ClickHandler() {
                public void onClick( final ClickEvent event ) {
                    if ( selectedPath != null && !"".equals( selectedPath ) ) {
                        addToCategory( selectedPath );
                    }
                    hide();
                }
            } );

        }

    }
}
