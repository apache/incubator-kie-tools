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

public abstract class PropertyAdapterWrapper<T, V, A extends PropertyAdapter<T, V>> implements PropertyAdapter<T, V> {

    protected A adapter;

    protected PropertyAdapterWrapper() {
        this(null);
    }

    public PropertyAdapterWrapper(A adapter) {
        this.adapter = adapter;
    }

    @Override
    public String getId(T pojo) {
        return adapter.getId(pojo);
    }

    @Override
    public String getCaption(T pojo) {
        return adapter.getCaption(pojo);
    }

    @Override
    public V getValue(T pojo) {
        return adapter.getValue(pojo);
    }

    @Override
    public void setValue(T pojo,
                         V value) {
        adapter.setValue(pojo,
                         value);
    }

    @Override
    public int getPriority() {
        return adapter.getPriority();
    }

    @Override
    public boolean accepts(Class<?> type) {
        return adapter.accepts(type);
    }
}
