/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.def;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.validation.groups.BeanDataSetDefValidation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BeanDataSetDef extends DataSetDef {

    @NotNull(groups = {BeanDataSetDefValidation.class})
    @Size(min = 1, groups = {BeanDataSetDefValidation.class})
    protected String generatorClass;
    protected Map<String,String> paramaterMap = new HashMap<String,String>();

    public BeanDataSetDef() {
        super.setProvider(DataSetProviderType.BEAN);
    }

    public String getGeneratorClass() {
        return generatorClass;
    }

    public void setGeneratorClass(String generatorClass) {
        this.generatorClass = generatorClass;
    }

    public Map<String, String> getParamaterMap() {
        return paramaterMap;
    }

    public void setParamaterMap(Map<String, String> paramaterMap) {
        this.paramaterMap = paramaterMap;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            BeanDataSetDef other = (BeanDataSetDef) obj;
            if (!super.equals(other)) {
                return false;
            }

            if (generatorClass != null && !generatorClass.equals(other.generatorClass)) {
                return false;
            }
            if (paramaterMap.size() != other.paramaterMap.size()) {
                return false;
            }
            for (String key : paramaterMap.keySet()) {
                String value = paramaterMap.get(key);
                if (!other.paramaterMap.containsKey(key)) {
                    return false;
                }
                if (!other.paramaterMap.get(key).equals(value)) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                generatorClass,
                paramaterMap);
    }

    @Override
    public DataSetDef clone() {
        BeanDataSetDef def = new BeanDataSetDef();
        clone(def);
        def.setGeneratorClass(getGeneratorClass());
        def.setParamaterMap(getParamaterMap());
        return def;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("UUID=").append(UUID).append("\n");
        out.append("Provider=").append(provider).append("\n");
        out.append("Public=").append(isPublic).append("\n");
        out.append("Push enabled=").append(pushEnabled).append("\n");
        out.append("Push max size=").append(pushMaxSize).append(" Kb\n");
        out.append("Generator class=").append(generatorClass).append("\n");
        for (String param : paramaterMap.keySet()) {
            out.append("Generator ").append(param).append("=").append(paramaterMap.get(param)).append("\n");
        }
        return out.toString();
    }
}
