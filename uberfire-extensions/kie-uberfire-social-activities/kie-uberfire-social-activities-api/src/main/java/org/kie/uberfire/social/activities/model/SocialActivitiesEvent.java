package org.kie.uberfire.social.activities.model;

import java.io.Serializable;
import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class SocialActivitiesEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    private static SocialActivitiesEvent dummyLastWrittenMarker;
    private Date timestamp;
    private SocialUser socialUser;
    private String type;
    private String linkLabel;
    private String linkTarget;
    private String[] additionalInfo;
    private String description;

    public SocialActivitiesEvent() {
    }

    public SocialActivitiesEvent( SocialUser socialUser,
                                  String type,
                                  Date timestamp ) {
        this.socialUser = socialUser;
        this.type = type;
        this.timestamp = timestamp;
    }

    public SocialActivitiesEvent( SocialUser socialUser,
                                  SocialEventType type,
                                  Date timestamp ) {
        this.socialUser = socialUser;
        this.type = type.name();
        this.timestamp = timestamp;
    }

    public SocialActivitiesEvent withLink( String linklabel,
                                           String linkTarget ) {
        this.linkLabel = linklabel;
        this.linkTarget = linkTarget;
        return this;
    }

    public SocialActivitiesEvent withDescription( String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasLink() {
        return linkLabel != null;
    }

    public String getLinkLabel() {
        return linkLabel;
    }

    public String getLinkTarget() {
        return linkTarget;
    }

    public SocialActivitiesEvent withAdicionalInfo( String... adicionalInfo ) {
        this.additionalInfo = adicionalInfo;
        return this;
    }

    public boolean hasAdicionalInfo() {
        return additionalInfo != null;
    }

    public String[] getAdditionalInfo() {
        return additionalInfo;
    }

    public String getAdicionalInfos() {
        String adicionalInfos = "";
        for ( String info : additionalInfo ) {
            adicionalInfos += info + " ";
        }
        return adicionalInfos;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public SocialUser getSocialUser() {
        return socialUser;
    }

    @Override
    public String toString() {

        return "SocialActivitiesEvent{" +
                "timestamp=" + timestamp +
                ", user=" + socialUser.getUserName() +
                ", type=" + type +
                ", add=" + getAdicionalInfos() +
                '}';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof SocialActivitiesEvent ) ) {
            return false;
        }

        SocialActivitiesEvent that = (SocialActivitiesEvent) o;

        if ( socialUser != null ? !socialUser.equals( that.socialUser ) : that.socialUser != null ) {
            return false;
        }
        if ( timestamp != null ? !compareDates( that ) : that.timestamp != null ) {
            return false;
        }
        if ( !type.equals( that.type ) ) {
            return false;
        }
        return true;
    }

    private boolean compareDates( SocialActivitiesEvent that ) {
        if ( that.getTimestamp() == null || that.getTimestamp() == null ) {
            return false;
        }
        final long difference = this.getTimestamp().getTime() - that.getTimestamp().getTime();
        if ( difference > -1000 && difference < 1000 ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + ( socialUser != null ? socialUser.hashCode() : 0 );
        result = 31 * result + ( type != null ? type.hashCode() : 0 );
        return result;
    }

    public boolean isDummyEvent() {
        return type != null && type.equals( DefaultTypes.DUMMY_EVENT.name() );
    }

    public static SocialActivitiesEvent getDummyLastWrittenMarker() {

        if ( dummyLastWrittenMarker == null ) {
            dummyLastWrittenMarker = new SocialActivitiesEvent( new SocialUser( "DUMMY" ),
                                                                DefaultTypes.DUMMY_EVENT,
                                                                new Date() );
        }
        return dummyLastWrittenMarker;
    }

    public String getType() {
        return type;
    }
}