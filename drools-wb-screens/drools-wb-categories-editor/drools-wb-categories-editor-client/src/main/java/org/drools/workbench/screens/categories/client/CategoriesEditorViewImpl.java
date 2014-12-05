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

package org.drools.workbench.screens.categories.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import org.drools.workbench.screens.categories.client.resources.Images;
import org.drools.workbench.screens.categories.client.resources.i18n.Constants;
import org.drools.workbench.screens.categories.client.widget.CategoryTreeEditorWidget;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.guvnor.common.services.shared.metadata.model.CategoryItem;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.ext.widgets.common.client.common.PrettyFormLayout;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

/**
 *
 */
@Dependent
public class CategoriesEditorViewImpl
        extends KieEditorViewImpl
        implements CategoriesEditorView {

    private CategoryTreeEditorWidget explorer;
    private PrettyFormLayout form;

    @PostConstruct
    public void init() {
        explorer = new CategoryTreeEditorWidget();
        form = new PrettyFormLayout();

        form.addHeader( Images.INSTANCE.EditCategories(), new HTML( Constants.INSTANCE.EditCategories() ) );
        form.startSection( Constants.INSTANCE.CategoriesPurposeTip() );

        final SimplePanel editable = new SimplePanel();
        editable.add( explorer );

        form.addAttribute( Constants.INSTANCE.CurrentCategories(), editable );

        final HorizontalPanel actions = new HorizontalPanel();

        form.addAttribute( "", actions );

        final Button newCat = new Button( Constants.INSTANCE.NewCategory() );
        newCat.setTitle( Constants.INSTANCE.CreateANewCategory() );
        newCat.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                final CategoryEditor newCat;
                if ( explorer.getSelectedCategory() == null ) {
                    newCat = new CategoryEditor( explorer.getCategories() );
                } else {
                    newCat = new CategoryEditor( explorer.getSelectedCategory() );
                }

                newCat.show();
            }
        } );

        actions.add( newCat );

        final Button rename = new Button( Constants.INSTANCE.RenameSelected() );
        rename.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                if ( !explorer.isSelected() ) {
                    Window.alert( Constants.INSTANCE.PleaseSelectACategoryToRename() );
                    return;
                }
                final String name = Window.prompt( Constants.INSTANCE.CategoryNewNamePleaseEnter(), "" );
                if ( name != null ) {
                    explorer.renameSelected( name );
                }
            }
        } );

        actions.add( rename );

        final Button delete = new Button( Constants.INSTANCE.DeleteSelected() );
        delete.addClickHandler( new ClickHandler() {
            public void onClick( final ClickEvent w ) {
                if ( !explorer.isSelected() ) {
                    Window.alert( Constants.INSTANCE.PleaseSelectACategoryToDelete() );
                    return;
                }
                if ( Window.confirm( Constants.INSTANCE.AreYouSureYouWantToDeleteCategory() + explorer.getSelectedCategory().getName() ) ) {
                    explorer.removeSelected();
                }
            }
        } );
        delete.setTitle( Constants.INSTANCE.DeleteSelectedCat() );

        actions.add( delete );

        form.endSection();

        initWidget( form );
    }

    @Override
    public void setContent( final Categories categories ) {
        explorer.setContent( categories );
    }

    @Override
    public Categories getContent() {
        return explorer.getCategories();
    }

    public class CategoryEditor extends FormStylePopup {

        private final CategoryItem parent;
        private final TextBox name = new TextBox();
        private final TextArea description = new TextArea();

        /**
         * This is used when creating a new category
         */
        public CategoryEditor( final CategoryItem parent ) {
            super( Constants.INSTANCE.CreateANewTopLevelCategory() );
            super.setTitle( getTitle( parent ) );
            this.parent = parent;

            addAttribute( Constants.INSTANCE.CategoryName(), name );

            Button ok = new Button( Constants.INSTANCE.OK() );
            ok.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    ok();
                }

            } );
            addAttribute( "", ok );
        }

        private String getTitle( CategoryItem categoryItem ) {
            if ( categoryItem == null ) {
                return Constants.INSTANCE.CreateANewTopLevelCategory();
            } else {
                return Constants.INSTANCE.CreateNewCategoryUnder0( categoryItem.getName() );
            }
        }

        void ok() {

            if ( "".equals( this.name.getText() ) ) {
                ErrorPopup.showMessage( Constants.INSTANCE.CanNotHaveAnEmptyCategoryName() );
            } else {
                if ( parent.contains( name.getText() ) ) {
                    ErrorPopup.showMessage( Constants.INSTANCE.CategoryWasNotSuccessfullyCreated() );
                } else {
                    explorer.addChildren( parent, name.getText(), description.getText() );
                    hide();
                }
            }
        }
    }

}
