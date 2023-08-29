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


package org.kie.workbench.common.stunner.svg.gen.model.impl;

import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;

public class ViewRefDefinitionImpl implements ViewRefDefinition {

    private final String href;
    private final String parent;
    private final String refViewId;
    private final String filePath;

    public ViewRefDefinitionImpl(final String href,
                                 final String parent,
                                 final String refViewId,
                                 final String filePath) {
        this.href = href;
        this.refViewId = refViewId;
        this.parent = parent;
        this.filePath = filePath;
    }

    @Override
    public String getViewRefId() {
        return refViewId;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ViewRefDefinitionImpl)) {
            return false;
        }
        ViewRefDefinitionImpl that = (ViewRefDefinitionImpl) o;
        return href.equals(that.href);
    }

    @Override
    public int hashCode() {
        return href == null ? 0 : ~~href.hashCode();
    }
}
