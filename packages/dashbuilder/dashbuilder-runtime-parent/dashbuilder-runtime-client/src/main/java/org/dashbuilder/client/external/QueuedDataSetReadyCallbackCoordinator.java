package org.dashbuilder.client.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import elemental2.dom.DomGlobal;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.def.DataSetDef;

@ApplicationScoped
public class QueuedDataSetReadyCallbackCoordinator {

    Map<DataSetDef, QueuedDataSetReadyCallback> queueMap;

    @PostConstruct
    void setup() {
        queueMap = new HashMap<>();
    }

    public DataSetReadyCallback getCallback(DataSetDef def, DataSetReadyCallback callback) {
        if (!queueMap.containsKey(def)) {
            var _queuedCallback = new QueuedDataSetReadyCallback(() -> queueMap.remove(def));
            queueMap.put(def, _queuedCallback);
        }
        var queuedCallback = queueMap.get(def);
        queuedCallback.addDataSetReadyCallback(callback);
        return queueMap.get(def);
    }

    public class QueuedDataSetReadyCallback implements DataSetReadyCallback {

        List<DataSetReadyCallback> queue;

        Runnable cleanUpCallback;

        public QueuedDataSetReadyCallback(Runnable cleanUpCallback) {
            this.cleanUpCallback = cleanUpCallback;
            this.queue = new ArrayList<>();
        }

        public void addDataSetReadyCallback(DataSetReadyCallback callback) {
            queue.add(callback);
        }

        @Override
        public void callback(DataSet dataSet) {
            queue.forEach(c -> {
                try {
                    c.callback(dataSet);
                } catch (Exception e) {
                    DomGlobal.console.warn("Not able to run dataset callback: " + e.getMessage());
                }
            });
            cleanUp();
        }

        @Override
        public void notFound() {
            queue.forEach(c -> {
                try {
                    c.notFound();
                } catch (Exception e) {
                    DomGlobal.console.warn("Not able to run not found: " + e.getMessage());
                }
            });
            queue.clear();
        }

        @Override
        public boolean onError(ClientRuntimeError error) {
            var result = queue.stream().allMatch(c -> {
                try {
                    return c.onError(error);
                } catch (Exception e) {
                    DomGlobal.console.warn("Not able to run onError on data set callback: " + e.getMessage());
                    return false;
                }
            });
            queue.clear();
            return result;
        }

        private void cleanUp() {
            queue.clear();
            cleanUpCallback.run();
        }

    }

}
