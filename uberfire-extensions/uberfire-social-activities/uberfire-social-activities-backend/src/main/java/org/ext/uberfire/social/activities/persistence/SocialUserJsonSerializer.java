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
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.ext.uberfire.social.activities.model.SocialUser;

public class SocialUserJsonSerializer implements JsonSerializer<SocialUser> {

    @Override
    public JsonElement serialize(SocialUser socialUser,
                                 Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userName",
                               socialUser.getUserName());
        jsonObject.addProperty("realName",
                               socialUser.getRealName());
        jsonObject.addProperty("email",
                               socialUser.getEmail());

        serializeList(socialUser,
                      "following",
                      socialUser.getFollowingName(),
                      jsonObject);
        serializeList(socialUser,
                      "followers",
                      socialUser.getFollowersName(),
                      jsonObject);

        return jsonObject;
    }

    private void serializeList(SocialUser socialUser,
                               String jsonName,
                               List<String> list,
                               JsonObject jsonObject) {
        final JsonArray jsonArray = new JsonArray();
        for (final String field : list) {
            final JsonPrimitive jsonSocialType = new JsonPrimitive(field);
            jsonArray.add(jsonSocialType);
        }
        jsonObject.add(jsonName,
                       jsonArray);
    }
}
