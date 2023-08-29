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

import java.util.Date;
import java.util.Map;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyAgent;
import org.jboss.errai.databinding.client.PropertyType;

public class UserProxy extends User implements BindableProxy<User> {

    private User user;

    public UserProxy(User user) {
        this.user = user;
    }

    @Override
    public User unwrap() {
        return null;
    }

    @Override
    public Object get(String propertyName) {
        if ("name".equals(propertyName)) {
            return user.getName();
        } else if ("lastName".equals(propertyName)) {
            return user.getLastName();
        } else if ("birtDay".equals(propertyName)) {
            return user.getBirtDay();
        } else if ("married".equals(propertyName)) {
            return user.getMarried();
        } else if ("address".equals(propertyName)) {
            return user.getAddress();
        }
        return null;
    }

    @Override
    public void set(String propertyName,
                    Object value) {
        if ("name".equals(propertyName)) {
            user.setName((String) value);
        } else if ("lastName".equals(propertyName)) {
            user.setLastName((String) value);
        } else if ("birtDay".equals(propertyName)) {
            user.setBirtDay((Date) value);
        } else if ("married".equals(propertyName)) {
            user.setMarried((Boolean) value);
        } else if ("address".equals(propertyName)) {
            user.setAddress((String) value);
        }
    }

    @Override
    public Map<String, PropertyType> getBeanProperties() {
        return null;
    }

    @Override
    public BindableProxyAgent<User> getBindableProxyAgent() {
        return null;
    }

    @Override
    public void updateWidgets() {

    }

    @Override
    public User deepUnwrap() {
        return user;
    }
}
