/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.widgets.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface CommonImages
        extends
        ClientBundle {

    public static final CommonImages INSTANCE = GWT.create( CommonImages.class );

    @Source("images/edit.gif")
    ImageResource edit();

    @Source("images/calendar.png")
    ImageResource calendar();

    @Source("images/validation_error.gif")
    ImageResource validationError();

    @Source("images/mandatory.png")
    ImageResource mandatory();

    @Source("images/delete_item_small.gif")
    ImageResource DeleteItemSmall();

    @Source("images/error.gif")
    ImageResource error();

    @Source("images/information.gif")
    ImageResource information();

    @Source("images/warning.gif")
    ImageResource warning();

    @Source("images/new_item_below.png")
    ImageResource newItemBelow();

    @Source("images/shuffle_down.gif")
    ImageResource shuffleDown();

    @Source("images/shuffle_up.gif")
    ImageResource shuffleUp();

}
