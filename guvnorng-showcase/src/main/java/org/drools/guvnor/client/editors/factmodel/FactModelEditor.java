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

import java.util.List;

import org.drools.guvnor.client.i18n.Constants;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.shared.common.vo.assets.factmodel.FactMetaModel;
import org.uberfire.client.common.AbstractLazyStackPanelHeader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FactModelEditor extends AbstractLazyStackPanelHeader {

    interface FactModelEditorBinder
            extends
            UiBinder<Widget, FactModelEditor> {
    }

    private static FactModelEditorBinder uiBinder = GWT.create( FactModelEditorBinder.class );

    @UiField
    Image                                icon;
    @UiField
    Label                                titleLabel;
    @UiField
    Image                                editIcon;
    @UiField
    Image                                moveUpIcon;
    @UiField
    Image                                moveDownIcon;
    @UiField
    Image                                deleteIcon;

    private final FactMetaModel          factMetaModel;
    private final List<FactMetaModel>    superTypeFactModels;
    private final ModelNameHelper        modelNameHelper;

    private Command                      deleteEvent;
    private Command                      moveUpCommand;
    private Command                      moveDownCommand;

    public FactModelEditor(FactMetaModel factMetaModel,
                           List<FactMetaModel> superTypeFactModels,
                           ModelNameHelper modelNameHelper) {

        this.factMetaModel = factMetaModel;
        this.superTypeFactModels = superTypeFactModels;
        this.modelNameHelper = modelNameHelper;

        add( uiBinder.createAndBindUi( this ) );

        titleLabel.setText( getTitleText() );

        ClickHandler expandClickHandler = new ClickHandler() {

            public void onClick(ClickEvent event) {
                onTitleClicked();
            }
        };
        icon.addClickHandler( expandClickHandler );
        titleLabel.addClickHandler( expandClickHandler );

        setIconImage();

        moveUpIcon.setTitle( Constants.INSTANCE.MoveUp() );
        moveDownIcon.setTitle( Constants.INSTANCE.MoveDown() );
        deleteIcon.setTitle( Constants.INSTANCE.RemoveThisFactType() );

        addOpenHandler( new OpenHandler<AbstractLazyStackPanelHeader>() {
            public void onOpen(OpenEvent<AbstractLazyStackPanelHeader> event) {
                expanded = true;
                setIconImage();
            }
        } );

        addCloseHandler( new CloseHandler<AbstractLazyStackPanelHeader>() {
            public void onClose(CloseEvent<AbstractLazyStackPanelHeader> event) {
                expanded = false;
                setIconImage();
            }
        } );
    }

    private String getTitleText() {
        StringBuilder sb = new StringBuilder();
        sb.append( factMetaModel.getName() );
        if ( factMetaModel.hasSuperType() ) {
            sb.append( " extends " );
            sb.append( factMetaModel.getSuperType() );
        }
        return sb.toString();
    }

    @UiHandler("editIcon")
    void editIconClick(ClickEvent event) {
        final FactEditorPopup popup = new FactEditorPopup( factMetaModel,
                                                           superTypeFactModels,
                                                           modelNameHelper );

        popup.setOkCommand( new Command() {
            public void execute() {
                titleLabel.setText( getTitleText() );
            }
        } );

        popup.show();
    }

    @UiHandler("moveUpIcon")
    void moveUpClick(ClickEvent event) {
        moveUpCommand.execute();
    }

    @UiHandler("moveDownIcon")
    void moveDownClick(ClickEvent event) {
        moveDownCommand.execute();
    }

    @UiHandler("deleteIcon")
    void deleteClick(ClickEvent event) {
        //Check if the Fact is a super type of any other facts
        for ( FactMetaModel fmm : superTypeFactModels ) {
            if ( fmm.hasSuperType() ) {
                if ( fmm.getSuperType().equals( factMetaModel.getName() ) ) {
                    Window.confirm( Constants.INSTANCE.CannotDeleteADeclarationThatIsASuperType() );
                    return;
                }
            }
        }
        if ( Window.confirm( Constants.INSTANCE.AreYouSureYouWantToRemoveThisFact() ) ) {
            deleteEvent.execute();
        }
    }

    private void setIconImage() {
        if ( expanded ) {
            icon.setResource( GuvnorImages.INSTANCE.collapse() );
        } else {
            icon.setResource( GuvnorImages.INSTANCE.expand() );
        }
    }

    public void setDeleteEvent(Command deleteEvent) {
        this.deleteEvent = deleteEvent;
    }

    public void setMoveDownCommand(Command moveDownCommand) {
        this.moveDownCommand = moveDownCommand;
    }

    public void setMoveUpCommand(Command moveUpCommand) {
        this.moveUpCommand = moveUpCommand;
    }

    public void setUpVisible(boolean visible) {
        moveUpIcon.setVisible( visible );
    }

    public void setDownVisible(boolean visible) {
        moveDownIcon.setVisible( visible );
    }

    public void expand() {
        if ( !expanded ) {
            onTitleClicked();
        }
    }

    public void collapse() {
        if ( expanded ) {
            onTitleClicked();
        }
    }

    private void onTitleClicked() {
        if ( expanded ) {
            CloseEvent.fire( this,
                             this );
        } else {
            OpenEvent.fire( this,
                            this );
        }
    }

    public FactMetaModel getFactModel() {
        return factMetaModel;
    }

    public Widget getContent() {
        return new FactFieldsEditor( factMetaModel.getFields(),
                                     factMetaModel.getAnnotations(),
                                     modelNameHelper );
    }
}
