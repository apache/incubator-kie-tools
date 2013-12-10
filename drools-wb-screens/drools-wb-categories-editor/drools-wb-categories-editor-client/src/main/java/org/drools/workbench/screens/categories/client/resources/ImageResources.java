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

package org.drools.workbench.screens.categories.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 */
public interface ImageResources extends
                                ClientBundle {

    public static final ImageResources INSTANCE = GWT.create( ImageResources.class );

    @Source("images/category_small.gif")
    ImageResource categorySmall();

    @Source("images/desc.gif")
    ImageResource desc();

    @Source("images/edit_category.gif")
    ImageResource editCategory();

    @Source("images/BPM_FileIcons_tag.png")
    ImageResource typeCategories();

}
