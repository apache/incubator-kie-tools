package org.uberfire.client.mvp;

import org.uberfire.shared.mvp.PlaceRequest;

public interface PlaceRequestHistoryMapper {

    /**
     * Returns the {@link PlaceRequest} associated with the given token.
     * 
     * @param token
     *            a String token
     * @return a {@link PlaceRequest} instance
     */
    PlaceRequest getPlaceRequest(String token);

    /**
     * Returns the String identifier associated with the given {@link PlaceRequest}.
     * 
     * @param placeRequest
     *            a {@link PlaceRequest} instance
     * @return a String identifier
     */
    String getIdentifier(PlaceRequest placeRequest);
}