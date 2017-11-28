package org.dashbuilder.client.widgets.dataset.editor.elasticsearch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.ValueBoxEditor;

import javax.enterprise.context.Dependent;

/**
 * <p>The Elastic Search Data Set attributes editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class ElasticSearchDataSetDefAttributesEditorView extends Composite implements ElasticSearchDataSetDefAttributesEditor.View {

    interface Binder extends UiBinder<Widget, ElasticSearchDataSetDefAttributesEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    ElasticSearchDataSetDefAttributesEditor presenter;

    @UiField(provided = true)
    ValueBoxEditor.View serverUrlView;

    @UiField(provided = true)
    ValueBoxEditor.View clusterNameView;

    @UiField(provided = true)
    ValueBoxEditor.View indexView;

    @UiField(provided = true)
    ValueBoxEditor.View typeView;

    @Override
    public void init(final ElasticSearchDataSetDefAttributesEditor presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public void initWidgets(final ValueBoxEditor.View serverUrlView, final ValueBoxEditor.View clusterNameView,
                            final ValueBoxEditor.View indexView, final ValueBoxEditor.View typeView) {
        this.serverUrlView = serverUrlView;
        this.clusterNameView = clusterNameView;
        this.indexView = indexView;
        this.typeView = typeView;
        initWidget(Binder.BINDER.createAndBindUi(this));

    }

}
