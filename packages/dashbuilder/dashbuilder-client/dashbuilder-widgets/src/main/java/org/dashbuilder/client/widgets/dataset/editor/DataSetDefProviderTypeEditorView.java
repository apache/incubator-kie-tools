package org.dashbuilder.client.widgets.dataset.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import javax.enterprise.context.Dependent;

/**
 * <p>The Data Set provider type editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataSetDefProviderTypeEditorView extends Composite implements DataSetDefProviderTypeEditor.View {

    
    interface Binder extends UiBinder<Widget, DataSetDefProviderTypeEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }
    
    @UiField(provided = true)
    IsWidget listEditorView;
    
    DataSetDefProviderTypeEditor presenter;

    @Override
    public void init(final DataSetDefProviderTypeEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initWidgets(final IsWidget listEditorView) {
        this.listEditorView = listEditorView;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

}
