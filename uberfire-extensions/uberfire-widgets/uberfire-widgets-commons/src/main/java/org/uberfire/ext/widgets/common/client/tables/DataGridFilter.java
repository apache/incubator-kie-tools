/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.widgets.common.client.tables;

import org.uberfire.mvp.Command;

public class DataGridFilter<T> implements Comparable {
    private String key;
    private String filterName;
    private Command filterCommand;
    private boolean visible = true;
    private boolean selected = false;

    public DataGridFilter( String key,
                           String filterName,
                           Command filterClickHandler ) {
        this.key=key;
        this.filterName = filterName;
        this.filterCommand = filterClickHandler;
    }

    public DataGridFilter( String key,
                           String filterName,
                           Command filterCommand,
                           boolean visible ) {
        this( key, filterName, filterCommand );
        this.visible = visible;
    }

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName( String filterName ) {
        this.filterName = filterName;
    }

    public Command getFilterCommand() {
        return filterCommand;
    }

    public void setFilterCommand( Command filterCommand ) {
        this.filterCommand = filterCommand;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof DataGridFilter )) {
            return 0;
        }
        DataGridFilter otherFilter = (DataGridFilter ) o;
        if( filterName!=null && filterName.trim().equals( otherFilter.getFilterName() ) )
            return 0;
        else
            return -1;

    }
}
