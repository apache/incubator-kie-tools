package org.dashbuilder.client.widgets.dataset.editor.external;

import javax.enterprise.context.Dependent;

import org.dashbuilder.common.client.editor.ValueBoxEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * <p>The External Data Set attributes editor view.</p>
 *
 */
@Dependent
public class ExternalDataSetDefAttributesEditorView extends Composite implements
                                                    ExternalDataSetDefAttributesEditor.View {

    interface Binder extends UiBinder<Widget, ExternalDataSetDefAttributesEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    ExternalDataSetDefAttributesEditor presenter;

    @UiField(provided = true)
    ValueBoxEditor.View urlView;

    @Override
    public void init(final ExternalDataSetDefAttributesEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initWidgets(final ValueBoxEditor.View urlView) {
        this.urlView = urlView;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

}
