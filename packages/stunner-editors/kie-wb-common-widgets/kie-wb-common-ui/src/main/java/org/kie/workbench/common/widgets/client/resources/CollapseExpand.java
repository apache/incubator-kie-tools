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
package org.kie.workbench.common.widgets.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface CollapseExpand
        extends
        ClientBundle {

    public static final CollapseExpand INSTANCE = GWT.create( CollapseExpand.class );

    @Source("images/collapse_expand/collapse.gif")
    ImageResource collapse();

    @Source("images/collapse_expand/collapseall.gif")
    ImageResource collapseAll();

    @Source("images/collapse_expand/expand.gif")
    ImageResource expand();

    @Source("images/collapse_expand/expandall.gif")
    ImageResource expandAll();
}
