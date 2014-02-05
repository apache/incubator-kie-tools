package org.uberfire.client.mvp;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;

public class PlaceRequestHistoryMapperImplTest {

    private PlaceRequestHistoryMapperImpl placeRequestHistoryMapper;

    @Before
    public void setup(){
        placeRequestHistoryMapper = new PlaceRequestHistoryMapperImpl(){
            @Override
            String urlDecode( String value ) {
                return value;
            }
        };
    }


    @Test
    public void createPlaceRequest() throws Exception {
        String url = " http://127.0.0.1:8888/org.uberfire.UberfireShowcase/out.16590-4829.erraiBus?z=12&clientId=16590-4829";
        PlaceRequest placeRequest = placeRequestHistoryMapper.getPlaceRequest( url );
        assertEquals( url, placeRequest.getFullIdentifier() );

        Map<String,String> parameters = placeRequest.getParameters();

        assertEquals("12",parameters.get("z"));
        assertEquals("16590-4829",parameters.get("clientId"));

    }



}
