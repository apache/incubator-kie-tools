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

package org.kie.workbench.common.dmn.client.marshaller.included;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class DMNMarshallerImportsContentServiceImpl implements DMNMarshallerImportsContentService {


    private final Promises promises;


    @Inject
    public DMNMarshallerImportsContentServiceImpl(final Promises promises) {
        this.promises = promises;
    }

    @Override
    public Promise<String> loadFile(final String fileUri) {
        return promises.resolve("");
    }

    @Override
    public Promise<String[]> getModelsURIs() {
        return promises.resolve(new String[]{});
    }

    @Override
    public Promise<String[]> getModelsDMNFilesURIs() {
        return promises.resolve(new String[]{});
    }

    @Override
    public Promise<String[]> getModelsPMMLFilesURIs() {
        return promises.resolve(new String[]{});
    }

    @Override
    public Promise<PMMLDocumentMetadata> getPMMLDocumentMetadata(final String fileUri) {
        return promises.resolve();
    }

    private Path makePath(final String fileUri) {
        return PathFactory.newPath(".", fileUri);
    }
}
