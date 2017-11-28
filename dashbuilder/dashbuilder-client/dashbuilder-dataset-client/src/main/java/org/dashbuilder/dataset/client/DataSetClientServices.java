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
package org.dashbuilder.dataset.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.common.client.backend.PathUrlFactory;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.client.resources.i18n.CommonConstants;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.engine.group.IntervalBuilderLocator;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.dashbuilder.dataset.events.DataSetModifiedEvent;
import org.dashbuilder.dataset.events.DataSetPushOkEvent;
import org.dashbuilder.dataset.events.DataSetPushingEvent;
import org.dashbuilder.dataset.events.DataSetStaleEvent;
import org.dashbuilder.dataset.group.AggregateFunctionManager;
import org.dashbuilder.dataset.service.DataSetDefServices;
import org.dashbuilder.dataset.service.DataSetExportServices;
import org.dashbuilder.dataset.service.DataSetLookupServices;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Data set services for clients.
 * <p>It hides to client widgets where the data sets are stored and how they are fetched and processed.</p>
 */
@ApplicationScoped
public class DataSetClientServices {

    private ClientDataSetManager clientDataSetManager;
    private PathUrlFactory pathUrlFactory;
    private AggregateFunctionManager aggregateFunctionManager;
    private IntervalBuilderLocator intervalBuilderLocator;
    private Event<DataSetPushingEvent> dataSetPushingEvent;
    private Event<DataSetPushOkEvent> dataSetPushOkEvent;
    private Event<DataSetModifiedEvent> dataSetModifiedEvent;
    private Caller<DataSetLookupServices> dataSetLookupServices;
    private Caller<DataSetDefServices> dataSetDefServices;
    private Caller<DataSetExportServices> dataSetExportServices;

    /**
     * A cache of DataSetMetadata instances
     */
    private Map<String, DataSetMetadata> remoteMetadataMap = new HashMap<String, DataSetMetadata>();

    /**
     * If enabled then remote data set can be pushed to clients.
     */
    private boolean pushRemoteDataSetEnabled = true;

    /**
     * It holds a set of data set push requests in progress.
     */
    private Map<String, DataSetPushHandler> pushRequestMap = new HashMap<String, DataSetPushHandler>();

    public DataSetClientServices() {
    }

    @Inject
    public DataSetClientServices(ClientDataSetManager clientDataSetManager,
                                 PathUrlFactory pathUrlFactory,
                                 AggregateFunctionManager aggregateFunctionManager,
                                 IntervalBuilderLocator intervalBuilderLocator,
                                 Event<DataSetPushingEvent> dataSetPushingEvent,
                                 Event<DataSetPushOkEvent> dataSetPushOkEvent,
                                 Event<DataSetModifiedEvent> dataSetModifiedEvent,
                                 Caller<DataSetLookupServices> dataSetLookupServices,
                                 Caller<DataSetDefServices> dataSetDefServices,
                                 Caller<DataSetExportServices> dataSetExportServices) {

        this.clientDataSetManager = clientDataSetManager;
        this.pathUrlFactory = pathUrlFactory;
        this.aggregateFunctionManager = aggregateFunctionManager;
        this.intervalBuilderLocator = intervalBuilderLocator;
        this.dataSetPushingEvent = dataSetPushingEvent;
        this.dataSetPushOkEvent = dataSetPushOkEvent;
        this.dataSetModifiedEvent = dataSetModifiedEvent;
        this.dataSetLookupServices = dataSetLookupServices;
        this.dataSetDefServices = dataSetDefServices;
        this.dataSetExportServices = dataSetExportServices;
    }

    public boolean isPushRemoteDataSetEnabled() {
        return pushRemoteDataSetEnabled;
    }

    /**
     * Enable/disable the ability to push remote data sets from server.
     */
    public void setPushRemoteDataSetEnabled(boolean pushRemoteDataSetEnabled) {
        this.pushRemoteDataSetEnabled = pushRemoteDataSetEnabled;
    }

