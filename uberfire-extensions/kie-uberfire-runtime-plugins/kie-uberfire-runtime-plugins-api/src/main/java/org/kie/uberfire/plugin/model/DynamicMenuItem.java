package org.kie.uberfire.plugin.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DynamicMenuItem {

    private String activityId;
    private String menuLabel;

    public DynamicMenuItem() {
    }

    public DynamicMenuItem( String activityId,
                            String menuLabel ) {
        this.activityId = activityId;
        this.menuLabel = menuLabel;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId( String activityId ) {
        this.activityId = activityId;
    }

    public String getMenuLabel() {
        return menuLabel;
    }

    public void setMenuLabel( String menuLabel ) {
        this.menuLabel = menuLabel;
    }
}
