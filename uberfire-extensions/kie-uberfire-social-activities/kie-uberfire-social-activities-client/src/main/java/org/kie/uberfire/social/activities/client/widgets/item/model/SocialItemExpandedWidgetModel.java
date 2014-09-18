package org.kie.uberfire.social.activities.client.widgets.item.model;

import java.util.List;

import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.UpdateItem;
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
