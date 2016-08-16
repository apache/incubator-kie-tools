/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.forms.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

@Portable
public class FormDefinition {
    private String id;
    private String name;

    private List<FieldDefinition> fields = new ArrayList<FieldDefinition>(  );
    private List<DataHolder> dataHolders = new ArrayList<DataHolder>(  );

    private LayoutTemplate layoutTemplate;

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<DataHolder> getDataHolders() {
        return dataHolders;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public LayoutTemplate getLayoutTemplate() {
        return layoutTemplate;
    }

    public void setLayoutTemplate( LayoutTemplate layoutTemplate ) {
        this.layoutTemplate = layoutTemplate;
    }

    public void addDataHolder (DataHolder dataH) {
        dataHolders.add( dataH );
    }

    public void removeDataHolder( String holderName ) {
        for (Iterator<DataHolder> it = dataHolders.iterator(); it.hasNext();) {
            DataHolder dataHolder = it.next();
            if (dataHolder.getName().equals( holderName ) ) {
                it.remove();
                return;
            }
        }
    }

    public FieldDefinition getFieldByName( String name ) {
        for (FieldDefinition definition : fields ) {
            if (definition.getName().equals( name )) {
                return definition;
            }
        }
        return null;
    }

    public FieldDefinition getFieldById(String fieldId) {
        for (FieldDefinition definition : fields ) {
            if (definition.getId().equals( fieldId )) {
                return definition;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = ~~result;
        return result;
    }

    public DataHolder getDataHolderByName(String name) {
        for (DataHolder holder : dataHolders) {
            if (holder.getName().equals(name)) return holder;
        }

        return null;
    }
}
