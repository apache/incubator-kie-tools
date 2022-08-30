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
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSetFactory;
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
    CSVParser csvParser;

    ExternalDataSetJSONParser externalParser;

    private Map<String, ExternalDataSetDef> externalDataSets;

    private Map<String, Double> scheduledTimeouts;

    SupportedMimeType DEFAULT_TYPE = SupportedMimeType.JSON;

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
                register(lookup, listener, def.getContent(), SupportedMimeType.JSON);
            } else {
                fetch(def, lookup, listener);
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

    private void fetch(ExternalDataSetDef def, DataSetLookup lookup, DataSetReadyCallback listener) {
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
            response.text().then(responseText -> {
                if (response.status == HttpResponseCodes.SC_OK) {
                    return register(lookup,
                            listener,
                            responseText,
                            mimeType);
                }
                return notAbleToRetrieveDataSet(def, listener);

            }, error -> notAbleToRetrieveDataSet(def, listener));
            return null;
        }).catch_(e -> notAbleToRetrieveDataSet(def, listener));
    }

    private IThenable<Object> register(final DataSetLookup lookup,
                                       final DataSetReadyCallback listener,
                                       final String responseText,
                                       final SupportedMimeType contentType) {
        var uuid = lookup.getDataSetUUID();
        var def = externalDataSets.get(uuid);
        var content = contentType.tranformer.apply(responseText);

        if (def.getExpression() != null && !def.getExpression().trim().isEmpty()) {
            try {
                content = applyExpression(def.getExpression(), content);
            } catch (Exception e) {
                listener.onError(new ClientRuntimeError("Error evaluating dataset expression", e));
                return null;
            }
        }

        var dataSet = DataSetFactory.newEmptyDataSet();
        try {
            dataSet = externalParser.parseDataSet(content);
        } catch (Exception e) {
            listener.onError(new ClientRuntimeError("Error parsing dataset: " + e.getMessage(), e));
            return null;
        }

        if (def != null && !def.getColumns().isEmpty()) {
            for (int i = 0; i < def.getColumns().size(); i++) {
                var defColumn = def.getColumns().get(i);
                var dsColumn = dataSet.getColumnByIndex(i);
                dsColumn.setId(defColumn.getId());
                dsColumn.setColumnType(defColumn.getColumnType());
            }
        }

        dataSet.setUUID(uuid);
        clientDataSetManager.registerDataSet(dataSet);
        var lookupResult = DataSetFactory.newEmptyDataSet();
        try {
            lookupResult = clientDataSetManager.lookupDataSet(lookup);
        } catch (Exception e) {
            listener.onError(new ClientRuntimeError("Error during dataset lookup: " + e.getMessage(), e));
            return null;
        }
        handleCache(uuid);
        listener.callback(lookupResult);
        return null;
    }

    private void handleCache(String uuid) {
        var def = externalDataSets.get(uuid);
        scheduledTimeouts.computeIfPresent(uuid, (k, v) -> {
            DomGlobal.clearTimeout(v);
            return null;
        });
        if (def != null && def.isCacheEnabled()) {
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
