/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractBasicFileAttributeView;
import org.uberfire.java.nio.base.AbstractPath;
import org.uberfire.java.nio.base.NeedsPreloadedAttrs;
import org.uberfire.java.nio.base.NotImplementedException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.uberfire.commons.data.Pair.newPair;
import static org.kie.soup.commons.validation.Preconditions.checkCondition;
import static org.kie.soup.commons.validation.Preconditions.checkNotEmpty;

public class OtherMetaView
        extends AbstractBasicFileAttributeView<AbstractPath>
        implements NeedsPreloadedAttrs {

    public static final String TAG = "othermeta.tag";
    public static final String MODE = "othermeta.mode";

    private static final Set<String> PROPERTIES = new HashSet<String>() {{
        add(TAG);
        add(MODE);
    }};

    private final OtherMetaAttributes attrs;

    public OtherMetaView(final AbstractPath path) {
        super(path);
        final Map<String, Object> content = path.getAttrStorage().getContent();

        final Map<Integer, String> _categories = new TreeMap<Integer, String>();

        for (final Map.Entry<String, Object> entry : content.entrySet()) {
            if (entry.getKey().startsWith(TAG)) {
                final Pair<Integer, Object> result = extractValue(entry);
                _categories.put(result.getK1(),
                                result.getK2().toString());
            }
        }

        final BasicFileAttributes fileAttrs = path.getFileSystem().provider().getFileAttributeView(path,
                                                                                                   BasicFileAttributeView.class).readAttributes();

        final List<String> categories = new ArrayList<String>(_categories.values());

        this.attrs = new OtherMetaAttributes() {

            @Override
            public List<String> tags() {
                return categories;
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

    private Pair<Integer, Object> extractValue(final Map.Entry<String, Object> entry) {
        int start = entry.getKey().indexOf('[');
        if (start < 0) {
            return newPair(0,
                           entry.getValue());
        }
        int end = entry.getKey().indexOf(']');

        return newPair(Integer.valueOf(entry.getKey().substring(start + 1,
                                                                end)),
                       entry.getValue());
    }

    @Override
    public String name() {
        return "othermeta";
    }

    @Override
    public OtherMetaAttributes readAttributes() throws IOException {
        return attrs;
    }

    @Override
    public Map<String, Object> readAttributes(final String... attributes) {
        return OtherMetaAttributesUtil.toMap(readAttributes(),
                                             attributes);
    }

    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{OtherMetaView.class};
    }

    @Override
    public void setAttribute(final String attribute,
                             final Object value) throws IOException {
        checkNotEmpty("attribute",
                      attribute);
        checkCondition("invalid attribute",
                       PROPERTIES.contains(attribute));

        throw new NotImplementedException();
    }
}
