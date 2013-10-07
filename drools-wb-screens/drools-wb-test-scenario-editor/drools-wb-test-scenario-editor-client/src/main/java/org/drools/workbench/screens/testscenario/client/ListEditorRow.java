package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.CollectionFieldData;
import org.drools.workbench.models.testscenarios.shared.FieldData;

public class ListEditorRow extends Composite {

    interface ListEditorRowBinder
            extends
            UiBinder<Widget, ListEditorRow> {

    }

    private static ListEditorRowBinder uiBinder = GWT.create( ListEditorRowBinder.class );

    @UiField(provided = true)
    FieldDataConstraintEditor fieldDataConstraintEditor;

    @UiField
    Image deleteItem;

    @UiField
    Image newItemBelow;

    @UiField
    Image suffleDown;

    @UiField
    Image suffleUp;

    private final int index;
    private final CollectionFieldData parentField;
    private final ScenarioParentWidget parent;

    public ListEditorRow( final int index,
                          final CollectionFieldData parentField,
                          final FieldData fieldData,
                          final FieldConstraintHelper helper,
                          final ScenarioParentWidget parent ) {

        this.index = index;
        this.parentField = parentField;
        this.parent = parent;

        fieldDataConstraintEditor = helper.createFieldDataConstraintEditor( fieldData );
        fieldDataConstraintEditor.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( ValueChangeEvent<String> stringValueChangeEvent ) {
                fieldData.setValue( stringValueChangeEvent.getValue() );
            }
        } );

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @UiHandler("deleteItem")
    public void deleteItem( final ClickEvent event ) {
        parentField.getCollectionFieldList().remove( index );
        parent.renderEditor();
    }

    @UiHandler("newItemBelow")
    public void newItemBelow( final ClickEvent event ) {
        FieldData newFieldData = new FieldData();
        newFieldData.setName( parentField.getName() );
        parentField.getCollectionFieldList().add( index + 1,
                                                  newFieldData );
        parent.renderEditor();
    }

    @UiHandler("suffleDown")
    public void suffleDown( final ClickEvent event ) {
        if ( index < parentField.getCollectionFieldList().size() - 1 ) {
            FieldData onMyLine = parentField.getCollectionFieldList().get( index );
            FieldData onDown = parentField.getCollectionFieldList().get( index + 1 );
            parentField.getCollectionFieldList().set( index + 1,
                                                      onMyLine );
            parentField.getCollectionFieldList().set( index,
                                                      onDown );
            parent.renderEditor();
        }
    }

    @UiHandler("suffleUp")
    public void suffleUp( final ClickEvent event ) {
        if ( index > 0 ) {
            FieldData oneUp = parentField.getCollectionFieldList().get( index - 1 );
            FieldData onMyLine = parentField.getCollectionFieldList().get( index );
            parentField.getCollectionFieldList().set( index,
                                                      oneUp );
            parentField.getCollectionFieldList().set( index - 1,
                                                      onMyLine );
            parent.renderEditor();
        }
    }
}
