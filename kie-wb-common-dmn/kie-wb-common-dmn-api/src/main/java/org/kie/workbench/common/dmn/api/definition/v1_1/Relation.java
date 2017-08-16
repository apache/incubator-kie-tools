/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@Portable
public class Relation extends Expression {

    private java.util.List<InformationItem> column;
    private java.util.List<List> row;

    public Relation() {
        this(new Id(),
             new Description(),
             new QName(),
             new ArrayList<>(),
             new ArrayList<>());
    }

    public Relation(final @MapsTo("id") Id id,
                    final @MapsTo("description") Description description,
                    final @MapsTo("typeRef") QName typeRef,
                    final @MapsTo("column") java.util.List<InformationItem> column,
                    final @MapsTo("row") java.util.List<List> row) {
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
}
