/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.workingset.client.factconstraints.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.guvnor.common.services.workingset.client.factconstraints.ConstraintConfiguration;

public class SimpleConstraintConfigurationImpl
        implements ConstraintConfiguration {

    private static final long serialVersionUID = 501l;
    private Map<String, String> args = new HashMap<String, String>();
    private String constraintName = null;
    private String factType;
    private String fieldName;

    public SimpleConstraintConfigurationImpl(ConstraintConfiguration constraintConfiguration) {
        copyFrom(constraintConfiguration);
    }

    public SimpleConstraintConfigurationImpl() {
    }

    public Set<String> getArgumentKeys() {
        return args.keySet();
    }

    public Object getArgumentValue(String key) {
        return args.get(key);
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public String getFactType() {
        return factType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setArgumentValue(String key,
                                 String value) {
        args.put(key,
                 value);
    }

    public void setFactType(String factType) {
        this.factType = factType;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean containsArgument(String key) {
        return args.containsKey(key);
    }

    @Override
    public String toString() {
        return "SimpleConstraintConfigurationImpl [args=" + args + ", constraintName=" + constraintName + ", factType="
                + factType + ", fieldName=" + fieldName + "]";
    }

    private void copyFrom(ConstraintConfiguration other) {
        if (constraintName != null) {
            throw new RuntimeException("can't copy configuration on a configured instance");
        }
        this.constraintName = other.getConstraintName();
        this.factType = other.getFactType();
        this.fieldName = other.getFieldName();
        this.args = new HashMap<String, String>();
        for (String argName : other.getArgumentKeys()) {
            this.args.put(argName,
                          (String) other.getArgumentValue(argName));
        }
    }
}
