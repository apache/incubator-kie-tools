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


package org.kie.workbench.common.forms.processing.engine.handling.impl.model;

import java.util.Map;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyAgent;
import org.jboss.errai.databinding.client.PropertyType;

public class ModelProxy extends Model implements BindableProxy {

    private Model model;

    public ModelProxy(Model model) {
        this.model = model;
    }

    @Override
    public BindableProxyAgent getBindableProxyAgent() {
        return null;
    }

    @Override
    public void updateWidgets() {

    }

    @Override
    public Object deepUnwrap() {
        return model;
    }

    @Override
    public Object get(String propertyName) {
        if("value".equals(propertyName)) {
            return model.getValue();
        } else if ("user".equals(propertyName)) {
            return model.getUser();
        }
        return null;
    }

    @Override
    public void set(String propertyName,
                    Object value) {
        if("value".equals(propertyName)) {
            model.setValue((Integer) value);
        } else if ("user".equals(propertyName)) {
            model.setUser((User) value);
        }
    }

    @Override
    public Map<String, PropertyType> getBeanProperties() {
        return null;
    }

    @Override
    public Object unwrap() {
        return null;
    }
}
