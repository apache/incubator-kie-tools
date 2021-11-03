package org.dashbuilder.client.widgets.dataset.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;

import javax.enterprise.context.Dependent;

/**
 * <p>Data Set columns and filter editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataSetDefColumnsFilterEditorView extends Composite implements DataSetDefColumnsFilterEditor.View {

    

    interface Binder extends UiBinder<Widget, DataSetDefColumnsFilterEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    DataSetDefColumnsFilterEditor presenter;

    @UiField
    TabListItem columnsTabItem;

    @UiField
    TabListItem filterTabItem;
    
    @UiField
    TabContent tabContent;
    
    @UiField
    TabPane columnsTabPane;

    @UiField
    TabPane filterTabPane;
    
    @UiField(provided = true)
    IsWidget columnsEditorView;

    @UiField(provided = true)
    DataSetDefFilterEditor.View dataSetFilterEditorView;

    @Override
    public void init(final DataSetDefColumnsFilterEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initWidgets(IsWidget columnsEditorView, DataSetDefFilterEditor.View dataSetFilterEditorView) {
        this.columnsEditorView = columnsEditorView;
        this.dataSetFilterEditorView = dataSetFilterEditorView;
        initWidget(Binder.BINDER.createAndBindUi(this));   
        columnsTabItem.setDataTargetWidget(columnsTabPane);
        filterTabItem.setDataTargetWidget(filterTabPane);
    }
    
    public void setMaxHeight(final String maxHeight) {
        tabContent.getElement().getStyle().setProperty("maxHeight", maxHeight);
    } 

}