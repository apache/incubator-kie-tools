/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.java.nio.fs.jgit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.fs.base.AttrHolder;
import org.uberfire.java.nio.fs.base.FlexibleFileAttributeView;
import org.uberfire.java.nio.fs.base.NotImplementedException;

import static org.uberfire.commons.util.Preconditions.*;

public class JGitFileAttributeView<T extends JGitFileAttributes>
        implements BasicFileAttributeView, FlexibleFileAttributeView {

    static final String IS_REGULAR_FILE = "isRegularFile";
    static final String IS_DIRECTORY = "isDirectory";
    static final String IS_SYMBOLIC_LINK = "isSymbolicLink";
    static final String IS_OTHER = "isOther";
    static final String SIZE = "size";
    static final String FILE_KEY = "fileKey";
    static final String LAST_MODIFIED_TIME = "lastModifiedTime";
    static final String LAST_ACCESS_TIME = "lastAccessTime";
    static final String CREATION_TIME = "creationTime";

    static final Set<String> PROPERTIES = new HashSet<String>() {{
        add(IS_REGULAR_FILE);
        add(IS_DIRECTORY);
        add(IS_SYMBOLIC_LINK);
        add(IS_OTHER);
        add(SIZE);
        add(FILE_KEY);
        add(LAST_MODIFIED_TIME);
        add(LAST_ACCESS_TIME);
        add(CREATION_TIME);
    }};

    private final AttrHolder<T> holder;

    public JGitFileAttributeView(final AttrHolder<T> holder) {
        this.holder = checkNotNull("holder", holder);
    }

    @Override
    public String name() {
        return "basic";
    }

    @Override
    public T readAttributes() throws IOException {
        return holder.getAttrs();
    }

    @Override
    public Map<String, Object> readAttributes(String... attributes) throws IOException {
        checkNotNull("attributes", attributes);
        final Map<String, Object> result = new HashMap<String, Object>();

        for (final String attribute : attributes) {
            checkNotEmpty("attribute", attribute);
            if (attribute.equals("*")) {
                result.put(IS_REGULAR_FILE, holder.getAttrs().isRegularFile());
                result.put(IS_DIRECTORY, holder.getAttrs().isDirectory());
                result.put(IS_SYMBOLIC_LINK, holder.getAttrs().isSymbolicLink());
                result.put(IS_OTHER, holder.getAttrs().isOther());
                result.put(SIZE, new Long(holder.getAttrs().size()));
                result.put(FILE_KEY, holder.getAttrs().fileKey());
                //todo check why errai can't serialize it
                result.put(LAST_MODIFIED_TIME, null);
                result.put(LAST_ACCESS_TIME, null);
                result.put(CREATION_TIME, null);
                break;

            } else if (attribute.equals(IS_REGULAR_FILE)) {
                result.put(IS_REGULAR_FILE, holder.getAttrs().isRegularFile());
            } else if (attribute.equals(IS_DIRECTORY)) {
                result.put(IS_DIRECTORY, holder.getAttrs().isDirectory());
            } else if (attribute.equals(IS_SYMBOLIC_LINK)) {
                result.put(IS_SYMBOLIC_LINK, holder.getAttrs().isSymbolicLink());
            } else if (attribute.equals(IS_OTHER)) {
                result.put(IS_OTHER, holder.getAttrs().isOther());
            } else if (attribute.equals(SIZE)) {
                result.put(SIZE, new Long(holder.getAttrs().size()));
            } else if (attribute.equals(FILE_KEY)) {
                result.put(FILE_KEY, holder.getAttrs().fileKey());
            } else if (attribute.equals(LAST_MODIFIED_TIME)) {
                result.put(LAST_MODIFIED_TIME, null);
            } else if (attribute.equals(LAST_ACCESS_TIME)) {
                result.put(LAST_ACCESS_TIME, null);
            } else if (attribute.equals(CREATION_TIME)) {
                result.put(CREATION_TIME, null);
            }
        }
        return result;
    }

    @Override
    public void setTimes(final FileTime lastModifiedTime, final FileTime lastAccessTime, final FileTime createTime)
            throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public void setAttribute(final String attribute, final Object value)
            throws IOException {
        checkNotEmpty("attribute", attribute);
        checkCondition("invalid attribute", PROPERTIES.contains(attribute));

        throw new NotImplementedException();
    }
}
