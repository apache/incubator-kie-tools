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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import java.util.Objects;

import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.util.UUID;

@Bindable
public class KeyValueRow {

    private String key;

    private String value;

    private String uuid;

    public KeyValueRow() {
        this("", "");
    }

    public KeyValueRow(String key, String value) {
        this.key = Objects.isNull(key) ? "" : key;
        this.value = Objects.isNull(value) ? "" : value;
        this.uuid = UUID.uuid();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyValueRow)) {
            return false;
        }
        KeyValueRow that = (KeyValueRow) o;
        return Objects.equals(getKey(), that.getKey()) &&
                Objects.equals(getValue(), that.getValue()) &&
                Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(getKey(), getValue(), uuid);
    }
}
