package org.dashbuilder.displayer.client;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.def.DataSetDef;

/**
 * @since 0.4.0
 */
public class DataSetEditHandler extends DataSetHandlerImpl {

    DataSetDef defEdit;

    public DataSetEditHandler(final DataSetClientServices clientServices, 
                              final DataSetLookup lookup, final DataSetDef defEdit) {
        super(clientServices, lookup);
        this.defEdit = defEdit;
    }

    @Override
    public void lookupDataSet(final DataSetReadyCallback callback) throws Exception {
        lookupCurrent.setTestMode(true);
        clientServices.lookupDataSet(defEdit, lookupCurrent, new DataSetReadyCallback() {
            public void callback(DataSet dataSet) {
                lastLookedUpDataSet = dataSet;
                callback.callback(dataSet);
            }

            public void notFound() {
                callback.notFound();
            }

            @Override
            public boolean onError(final ClientRuntimeError error) {
                return callback.onError(error);
            }
        });
    }
}
