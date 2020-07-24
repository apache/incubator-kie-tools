/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.uberfire.ext.metadata.io;

import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;

public class CoreIndexer implements Indexer {

    private final IOService ioService;
    private final Class<? extends FileAttributeView>[] views;

    @SafeVarargs
    public CoreIndexer(IOService ioService, Class<? extends FileAttributeView>... views) {
        this.ioService = ioService;
        this.views = views;
    }

    @Override
    public boolean supportsPath(Path path) {
        return true;
    }

    @Override
    public KObject toKObject(Path path) {
        if (!ioService.exists(path)) {
            return null;
        }
        //Default indexing
        for (Class<? extends FileAttributeView> view : views) {
            ioService.getFileAttributeView(path, view);
        }
        final FileAttribute<?>[] attrs = ioService.convert(ioService.readAttributes(path));
        return KObjectUtil.toKObject(path, attrs);
    }

    @Override
    public KObjectKey toKObjectKey(Path path) {
        return KObjectUtil.toKObjectKey(path);
    }

}
