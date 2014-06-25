package org.kie.uberfire.social.activities.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class SocialActivitiesEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    private Date timestamp;
    private SocialUser socialUser;
    private String type;
    private String[] adicionalInfo;

    private static SocialActivitiesEvent dummyLastWritedMarker;

    public SocialActivitiesEvent() {
    }

    public SocialActivitiesEvent( SocialUser socialUser,
                                  SocialEventType type,
                                  Date timestamp,
                                  String... adicionalInfo ) {
        this.socialUser = socialUser;
        this.type = type.name();
        this.adicionalInfo = adicionalInfo;
        this.timestamp = timestamp;
    }
    public SocialActivitiesEvent( SocialUser socialUser,
                                  String typeName,
                                  Date timestamp,
                                  String... adicionalInfo ) {
        this.socialUser = socialUser;
        this.type = typeName;
        this.adicionalInfo = adicionalInfo;
        this.timestamp = timestamp;
    }

    public String[] getAdicionalInfo() {
        return adicionalInfo;
    }

    public String getAdicionalInfos() {
        String adicionalInfos = "";
        for ( String info : adicionalInfo ) {
            adicionalInfos += info + " ";
        }
        return adicionalInfos;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public SocialUser getSocialUser() {
        return socialUser;
    }

    @Override
    public String toString() {

        return "SocialActivitiesEvent{" +
                "timestamp=" + timestamp +
                ", user=" + socialUser.getName() +
                ", type=" + type +
                ", " + getAdicionalInfos() +
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

        if ( !Arrays.equals( adicionalInfo, that.adicionalInfo ) ) {
            return false;
        }
        if ( socialUser != null ? !socialUser.equals( that.socialUser ) : that.socialUser != null ) {
            return false;
        }
        if ( timestamp != null ? !compareDates( that ) : that.timestamp != null ) {
            return false;
        }
        if ( !type.equalsIgnoreCase(that.type) ) {
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
        result = 31 * result + ( adicionalInfo != null ? Arrays.hashCode( adicionalInfo ) : 0 );
        return result;
    }

    public boolean isDummyEvent() {
        return type!=null&& type.equals(DefaultTypes.DUMMY_EVENT.name());
    }

    public static SocialActivitiesEvent getDummyLastWritedMarker() {

        if ( dummyLastWritedMarker == null ) {
           dummyLastWritedMarker = new SocialActivitiesEvent(new SocialUser( "DUMMY" ),
                                                            DefaultTypes.DUMMY_EVENT,
                                                              new Date(  ),
                                                              "" );
        }
        return dummyLastWritedMarker;
    }
}