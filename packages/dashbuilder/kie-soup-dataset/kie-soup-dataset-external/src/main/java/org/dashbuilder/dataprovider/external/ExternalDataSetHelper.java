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

package org.dashbuilder.dataprovider.external;

import org.dashbuilder.dataset.def.ExternalDataSetDef;

public class ExternalDataSetHelper {
    
    private static final String DATASET_URL_PROP = "dashbuilder.dataset.%s.url";
    
    private ExternalDataSetHelper() {
        // do nothing
    }

    public static String getUrl(ExternalDataSetDef def) {
        var uuidProp = String.format(DATASET_URL_PROP, def.getUUID());
        var nameProp = String.format(DATASET_URL_PROP, def.getName());
        var urlByDefUUID = System.getProperty(uuidProp);
        var urlByDefName = System.getProperty(nameProp);

        if (urlByDefUUID != null) {
            return urlByDefUUID;
        }

        if (urlByDefName != null) {
            return urlByDefName;
        }
        if (def.getUrl() != null) {
            return def.getUrl();
        }

        throw new IllegalArgumentException("URL for DataSet definition " + def.getName() + "not set.");
    }
}