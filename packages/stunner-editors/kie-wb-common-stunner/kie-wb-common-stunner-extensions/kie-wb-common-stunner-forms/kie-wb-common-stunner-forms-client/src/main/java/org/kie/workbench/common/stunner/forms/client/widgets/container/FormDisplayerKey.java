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


package org.kie.workbench.common.stunner.forms.client.widgets.container;

import java.util.Objects;

public class FormDisplayerKey {

    private String graphUuid;
    private String elementUid;

    public FormDisplayerKey(String graphUuid, String elementUid) {
        this.graphUuid = graphUuid;
        this.elementUid = elementUid;
    }

    public String getGraphUuid() {
        return graphUuid;
    }

    public String getElementUid() {
        return elementUid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FormDisplayerKey that = (FormDisplayerKey) o;

        return Objects.equals(graphUuid, that.graphUuid) && Objects.equals(elementUid, that.elementUid);
    }

    @Override
    public int hashCode() {
        int result = graphUuid.hashCode();
        result = ~~result;
        result = 31 * result + elementUid.hashCode();
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "FormDisplayerKey{" +
                "graphUuid='" + graphUuid + '\'' +
                ", elementUid='" + elementUid + '\'' +
                '}';
    }
}
