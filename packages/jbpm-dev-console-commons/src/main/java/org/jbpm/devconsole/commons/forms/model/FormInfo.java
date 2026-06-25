/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.devconsole.commons.forms.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class FormInfo {

    public enum FormType {
        HTML("html"),
        TSX("tsx");

        private final String value;

        FormType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static FormType fromExtension(String extension) {
            for (FormType type : values()) {
                if (type.value.equalsIgnoreCase(extension)) {
                    return type;
                }
            }
            return null;
        }
    }

    private String name;
    private FormType type;
    private LocalDateTime lastModified;

    public FormInfo() {
    }

    public FormInfo(String name, FormType type, LocalDateTime lastModified) {
        this.name = name;
        this.type = type;
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormType getType() {
        return type;
    }

    public void setType(FormType type) {
        this.type = type;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FormInfo formInfo = (FormInfo) o;
        return Objects.equals(name, formInfo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
