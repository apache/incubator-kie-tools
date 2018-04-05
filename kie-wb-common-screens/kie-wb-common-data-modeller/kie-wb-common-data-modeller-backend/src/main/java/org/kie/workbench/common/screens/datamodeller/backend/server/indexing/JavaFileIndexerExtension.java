/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.backend.server.indexing;

import org.jboss.forge.roaster.model.JavaType;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.IndexBuilder;

/**
 * Extension allowing clients to modify index definition for JavaType objects.
 */
public interface JavaFileIndexerExtension {

    void process( IndexBuilder builder, JavaType javaType );
}
