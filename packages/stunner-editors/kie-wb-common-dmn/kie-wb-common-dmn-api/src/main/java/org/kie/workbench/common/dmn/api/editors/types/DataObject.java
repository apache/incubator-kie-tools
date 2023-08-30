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

package org.kie.workbench.common.dmn.api.editors.types;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DataObject {

    private String classType;

    private List<DataObjectProperty> properties;

    public DataObject() {
        this("");
    }

    public DataObject(final String classType) {
        this.classType = classType;
        this.properties = new ArrayList<>();
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(final String classType) {
        this.classType = classType;
    }

    public List<DataObjectProperty> getProperties() {
        return properties;
    }

    public void setProperties(final List<DataObjectProperty> properties) {
        this.properties = properties;
    }

    public String getClassNameWithoutPackage() {

        int lastIndex = 0;
        if (classType.contains(".")) {
            lastIndex = classType.lastIndexOf('.') + 1;
        }

        return classType.substring(lastIndex);
    }
}