    /**
     * Fetch the metadata instance for the specified data set.
     *
     * @param uuid The UUID of the data set
     * @throws Exception It there is an unexpected error trying to execute the lookup request.
     */
    public void fetchMetadata(final String uuid,
                              final DataSetMetadataCallback listener) throws Exception {
        final DataSetMetadata metadata = clientDataSetManager.getDataSetMetadata(uuid);

        if (metadata != null) {
            listener.callback(metadata);
        } else if (dataSetLookupServices != null) {
            if (remoteMetadataMap.containsKey(uuid)) {
                listener.callback(remoteMetadataMap.get(uuid));
            } else {
                dataSetLookupServices.call((DataSetMetadata result) -> {
                                               if (result == null) {
                                                   listener.notFound();
                                               } else {
                                                   remoteMetadataMap.put(uuid,
                                                                         result);
                                                   listener.callback(result);
                                               }
                                           },
                                           (message, throwable) -> {
                                               return listener.onError(new ClientRuntimeError(throwable));
                                           }).lookupDataSetMetadata(uuid);
            }
        } else {
            listener.notFound();
        }
    }

    Map<String, DataSetMetadata> getRemoteMetadataMap() {
        return remoteMetadataMap;
    }

    /**
     * Get the cached metadata instance for the specified data set.
     *
     * @param uuid The UUID of the data set. Null if the metadata is not stored on client yet.
     */
    public DataSetMetadata getMetadata(String uuid) {
        DataSetMetadata metadata = clientDataSetManager.getDataSetMetadata(uuid);
        if (metadata != null) {
            return metadata;
        }

        return remoteMetadataMap.get(uuid);
    }

    /**
     * Export a data set, specified by a data set lookup request, to CSV format.
     *
     * @param request The data set lookup request
     * @throws Exception It there is an unexpected error during the export.
     */
    public void exportDataSetCSV(final DataSetLookup request,
                                 final DataSetExportReadyCallback listener) throws Exception {

        if (dataSetLookupServices != null) {
            // Look always into the client data set manager.
            if (clientDataSetManager.getDataSet(request.getDataSetUUID()) != null) {
                DataSet dataSet = clientDataSetManager.lookupDataSet(request);
                dataSetExportServices.call(
                        new RemoteCallback<Path>() {
                            public void callback(Path csvFilePath) {
                                listener.exportReady(csvFilePath);
                            }
                        }
                        ,
                        new ErrorCallback<Message>() {
                            public boolean error(Message message,
                                                 Throwable throwable) {
                                listener.onError(new ClientRuntimeError(throwable));
                                return true;
                            }
                        }).exportDataSetCSV(dataSet);
            }
            // Data set not found on client.
            else {
                // If the data set is not in client, then look up remotely (only if the remote access is available).
                try {
                    dataSetExportServices.call(
                            new RemoteCallback<Path>() {
                                public void callback(Path csvFilePath) {
                                    listener.exportReady(csvFilePath);
                                }
                            }
                            ,
                            new ErrorCallback<Message>() {
                                public boolean error(Message message,
                                                     Throwable throwable) {
                                    listener.onError(new ClientRuntimeError(throwable));
                                    return true;
                                }
                            }).exportDataSetCSV(request);
                } catch (Exception e) {
                    listener.onError(new ClientRuntimeError(e));
                }
            }
        } else {
            listener.onError(new ClientRuntimeError(CommonConstants.INSTANCE.exc_no_client_side_data_export()));
        }
    }

