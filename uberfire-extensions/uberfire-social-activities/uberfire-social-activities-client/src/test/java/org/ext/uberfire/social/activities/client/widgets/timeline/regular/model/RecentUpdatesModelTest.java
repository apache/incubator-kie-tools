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

package org.ext.uberfire.social.activities.client.widgets.timeline.regular.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.ext.uberfire.social.activities.model.DefaultTypes;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;

import static org.junit.Assert.*;

public class RecentUpdatesModelTest {

    @Test
    public void simpleModelGeneration(){
        List<SocialActivitiesEvent> events  = new ArrayList<SocialActivitiesEvent>(  );
        SocialUser user = new SocialUser( "user" );
        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,1 ) ).withLink( "alias1", "file1" ) );
        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,2 ) ).withLink( "alias2", "file2" ) );
        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,3 ) ).withLink( "alias3", "file3" ) );
        RecentUpdatesModel model = RecentUpdatesModel.generate( events );

        Map<String, List<UpdateItem>> updateItems = model.getUpdateItems();
        Set<String> keys = updateItems.keySet();
        Iterator<String> iterator = keys.iterator();
        assertEquals( "alias3", iterator.next() );
        assertEquals( "alias2", iterator.next() );
        assertEquals( "alias1", iterator.next() );
        assertTrue(true);
    }

    @Test
    public void modelGenerationWithMoreChanges(){
        List<SocialActivitiesEvent> events  = new ArrayList<SocialActivitiesEvent>(  );
        SocialUser user = new SocialUser( "user" );
        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,1 ) ).withLink( "alias1", "file1" ) );
        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,2 ) ).withLink( "alias2", "file2" ) );
        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,3 ) ).withLink( "alias3", "file3" ) );
        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,4 ) ).withLink( "alias1", "file1" ) );
        RecentUpdatesModel model = RecentUpdatesModel.generate( events );

        Map<String, List<UpdateItem>> updateItems = model.getUpdateItems();
        Set<String> keys = updateItems.keySet();
        Iterator<String> iterator = keys.iterator();
        assertEquals( "alias1", iterator.next() );
        List<UpdateItem> items = model.getUpdateItems( "alias1" );
        assertTrue(items.size()==2);
        assertEquals("alias1",items.get( 0 ).getEvent().getLinkLabel()) ;
        assertEquals("alias1",items.get( 1 ).getEvent().getLinkLabel()) ;

        assertEquals( "alias3", iterator.next() );
        assertEquals( "alias2", iterator.next() );
        assertTrue(true);
    }


    private Date createDate( int minute,
                             int second ){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2014, 06, 1, 1, minute, second);
        Date date = cal.getTime();
        return date;
    }

}
