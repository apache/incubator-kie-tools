/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.service;

import org.dashbuilder.dataset.backend.EditDataSetDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;

/**
 * Services for the handling the storage of data set definitions
 */
@Remote
public interface DataSetDefVfsServices extends SupportsDelete, SupportsCopy {

    Path resolve(DataSetDef dataSetDef);
    DataSetDef get(Path path);
    EditDataSetDef load(Path path) throws Exception;
    Path save(DataSetDef dataSetDef, String commitMessage);
}
