package org.dashbuilder.client.external;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import elemental2.dom.DomGlobal;
import elemental2.dom.Response;
import elemental2.promise.IThenable;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.ClientDataSetManager;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.json.ExternalDataSetJSONParser;
import org.jboss.resteasy.util.HttpResponseCodes;

@ApplicationScoped
public class ExternalDataSetRegister {

    @Inject
    ClientDataSetManager clientDataSetManager;

    ExternalDataSetJSONParser externalParser;

    private Map<String, ExternalDataSetDef> externalDataSets;

    private Map<String, Double> scheduledTimeouts;

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
            fetch(def, lookup, listener);
        } else {
            listener.notFound();
        }
    }

    public void register(ExternalDataSetDef def) {
        externalDataSets.put(def.getUUID(), def);
    }

    public Optional<ExternalDataSetDef> get(String uuid) {
        return Optional.ofNullable(externalDataSets.get(uuid));
    }

    public void unregister(String uuid) {
        externalDataSets.remove(uuid);
    }

    public void clear() {
        externalDataSets.clear();
    }

    private void fetch(ExternalDataSetDef def, DataSetLookup lookup, DataSetReadyCallback listener) {
        DomGlobal.fetch(def.getUrl()).then((Response response) -> {
            response.text().then(responseText -> {
                if (response.status == HttpResponseCodes.SC_OK) {
                    return register(lookup, listener, responseText);
                }
                return notAbleToRetrieveDataSet(def, listener);

            }, error -> notAbleToRetrieveDataSet(def, listener));
            return null;
        }).catch_(e -> notAbleToRetrieveDataSet(def, listener));
    }

    private IThenable<Object> register(DataSetLookup lookup,
                                       DataSetReadyCallback listener,
                                       String responseText) {
        var uuid = lookup.getDataSetUUID();
        var def = externalDataSets.get(uuid);
        var dataSet = externalParser.parseDataSet(responseText);

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
        var lookupResult = clientDataSetManager.lookupDataSet(lookup);
        handleCache(uuid);
        listener.callback(lookupResult);
        return null;
    }

    private void handleCache(String uuid) {
        var def = externalDataSets.get(uuid);
        if (def != null && def.isCacheEnabled()) {
            scheduledTimeouts.computeIfPresent(uuid, (k, v) -> {
                DomGlobal.clearTimeout(v);
                return null;
            });
            var id = DomGlobal.setTimeout(params -> {
                clientDataSetManager.removeDataSet(uuid);
                scheduledTimeouts.remove(uuid);
            }, def.getRefreshTimeAmount().toMillis());
            scheduledTimeouts.put(uuid, id);
        } else {
            clientDataSetManager.removeDataSet(uuid);
        }
    }

    private IThenable<Object> notAbleToRetrieveDataSet(ExternalDataSetDef def, DataSetReadyCallback listener) {
        if (def != null) {
            unregister(def.getUUID());
        }
        listener.onError(new ClientRuntimeError("Not able to retrieve data set on client side"));
        return null;
    }

}
