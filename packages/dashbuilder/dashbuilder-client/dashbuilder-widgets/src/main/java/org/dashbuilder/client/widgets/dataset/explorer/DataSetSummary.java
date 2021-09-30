/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.widgets.dataset.explorer;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.resources.i18n.DataSetExplorerConstants;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetMetadataCallback;
import org.dashbuilder.dataset.client.resources.bundles.DataSetClientResources;
import org.dashbuilder.dataset.def.DataSetDef;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * <p>Data Set Summary widget</p>
 * <p>It displays:</p>
 * <ul>
 *     <li>The current cache status</li>
 *     <li>Current size on backend / client side</li>
 * </ul>
 * 
 * @since 0.3.0 
 */
@Dependent
public class DataSetSummary implements IsWidget {

    private final static String ESTIMATIONS_FORMAT = "#,###.0";
    private final static String ICON_COLOR_DEFAULT = "black";
    private final static String ICON_COLOR_ERROR = "red";
    
    public interface View extends UberView<DataSetSummary> {

        /**
         * <p>Displays some data set feature's status in a panel.It produces the loading icon in the status panel to be removed from parent.</p>
         * @param backendCacheStatus The status for the backend cache. If <code>null</code>, the widget for the status will be not displayed.
         * @param pushEnabled The status for the client push feature. If <code>null</code>, the widget for the status will be not displayed.
         * @param refreshStatus The status for the refresh feature. If <code>null</code>, the widget for the status will be not displayed.
         * @return The view instance.
         */
        View showStatusPanel(final Boolean backendCacheStatus, final Boolean pushEnabled,
                             final Boolean refreshStatus);

        /**
         * <p>Displays an icon icon in the size panel such as a loading or error icon.</p>
         * @return The view instance.
         */
        View showSizePanelIcon(final IconType type, final String title, final String color, final boolean spin);
        
        /**
         * <p>Displays some data set feature's sizes in a panel.</p>
         * @param backendSizeRow The formatted size value for backend cache (in rows).
         * @param clientSizeKb The formatted size value for client cache (in KByes).
         * @return The view instance.
         */
        View showSizePanel(final String backendSizeRow, final String clientSizeKb);

    }

    DataSetClientServices clientServices;
    Event<ErrorEvent> errorEvent;
    View view;

    @Inject
    public DataSetSummary(final DataSetClientServices clientServices, 
                          final Event<ErrorEvent> errorEvent, 
                          final View view) {
        this.clientServices = clientServices;
        this.errorEvent = errorEvent;
        this.view = view;
    }
    
    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void show(final DataSetDef def) {
        if (def != null) {
            // Cache status panel.
            final boolean isShowBackendCache = def.getProvider() != null
                    && ( !DataSetProviderType.BEAN.equals(def.getProvider())
                    && !DataSetProviderType.CSV.equals(def.getProvider() ));
            final Boolean isCacheEnabled = isShowBackendCache ? def.isCacheEnabled() : null;
            final boolean isPushEnabled = def.isPushEnabled();
            final boolean isRefreshEnabled = def.getRefreshTime() != null;
            view.showStatusPanel(isCacheEnabled, isPushEnabled, isRefreshEnabled);
            
            
            // Show loading icon on size panel while performing the backend request.
            showLoadingIcon();

            getMetadata(def, new DataSetMetadataCallback() {
                @Override
                public void callback(final DataSetMetadata metadata) {
                    final int estimatedSize = metadata.getEstimatedSize();
                    final int rowCount = metadata.getNumberOfRows();
                    view.showSizePanel(humanReadableRowCount(rowCount) + " " + DataSetExplorerConstants.INSTANCE.rows(), 
                            humanReadableByteCount(estimatedSize));
                }

                @Override
                public void notFound() {
                    showErrorIcon();
                    showError(def.getUUID(), DataSetExplorerConstants.INSTANCE.notFound());
                    
                }

                @Override
                public boolean onError(final ClientRuntimeError error) {
                    showErrorIcon();
                    showError(def.getUUID(), error);
                    return false;
                }
            });
        }
    }

    String humanReadableByteCount(long bytes) {
        final String _b = " " + DataSetExplorerConstants.INSTANCE.bytes();
        int unit = 1024;
        if (bytes < unit) return Long.toString(bytes) + _b;
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp-1) + _b;
        return NumberFormat.getFormat(ESTIMATIONS_FORMAT).format(bytes / Math.pow(unit, exp)) + pre;
    }

    String humanReadableRowCount(long rows) {
        int unit = 1000;
        if (rows < unit) return Long.toString(rows);
        int exp = (int) (Math.log(rows) / Math.log(unit));
        String pre = ("KMGTPE" ).charAt(exp-1) + ("");
        return NumberFormat.getFormat(ESTIMATIONS_FORMAT).format(rows / Math.pow(unit, exp)) + pre;
    }
    
    private void getMetadata(final DataSetDef def, final DataSetMetadataCallback callback) {
        try {
            clientServices.fetchMetadata(def.getUUID(), callback);
        } catch (Exception e) {
            showError(def.getUUID(), e);
        }
    }
    
    void showLoadingIcon() {
        view.showSizePanelIcon(IconType.REFRESH, DataSetExplorerConstants.INSTANCE.loading(), ICON_COLOR_DEFAULT, true);
    }

    void showErrorIcon() {
        view.showSizePanelIcon(IconType.EXCLAMATION_TRIANGLE, DataSetExplorerConstants.INSTANCE.error(), ICON_COLOR_ERROR, false);
    }

    void showError(final String uuid, final ClientRuntimeError error) {
        errorEvent.fire(new ErrorEvent(DataSetSummary.this, error, uuid));
    }
    
    void showError(final String uuid, final Throwable throwable) {
        final String msg = throwable != null ? throwable.getMessage() : DataSetExplorerConstants.INSTANCE.error();
        errorEvent.fire(new ErrorEvent(DataSetSummary.this, msg, uuid));
    }

    void showError(final String uuid, final String message) {
        errorEvent.fire(new ErrorEvent(DataSetSummary.this, message, uuid));
    }

}
