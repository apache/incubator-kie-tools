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


package org.kie.workbench.common.forms.model.impl.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.model.MetaDataEntry;
import org.kie.workbench.common.forms.model.ModelMetaData;

@Portable
public class ModelMetaDataImpl implements ModelMetaData {

    private List<MetaDataEntry> entries = new ArrayList<>();

    @Override
    public Collection<MetaDataEntry> getEntries() {
        return entries;
    }

    @Override
    public MetaDataEntry getEntry(String name) {
        return entries.stream()
                .filter(entry -> entry.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addEntry(MetaDataEntry entry) {
        entries.add(entry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ModelMetaDataImpl that = (ModelMetaDataImpl) o;

        return entries.equals(that.entries);
    }

    @Override
    public int hashCode() {
        int result = entries.hashCode();
        result = ~~result;
        return result;
    }
}
