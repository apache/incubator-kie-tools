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
package org.uberfire.ext.widgets.common.client.tables;

import org.uberfire.mvp.Command;

public class DataGridFilter<T> implements Comparable {
    private String key;
    private Command filterCommand;


    public DataGridFilter( String key,
                           Command filterCommand ) {
        this.key=key;
        this.filterCommand = filterCommand;
    }

    public DataGridFilter( String key,String filterName,
                           Command filterCommand ) {
        this.key=key;
        this.filterCommand = filterCommand;
    }

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }


    public Command getFilterCommand() {
        return filterCommand;
    }

    public void setFilterCommand( Command filterCommand ) {
        this.filterCommand = filterCommand;
    }


    @Override
    public int compareTo(Object o) {
        if (!(o instanceof DataGridFilter )) {
            return 0;
        }
        DataGridFilter otherFilter = (DataGridFilter ) o;
        if( key!=null && key.trim().equals( otherFilter.getKey() ) )
            return 0;
        else
            return -1;

    }
}
