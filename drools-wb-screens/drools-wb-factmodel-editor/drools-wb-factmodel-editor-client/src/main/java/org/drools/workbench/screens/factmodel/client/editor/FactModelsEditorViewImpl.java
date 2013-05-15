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
package org.drools.workbench.screens.factmodel.client.editor;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.factmodel.client.resources.i18n.Constants;
import org.drools.workbench.screens.factmodel.model.FactMetaModel;
import org.drools.workbench.screens.factmodel.model.FactModels;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.common.AbstractLazyStackPanelHeader;
import org.uberfire.client.common.AddButton;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.LazyStackPanel;
import org.uberfire.client.common.LoadContentCommand;

public class FactModelsEditorViewImpl
        extends Composite
        implements FactModelsEditorView {

    interface FactModelsEditorBinder
            extends
            UiBinder<Widget, FactModelsEditorViewImpl> {

    }

    private static FactModelsEditorBinder uiBinder = GWT.create( FactModelsEditorBinder.class );

    @UiField
    LazyStackPanel factModelsPanel;

    @UiField
    AddButton addFactIcon;

    private FactModels factModels;

    private List<FactMetaModel> superTypeFactModels;

    private ModelNameHelper modelNameHelper;

    public FactModelsEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );

        addFactIcon.setTitle( Constants.INSTANCE.AddNewFactType() );
        addFactIcon.setText( Constants.INSTANCE.AddNewFactType() );
    }

    @Override
    public void setContent( final FactModels content,
                            final List<FactMetaModel> superTypeFactModels,
                            final ModelNameHelper modelNameHelper ) {
        this.factModels = content;
        this.superTypeFactModels = superTypeFactModels;
        this.modelNameHelper = modelNameHelper;
        this.factModelsPanel.clean();

        for ( final FactMetaModel factMetaModel : factModels.getModels() ) {
            addFactModelToStackPanel( factMetaModel );
        }
    }

    @Override
    public FactModels getContent() {
        return factModels;
    }

    @Override
    public boolean isDirty() {
        //TODO This editor does not currently support "isDirty"
        return false;
    }

    @Override
    public void setNotDirty() {
        //TODO This editor does not currently support "isDirty"
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

    @Override
    public void alertReadOnly() {
        Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
    }

    private void addFactModelToStackPanel( final FactMetaModel factMetaModel ) {
        final FactModelEditor editor = new FactModelEditor( factMetaModel,
                                                            superTypeFactModels,
                                                            modelNameHelper );

        editor.setMoveDownCommand( getMoveDownCommand( factMetaModel ) );

        editor.setMoveUpCommand( getMoveUpCommand( factMetaModel ) );

        editor.setDeleteEvent( getDeleteCommand( factMetaModel ) );

        factModelsPanel.add( editor,
                             new LoadContentCommand() {
                                 public Widget load() {
                                     return editor.getContent();
                                 }
                             } );

        renderEditorArrows();
    }

    private Command getDeleteCommand( final FactMetaModel factMetaModel ) {
        return new Command() {
            public void execute() {
                int index = factModels.getModels().indexOf( factMetaModel );

                modelNameHelper.getTypeDescriptions().remove( factMetaModel.getName() );
                factModels.getModels().remove( factMetaModel );
                superTypeFactModels.remove( factMetaModel );
                factModelsPanel.remove( index );
            }
        };
    }

    private Command getMoveUpCommand( final FactMetaModel factMetaModel ) {
        return new Command() {

            public void execute() {
                int editingFactIndex = factModels.getModels().indexOf( factMetaModel );
                int newIndex = editingFactIndex - 1;

                swap( editingFactIndex, newIndex );

                renderEditorArrows();
            }

        };
    }

    private Command getMoveDownCommand( final FactMetaModel factMetaModel ) {
        return new Command() {

            public void execute() {
                int editingFactIndex = factModels.getModels().indexOf( factMetaModel );
                int newIndex = editingFactIndex + 1;

                swap( editingFactIndex,
                      newIndex );

                renderEditorArrows();
            }
        };
    }

    private void swap( int editingFactIndex,
                       int newIndex ) {
        Collections.swap( factModels.getModels(), editingFactIndex, newIndex );

        factModelsPanel.swap( editingFactIndex, newIndex );
    }

    private void renderEditorArrows() {
        Iterator<AbstractLazyStackPanelHeader> iterator = factModelsPanel.getHeaderIterator();

        while ( iterator.hasNext() ) {
            final AbstractLazyStackPanelHeader widget = iterator.next();

            if ( widget instanceof FactModelEditor ) {
                final FactModelEditor editor = (FactModelEditor) widget;

                int index = factModels.getModels().indexOf( editor.getFactModel() );
                editor.setUpVisible( index != 0 );
                editor.setDownVisible( index != ( factModels.getModels().size() - 1 ) );
            }
        }
    }

    @UiHandler("addFactIcon")
    void addFactClick( final ClickEvent event ) {
        final FactEditorPopup popup = new FactEditorPopup( modelNameHelper,
                                                           superTypeFactModels );

        popup.setOkCommand( new Command() {
            public void execute() {
                FactMetaModel factMetaModel = popup.getFactModel();

                factModels.getModels().add( factMetaModel );
                superTypeFactModels.add( factMetaModel );
                addFactModelToStackPanel( factMetaModel );
            }
        } );
        popup.show();
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
