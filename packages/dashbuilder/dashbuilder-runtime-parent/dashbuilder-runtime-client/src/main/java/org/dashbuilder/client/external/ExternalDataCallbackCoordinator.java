/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import elemental2.dom.DomGlobal;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.def.DataSetDef;

/**
 * This class avoids problems when multiple datasets attempts to fetch data at the same time, avoiding useless requests.
 *
 */
@ApplicationScoped
public class ExternalDataCallbackCoordinator {

    Map<DataSetDef, QueuedDataSetReadyCallback> queueMap;

    @PostConstruct
    void setup() {
        queueMap = new HashMap<>();
    }

    public DataSetReadyCallback getCallback(DataSetDef def,
                                            DataSetReadyCallback callback,
                                            Consumer<DataSetReadyCallback> action) {
        if (!queueMap.containsKey(def)) {
            var _queuedCallback = new QueuedDataSetReadyCallback(() -> queueMap.remove(def));
            queueMap.put(def, _queuedCallback);
            action.accept(_queuedCallback);
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
            cleanUp();
        }

        @Override
        public boolean onError(ClientRuntimeError error) {
            var result = queue.stream().map(c -> {
                try {
                    return c.onError(error);
                } catch (Exception e) {
                    DomGlobal.console.warn("Not able to run onError on data set callback: " + e.getMessage());
                    return false;
                }
            }).reduce((r1, r2) -> r1 && r2).orElse(false);
            cleanUp();
            return result;
        }

        private void cleanUp() {
            queue.clear();
            cleanUpCallback.run();
        }

    }

}
