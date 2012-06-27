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

package org.drools.java.nio.fs.base;

import org.drools.java.nio.IOException;
import org.drools.java.nio.file.attribute.BasicFileAttributeView;
import org.drools.java.nio.file.attribute.BasicFileAttributes;
import org.drools.java.nio.file.attribute.FileTime;

public class GeneralFileAttributeView implements BasicFileAttributeView {

    private final AttrHolder<? extends BasicFileAttributes> holder;

    public GeneralFileAttributeView(final AttrHolder<? extends BasicFileAttributes> holder) {
        this.holder = holder;
    }

    @Override
    public BasicFileAttributes readAttributes() throws IOException {
        return holder.getAttrs();
    }

    @Override
    public void setTimes(FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime)
            throws IOException {
    }

    @Override
    public String name() {
        return "basic";
    }
}
