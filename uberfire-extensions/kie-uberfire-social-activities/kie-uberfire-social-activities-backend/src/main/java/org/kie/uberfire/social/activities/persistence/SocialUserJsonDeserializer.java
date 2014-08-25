package org.kie.uberfire.social.activities.persistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.kie.uberfire.social.activities.model.SocialUser;

public class SocialUserJsonDeserializer implements JsonDeserializer<SocialUser> {

    @Override
    public SocialUser deserialize( JsonElement jsonElement,
                                   Type type,
                                   JsonDeserializationContext jsonDeserializationContext ) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();

        final JsonElement userName = jsonObject.get( "userName" );
        final JsonElement realName = jsonObject.get( "realName" );
        final JsonElement email = jsonObject.get( "email" );
        List<String> followers = deserializeUserNames( jsonObject.get( "followers" ).getAsJsonArray() );
        List<String> following = deserializeUserNames( jsonObject.get( "following" ).getAsJsonArray() );
        SocialUser socialUser = new SocialUser( userName.getAsString(), realName.getAsString(), email.getAsString(), followers, following );
        return socialUser;
    }

    private List<String> deserializeUserNames( JsonArray jsonFollowingEventsArray ) {
        List<String> followers = new ArrayList<String>();
        for ( int i = 0; i < jsonFollowingEventsArray.size(); i++ ) {
            final JsonElement jsonAuthor = jsonFollowingEventsArray.get( i );
            followers.add( jsonAuthor.getAsString() );
        }
        return followers;
    }

}
