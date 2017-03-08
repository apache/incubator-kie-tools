/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.globals.client.editor;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.workbench.screens.globals.client.resources.i18n.GlobalsEditorConstants;
import org.drools.workbench.screens.globals.model.Global;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.mvp.Command;

@Templated
@Dependent
public class GlobalsEditorViewImpl
        extends KieEditorViewImpl
        implements GlobalsEditorView {

    @DataField("generatedLabel")
    Label generatedLabel;

    @DataField("addGlobalButton")
    Button addGlobalButton;

    @DataField("globalsTable")
    CellTable<Global> table = new CellTable<>();

    private AddGlobalPopup addGlobalPopup;

    private TranslationService translationService;

    private ButtonCell deleteGlobalButton;

    private List<Global> globals = new ArrayList<>();
    private ListDataProvider<Global> dataProvider = new ListDataProvider<>();
    private final Command addGlobalCommand = makeAddGlobalCommand();

    private List<String> fullyQualifiedClassNames;

    public GlobalsEditorViewImpl() {
    }

    @Inject
    public GlobalsEditorViewImpl( final Label generatedLabel,
                                  final Button addGlobalButton,
                                  final AddGlobalPopup addGlobalPopup,
                                  final TranslationService translationService) {
        this.generatedLabel = generatedLabel;
        this.addGlobalButton = addGlobalButton;
        this.addGlobalPopup = addGlobalPopup;
        this.translationService = translationService;

        setup();

        // Hide/disable the following until content is loaded
        generatedLabel.setVisible( false );
        addGlobalButton.setEnabled( false );
    }

    private void setup() {
        //Setup table
        table.setStriped( true );
        table.setCondensed( true );
        table.setBordered( true );
        table.setEmptyTableWidget( new Label( translationService.getTranslation( GlobalsEditorConstants.GlobalsEditorViewImplNoGlobalsDefined ) ) );

        //Columns
        final TextColumn<Global> aliasColumn = new TextColumn<Global>() {

            @Override
            public String getValue( final Global global ) {
                return global.getAlias();
            }
        };

        final TextColumn<Global> classNameColumn = new TextColumn<Global>() {

            @Override
            public String getValue( final Global global ) {
                return global.getClassName();
            }
        };

        deleteGlobalButton = new ButtonCell( IconType.MINUS,
                                             ButtonType.DANGER,
                                             ButtonSize.SMALL );
        final Column<Global, String> deleteGlobalColumn = new Column<Global, String>( deleteGlobalButton ) {
            @Override
            public String getValue( final Global global ) {
                return translationService.getTranslation( GlobalsEditorConstants.GlobalsEditorViewImplRemove );
            }
        };
        deleteGlobalColumn.setFieldUpdater( ( index, global, value ) -> {
            if ( Window.confirm( translationService.format( GlobalsEditorConstants.GlobalsEditorViewImplPromptForRemovalOfGlobal,
                                                            global.getAlias() ) ) ) {
                dataProvider.getList().remove( index );
            }
        } );

        table.addColumn( aliasColumn,
                         new TextHeader( translationService.getTranslation( GlobalsEditorConstants.GlobalsEditorViewImplAlias ) ) );
        table.addColumn( classNameColumn,
                         new TextHeader( translationService.getTranslation( GlobalsEditorConstants.GlobalsEditorViewImplClassName ) ) );
        table.addColumn( deleteGlobalColumn,
                         translationService.getTranslation( GlobalsEditorConstants.GlobalsEditorViewImplRemove ) );

        //Link data
        dataProvider.addDataDisplay( table );
        dataProvider.setList( globals );

        generatedLabel.setText( translationService.getTranslation( GlobalsEditorConstants.GlobalsEditorViewImplAutoGeneratedFile ) );
        addGlobalButton.setText( translationService.getTranslation( GlobalsEditorConstants.GlobalsEditorViewImplAdd ) );
        addGlobalButton.setIcon( IconType.PLUS );
    }

    @Override
    public void setContent( final List<Global> globals,
                            final List<String> fullyQualifiedClassNames,
                            final boolean isReadOnly,
                            final boolean isGenerated ) {
        this.globals = globals;
        this.fullyQualifiedClassNames = fullyQualifiedClassNames;
        this.dataProvider.setList( globals );
        this.generatedLabel.setVisible( isGenerated );
        this.addGlobalButton.setEnabled( !isReadOnly && !isGenerated );
        this.deleteGlobalButton.setEnabled( !isReadOnly && !isGenerated );
    }

    @EventHandler("addGlobalButton")
    public void onClickAddGlobalButton( final ClickEvent event ) {
        addGlobalPopup.show( addGlobalCommand,
                             () -> {},
                             fullyQualifiedClassNames );
    }

    private Command makeAddGlobalCommand() {
        return () -> {
            final String alias = addGlobalPopup.getAlias();
            final String className = addGlobalPopup.getClassName();
            dataProvider.getList().add( new Global( alias,
                                                    className ) );
        };
    }
}
