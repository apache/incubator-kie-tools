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


package org.kie.workbench.common.stunner.bpmn.workitem;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class IconDefinition {

    private String uri;
    private String iconData;

    public IconDefinition setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public IconDefinition setIconData(String iconData) {
        this.iconData = iconData;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public String getIconData() {
        return iconData;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes((null != uri) ? uri.hashCode() : 0,
                                         (null != iconData) ? iconData.hashCode() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IconDefinition) {
            final IconDefinition other = (IconDefinition) o;
            return Objects.equals(iconData, other.iconData) && Objects.equals(uri, other.uri);
        }
        return false;
    }
}
