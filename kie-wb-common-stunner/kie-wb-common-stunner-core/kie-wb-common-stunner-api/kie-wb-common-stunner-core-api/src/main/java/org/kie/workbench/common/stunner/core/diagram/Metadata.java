/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.diagram;

import org.uberfire.backend.vfs.Path;

public interface Metadata {

    String getDefinitionSetId();

    String getTitle();

    void setTitle(final String title);

    String getShapeSetId();

    void setShapeSetId(final String id);

    String getCanvasRootUUID();

    void setCanvasRootUUID(final String uuid);

    String getThumbData();

    void setThumbData(final String data);

    Path getPath();

    void setPath(final Path path);

    Path getRoot();

    void setRoot(final Path path);

    Class<? extends Metadata> getMetadataType();
}
