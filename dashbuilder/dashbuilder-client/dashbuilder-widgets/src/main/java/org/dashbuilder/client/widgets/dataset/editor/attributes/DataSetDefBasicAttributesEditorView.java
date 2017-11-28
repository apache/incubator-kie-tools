package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.ValueBoxEditor;

import javax.enterprise.context.Dependent;

/**
 * <p>The Data Set Editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataSetDefBasicAttributesEditorView extends Composite implements DataSetDefBasicAttributesEditor.View {

    interface Binder extends UiBinder<Widget, DataSetDefBasicAttributesEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    DataSetDefBasicAttributesEditor presenter;

    @UiField(provided = true)
    ValueBoxEditor.View uuid;

    @UiField(provided = true)
    ValueBoxEditor.View name;

    @Override
    public void init(final DataSetDefBasicAttributesEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initWidgets(final ValueBoxEditor.View uuidEditor, final ValueBoxEditor.View nameEditor) {
        this.uuid = uuidEditor;
        this.name = nameEditor;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }
    
}
