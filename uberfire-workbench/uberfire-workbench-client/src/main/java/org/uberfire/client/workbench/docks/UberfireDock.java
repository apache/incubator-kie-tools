/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.docks;

import com.google.gwt.resources.client.ImageResource;
import org.uberfire.mvp.PlaceRequest;

public class UberfireDock {

    private PlaceRequest placeRequest;

    private String iconType;

    private UberfireDockPosition uberfireDockPosition;

    private String associatedPerspective;

    private Double size;

    private String label;

    private ImageResource imageIcon;

    private ImageResource imageIconFocused;

    public UberfireDock(UberfireDockPosition uberfireDockPosition,
                        String iconType,
                        PlaceRequest placeRequest,
                        String associatedPerspective) {
        this.uberfireDockPosition = uberfireDockPosition;
        this.iconType = iconType;
        this.placeRequest = placeRequest;
        this.associatedPerspective = associatedPerspective;
        this.label = placeRequest.getIdentifier();
    }

    public UberfireDock(UberfireDockPosition uberfireDockPosition,
                        String iconType,
                        PlaceRequest placeRequest) {
        this(uberfireDockPosition, iconType, placeRequest, null);
    }

    public UberfireDock(UberfireDockPosition uberfireDockPosition,
                        ImageResource imageIcon,
                        ImageResource imageIconFocused,
                        PlaceRequest placeRequest,
                        String associatedPerspective) {
        this.uberfireDockPosition = uberfireDockPosition;
        this.imageIcon = imageIcon;
        this.imageIconFocused = imageIconFocused;
        this.placeRequest = placeRequest;
        this.associatedPerspective = associatedPerspective;
        this.label = placeRequest.getIdentifier();
    }

    public UberfireDock(UberfireDockPosition uberfireDockPosition,
                        ImageResource imageIcon,
                        ImageResource imageIconFocused,
                        PlaceRequest placeRequest) {
        this(uberfireDockPosition, imageIcon, imageIconFocused, placeRequest, null);
    }

    public UberfireDock withLabel(String label) {
        this.label = label;
        return this;
    }

    public UberfireDock withSize(double size) {
        this.size = size;
        return this;
    }

    public void setUberfireDockPosition(UberfireDockPosition uberfireDockPosition) {
        this.uberfireDockPosition = uberfireDockPosition;
    }

    public String getAssociatedPerspective() {
        return associatedPerspective;
    }

    public String getIdentifier() {
        return placeRequest.getIdentifier();
    }

    public PlaceRequest getPlaceRequest() {
        return placeRequest;
    }

    public UberfireDockPosition getDockPosition() {
        return uberfireDockPosition;
    }

    public Double getSize() {
        return size;
    }

    public String getLabel() {
        return label;
    }

    public String getIconType() {
        return iconType;
    }

    public ImageResource getImageIcon() {
        return imageIcon;
    }

    public ImageResource getImageIconFocused() {
        return imageIconFocused;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UberfireDock that = (UberfireDock) o;

        if (placeRequest != null ? !placeRequest.equals(that.placeRequest) : that.placeRequest != null) return false;
        if (iconType != that.iconType) return false;
        if (uberfireDockPosition != that.uberfireDockPosition) return false;
        if (associatedPerspective != null ? !associatedPerspective.equals(that.associatedPerspective) : that.associatedPerspective != null)
            return false;
        if (size != null ? !size.equals(that.size) : that.size != null) return false;
        return !(label != null ? !label.equals(that.label) : that.label != null);

    }

    @Override
    public int hashCode() {
        int result = placeRequest != null ? placeRequest.hashCode() : 0;
        result = 31 * result + (iconType != null ? iconType.hashCode() : 0);
        result = 31 * result + (uberfireDockPosition != null ? uberfireDockPosition.hashCode() : 0);
        result = 31 * result + (associatedPerspective != null ? associatedPerspective.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }
}
