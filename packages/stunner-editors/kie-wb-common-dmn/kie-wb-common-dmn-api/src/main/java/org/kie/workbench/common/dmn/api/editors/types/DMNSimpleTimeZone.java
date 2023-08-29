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

package org.kie.workbench.common.dmn.api.editors.types;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DMNSimpleTimeZone {

    private String id;
    private double offset;
    private String offsetString;

    public DMNSimpleTimeZone() {
        this.id = "";
        this.offset = 0;
        this.offsetString = "";
    }

    public DMNSimpleTimeZone(final String id,
                             final double offset,
                             final String offsetString) {
        this.id = id;
        this.offset = offset;
        this.offsetString = offsetString;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(final double offSet) {
        this.offset = offSet;
    }

    public String getOffsetString() {
        return offsetString;
    }

    public void setOffsetString(final String offsetString) {
        this.offsetString = offsetString;
    }
}