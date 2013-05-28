/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.enums.client.editor;

import org.uberfire.paging.AbstractPageRow;

public class EnumRow extends AbstractPageRow {

    private String fieldName = "";
    private String factName = "";
    private String context = "";

    public EnumRow() {
    }

    public EnumRow( final String factName,
                    final String fieldName,
                    final String context ) {
        this.factName = factName;
        this.fieldName = fieldName;
        this.context = context;
    }

    public String getFactName() {
        return factName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getContext() {
        return context;
    }

    public void setFactName( final String factName ) {
        this.factName = factName;

    }

    public void setFieldName( final String fieldName ) {
        this.fieldName = fieldName;
    }

    public void setContext( final String context ) {
        this.context = context;
    }

    public boolean isValid() {
        return isValid( factName ) && isValid( fieldName ) && isValid( context );
    }

    private boolean isValid( final String value ) {
        return !( value == null || value.isEmpty() );
    }

    @Override
    public String toString() {
        return "'" + factName + "." + fieldName + "' : " + context;
    }
}
