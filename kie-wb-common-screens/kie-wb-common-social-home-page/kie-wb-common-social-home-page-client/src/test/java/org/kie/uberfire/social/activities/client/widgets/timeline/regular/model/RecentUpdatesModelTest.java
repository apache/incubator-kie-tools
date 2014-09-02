//package org.kie.uberfire.social.activities.client.widgets.timeline.regular.model;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.junit.Test;
//import org.kie.uberfire.social.activities.model.DefaultTypes;
//import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
//import org.kie.uberfire.social.activities.model.SocialUser;
//import org.kie.workbench.common.screens.social.hp.client.model.RecentUpdatesModel;
//import org.kie.workbench.common.screens.social.hp.client.model.UpdateItem;
//
//import static org.junit.Assert.*;
//
//public class RecentUpdatesModelTest {
//
//    @Test
//    public void simpleModelGeneration(){
//        List<SocialActivitiesEvent> events  = new ArrayList<SocialActivitiesEvent>(  );
//        SocialUser user = new SocialUser( "user" );
//        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,1 ) ).withLink( "alias", "file1" ) );
//        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,2 ) ).withLink( "alias", "file2" ) );
//        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,3 ) ).withLink( "alias", "file3" ) );
//        RecentUpdatesModel model = RecentUpdatesModel.generate( events );
//
//        Map<String, List<UpdateItem>> updateItems = model.getUpdateItems();
//        Set<String> keys = updateItems.keySet();
//        Iterator<String> iterator = keys.iterator();
//        assertEquals( "file3", iterator.next() );
//        assertEquals( "file2", iterator.next() );
//        assertEquals( "file1", iterator.next() );
//        assertTrue(true);
//    }
//
//    @Test
//    public void modelGenerationWithMoreChanges(){
//        List<SocialActivitiesEvent> events  = new ArrayList<SocialActivitiesEvent>(  );
//        SocialUser user = new SocialUser( "user" );
//        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,1 ) ).withLink( "alias1", "file1" ) );
//        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,2 ) ).withLink( "alias", "file2" ) );
//        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,3 ) ).withLink( "alias", "file3" ) );
//        events.add( new SocialActivitiesEvent( user, DefaultTypes.DUMMY_EVENT,createDate( 1,4 ) ).withLink( "alias2", "file1" ) );
//        RecentUpdatesModel model = RecentUpdatesModel.generate( events );
//
//        Map<String, List<UpdateItem>> updateItems = model.getUpdateItems();
//        Set<String> keys = updateItems.keySet();
//        Iterator<String> iterator = keys.iterator();
//        assertEquals( "file1", iterator.next() );
//        List<UpdateItem> items = model.getUpdateItems( "file1" );
//        assertTrue(items.size()==2);
//        assertEquals("alias1",items.get( 0 ).getEvent().getLinkLabel()) ;
//        assertEquals("alias2",items.get( 1 ).getEvent().getLinkLabel()) ;
//
//        assertEquals( "file3", iterator.next() );
//        assertEquals( "file2", iterator.next() );
//        assertTrue(true);
//    }
//
//
//    private Date createDate( int minute,
//                             int second ){
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(0);
//        cal.set(2014, 06, 1, 1, minute, second);
//        Date date = cal.getTime();
//        return date;
//    }
//
//}
