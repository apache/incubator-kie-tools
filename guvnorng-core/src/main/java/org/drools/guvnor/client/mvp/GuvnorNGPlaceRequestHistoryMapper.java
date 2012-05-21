package org.drools.guvnor.client.mvp;


import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers(
        {
            MyAdminAreaPlace.Tokenizer.class,
            MyAdminAreaPlace2.Tokenizer.class
        }
)
public class GuvnorNGPlaceRequestHistoryMapper implements PlaceRequestHistoryMapper {

    @Override
    public PlaceRequest getPlaceRequest(String token) {
        if("AdminArea".equals(token)) {
            return new PlaceRequest("AdminArea");
        } else if("AdminArea2".equals(token)) {
            return new PlaceRequest("AdminArea2");
        }
        return null;
    }

    @Override
    public String getToken(PlaceRequest placeRequest) {
        return placeRequest.getNameToken();
    }
}
