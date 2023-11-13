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


package org.kie.workbench.common.stunner.client.lienzo.components;

import java.lang.annotation.Annotation;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.session.impl.SessionInitializer;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

@Default
@Dependent
public class LienzoImageStripLoader implements SessionInitializer {

    private final DefinitionUtils definitionUtils;
    private final ImageStripRegistry stripRegistry;
    private final LienzoImageStrips lienzoImageStrips;
    private ImageStrip[] strips;

    // CDI proxy.
    protected LienzoImageStripLoader() {
        this(null, null, null);
    }

    @Inject
    public LienzoImageStripLoader(final DefinitionUtils definitionUtils,
                                  final ImageStripRegistry stripRegistry,
                                  final LienzoImageStrips lienzoImageStrips) {
        this.definitionUtils = definitionUtils;
        this.stripRegistry = stripRegistry;
        this.lienzoImageStrips = lienzoImageStrips;
    }

    @Override
    public void init(final Metadata metadata,
                     final Command completeCallback) {
        final Annotation qualifier = definitionUtils.getQualifier(metadata.getDefinitionSetId());
        strips = stripRegistry.get(DefinitionManager.DEFAULT_QUALIFIER,
                                   qualifier);
        lienzoImageStrips.register(strips, completeCallback);
    }

    @Override
    public void destroy() {
        lienzoImageStrips.remove(strips);
        strips = null;
    }
}
