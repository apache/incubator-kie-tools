/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.backend.services.dataset.sql;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.sql.DataSource;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalPropertiesReader;
import io.quarkus.runtime.ShutdownEvent;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SQLDataSourceLoader {

    Logger logger = LoggerFactory.getLogger(SQLDataSourceLoader.class);

    private static final String PREFIX = "dashbuilder";
    private static final String DATASOURCES = PREFIX + ".datasources";
    private static final String DATASOURCE = PREFIX + ".datasource";
    private static final String PREFIX_TEMPLATE = DATASOURCE + ".%s.";

    @Inject
    Config config;

    @ConfigProperty(name = DATASOURCES, defaultValue = "")
    Optional<List<String>> datasourcesProp;

    Map<String, AgroalDataSource> registeredDataSources;

    @PostConstruct
    void loadDataSets() throws SQLException {
        var allProps = new HashMap<String, String>();
        var datasources = datasourcesProp.orElse(Collections.emptyList());

        registeredDataSources = new HashMap<>();
        StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                     .filter(p -> p.startsWith(DATASOURCE))
                     .forEach(k -> allProps.put(k, config.getValue(k, String.class)));

        for (String ds : datasources) {
            var prefix = String.format(PREFIX_TEMPLATE, ds, AgroalPropertiesReader.JDBC_URL);
            var agroalProps = new AgroalPropertiesReader(prefix);
            agroalProps.readProperties(allProps);
            registeredDataSources.put(ds, AgroalDataSource.from(agroalProps.get()));
        }

        if (!registeredDataSources.isEmpty()) {
            logger.info("Registered datasources: {}", registeredDataSources.keySet());
        }

    }

    void stop(@Observes ShutdownEvent shutdown) {
        logger.info("Closing datasources");
        registeredDataSources.values()
                             .forEach(AgroalDataSource::close);
        logger.info("Closing SQL datasources finished.");

    }

    public Optional<DataSource> getDataSource(String name) {
        return Optional.ofNullable(registeredDataSources.get(name));
    }

    public Set<String> datasources() {
        return registeredDataSources.keySet();
    }

}