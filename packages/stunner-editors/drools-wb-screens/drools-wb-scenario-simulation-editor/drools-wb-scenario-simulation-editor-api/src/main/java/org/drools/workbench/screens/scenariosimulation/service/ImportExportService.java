/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.workbench.screens.scenariosimulation.service;

import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;

/**
 * This interface define the service to export and import data from a <code>AbstractScesimModel</code>
 */
public interface ImportExportService {

    /**
     * This method export the given <code>AbstractScesimModel</code> to the requested type
     * @param type
     * @param scesimModel
     * @return
     */
    Object exportScesimModel(ImportExportType type, AbstractScesimModel<? extends AbstractScesimData> scesimModel);

    /**
     * This method parse the raw value and return a new <code>AbstractScesimModel</code>. The <b>originalSimulation</b> can be used to retrieve
     * some metadata not available in the export (i.e. FactMapping)
     * @param type
     * @param raw
     * @param originalScesimModel
     * @return
     */
    <T extends AbstractScesimData> AbstractScesimModel<T> importScesimModel(ImportExportType type, Object raw, AbstractScesimModel<T> originalScesimModel);
}
