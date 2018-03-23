/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.client.file.exports;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.jboss.errai.common.client.dom.Blob;
import org.uberfire.ext.editor.commons.client.file.exports.jso.JsFileSaver;

public abstract class AbstractFileExport<T> implements FileExport<T> {

    private final BiConsumer<Blob, String> fileSaver;

    public AbstractFileExport() {
        this((blob, name) -> {
            JsFileSaver.saveAs(blob,
                               name,
                               Boolean.TRUE);
        });
    }

    protected AbstractFileExport(final BiConsumer<Blob, String> saveAs) {
        this.fileSaver = saveAs;
    }

    protected abstract Optional<Blob> getContent(final T entity);

    @Override
    public void export(final T entity,
                       final String fileName) {
        getContent(entity)
                .ifPresent(blob -> fileSaver.accept(blob,
                                                    fileName));
    }
}
