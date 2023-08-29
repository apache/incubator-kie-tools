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


package org.jboss.errai.reflections.scanners;

/** scans fields and methods and stores fqn as key and elements as values */
@SuppressWarnings({"unchecked"})
public class TypeElementsScanner extends AbstractScanner {
    public void scan(Object cls) {
        //avoid scanning JavaCodeSerializer outputs
        if (TypesScanner.isJavaCodeSerializer(getMetadataAdapter().getInterfacesNames(cls))) return;

        String className = getMetadataAdapter().getClassName(cls);

        for (Object field : getMetadataAdapter().getFields(cls)) {
            String fieldName = getMetadataAdapter().getFieldName(field);
            getStore().put(className, fieldName);
        }

        for (Object method : getMetadataAdapter().getMethods(cls)) {
            getStore().put(className, getMetadataAdapter().getMethodKey(cls, method));
        }
    }
}
