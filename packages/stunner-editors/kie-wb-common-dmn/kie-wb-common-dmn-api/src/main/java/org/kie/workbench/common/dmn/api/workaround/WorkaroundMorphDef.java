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
package org.kie.workbench.common.dmn.api.workaround;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.definition.clone.ClonePolicy;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;

/**
 * Without a portable impl of MorphDefinition, compilation fails.
 */
@Portable
public class WorkaroundMorphDef implements MorphDefinition {

    @Override
    public boolean accepts(final String definitionId) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public String getBase() {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public String getDefault() {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public Iterable<String> getTargets(final String definitionId) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public ClonePolicy getPolicy() {
        throw new RuntimeException("Not yet implemented.");
    }
}