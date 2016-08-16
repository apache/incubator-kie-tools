/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.model.impl.basic.selectors;

import java.util.List;

import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class SelectorFieldBase<T extends SelectorOption> extends FieldDefinition {

    protected String dataProvider = "";

    protected SelectorFieldBase( String code ) {
        super( code );
    }

    public abstract List<T> getOptions();

    public abstract void setOptions( List<T> options );

    public String getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider( String dataProvider ) {
        this.dataProvider = dataProvider;
    }

    protected void doCopyFrom( FieldDefinition other ) {
        if ( other instanceof SelectorFieldBase ) {
            if ( (standaloneClassName == null && other.getStandaloneClassName() == null ) ||
                    ( standaloneClassName != null &&  standaloneClassName.equals( other.getStandaloneClassName() ) ) ) {
                SelectorFieldBase otherSelector = (SelectorFieldBase) other;
                setOptions( otherSelector.getOptions() );
                setDataProvider( otherSelector.getDataProvider() );
            }
        }
    }
}
