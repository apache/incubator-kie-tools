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

package org.ext.uberfire.social.activities.server;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.ext.uberfire.social.activities.model.DefaultTypes;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.persistence.SocialUserJsonDeserializer;
import org.ext.uberfire.social.activities.persistence.SocialUserJsonSerializer;

import static org.junit.Assert.*;

public class PersistenceJsonConverterTest {

    Gson gson;

    @Before
    public void setup() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter( SocialUser.class, new SocialUserJsonSerializer() );
        gsonBuilder.registerTypeAdapter( SocialUser.class, new SocialUserJsonDeserializer() );
        gson = gsonBuilder.create();
    }

    @Test
    public void user_toJSON_fromJson_Test() {

        SocialUser user = new SocialUser( "user1" );
        SocialUser user2 = new SocialUser( "user2" );
        SocialUser user3 = new SocialUser( "user3" );
        SocialUser user4 = new SocialUser( "user4" );
        user.follow( user2 );
        user.follow( user3 );
        user4.follow( user );
        String json = gson.toJson( user );

        SocialUser jsonUser = gson.fromJson( json, SocialUser.class );
        assertEquals( "user1", jsonUser.getUserName() );
        assertEquals( "user4", jsonUser.getFollowersName().get( 0 ) );
        assertEquals( "user2", jsonUser.getFollowingName().get( 0 ) );
        assertEquals( "user3", jsonUser.getFollowingName().get( 1 ) );
    }

    @Test
    public void SocialActivitiesEvent_to_and_from_JSON() {

        SocialActivitiesEvent event1 = new SocialActivitiesEvent( new SocialUser( "admin" ), DefaultTypes.DUMMY_EVENT, new Date() ).withAdicionalInfo( "adicional1" );
        SocialActivitiesEvent event2 = new SocialActivitiesEvent( new SocialUser( "system" ), DefaultTypes.DUMMY_EVENT, new Date() ).withAdicionalInfo( "adicional2" );

        List<SocialActivitiesEvent> events = new ArrayList<SocialActivitiesEvent>();
        events.add( event1 );
        events.add( event2 );
        String json = gson.toJson( events );

        Type collectionType = new TypeToken<Collection<SocialActivitiesEvent>>() {
        }.getType();
        List<SocialActivitiesEvent> jsonEvents = gson.fromJson( json, collectionType );
        compare( event1, jsonEvents.get( 0 ) );
        compare( event2, jsonEvents.get( 1 ) );
    }

    private void compare( SocialActivitiesEvent event,
                          SocialActivitiesEvent json ) {
        assertEquals( event.getAdicionalInfos(), json.getAdicionalInfos() );
        assertEquals( event.getSocialUser().getUserName(), json.getSocialUser().getUserName() );
        assertEquals( event.getType(), json.getType() );
        assertTrue( event.equals( json ) );
    }

}
