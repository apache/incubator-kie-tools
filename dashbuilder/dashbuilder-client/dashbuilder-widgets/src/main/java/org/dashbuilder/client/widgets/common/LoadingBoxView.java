package org.dashbuilder.client.widgets.common;

import org.uberfire.ext.widgets.common.client.common.BusyPopup;

/**
 * <p>A loading box view using <code>org.uberfire.ext.widgets.common.client.common.BusyPopup</code>.</p>
 *
 * @since 0.8.0
 */
public class LoadingBoxView implements LoadingBox.View {

    @Override
    public void show(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void close() {
        BusyPopup.close();
    }


}
