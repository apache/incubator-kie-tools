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


package org.kie.workbench.common.stunner.core.definition.adapter;

/**
 * A Property pojo adapter..
 */
public interface PropertyAdapter<T, V> extends PriorityAdapter {

    /**
     * Returns the property's identifier for a given pojo.
     */
    String getId(T pojo);

    /**
     * Returns the property's caption for a given pojo.
     */
    String getCaption(T pojo);

    /**
     * Returns the property's value for a given pojo.
     */
    V getValue(T pojo);

    /**
     * Update's the property value for a given pojo..
     */
    void setValue(T pojo, V value);
}
