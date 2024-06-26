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

package org.kogito.core.internal.api;

/**
 *  Must be synchronized with JavaCodeCompletionAccessor.ts 
 */
public class GetPublicResult {

    /**
     * The Java Class that cointans the below accessor in FQCN format (eg. `org.Author`)
     */
    private String fqcn;
    /**
     * Accessor's name (eg. `getName()` if a method, `name` if a field
     */
    private String accessor;
    /**
     * Accessor's type in FQCN format (eg. `java.lang.String`)
     */
    private String type;

    public String getFqcn() {
        return fqcn;
    }

    public void setFqcn(String fqcn) {
        this.fqcn = fqcn;
    }

    public String getAccessor() {
        return accessor;
    }

    public void setAccessor(String accessor) {
        this.accessor = accessor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
