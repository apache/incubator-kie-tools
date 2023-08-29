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


package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.IsElement;

public abstract class FieldEditorPresenter<T> {

    public interface ValueChangeHandler<T> {

        void onValueChange(T oldValue,
                           T newValue);
    }

    protected T value;

    protected List<ValueChangeHandler<T>> changeHandlers = new ArrayList<>();

    protected abstract IsElement getView();

    public abstract void init();

    public abstract void setReadOnly(final boolean readOnly);

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void addChangeHandler(ValueChangeHandler<T> changeHandler) {
        if (!changeHandlers.contains(changeHandler)) {
            changeHandlers.add(changeHandler);
        }
    }

    protected void notifyChange(T oldValue,
                                T newValue) {
        changeHandlers.forEach(changeHandler -> changeHandler.onValueChange(oldValue,
                                                                            newValue));
    }
}
