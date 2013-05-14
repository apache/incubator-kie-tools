/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;

import java.util.*;

public class DataModelImpl implements DataModel {

    Map<String, DataObject> dataObjects = new HashMap<String, DataObject>();

    public DataModelImpl() {
    }

    @Override
    public Set<DataObject> getDataObjects() {
        HashSet<DataObject> set = new HashSet<DataObject>();
        set.addAll(dataObjects.values());
        return set;
    }

    @Override
    public DataObject getDataObject(String className) {
        return dataObjects.get(className);
    }

    @Override
    public DataObject removeDataObject(String className) {
        return dataObjects.remove(className);
    }

    @Override
    public DataObject addDataObject(String packageName, String name) {
        DataObject dataObject = new DataObjectImpl(packageName, name);
        dataObjects.put(dataObject.getClassName(), dataObject);
        return dataObject;
    }

    @Override
    public DataObject addDataObject(String className) {
        String name = NamingUtils.getInstance().extractClassName(className);
        String packageName = NamingUtils.getInstance().extractPackageName(className);
        return addDataObject(packageName, name);
    }

}