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

package org.drools.guvnor.vfs.impl;

import org.drools.guvnor.vfs.SimplePath;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class SimplePathImpl implements SimplePath {

    protected String uri;
    protected String fileName;

    public SimplePathImpl() {

    }

    public SimplePathImpl(final String uri) {
        this.uri = uri;
        this.fileName = null;
    }

    public SimplePathImpl(final String fileName, final String uri) {
        this.fileName = fileName;
        this.uri = uri;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String toURI() {
        return uri;
    }

    @Override public String toString() {
        return "SimplePathImpl{" +
                "uri='" + uri + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
