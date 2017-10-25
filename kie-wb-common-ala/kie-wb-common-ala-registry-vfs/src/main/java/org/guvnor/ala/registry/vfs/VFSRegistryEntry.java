/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.registry.vfs;

/**
 * Class for managing the contents of VFS based registries.
 * This class is used as the bag for storing all the VFS registries entries. Changes in this class might affect
 * de marshalling/unmarshalling of this registries.
 */
public class VFSRegistryEntry {

    private String contentType;

    private String content;

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public VFSRegistryEntry() {
        //no args constructor for marshalling purposes.
    }

    public VFSRegistryEntry(final String contentType,
                            final String content) {
        this.contentType = contentType;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VFSRegistryEntry entry = (VFSRegistryEntry) o;

        if (contentType != null ? !contentType.equals(entry.contentType) : entry.contentType != null) {
            return false;
        }
        return content != null ? content.equals(entry.content) : entry.content == null;
    }

    @Override
    public int hashCode() {
        int result = contentType != null ? contentType.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
