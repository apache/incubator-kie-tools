/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.dev.util.HttpHeaders;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.Headers;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.promise.IThenable;
import org.dashbuilder.client.external.transformer.JSONAtaInjector;
import org.dashbuilder.client.external.transformer.JSONAtaTransformer;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.ClientDataSetManager;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.json.ExternalDataSetJSONParser;
import org.jboss.resteasy.util.HttpResponseCodes;

@ApplicationScoped
public class ExternalDataSetClientProvider {

    @Inject
    ClientDataSetManager clientDataSetManager;

    @Inject
    ExternalDataCallbackCoordinator dataSetCallbackCoordinator;

    ExternalDataSetJSONParser externalParser;

    private Map<String, ExternalDataSetDef> externalDataSets;

    private Map<String, Double> scheduledTimeouts;

    private static final SupportedMimeType DEFAULT_TYPE = SupportedMimeType.JSON;

    @PostConstruct
    public void setup() {
        externalDataSets = new HashMap<>();
        scheduledTimeouts = new HashMap<>();

        var format = DateTimeFormat.getFormat(PredefinedFormat.ISO_8601);
        externalParser = new ExternalDataSetJSONParser(format::parse);
    }

    public void fetchAndRegister(String uuid, DataSetLookup lookup, DataSetReadyCallback listener) {
        var defOp = get(uuid);
        if (defOp.isPresent()) {
            var def = defOp.get();
            if (def.getContent() != null && def.getUrl() == null) {
                register(def, new DataSetReadyCallback() {

                    @Override
                    public boolean onError(ClientRuntimeError error) {
                        return listener.onError(error);
                    }

                    @Override
                    public void notFound() {
                        listener.notFound();

                    }

                    @Override
                    public void callback(DataSet dataSet) {
                        doLookup(lookup, listener);
                    }
                }, def.getContent(), SupportedMimeType.JSON);

            } else {
                dataSetCallbackCoordinator.getCallback(def,
                        new DataSetReadyCallback() {

                            @Override
                            public boolean onError(ClientRuntimeError error) {
                                return listener.onError(error);
                            }

                            @Override
                            public void notFound() {
                                listener.notFound();
                            }

                            @Override
                            public void callback(DataSet dataSet) {
                                doLookup(lookup, listener);
                            }
                        },
                        callback -> fetch(def, callback),
                        () -> handleCache(def.getUUID()));
            }
        } else {
            listener.notFound();
        }
    }

    public void register(ExternalDataSetDef def) {
        var existingDef = externalDataSets.get(def.getUUID());
        if (existingDef == null || !def.equals(existingDef)) {
            clientDataSetManager.removeDataSet(def.getUUID());
            externalDataSets.put(def.getUUID(), def);
        }
    }

    public Optional<ExternalDataSetDef> get(String uuid) {
        return Optional.ofNullable(externalDataSets.get(uuid));
    }

    public void unregister(String uuid) {
        clearRegisteredDataSets();
        externalDataSets.remove(uuid);
    }

    public void clear() {
        clearRegisteredDataSets();
        externalDataSets.clear();
    }

    private void fetch(ExternalDataSetDef def, DataSetReadyCallback callback) {
        var req = RequestInit.create();
        if (def.getHeaders() != null) {
            var headers = new Headers();
            def.getHeaders().forEach(headers::append);
            req.setHeaders(headers);
        }

        DomGlobal.fetch(def.getUrl(), req).then((Response response) -> {
            var contentType = response.headers.get(HttpHeaders.CONTENT_TYPE);
            var mimeType = SupportedMimeType.byMimeTypeOrUrl(contentType, def.getUrl())
                    .orElse(DEFAULT_TYPE);
            return response.text().then(responseText -> {
                if (response.status == HttpResponseCodes.SC_OK) {
                    return register(def, callback, responseText, mimeType);
                } else {
                    var exception = buildExceptionForResponse(responseText, response);
                    return notAbleToRetrieveDataSet(def, callback, exception);
                }

            }, error -> notAbleToRetrieveDataSet(def, callback));
        }).catch_(e -> notAbleToRetrieveDataSet(def, callback,
                new RuntimeException("Request not started, make sure that CORS is enabled.\nMessage: " + e)));
    }

    private Throwable buildExceptionForResponse(String responseText, Response response) {
        var sb = new StringBuilder("The dataset URL is unreachable with status ");
        sb.append(response.status);
        sb.append(" - ");
        sb.append(response.statusText);

        if (responseText != null && !responseText.trim().isEmpty()) {
            sb.append("\n");
            sb.append("Response Text: ");
            sb.append(responseText);
        }
        return new RuntimeException(sb.toString());
    }

