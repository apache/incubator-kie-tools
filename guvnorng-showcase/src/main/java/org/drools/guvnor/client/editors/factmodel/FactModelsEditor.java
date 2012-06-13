/*
 * Copyright 2010 JBoss Inc
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
package org.drools.guvnor.client.editors.factmodel;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.common.AbstractLazyStackPanelHeader;
import org.drools.guvnor.client.common.AddButton;
import org.drools.guvnor.client.common.LazyStackPanel;
import org.drools.guvnor.client.common.LoadContentCommand;
import org.drools.guvnor.client.i18n.Constants;
import org.drools.guvnor.shared.common.vo.assets.factmodel.FactMetaModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FactModelsEditor extends Composite {

    interface FactModelsEditorBinder
        extends
        UiBinder<Widget, FactModelsEditor> {
    }

    private static FactModelsEditorBinder uiBinder = GWT.create( FactModelsEditorBinder.class );

    @UiField
    LazyStackPanel                        factModelsPanel;

    @UiField
    AddButton                             addFactIcon;

    private final List<FactMetaModel>     factModels;

    private final List<FactMetaModel>     superTypeFactModels;

    private final ModelNameHelper         modelNameHelper;

    public FactModelsEditor(final List<FactMetaModel> factModels,
                            final List<FactMetaModel> superTypeFactModels,
                            final ModelNameHelper modelNameHelper) {
        this.factModels = factModels;
        this.superTypeFactModels = superTypeFactModels;
        this.modelNameHelper = modelNameHelper;

        initWidget( uiBinder.createAndBindUi( this ) );

        addFactIcon.setTitle( Constants.INSTANCE.AddNewFactType() );
        addFactIcon.setText( Constants.INSTANCE.AddNewFactType() );

        fillModels();
    }

    public void addFactModelToStackPanel(final FactMetaModel factMetaModel) {
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

    private Command getDeleteCommand(final FactMetaModel factMetaModel) {
        return new Command() {
            public void execute() {
                int index = factModels.indexOf( factMetaModel );

                modelNameHelper.getTypeDescriptions().remove( factMetaModel.getName() );
                factModels.remove( factMetaModel );
                superTypeFactModels.remove( factMetaModel );
                factModelsPanel.remove( index );
            }
        };
    }

    private Command getMoveUpCommand(final FactMetaModel factMetaModel) {
        return new Command() {

            public void execute() {
                int editingFactIndex = factModels.indexOf( factMetaModel );
                int newIndex = editingFactIndex - 1;

                swap( editingFactIndex,
                      newIndex );

                renderEditorArrows();
            }

        };
    }

    private Command getMoveDownCommand(final FactMetaModel factMetaModel) {
        return new Command() {

            public void execute() {
                int editingFactIndex = factModels.indexOf( factMetaModel );
                int newIndex = editingFactIndex + 1;

                swap( editingFactIndex,
                      newIndex );

                renderEditorArrows();
            }
        };
    }

    private void swap(int editingFactIndex,
                      int newIndex) {
        Collections.swap( factModels,
                          editingFactIndex,
                          newIndex );

        factModelsPanel.swap( editingFactIndex,
                              newIndex );
    }

    private void renderEditorArrows() {
        Iterator<AbstractLazyStackPanelHeader> iterator = factModelsPanel.getHeaderIterator();

        while ( iterator.hasNext() ) {
            AbstractLazyStackPanelHeader widget = (AbstractLazyStackPanelHeader) iterator.next();

            if ( widget instanceof FactModelEditor ) {
                FactModelEditor editor = (FactModelEditor) widget;

                int index = factModels.indexOf( editor.getFactModel() );
                editor.setUpVisible( index != 0 );
                editor.setDownVisible( index != (factModels.size() - 1) );
            }
        }
    }

    private void fillModels() {
        for ( final FactMetaModel factMetaModel : factModels ) {
            addFactModelToStackPanel( factMetaModel );
        }
    }

    @UiHandler("addFactIcon")
    void addFactClick(ClickEvent event) {
        final FactEditorPopup popup = new FactEditorPopup( modelNameHelper,
                                                           superTypeFactModels );

        popup.setOkCommand( new Command() {
            public void execute() {
                FactMetaModel factMetaModel = popup.getFactModel();

                factModels.add( factMetaModel );
                superTypeFactModels.add( factMetaModel );
                addFactModelToStackPanel( factMetaModel );
            }
        } );
        popup.show();
    }
}
