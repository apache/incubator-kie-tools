/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.dmn.api.definition.v1_1.common.HasTypeRefHelper.getFlatHasTypeRefs;

@Portable
public class Relation extends Expression {

    private java.util.List<InformationItem> column;
    private java.util.List<List> row;

    public Relation() {
        this(new Id(),
             new Description(),
             new QName(),
             null,
             null);
    }

    public Relation(final Id id,
                    final Description description,
                    final QName typeRef,
                    final java.util.List<InformationItem> column,
                    final java.util.List<List> row) {
        super(id,
              description,
              typeRef);
        this.column = column;
        this.row = row;
    }

    public java.util.List<InformationItem> getColumn() {
        if (column == null) {
            column = new ArrayList<>();
        }
        return this.column;
    }

    public java.util.List<List> getRow() {
        if (row == null) {
            row = new ArrayList<>();
        }
        return this.row;
    }

    @Override
    public java.util.List<HasTypeRef> getHasTypeRefs() {

        final java.util.List<HasTypeRef> hasTypeRefs = super.getHasTypeRefs();

        hasTypeRefs.addAll(getFlatHasTypeRefs(getColumn()));
        hasTypeRefs.addAll(getFlatHasTypeRefs(getRow()));

        return hasTypeRefs;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Relation)) {
            return false;
        }

        final Relation that = (Relation) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (typeRef != null ? !typeRef.equals(that.typeRef) : that.typeRef != null) {
            return false;
        }
        if (column != null ? !column.equals(that.column) : that.column != null) {
            return false;
        }
        return row != null ? row.equals(that.row) : that.row == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0,
                                         column != null ? column.hashCode() : 0,
                                         row != null ? row.hashCode() : 0);
    }
}
