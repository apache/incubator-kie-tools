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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
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

/**
 *
 */
public class DiscussionView
        extends AbstractBasicFileAttributeView<AbstractPath>
        implements NeedsPreloadedAttrs {

    public static final String DISCUSS = "discuss";
    public static final String TIMESTAMP = DISCUSS + ".ts";
    public static final String AUTHOR = DISCUSS + ".author";
    public static final String NOTE = DISCUSS + ".note";

    private final DiscussionAttributes attrs;

    public DiscussionView(final AbstractPath path) {
        super(path);
        final Map<String, Object> content = path.getAttrStorage().getContent();

        final Map<Integer, Long> timestamps = new HashMap<Integer, Long>(content.size());
        final Map<Integer, String> authors = new HashMap<Integer, String>(content.size());
        final Map<Integer, String> notes = new HashMap<Integer, String>(content.size());

        for (final Map.Entry<String, Object> entry : content.entrySet()) {
            if (entry.getKey().startsWith(TIMESTAMP)) {
                final Pair<Integer, Object> result = extractValue(entry);
                timestamps.put(result.getK1(),
                               (Long) result.getK2());
            } else if (entry.getKey().startsWith(AUTHOR)) {
                final Pair<Integer, Object> result = extractValue(entry);
                authors.put(result.getK1(),
                            result.getK2().toString());
            } else if (entry.getKey().startsWith(NOTE)) {
                final Pair<Integer, Object> result = extractValue(entry);
                notes.put(result.getK1(),
                          result.getK2().toString());
            }
        }

        final List<DiscussionRecord> result = new ArrayList<DiscussionRecord>(timestamps.size());

        for (int i = 0; i < timestamps.size(); i++) {
            final Long ts = timestamps.get(i);
            final String author = authors.get(i);
            final String note = notes.get(i);
            result.add(new DiscussionRecord(ts,
                                            author,
                                            note));
        }

        Collections.sort(result,
                         new Comparator<DiscussionRecord>() {
                             @Override
                             public int compare(final DiscussionRecord o1,
                                                final DiscussionRecord o2) {
                                 return o1.getTimestamp().compareTo(o2.getTimestamp());
                             }
                         });

        final BasicFileAttributes fileAttrs = path.getFileSystem().provider().getFileAttributeView(path,
                                                                                                   BasicFileAttributeView.class).readAttributes();

        this.attrs = new DiscussionAttributes() {
            @Override
            public List<DiscussionRecord> discussion() {
                return result;
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
        return DISCUSS;
    }

    @Override
    public DiscussionAttributes readAttributes() throws IOException {
        return attrs;
    }

    @Override
    public Map<String, Object> readAttributes(final String... attributes) {
        return DiscussionAttributesUtil.toMap(readAttributes(),
                                              attributes);
    }

    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{DiscussionView.class};
    }

    @Override
    public void setAttribute(final String attribute,
                             final Object value) throws IOException {
        checkNotEmpty("attribute",
                      attribute);
        checkCondition("invalid attribute",
                       attribute.equals(name()));

        throw new NotImplementedException();
    }
}
