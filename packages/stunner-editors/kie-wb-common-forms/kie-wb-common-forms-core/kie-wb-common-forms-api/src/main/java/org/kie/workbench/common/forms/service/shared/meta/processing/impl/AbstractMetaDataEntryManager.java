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


package org.kie.workbench.common.forms.service.shared.meta.processing.impl;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.forms.model.MetaDataEntry;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryManager;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryProcessor;

public abstract class AbstractMetaDataEntryManager implements MetaDataEntryManager {

    private Map<String, MetaDataEntryProcessor> processors = new HashMap<>();

    protected void registerProcessor(MetaDataEntryProcessor processor) {
        processors.put(processor.getEntryName(),
                       processor);
    }

    @Override
    public Class<? extends MetaDataEntry> getMetaDataEntryClass(String entryName) {
        MetaDataEntryProcessor processor = getProcessorForEntry(entryName);

        if (processor != null) {
            return processor.getEntryClass();
        }

        return null;
    }

    @Override
    public MetaDataEntryProcessor getProcessorForEntry(MetaDataEntry entry) {
        return getProcessorForEntry(entry.getName());
    }

    @Override
    public MetaDataEntryProcessor getProcessorForEntry(String entryName) {
        return processors.get(entryName);
    }
}
