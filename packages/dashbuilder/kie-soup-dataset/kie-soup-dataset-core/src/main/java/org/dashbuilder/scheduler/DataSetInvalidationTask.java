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

package org.dashbuilder.scheduler;

import java.util.function.Consumer;

import org.dashbuilder.dataset.def.DataSetDef;

public class DataSetInvalidationTask extends SchedulerTask {
    
    
    DataSetDef def;
    private Consumer<String> action;
    
    
    /**
     * 
     * Builds a task key for the given def
     * @param def
     * @return
     */
    public static String key(DataSetDef def) {
        return "Invalidate " + def.getUUID();
    }

    
    public DataSetInvalidationTask(DataSetDef def, Consumer<String> action) {
        this.def = def;
        this.action = action;
    }

    @Override
    public String getKey() {
        return key(def);
    }

    @Override
    public String getDescription() {
        return "Cache Invalidation for DataSet Definition " + def.getName();
    }

    @Override
    public void execute() {
        this.action.accept(def.getUUID());
    }
    
}
