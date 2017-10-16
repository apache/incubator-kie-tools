/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.explorer.model;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

/**
 * The contents of a folder
 */
@Portable
public class FolderListing {

    private FolderItem item;
    private List<FolderItem> content;
    private List<FolderItem> segments;

    public FolderListing() {
        //For Errai-marshalling
    }

    public FolderListing(final FolderItem item,
                         final List<FolderItem> content,
                         final List<FolderItem> segments) {
        this.item = item;
        this.content = checkNotNull("content",
                                    content);
        this.segments = checkNotNull("segments",
                                     segments);
    }

    public FolderItem getItem() {
        return item;
    }

    public List<FolderItem> getContent() {
        return content;
    }

    public List<FolderItem> getSegments() {
        return segments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FolderListing)) {
            return false;
        }

        FolderListing that = (FolderListing) o;

        if (content != null ? !content.equals(that.content) : that.content != null) {
            return false;
        }
        if (item != null ? !item.equals(that.item) : that.item != null) {
            return false;
        }
        if (segments != null ? !segments.equals(that.segments) : that.segments != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (segments != null ? segments.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
