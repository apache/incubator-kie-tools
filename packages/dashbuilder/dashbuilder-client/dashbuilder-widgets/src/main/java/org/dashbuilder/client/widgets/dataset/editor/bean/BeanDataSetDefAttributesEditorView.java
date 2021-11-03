package org.dashbuilder.client.widgets.dataset.editor.bean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.ValueBoxEditor;

import javax.enterprise.context.Dependent;

/**
 * <p>The Bean Data Set attributes editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class BeanDataSetDefAttributesEditorView extends Composite implements BeanDataSetDefAttributesEditor.View {

    interface Binder extends UiBinder<Widget, BeanDataSetDefAttributesEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    BeanDataSetDefAttributesEditor presenter;

    @UiField(provided = true)
    ValueBoxEditor.View generatorClassView;

    @UiField(provided = true)
    IsWidget parameterMapView;

    @Override
    public void init(final BeanDataSetDefAttributesEditor presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public void initWidgets(final ValueBoxEditor.View generatorClassView, final IsWidget parameterMapView) {
        this.generatorClassView = generatorClassView;
        this.parameterMapView = parameterMapView;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

}
