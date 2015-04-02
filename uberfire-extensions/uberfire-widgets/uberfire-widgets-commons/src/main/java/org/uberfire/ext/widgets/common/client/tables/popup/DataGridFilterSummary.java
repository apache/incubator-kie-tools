package org.uberfire.ext.widgets.common.client.tables.popup;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.paging.AbstractPageRow;

import java.io.Serializable;


@Portable
public class DataGridFilterSummary extends AbstractPageRow implements Serializable {

    private String filterName;

    public DataGridFilterSummary(  ) {

    }

    public DataGridFilterSummary( String filterName ) {
        this.filterName = filterName;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName( String filterName ) {
        this.filterName = filterName;
    }
}
