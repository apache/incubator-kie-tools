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

package org.dashbuilder.dataset.def;

import javax.validation.constraints.NotNull;

import org.dashbuilder.dataset.ColumnType;

/**
 * <p>This class is used to define the structure and runtime behaviour of a data set definition's column instance.</p>
 */
public class DataColumnDef {

    @NotNull()
    private String id;
    @NotNull()
    private ColumnType columnType;

    public DataColumnDef() {
    }

    public DataColumnDef(String id, ColumnType columnType) {
        this.id = id;
        this.columnType = columnType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getId() == null) return false;

        try {
            DataColumnDef d = (DataColumnDef) obj;
            return getId().equals(d.getId());
        } catch (ClassCastException e) {
            return false;
        }
    }

    public DataColumnDef clone() {
        return  new DataColumnDef(id, ColumnType.getByName(columnType.name()));
    }
}
