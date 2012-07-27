/*
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is a row of data from a table.
 */
public class TableDataRow
    implements
    IsSerializable,
    Comparable<TableDataRow> {

    /**
     * The unique ID for the resource. Most likely a UUID
     */
    public String   id;

    /**
     * The type of resource (eg DRL rule, business rule etc).
     */
    public String   format;

    /**
     * The actual values to display We will assume that the first one is the
     * display name when opening.
     */
    public String[] values;

    public String getDisplayName() {
        return values[0];
    }

    /**
     * Returns a key that can be used to drive an "open" event. Use getId and
     * getType to break it apart.
     */
    public String getKeyValue() {
        return id
               + ","
               + format;
    }

    /**
     * Gets the ID from the key value.
     */
    public static String getId(String key) {
        return key.split( "\\," )[0];
    }

    /**
     * Gets the format from the keyvalue
     */
    public static String getFormat(String key) {
        return key.split( "\\," )[1];
    }

    public int compareTo(TableDataRow other) {
        return id.compareTo( other.id );
    }

}
