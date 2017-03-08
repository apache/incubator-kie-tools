/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.ext.uberfire.social.activities.persistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.ext.uberfire.social.activities.model.SocialUser;

public class SocialUserJsonDeserializer implements JsonDeserializer<SocialUser> {

    @Override
    public SocialUser deserialize(JsonElement jsonElement,
                                  Type type,
                                  JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();

        final JsonElement userName = jsonObject.get("userName");
        final JsonElement realName = jsonObject.get("realName");
        final JsonElement email = jsonObject.get("email");
        List<String> followers = deserializeUserNames(jsonObject.get("followers").getAsJsonArray());
        List<String> following = deserializeUserNames(jsonObject.get("following").getAsJsonArray());
        SocialUser socialUser = new SocialUser(userName.getAsString(),
                                               realName.getAsString(),
                                               email.getAsString(),
                                               followers,
                                               following);
        return socialUser;
    }

    private List<String> deserializeUserNames(JsonArray jsonFollowingEventsArray) {
        List<String> followers = new ArrayList<String>();
        for (int i = 0; i < jsonFollowingEventsArray.size(); i++) {
            final JsonElement jsonAuthor = jsonFollowingEventsArray.get(i);
            followers.add(jsonAuthor.getAsString());
        }
        return followers;
    }
}
