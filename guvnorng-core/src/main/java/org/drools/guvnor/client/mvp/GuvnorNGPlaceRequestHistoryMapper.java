package org.drools.guvnor.client.mvp;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import com.google.gwt.http.client.URL;


public class GuvnorNGPlaceRequestHistoryMapper implements PlaceRequestHistoryMapper {

    @Override
    public PlaceRequest getPlaceRequest(String token) {
        String nameToken = token.indexOf("?") != -1 ? token.substring(0,  token.indexOf("?")-1) : token;
        String query = token.indexOf("?") != -1 ? token.substring(token.indexOf("?")) : "";
        Map<String, String> parameters = getParameters(token);
       
        PlaceRequest placeRequest = new PlaceRequest(nameToken);
        for(String parameterName : parameters.keySet()) {
            placeRequest.parameter(parameterName, parameters.get(parameterName));
        }

        return placeRequest;
    }

    @Override
    public String getToken(PlaceRequest placeRequest) {
        StringBuilder token = new StringBuilder();
        token.append(placeRequest.getNameToken());

        if (placeRequest.getParameterNames().size() > 0) {
            token.append("?");
        }
        for (String name : placeRequest.getParameterNames()) {
            token.append(name).append("=")
                    .append(placeRequest.getParameter(name, null));
            token.append("&");
        }
        
        if(token.length() != 0 && token.lastIndexOf("&")+1 == token.length()) {
            token.deleteCharAt(token.length()-1);           
        }

        return token.toString();
    }

    private static Map<String, String> getParameters(String query) {
       Map<String, String> parameters = new HashMap<String, String>();

        if (query !=null && !"".equalsIgnoreCase(query)) {
            List<String> parts = Arrays.asList(query.split("&"));
            for (String part : parts) {
                int index = part.indexOf('=');
                String name = null;
                String value = null;
                if (index == -1) {
                    name = part;
                    value = "";
                } else {
                    name = part.substring(0, index);
                    value = index < part.length() ? part.substring(index + 1) : "";
                    value = urlDecode(value);
                }
                parameters.put(urlDecode(name), value);
            }
        }
        
        return parameters;
    }
    
    private static String urlDecode(String value) {
      return URL.decode(value);
    }
}
