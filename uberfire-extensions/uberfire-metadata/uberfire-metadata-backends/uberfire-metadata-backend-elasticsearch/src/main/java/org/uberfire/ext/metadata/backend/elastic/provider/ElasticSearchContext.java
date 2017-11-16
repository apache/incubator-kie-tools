/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata.backend.elastic.provider;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.backend.elastic.exceptions.MetadataException;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class ElasticSearchContext {

    private static final String PORT = "org.appformer.ext.metadata.elastic.port";
    private static final String HOST = "org.appformer.ext.metadata.elastic.host";
    private static final String CLUSTER = "org.appformer.ext.metadata.elastic.cluster";
    private static final String RETRIES = "org.appformer.ext.metadata.elastic.retries";
    public static final String ES_CLUSTER_NAME = "cluster.name";
    public static final String ES_TRANSPORT_TYPE = "transport.type";
    public static final String ES_NETTY_4 = "netty4";

    private static ElasticSearchContext INSTANCE;
    private final String cluster;
    private final int retries;
    private TransportClient transportClient;
    private String hostname;
    private int port;

    private Logger logger = LoggerFactory.getLogger(ElasticSearchContext.class);

    public static ElasticSearchContext getInstance() {
        if (INSTANCE == null) {
            Map<String, String> properties = new HashMap<String, String>() {{
                put(HOST,
                    System.getProperty(HOST,
                                       "127.0.0.1"));
                put(PORT,
                    System.getProperty(PORT,
                                       "9300"));
                put(CLUSTER,
                    System.getProperty(CLUSTER,
                                       "kie-cluster"));
                put(RETRIES,
                    System.getProperty(RETRIES,
                                       "10"));
            }};
            INSTANCE = new ElasticSearchContext(properties);
        }
        return INSTANCE;
    }

    public static ElasticSearchContext getInstance(Map<String, String> properties) {
        if (INSTANCE == null) {
            INSTANCE = new ElasticSearchContext(properties);
        }
        return INSTANCE;
    }

    private ElasticSearchContext(Map<String, String> properties) {
        this.port = Integer.parseInt(checkNotEmpty("port",
                                                   properties.get(PORT)));
        this.hostname = checkNotEmpty("host",
                                      properties.get(HOST));
        this.cluster = checkNotNull("cluster",
                                    properties.get(CLUSTER));
        this.retries = Integer.parseInt(properties.get(RETRIES));
    }

    private TransportClient createTransportClient(String cluster,
                                                  String hostname,
                                                  int port) {

        int retries = 0;
        TransportClient client = null;
        while (client == null && retries <= this.retries) {
            try {

                logger.info("Creating Elasticsearch transport client");

                Settings settings = Settings.builder()
                        .put(ES_CLUSTER_NAME,
                             cluster)
                        .put(ES_TRANSPORT_TYPE,
                             ES_NETTY_4)
                        .build();
                client = new PreBuiltTransportClient(settings)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostname),
                                                                            port));
            } catch (Exception e) {
                logger.error("Error trying to create Transport Client, retrying",
                             e);
                retries++;
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e1) {
                    logger.error("Error trying to create Transport Client, retrying",
                                 e1);
                }
            }
        }

        if (client == null) {
            throw new MetadataException("Error trying to create Transport Client");
        } else {
            return client;
        }
    }

    public Client getTransportClient() {
        if (this.transportClient == null) {
            this.transportClient = this.createTransportClient(cluster,
                                                              hostname,
                                                              port);
        }
        return this.transportClient;
    }

    public void destroy() {
        this.transportClient.close();
        this.transportClient = null;
    }
}
