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
import org.dashbuilder.dataset.validation.groups.SQLDataSetDefDbSQLValidation;
import org.dashbuilder.dataset.validation.groups.SQLDataSetDefDbTableValidation;
import org.dashbuilder.dataset.validation.groups.SQLDataSetDefValidation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class SQLDataSetDef extends DataSetDef {

    @NotNull(groups = {SQLDataSetDefValidation.class})
    @Size(min = 1, groups = {SQLDataSetDefValidation.class})
    protected String dataSource;

    protected String dbSchema;

    @NotNull(groups = {SQLDataSetDefDbTableValidation.class})
    @Size(min = 1, groups = {SQLDataSetDefDbTableValidation.class})
    protected String dbTable;

    @NotNull(groups = {SQLDataSetDefDbSQLValidation.class})
    @Size(min = 1, groups = {SQLDataSetDefDbSQLValidation.class})
    protected String dbSQL;
    
    protected boolean estimateSize = true;

    public SQLDataSetDef() {
        super.setProvider(DataSetProviderType.SQL);
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDbTable() {
        return dbTable;
    }

    public void setDbTable(String dbTable) {
        this.dbTable = dbTable;
    }

    public String getDbSchema() {
        return dbSchema;
    }

    public void setDbSchema(String dbSchema) {
        this.dbSchema = dbSchema;
    }

    public String getDbSQL() {
        return dbSQL;
    }

    public void setDbSQL(String dbSQL) {
        this.dbSQL = dbSQL;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public Integer getCacheMaxRows() {
        return cacheMaxRows;
    }

    public void setCacheMaxRows(Integer cacheMaxRows) {
        this.cacheMaxRows = cacheMaxRows;
    }

    public void setEstimateSize(boolean estimateSize) {
        this.estimateSize = estimateSize;
    }

    public boolean isEstimateSize() {
        return estimateSize;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            SQLDataSetDef other = (SQLDataSetDef) obj;
            if (!super.equals(other)) {
                return false;
            }
            if (dataSource != null && !dataSource.equals(other.dataSource)) {
                return false;
            }
            if (dbSchema != null && !dbSchema.equals(other.dbSchema)) {
                return false;
            }
            if (dbTable != null && !dbTable.equals(other.dbTable)) {
                return false;
            }
            if (dbSQL != null && !dbSQL.equals(other.dbSQL)) {
                return false;
            }
            if(estimateSize != other.estimateSize){
                return false;
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                dataSource,
                dbSchema,
                dbTable,
                dbSQL);
    }

    @Override
    public DataSetDef clone() {
        SQLDataSetDef def = new SQLDataSetDef();
        clone(def);
        def.setDataSource(getDataSource());
        def.setDbSchema(getDbSchema());
        def.setDbTable(getDbTable());
        def.setDbSQL(getDbSQL());
        def.setEstimateSize(isEstimateSize());
        return def;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("UUID=").append(UUID).append("\n");
        out.append("Provider=").append(provider).append("\n");
        out.append("Public=").append(isPublic).append("\n");
        out.append("Push enabled=").append(pushEnabled).append("\n");
        out.append("Push max size=").append(pushMaxSize).append(" Kb\n");
        if (refreshTime != null) {
            out.append("Refresh time=").append(refreshTime).append("\n");
            out.append("Refresh always=").append(refreshAlways).append("\n");
        }
        out.append("Data source=").append(dataSource).append("\n");
        if (dbSchema != null) out.append("DB Schema=").append(dbSchema).append("\n");
        out.append("DB Table=").append(dbTable).append("\n");
        out.append("DB SQL=").append(dbSQL).append("\n");
        out.append("Get all columns=").append(allColumnsEnabled).append("\n");
        out.append("Cache enabled=").append(cacheEnabled).append("\n");
        out.append("Cache max rows=").append(cacheMaxRows).append(" Kb\n");
        out.append("Estimate size=").append(estimateSize).append("\n");
        return out.toString();
    }
}
