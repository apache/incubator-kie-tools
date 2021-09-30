package org.dashbuilder.dataset.backend;

import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;

import java.util.List;

/**
 * <p>Response model for a DataSetDef edition.</p>
 * <p>Provides a cloned DataSetDef instance from the original one and the original column definition list.</p>
 */
public class EditDataSetDef {

    private DataSetDef definition;
    private List<DataColumnDef> columns;

    public EditDataSetDef() {
        
    }

    public EditDataSetDef(final DataSetDef definition, final List<DataColumnDef> columns) {
        this.definition = definition;
        this.columns = columns;
    }

    public DataSetDef getDefinition() {
        return definition;
    }

    public List<DataColumnDef> getColumns() {
        return columns;
    }
    
}
