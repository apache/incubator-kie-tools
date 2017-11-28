/**
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
package org.dashbuilder.dataset.service;

import java.io.File;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetDefDeployerCDI;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetManagerCDI;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.exception.DataSetLookupException;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.uuid.UUIDGenerator;
import org.dashbuilder.exception.ExceptionManager;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.api.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Service
public class DataSetLookupServicesImpl implements DataSetLookupServices {

    protected static Logger log = LoggerFactory.getLogger(DataSetLookupServicesImpl.class);
    protected DataSetManagerCDI dataSetManager;
    protected UUIDGenerator uuidGenerator;
    protected DataSetDefDeployerCDI dataSetDefDeployer;
    protected ExceptionManager exceptionManager;

    public DataSetLookupServicesImpl() {
    }

    @Inject
    public DataSetLookupServicesImpl(DataSetManagerCDI dataSetManager,
                                     DataSetDefDeployerCDI dataSetDefDeployer,
                                     ExceptionManager exceptionManager) {
        this.dataSetManager = dataSetManager;
        this.uuidGenerator = DataSetCore.get().getUuidGenerator();
        this.dataSetDefDeployer = dataSetDefDeployer;
        this.exceptionManager = exceptionManager;
    }

    @PostConstruct
    protected void init() {
        // By default, enable the register of data set definitions stored into the deployment folder.
        ServletContext servletContext = RpcContext.getHttpSession().getServletContext();
        if (!dataSetDefDeployer.isRunning() && servletContext != null) {
            String dir = servletContext.getRealPath("WEB-INF/datasets");
            if (dir != null && new File(dir).exists()) {
                dir = dir.replaceAll("\\\\", "/");
                dataSetDefDeployer.deploy(dir);
            }
        }
    }

    public DataSet lookupDataSet(DataSetLookup lookup) throws Exception {
        DataSet _d = null;
        try {
            _d = dataSetManager.lookupDataSet(lookup);
        } catch (DataSetLookupException e) {
            throw exceptionManager.handleException(e);
        }
        
        return _d;
    }

    public DataSet lookupDataSet(DataSetDef def, DataSetLookup lookup) throws Exception {
        try {
            // Although if using a not registered definition, it must have an uuid set for performing lookups.
            if (def.getUUID() == null) {
                final String uuid = uuidGenerator.newUuid();
                def.setUUID(uuid);
                lookup.setDataSetUUID(uuid);
            }
            return dataSetManager.resolveProvider(def)
                    .lookupDataSet(def, lookup);
        } catch (Exception e) {
            throw exceptionManager.handleException(e);
        }
    }

    public DataSetMetadata lookupDataSetMetadata(String uuid) throws Exception {
        DataSetMetadata _d = null;
        try {
            _d = dataSetManager.getDataSetMetadata(uuid);
        } catch (DataSetLookupException e) {
            throw exceptionManager.handleException(e);
        }

        return _d;
    }
}
