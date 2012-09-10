/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.backend.server.impl;

import java.util.Iterator;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.resteasy.client.ClientRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uberfire.backend.server.GadgetService;
import org.uberfire.shared.gadget.UserPreference;
import org.uberfire.shared.gadget.WidgetModel;

@Service
@ApplicationScoped
public class GadgetServiceImpl implements GadgetService {

 	@Override
 	public WidgetModel getWidgetModel(String gadgetUrl) {
 	    String USER_PREFS = "userPrefs";
	    String DATA_TYPE = "dataType";
	    
        String responseString = getMetadata(gadgetUrl);

        //logger.info( "gadget url is: " + gadgetUrl +  ", gadget metadata is: " + responseString);

        //now trim back the response to just the metadata for the single gadget
        try {
            JSONObject responseObject = new JSONArray(responseString).
                    getJSONObject(0).
                    getJSONObject("result").
                    getJSONObject(gadgetUrl);

            WidgetModel model = new WidgetModel();
            model.setIframeUrl("http:" + responseObject.getString("iframeUrl"));
            model.setName(responseObject.getJSONObject("modulePrefs").getString("title"));
            model.setSpecUrl(gadgetUrl);

            //logger.info(responseObject.toString());

            // check to see if this gadget has at least one non-hidden user pref
            // to determine if we should display the edit prefs button
            boolean hasPrefsToEdit = false;

            if (responseObject.has(USER_PREFS)) {
                UserPreference userPref = new UserPreference();
                JSONObject userPrefs = responseObject.getJSONObject(USER_PREFS);
                Iterator keys = userPrefs.keys();
                while(keys.hasNext()) {
                    String settingName = (String) keys.next();
                    UserPreference.UserPreferenceSetting theSetting = new UserPreference.UserPreferenceSetting();
                    JSONObject setting = userPrefs.getJSONObject(settingName);
                    String theType = setting.getString(DATA_TYPE);
                    if (!UserPreference.Type.HIDDEN.toString().equals(theType)) {
                        hasPrefsToEdit = true;
                    }

                    theSetting.setName(setting.getString("name"));
                    theSetting.setDefaultValue(setting.getString("defaultValue"));
                    theSetting.setDisplayName(setting.getString("displayName"));
                    theSetting.setRequired(Boolean.valueOf(setting.getString("required")));
                    theSetting.setType(UserPreference.Type.valueOf(theType));

                    if (setting.has("orderedEnumValues")) {
                        JSONArray enumValues = setting.getJSONArray("orderedEnumValues");
                        for (int i =0; i < enumValues.length(); i++) {
                            UserPreference.Option option = new UserPreference.Option();
                            JSONObject theOption = enumValues.getJSONObject(i);
                            option.setValue(theOption.getString("value"));
                            option.setDisplayValue(theOption.getString("displayValue"));
                            theSetting.addEnumOption(option);
                        }
                    }
                    userPref.addUserPreferenceSetting(theSetting);
                }
                userPref.setNeedToEdit(hasPrefsToEdit);
                model.setUserPreference(userPref);
            }

            return model;
        } catch (JSONException e) {
            throw new IllegalArgumentException("Error occurred while processing response from shindig metadata call", e);
        }

    }

    private String getMetadata(String gadgetUrl) {
        JSONArray rpcArray = new JSONArray();
        try {
            JSONObject fetchMetadataRpcOperation = new JSONObject()
                    .put("method", "gadgets.metadata")
                    .put("id", "gadgets.metadata")
                    .put("params", new JSONObject()
                            .put("container", "default")
                            .put("view", "home")
                            .put("st", "default")
                            .put("debug", true)

                            .append("ids", gadgetUrl)
                            .append("fields", "iframeUrl")
                            .append("fields", "modulePrefs.*")
                            .append("fields", "needsTokenRefresh")
                            .append("fields", "userPrefs.*")
                            .append("fields", "views.preferredHeight")
                            .append("fields", "views.preferredWidth")
                            .append("fields", "expireTimeMs")
                            .append("fields", "responseTimeMs")

                            .put("userId", "@viewer")
                            .put("groupId", "@self")
                    );

            rpcArray.put(fetchMetadataRpcOperation);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Error occurred while generating data for shindig metadata call", e);
        }

        //convert the json object to a string
        String postData = rpcArray.toString();

/*        if (logger.isDebugEnabled()) {
            logger.debug("requestContent: {}", postData);
        }*/
        String SHINDIG_RPC_URL = "http://localhost:8080/gadget-server/rpc";

        ClientRequest request = new ClientRequest(SHINDIG_RPC_URL);
        request.accept("application/json").body(MediaType.APPLICATION_JSON, postData);

        String responseString = null;
        try {
            responseString = request.postTarget(String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }

}

