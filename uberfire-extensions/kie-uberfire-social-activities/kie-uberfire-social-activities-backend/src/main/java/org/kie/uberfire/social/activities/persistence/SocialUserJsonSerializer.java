package org.kie.uberfire.social.activities.persistence;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.kie.uberfire.social.activities.model.SocialUser;

public class SocialUserJsonSerializer implements JsonSerializer<SocialUser> {

    @Override
    public JsonElement serialize( SocialUser socialUser,
                                  Type type,
                                  JsonSerializationContext jsonSerializationContext ) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", socialUser.getName());

        serializeList(socialUser,"following", socialUser.getFollowingName(), jsonObject);
        serializeList(socialUser,"followers", socialUser.getFollowersName(), jsonObject);

        return jsonObject;
    }

    private void serializeList(SocialUser socialUser, String jsonName, List<String> list,  JsonObject jsonObject) {
        final JsonArray jsonArray = new JsonArray();
        for (final String field : list) {
            final JsonPrimitive jsonSocialType = new JsonPrimitive(field);
            jsonArray.add( jsonSocialType );
        }
        jsonObject.add(jsonName, jsonArray);
    }

}
