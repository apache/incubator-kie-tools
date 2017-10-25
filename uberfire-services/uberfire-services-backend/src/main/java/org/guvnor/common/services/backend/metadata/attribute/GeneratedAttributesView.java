/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.metadata.attribute;

import java.util.Map;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractBasicFileAttributeView;
import org.uberfire.java.nio.base.AbstractPath;
import org.uberfire.java.nio.base.NeedsPreloadedAttrs;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileTime;

public class GeneratedAttributesView extends AbstractBasicFileAttributeView<AbstractPath> implements NeedsPreloadedAttrs {

    public static final String GENERATED_VIEW_NAME = "generated";

    public static final String GENERATED_ATTRIBUTE_NAME = GENERATED_VIEW_NAME + ".generated";

    private GeneratedFileAttributes generatedFileAttributes;

    public GeneratedAttributesView(AbstractPath path) {
        super(path);

        final boolean generated = extractGenerated();

        final BasicFileAttributes fileAttrs = path.getFileSystem().provider().getFileAttributeView(path,
                                                                                                   BasicFileAttributeView.class).readAttributes();

        this.generatedFileAttributes = new GeneratedFileAttributes() {
            @Override
            public boolean isGenerated() {
                return generated;
            }

            @Override
            public FileTime lastModifiedTime() {
                return fileAttrs.lastModifiedTime();
            }

            @Override
            public FileTime lastAccessTime() {
                return fileAttrs.lastAccessTime();
            }

            @Override
            public FileTime creationTime() {
                return fileAttrs.creationTime();
            }

            @Override
            public boolean isRegularFile() {
                return fileAttrs.isRegularFile();
            }

            @Override
            public boolean isDirectory() {
                return fileAttrs.isDirectory();
            }

            @Override
            public boolean isSymbolicLink() {
                return fileAttrs.isSymbolicLink();
            }

            @Override
            public boolean isOther() {
                return fileAttrs.isOther();
            }

            @Override
            public long size() {
                return fileAttrs.size();
            }

            @Override
            public Object fileKey() {
                return fileAttrs.fileKey();
            }
        };
    }

    private boolean extractGenerated() {
        final Map<String, Object> content = path.getAttrStorage().getContent();

        final Object generatedFileAttribute = content.get(GENERATED_ATTRIBUTE_NAME);

        if (generatedFileAttribute instanceof Boolean) {
            return (Boolean) generatedFileAttribute;
        }

        return false;
    }

    @Override
    public String name() {
        return GENERATED_VIEW_NAME;
    }

    @Override
    public Class[] viewTypes() {
        return new Class[]{GeneratedAttributesView.class};
    }

    @Override
    public GeneratedFileAttributes readAttributes() throws IOException {
        return generatedFileAttributes;
    }
}
