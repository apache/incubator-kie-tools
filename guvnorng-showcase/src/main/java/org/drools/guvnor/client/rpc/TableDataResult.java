/*
 * Copyright 2011 JBoss Inc
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
 * This contains the results returned to populate a table/grid.
 */
public class TableDataResult
    implements
    IsSerializable {

    public TableDataRow[] data;
    public long           total = -1;     // -1 means we won't show a total, as
                                           // we just don't know...
    public boolean        hasNext;
    public long           currentPosition; // the current cursor position in the
                                           // result set // TODO this is not of
                                           // the first row but apparently of
                                           // the last row ?

}
