/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.gallery;

import java.util.ArrayList;

/**
 * A list of gallery nodes.
 */
public class GalleryTreeNodeList extends GalleryTreeNode {

    public GalleryTreeNodeList(String name) {
        super(name);
    }

    public void add(GalleryTreeNode node) {
        if (children == null) children = new ArrayList<GalleryTreeNode>();
        children.add(node);
    }
}
