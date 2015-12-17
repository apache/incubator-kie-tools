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

package org.kie.workbench.common.widgets.decoratedgrid.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface TableImageResources
        extends ClientBundle {

    public static final TableImageResources INSTANCE = GWT.create( TableImageResources.class );

    @Source("images/downArrow.png")
    ImageResource downArrow();

    @Source("images/smallDownArrow.png")
    ImageResource smallDownArrow();

    @Source("images/upArrow.png")
    ImageResource upArrow();

    @Source("images/smallUpArrow.png")
    ImageResource smallUpArrow();

    @Source("images/columnPicker.png")
    ImageResource columnPicker();

}
