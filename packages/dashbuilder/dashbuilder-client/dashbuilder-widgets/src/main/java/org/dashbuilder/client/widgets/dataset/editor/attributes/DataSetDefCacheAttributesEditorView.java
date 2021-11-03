package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.uberfire.client.mvp.UberView;

/**
 * <p>The Data Set cache attributes editor view.</p>
 *
 * @since 0.4.0
 */
public interface DataSetDefCacheAttributesEditorView extends UberView<DataSetDefCacheAttributesEditorView.ViewCallback> {
    
    interface ViewCallback {
        void onValueChange(Double value);
    }

    /**
     * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
     */
    void init(String title, String units, IsWidget enabledView, ValueBoxEditor.View valueView);

    void setValue(final Double value);

    void setRange(Double min, Double max);

    void setEnabled(final boolean isEnabled);
    
}
