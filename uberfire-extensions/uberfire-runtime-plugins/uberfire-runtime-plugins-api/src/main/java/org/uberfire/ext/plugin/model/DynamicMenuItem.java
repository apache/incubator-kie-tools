package org.uberfire.ext.plugin.model;

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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DynamicMenuItem ) ) {
            return false;
        }

        DynamicMenuItem that = (DynamicMenuItem) o;

        if ( activityId != null ? !activityId.equals( that.activityId ) : that.activityId != null ) {
            return false;
        }
        if ( menuLabel != null ? !menuLabel.equals( that.menuLabel ) : that.menuLabel != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = activityId != null ? activityId.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( menuLabel != null ? menuLabel.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
