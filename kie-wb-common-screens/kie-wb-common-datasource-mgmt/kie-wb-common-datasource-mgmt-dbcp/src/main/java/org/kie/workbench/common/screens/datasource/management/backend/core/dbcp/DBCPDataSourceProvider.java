/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.backend.core.dbcp;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSource;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceProvider;
import org.kie.workbench.common.screens.datasource.management.backend.core.impl.AbstractDataSource;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceStatus;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.util.MavenArtifactResolver;
import org.kie.workbench.common.screens.datasource.management.util.URLConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Multi-platform implementation of a DataSourceProvider.
 */
@ApplicationScoped
@Named(value = "DBCPDataSourceProvider")
public class DBCPDataSourceProvider
        implements DataSourceProvider {

    private static final Logger logger = LoggerFactory.getLogger(DBCPDataSourceProvider.class);

    private DBCPDriverProvider driverProvider;

    private MavenArtifactResolver artifactResolver;

    private Map< String, DBCPDataSource > deploymentRegistry = new HashMap<>();

    private Map< String, DataSourceDeploymentInfo > deploymentInfos = new HashMap<>();

    private Map< String, DataSourceDef > deployedDataSources = new HashMap<>();

    public DBCPDataSourceProvider() {
    }

    @Inject
    public DBCPDataSourceProvider(DBCPDriverProvider driverProvider,
                                  MavenArtifactResolver artifactResolver) {
        this.driverProvider = driverProvider;
        this.artifactResolver = artifactResolver;
    }

    @Override
    public DataSourceDeploymentInfo deploy(DataSourceDef dataSourceDef) throws Exception {

        DriverDef driverDef = null;
        for (DriverDef _driverDef : driverProvider.getDeployments()) {
            if (_driverDef.getUuid().equals(dataSourceDef.getDriverUuid())) {
                driverDef = _driverDef;
                break;
            }
        }

        if (driverDef == null) {
            throw new Exception("Required driver: " + dataSourceDef.getDriverUuid() + " is not deployed");
        }

        final URI uri = artifactResolver.resolve(driverDef.getGroupId(),
                                                 driverDef.getArtifactId(),
                                                 driverDef.getVersion());
        if (uri == null) {
            throw new Exception("Unable to get driver library artifact for driver: " + driverDef);
        }

        final Properties properties = new Properties();
        properties.setProperty("user",
                               dataSourceDef.getUser());
        properties.setProperty("password",
                               dataSourceDef.getPassword());
        final URLConnectionFactory urlConnectionFactory = buildConnectionFactory(uri,
                                                                                 driverDef.getDriverClass(),
                                                                                 dataSourceDef.getConnectionURL(),
                                                                                 properties);

        //Connection Factory that the pool will use for creating connections.
        ConnectionFactory connectionFactory = new DBCPConnectionFactory(urlConnectionFactory);

        //Poolable connection factory
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,
                                                                                            null);

        //The pool to be used by the ConnectionFactory
        ObjectPool< PoolableConnection > connectionPool = new GenericObjectPool<>(poolableConnectionFactory);

        //Set the factory's pool property to the owning pool
        poolableConnectionFactory.setPool(connectionPool);

        //Finally create DataSource
        PoolingDataSource< PoolableConnection > dataSource = new PoolingDataSource<>(connectionPool);

        DataSourceDeploymentInfo deploymentInfo = new DataSourceDeploymentInfo(dataSourceDef.getUuid(),
                                                                               true,
                                                                               dataSourceDef.getUuid(),
                                                                               false);

        deploymentRegistry.put(deploymentInfo.getDeploymentId(),
                               new DBCPDataSource(dataSource));
        deploymentInfos.put(deploymentInfo.getDeploymentId(),
                            deploymentInfo);
        deployedDataSources.put(deploymentInfo.getDeploymentId(),
                                dataSourceDef);

        return deploymentInfo;
    }

    @Override
    public DataSourceDeploymentInfo resync(final DataSourceDef dataSourceDef,
                                           final DataSourceDeploymentInfo deploymentInfo) throws Exception {
        //no more processing required for this driver.
        return deploymentInfo;
    }

    @Override
    public void undeploy(DataSourceDeploymentInfo deploymentInfo) throws Exception {
        DataSourceDeploymentInfo currentDeploymentInfo = deploymentInfos.get(deploymentInfo.getDeploymentId());
        if (currentDeploymentInfo == null) {
            throw new Exception("DataSource: " + deploymentInfo.getUuid() + " is not deployed");
        }

        DBCPDataSource dataSource = deploymentRegistry.remove(
                currentDeploymentInfo.getDeploymentId());
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (Exception e) {
                logger.warn("An error was produced during datasource close",
                            e);
            }
        }
        deploymentRegistry.remove(currentDeploymentInfo.getDeploymentId());
        deployedDataSources.remove(currentDeploymentInfo.getDeploymentId());
        deploymentInfos.remove(currentDeploymentInfo.getDeploymentId());
    }

    @Override
    public DataSourceDeploymentInfo getDeploymentInfo(String uuid) throws Exception {
        return deploymentInfos.get(uuid);
    }

    @Override
    public List< DataSourceDeploymentInfo > getDeploymentsInfo() throws Exception {
        List< DataSourceDeploymentInfo > result = new ArrayList<>();
        result.addAll(deploymentInfos.values());
        return result;
    }

    @Override
    public List< DataSourceDef > getDeployments() throws Exception {
        List< DataSourceDef > result = new ArrayList<>();
        result.addAll(deployedDataSources.values());
        return result;
    }

    @Override
    public void loadConfig(Properties properties) {
    }

    @Override
    public DataSource lookupDataSource(DataSourceDeploymentInfo deploymentInfo) throws Exception {
        DBCPDataSource dataSource = deploymentRegistry.get(deploymentInfo.getDeploymentId());
        if (dataSource != null) {
            if (dataSource.isNew()) {
                //first access to the data source
                dataSource.setStatus(DataSourceStatus.REFERENCED);
            }
            DataSourceDeploymentInfo _deploymentInfo = deploymentInfos.get(deploymentInfo.getDeploymentId());
            if (_deploymentInfo != null) {
                DataSourceDeploymentInfo updatedDeploymentInfo = new DataSourceDeploymentInfo(
                        deploymentInfo.getDeploymentId(),
                        true,
                        deploymentInfo.getUuid(),
                        true);
                deploymentInfos.put(deploymentInfo.getDeploymentId(),
                                    updatedDeploymentInfo);
            }
            return dataSource;
        } else {
            throw new Exception("Data source for: " + deploymentInfo + " is not deployed in current system.");
        }
    }

    @Override
    public void hasStarted() throws Exception {
        //no additional checks are required for this provider.
    }

    /**
     * facilitates tests programming.
     */
    protected URLConnectionFactory buildConnectionFactory(URI uri,
                                                          String driverClass,
                                                          String connectionURL,
                                                          Properties connectionProperties) throws Exception {
        return new URLConnectionFactory(uri.toURL(),
                                        driverClass,
                                        connectionURL,
                                        connectionProperties);
    }

    private class DBCPConnectionFactory
            implements ConnectionFactory {

        URLConnectionFactory urlConnectionFactory;

        public DBCPConnectionFactory(URLConnectionFactory urlConnectionFactory) {
            this.urlConnectionFactory = urlConnectionFactory;
        }

        @Override
        public Connection createConnection() throws SQLException {
            return urlConnectionFactory.createConnection();
        }
    }

    private class DBCPDataSource extends AbstractDataSource {

        public DBCPDataSource(PoolingDataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Connection getConnection() throws Exception {
            return dataSource.getConnection();
        }

        public void setStatus(DataSourceStatus status) {
            this.status = status;
            notifyStatusChange(status);
        }

        public void close() throws Exception {
            ((PoolingDataSource) dataSource).close();
        }
    }
}