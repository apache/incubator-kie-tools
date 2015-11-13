/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.common.client.tables.popup;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.paging.AbstractPageRow;

import java.io.Serializable;


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
