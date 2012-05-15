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

package org.drools.guvnor.client.perspective.workspace.explorer;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.common.Util;

public abstract class AbstractTree extends Composite implements SelectionHandler<TreeItem> {

    protected String name;
    protected ImageResource image;
    protected Tree mainTree;

    protected Map<TreeItem, String> itemWidgets = new HashMap<TreeItem, String>();

    protected void init() {
        mainTree = createTree();
        initWidget(mainTree);
    }

    protected abstract Tree createTree();

    /**
     * Get a string representation of the header that includes an image and some
     * text.
     * @return the header as a string
     */
    public HTML getHeaderHTML() {
        return Util.getHeaderHTML(image, name);
    }

    public void refreshTree() {
    }

}