    /**
     * Export a data set, specified by a data set lookup request, to Excel format.
     *
     * @param request The data set lookup request
     * @throws Exception It there is an unexpected error during the export.
     */
    public void exportDataSetExcel(final DataSetLookup request,
                                   final DataSetExportReadyCallback listener) throws Exception {

        if (dataSetLookupServices != null) {
            // Look always into the client data set manager.
            if (clientDataSetManager.getDataSet(request.getDataSetUUID()) != null) {
                DataSet dataSet = clientDataSetManager.lookupDataSet(request);
                try {
                    dataSetExportServices.call(
                            new RemoteCallback<Path>() {
                                public void callback(Path excelFilePath) {
                                    listener.exportReady(excelFilePath);
                                }
                            }).exportDataSetExcel(dataSet);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            // Data set not found on client.
            else {
                // If the data set is not in client, then look up remotely (only if the remote access is available).
                try {
                    dataSetExportServices.call(
                            new RemoteCallback<Path>() {
                                public void callback(Path excelFilePath) {
                                    listener.exportReady(excelFilePath);
                                }
                            }
                            ,
                            new ErrorCallback<Message>() {
                                public boolean error(Message message,
                                                     Throwable throwable) {
                                    listener.onError(new ClientRuntimeError(throwable));
                                    return true;
                                }
                            }).exportDataSetExcel(request);
                } catch (Exception e) {
                    listener.onError(new ClientRuntimeError(e));
                }
            }
        } else {
            listener.onError(new ClientRuntimeError(CommonConstants.INSTANCE.exc_no_client_side_data_export()));
        }
    }

    /**
     * Creates a brand new data set definition for the provider type specified
     *
     * @param type The provider type
     * @return A data set definition instance
     */
    public void newDataSet(DataSetProviderType type,
                           RemoteCallback<DataSetDef> callback) throws Exception {
        dataSetDefServices.call(callback).createDataSetDef(type);
    }

    /**
     * Process the specified data set lookup request for a given definition.
     *
     * @param def     The data set definition
     * @param request The data set lookup request
     * @throws Exception It there is an unexpected error trying to execute the lookup request.
     */
    public void lookupDataSet(final DataSetDef def,
                              final DataSetLookup request,
                              final DataSetReadyCallback listener) throws Exception {

        if (dataSetLookupServices != null) {
            try {
                dataSetLookupServices.call(
                        new RemoteCallback<DataSet>() {
                            public void callback(DataSet result) {
                                if (result == null) {
                                    listener.notFound();
                                } else {
                                    listener.callback(result);
                                }
                            }
                        },
                        new ErrorCallback<Message>() {
                            @Override
                            public boolean error(Message message,
                                                 Throwable throwable) {
                                return listener.onError(new ClientRuntimeError(throwable));
                            }
                        })
                        .lookupDataSet(def,
                                       request);
            } catch (Exception e) {
                listener.onError(new ClientRuntimeError(e));
            }
        }
        // Data set not found on client.
        else {
            listener.notFound();
        }
    }

    /**
     * Process the specified data set lookup request.
     *
     * @param request The data set lookup request
     * @throws Exception It there is an unexpected error trying to execute the lookup request.
     */
    public void lookupDataSet(final DataSetLookup request,
                              final DataSetReadyCallback listener) throws Exception {

        // Look always into the client data set manager.
        if (clientDataSetManager.getDataSet(request.getDataSetUUID()) != null) {
            DataSet dataSet = clientDataSetManager.lookupDataSet(request);
            listener.callback(dataSet);
        }
        // If the data set is not in client, then look up remotely (only if the remote access is available).
        else if (dataSetLookupServices != null) {

            // First of all, get the target data set estimated size.
            fetchMetadata(request.getDataSetUUID(),
                          new DataSetMetadataCallback() {
                              public void callback(DataSetMetadata metatada) {

                                  // Push the data set to client if and only if the push feature is enabled, the data set is
                                  // pushable & the data set is smaller than the max push size defined.
                                  DataSetDef dsetDef = metatada.getDefinition();
                                  int estimatedSize = metatada.getEstimatedSize() / 1000;
                                  boolean isPushable = dsetDef != null && dsetDef.isPushEnabled() && estimatedSize < dsetDef.getPushMaxSize();
                                  if (pushRemoteDataSetEnabled && isPushable) {

                                      // Check if a push is already in progress.
                                      // (This is necessary in order to avoid repeating multiple push requests over the same data set).
                                      DataSetPushHandler pushHandler = pushRequestMap.get(request.getDataSetUUID());
                                      if (pushHandler == null) {
                                          // Create a push handler.
                                          pushHandler = new DataSetPushHandler(metatada);

                                          // Send the lookup request to the server...
                                          DataSetLookup lookupSourceDataSet = new DataSetLookup(request.getDataSetUUID());
                                          _lookupDataSet(lookupSourceDataSet,
                                                         pushHandler);
                                      }
                                      // Register the lookup request into the current handler.
                                      pushHandler.registerLookup(request,
                                                                 listener);
                                  }
                                  // Lookup the remote data set otherwise.
                                  else {
                                      _lookupDataSet(request,
                                                     listener);
                                  }
                              }

                              // Data set metadata not found
                              public void notFound() {
                                  listener.notFound();
                              }

                              @Override
                              public boolean onError(final ClientRuntimeError error) {
                                  return listener.onError(error);
                              }
                          });
        }
        // Data set not found on client.
        else {
            listener.notFound();
        }
    }

    private void _lookupDataSet(DataSetLookup request,
                                final DataSetReadyCallback listener) {
        try {
            dataSetLookupServices.call(
                    new RemoteCallback<DataSet>() {
                        public void callback(DataSet result) {
                            if (result == null) {
                                listener.notFound();
                            } else {
                                listener.callback(result);
                            }
                        }
                    },
                    new ErrorCallback<Message>() {
                        @Override
                        public boolean error(Message message,
                                             Throwable throwable) {
                            return listener.onError(new ClientRuntimeError(throwable));
                        }
                    })
                    .lookupDataSet(request);
        } catch (Exception e) {
            listener.onError(new ClientRuntimeError(e));
        }
    }

    /**
     * @since 0.3.0.Final
     * @deprecated Use <i>getPublicDataSetDefs</i> instead
     */
    public void getRemoteSharedDataSetDefs(RemoteCallback<List<DataSetDef>> callback) {
        getPublicDataSetDefs(callback);
    }

    public void getPublicDataSetDefs(RemoteCallback<List<DataSetDef>> callback) {
        try {
            dataSetDefServices.call(callback).getPublicDataSetDefs();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AggregateFunctionManager getAggregateFunctionManager() {
        return aggregateFunctionManager;
    }

    public IntervalBuilderLocator getIntervalBuilderLocator() {
        return intervalBuilderLocator;
    }

    // Classes for the handling of concurrent lookup requests over any push-able data set

    private void onDataSetStaleEvent(@Observes DataSetStaleEvent event) {
        checkNotNull("event",
                     event);
        String uuid = event.getDataSetDef().getUUID();

        // Remove any stale data existing on the client.
        // This will force next lookup requests to push a refreshed data set.
        clientDataSetManager.removeDataSet(uuid);
        remoteMetadataMap.remove(uuid);

        // If a data set has been updated on the sever then fire an event.
        // In this case the notification is always send, no matter whether the data set is pushed to the client or not.
        dataSetModifiedEvent.fire(new DataSetModifiedEvent(event.getDataSetDef()));
    }

    private void onDataSetRemovedEvent(@Observes DataSetDefRemovedEvent event) {
        checkNotNull("event",
                     event);
        String uuid = event.getDataSetDef().getUUID();
        clientDataSetManager.removeDataSet(uuid);
        remoteMetadataMap.remove(uuid);

        // If a data set has been updated on the sever then fire an event.
        // In this case the notification is always send, no matter whether the data set is pushed to the client or not.
        dataSetModifiedEvent.fire(new DataSetModifiedEvent(event.getDataSetDef()));
    }

    // Catch backend events

    /**
     * <p>Returns the download URL for a given file provided by a servlet method.</p>
     *
     * @param path The path of the file.
     */
    public String getDownloadFileUrl(final Path path) {
        return pathUrlFactory.getDownloadFileUrl(path);
    }

    /**
     * <p>Returns the upload URL for a given file provided by a servlet method.</p>
     *
     * @param path The path of the file.
     */
    public String getUploadFileUrl(String path) {
        return pathUrlFactory.getUploadFileUrl(path);
    }

    private class DataSetPushHandler implements DataSetReadyCallback {

        private DataSetMetadata dataSetMetadata = null;
        private List<DataSetLookupListenerPair> listenerList = new ArrayList<DataSetLookupListenerPair>();

        private DataSetPushHandler(DataSetMetadata metadata) {
            this.dataSetMetadata = metadata;

            pushRequestMap.put(dataSetMetadata.getUUID(),
                               this);

            dataSetPushingEvent.fire(new DataSetPushingEvent(dataSetMetadata));
        }

        public void registerLookup(DataSetLookup lookup,
                                   DataSetReadyCallback listener) {
            listenerList.add(new DataSetLookupListenerPair(lookup,
                                                           listener));
        }

        public void callback(DataSet dataSet) {
            pushRequestMap.remove(dataSetMetadata.getUUID());

            clientDataSetManager.registerDataSet(dataSet);

            dataSetPushOkEvent.fire(new DataSetPushOkEvent(dataSetMetadata));

            for (DataSetLookupListenerPair pair : listenerList) {
                DataSet result = clientDataSetManager.lookupDataSet(pair.lookup);
                pair.listener.callback(result);
            }
        }

        public void notFound() {
            pushRequestMap.remove(dataSetMetadata.getUUID());

            for (DataSetLookupListenerPair pair : listenerList) {
                pair.listener.notFound();
            }
        }

        @Override
        public boolean onError(final ClientRuntimeError error) {
            boolean t = false;
            for (DataSetLookupListenerPair pair : listenerList) {
                if (pair.listener.onError(error)) {
                    t = true;
                }
            }
            return t;
        }
    }

    private class DataSetLookupListenerPair {

        DataSetLookup lookup;
        DataSetReadyCallback listener;

        private DataSetLookupListenerPair(DataSetLookup lookup,
                                          DataSetReadyCallback listener) {
            this.lookup = lookup;
            this.listener = listener;
        }
    }
}
