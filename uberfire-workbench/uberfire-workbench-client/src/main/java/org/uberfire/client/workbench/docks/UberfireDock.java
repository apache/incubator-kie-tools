package org.uberfire.client.workbench.docks;

import org.uberfire.mvp.PlaceRequest;

public class UberfireDock {

    private final PlaceRequest placeRequest;

    private UberfireDockPosition uberfireDockPosition;

    private String associatedPerspective;

    private Double size;

    public UberfireDock( UberfireDockPosition uberfireDockPosition,
                         PlaceRequest placeRequest,
                         String associatedPerspective ) {
        this.uberfireDockPosition = uberfireDockPosition;
        this.placeRequest = placeRequest;
        this.associatedPerspective = associatedPerspective;
    }

    public UberfireDock( UberfireDockPosition uberfireDockPosition,
                         PlaceRequest placeRequest ) {
        this.uberfireDockPosition = uberfireDockPosition;
        this.placeRequest = placeRequest;
    }

    public UberfireDock withSize(double size){
        this.size = size;
        return this;
    }

    public void setUberfireDockPosition( UberfireDockPosition uberfireDockPosition ) {
        this.uberfireDockPosition = uberfireDockPosition;
    }

    public String getAssociatedPerspective() {
        return associatedPerspective;
    }

    public String getIdentifier() {
        return placeRequest.getIdentifier();
    }

    public UberfireDockPosition getDockPosition() {
        return uberfireDockPosition;
    }

    public Double getSize() {
        return size;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof UberfireDock ) ) {
            return false;
        }

        UberfireDock that = (UberfireDock) o;

        if ( placeRequest != null ? !placeRequest.equals( that.placeRequest ) : that.placeRequest != null ) {
            return false;
        }
        return !( associatedPerspective != null ? !associatedPerspective.equals( that.associatedPerspective ) : that.associatedPerspective != null );

    }

    @Override
    public int hashCode() {
        int result = placeRequest != null ? placeRequest.hashCode() : 0;
        result = 31 * result + ( associatedPerspective != null ? associatedPerspective.hashCode() : 0 );
        return result;
    }
}
