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

package org.uberfire.backend.vfs.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class PathImpl implements Path, IsSerializable {

    private String uri = null;
    private String fileName = null;
    private HashMap<String, Object> attributes = null;

    private String uuid;
    
    public PathImpl() {
    }

    public PathImpl(final String uri) {
        this(null, uri, null);
    }

    public PathImpl(final String fileName, final String uri) {
        this(fileName, uri, null);
    }

    public PathImpl(final String fileName, final String uri, final Map<String, Object> attrs) {
        this.fileName = fileName;
        this.uri = uri;
        if (attrs == null){
            this.attributes = new HashMap<String, Object>();
        } else {
            this.attributes = new HashMap<String, Object>(attrs);
        }
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String toURI() {
        return uri;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}
	
	public int compareTo(Path another) {
		//TODO: compare using path
		return this.getUUID().compareTo(another.getUUID());
	}

	public boolean equals(Path another) {
		//TODO:
		return this.getUUID().equals(another);
	}
	
	@Override
    public String toString() {
        return "PathImpl{" +
                "uri='" + uri + '\'' +
                ", fileName='" + fileName + '\'' +
                ", attrs=" + attributes +
                '}';
    }
}