    private IThenable<Object> register(ExternalDataSetDef def,
                                       final DataSetReadyCallback callback,
                                       final String responseText,
                                       final SupportedMimeType contentType) {
        DataSet dataSet = null;
        var content = contentType.tranformer.apply(responseText);

        if (def.getType() != null && StringUtils.isBlank(def.getExpression())) {
            def.setExpression(def.getType().getExpression());
        }

        if (def.getExpression() != null && !def.getExpression().trim().isEmpty()) {
            try {
                content = applyExpression(def.getExpression(), content);
            } catch (Exception e) {
                callback.onError(new ClientRuntimeError("Error evaluating dataset expression", e));
                return null;
            }
        } else if (def.getColumns().isEmpty()) {
            var columns = contentType.columnsFunction.apply(responseText);
            def.setColumns(columns);
        }

        try {
            dataSet = externalParser.parseDataSet(content);
        } catch (Exception e) {
            callback.onError(new ClientRuntimeError("Error parsing dataset: " + e.getMessage(), e));
            return null;
        }

        if (!def.getColumns().isEmpty()) {
            for (int i = 0; i < def.getColumns().size(); i++) {
                var defColumn = def.getColumns().get(i);
                var dsColumn = dataSet.getColumnByIndex(i);
                dsColumn.setId(defColumn.getId());
                dsColumn.setColumnType(defColumn.getColumnType());
            }
        }

        var existingDs = clientDataSetManager.getDataSet(def.getUUID());
        if (def.isAccumulate() && existingDs != null) {
            // no new data, so keep existing data set
            if (dataSet.getRowCount() == 0) {
                dataSet = existingDs;
            } else {
                accumulateDataSet(dataSet, existingDs);
            }
        }
        dataSet.setDefinition(def);
        dataSet.setUUID(def.getUUID());
        clientDataSetManager.registerDataSet(dataSet);
        callback.callback(dataSet);
        return null;
    }

    void accumulateDataSet(DataSet dataSet, DataSet existingDs) {
        if (dataSet.getRowCount() > 0 && !existingDs.getColumns().equals(dataSet.getColumns())) {
            throw new RuntimeException("New data is not compatible with existing data.");
        }
        for (int i = dataSet.getRowCount(), j = 0; i < existingDs.getDefinition().getCacheMaxRows() && j < existingDs
                .getRowCount(); i++, j++) {
            final int row = j;
            var values = existingDs.getColumns()
                    .stream()
                    .map(cl -> existingDs.getValueAt(row, cl.getId()))
                    .toArray(Object[]::new);
            dataSet.addValues(values);
        }
    }

    private void doLookup(DataSetLookup lookup, DataSetReadyCallback listener) {
        try {
            var result = clientDataSetManager.lookupDataSet(lookup);
            listener.callback(result);
        } catch (Exception e) {
            listener.onError(new ClientRuntimeError("Error during data set lookup", e));
        }
    }

    private void handleCache(String uuid) {
        var def = externalDataSets.get(uuid);
        if (def == null || def.isAccumulate()) {
            return;
        }
        scheduledTimeouts.computeIfPresent(uuid, (k, v) -> {
            DomGlobal.clearTimeout(v);
            return null;
        });
        if (def.isCacheEnabled()) {
            var refreshTimeAmount = def.getRefreshTimeAmount();
            if (refreshTimeAmount != null) {
                var id = DomGlobal.setTimeout(params -> {
                    clientDataSetManager.removeDataSet(uuid);
                    scheduledTimeouts.remove(uuid);
                }, refreshTimeAmount.toMillis());
                scheduledTimeouts.put(uuid, id);
            }
        } else {
            clientDataSetManager.removeDataSet(uuid);
        }
    }

    private IThenable<Object> notAbleToRetrieveDataSet(ExternalDataSetDef def, DataSetReadyCallback listener) {
        return notAbleToRetrieveDataSet(def, listener, new RuntimeException("Unknown Error"));
    }

    private IThenable<Object> notAbleToRetrieveDataSet(ExternalDataSetDef def,
                                                       DataSetReadyCallback listener,
                                                       Throwable e) {
        if (def != null) {
            unregister(def.getUUID());
        }
        listener.onError(new ClientRuntimeError("Not able to retrieve dataset content", e));
        return null;
    }

    private String applyExpression(String expression, String responseText) {
        JSONAtaInjector.ensureJSONAtaInjected();
        var json = Global.JSON.parse(responseText);
        var result = JSONAtaTransformer.jsonata(expression).evaluate(json);
        return Global.JSON.stringify(result);
    }

    private void clearRegisteredDataSets() {
        externalDataSets.keySet().forEach(d -> clientDataSetManager.removeDataSet(d));
    }
}
