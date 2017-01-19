/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.ext.uberfire.social.activities.client.widgets.item.model;

import java.util.List;

import org.ext.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.ext.uberfire.social.activities.client.widgets.timeline.regular.model.UpdateItem;
import org.uberfire.mvp.Command;

public class SocialItemExpandedWidgetModel {

    private final String fileName;
    private final List<UpdateItem> updateItems;
    private final SocialTimelineWidgetModel model;

    public SocialItemExpandedWidgetModel( String fileName,
                                          List<UpdateItem> updateItems,
                                          SocialTimelineWidgetModel model ) {
        this.fileName = fileName;
        this.updateItems = updateItems;
        this.model = model;
    }

    public String getFileName() {
        return fileName;
    }

    public List<UpdateItem> getUpdateItems() {
        return updateItems;
    }

    public SocialTimelineWidgetModel getModel() {
        return model;
    }

}
