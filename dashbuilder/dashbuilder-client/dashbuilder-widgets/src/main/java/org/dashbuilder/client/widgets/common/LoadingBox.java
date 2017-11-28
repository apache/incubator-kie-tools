package org.dashbuilder.client.widgets.common;

import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * <p>A loading box presenter.</p>
 *
 * @since 0.8.0
 */
@ApplicationScoped
public class LoadingBox {

    public interface View {
        void show(String message);
        void close();
    }

    View view;

    public LoadingBox() {
    }

    @Inject
    public LoadingBox(View view) {
        this.view = view;
    }

    public void show() {
        view.show(DataSetEditorConstants.INSTANCE.loading());
    }
    
    public void hide() {
        view.close();
    }
    
}
