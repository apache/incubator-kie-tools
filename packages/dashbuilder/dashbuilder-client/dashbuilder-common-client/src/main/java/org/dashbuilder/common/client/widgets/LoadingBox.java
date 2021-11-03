package org.dashbuilder.common.client.widgets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.common.client.resources.i18n.DashbuilderCommonConstants;

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
        view.show(DashbuilderCommonConstants.INSTANCE.loading());
    }
    
    public void hide() {
        view.close();
    }
    
}
