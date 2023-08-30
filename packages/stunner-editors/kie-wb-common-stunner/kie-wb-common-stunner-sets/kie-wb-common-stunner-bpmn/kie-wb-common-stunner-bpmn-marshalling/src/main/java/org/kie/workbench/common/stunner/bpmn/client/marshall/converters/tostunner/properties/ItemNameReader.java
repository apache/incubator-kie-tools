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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.Property;

public class ItemNameReader {

    String name;

    public static ItemNameReader from(ItemAwareElement element) {
        return new ItemNameReader(element);
    }

    private ItemNameReader(ItemAwareElement element) {
        if (element instanceof Property) {
            name = ((Property) element).getName();
        } else if (element instanceof DataInput) {
            name = ((DataInput) element).getName();
        } else if (element instanceof DataOutput) {
            name = ((DataOutput) element).getName();
        }
        // legacy uses ID instead of name
        name = name == null ? element.getId() : name;
    }

    public String getName() {
        return name;
    }
}