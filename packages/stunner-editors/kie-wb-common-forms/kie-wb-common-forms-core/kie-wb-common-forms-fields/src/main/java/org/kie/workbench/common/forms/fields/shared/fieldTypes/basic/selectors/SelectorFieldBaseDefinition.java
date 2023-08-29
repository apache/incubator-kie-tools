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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors;

import java.util.List;

import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.HasDefaultValue;
import org.kie.workbench.common.forms.model.RefreshOnFieldChange;

public abstract class SelectorFieldBaseDefinition<OPTION extends SelectorOption<TYPE>, TYPE> extends AbstractFieldDefinition implements HasDefaultValue<TYPE>,
                                                                                                                                        RefreshOnFieldChange {

    @SkipFormField
    protected String dataProvider = "";

    @SkipFormField
    protected String relatedField;

    public SelectorFieldBaseDefinition(String className) {
        super(className);
    }

    public abstract List<OPTION> getOptions();

    public abstract void setOptions(List<OPTION> options);

    public String getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(String dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public String getRelatedField() {
        return relatedField;
    }

    @Override
    public void setRelatedField(String relatedField) {
        this.relatedField = relatedField;
    }

    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof SelectorFieldBaseDefinition) {
            if ((standaloneClassName == null && other.getStandaloneClassName() == null) ||
                    (standaloneClassName != null && standaloneClassName.equals(other.getStandaloneClassName()))) {
                SelectorFieldBaseDefinition otherSelector = (SelectorFieldBaseDefinition) other;
                setOptions(otherSelector.getOptions());
                setDataProvider(otherSelector.getDataProvider());
                setDefaultValue((TYPE) otherSelector.getDefaultValue());
                setRelatedField(otherSelector.getRelatedField());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SelectorFieldBaseDefinition<?, ?> that = (SelectorFieldBaseDefinition<?, ?>) o;

        if (dataProvider != null ? !dataProvider.equals(that.dataProvider) : that.dataProvider != null) {
            return false;
        }
        if (relatedField != null ? !relatedField.equals(that.relatedField) : that.relatedField != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dataProvider != null ? dataProvider.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (relatedField != null ? relatedField.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
