package org.kie.uberfire.social.activities.persistence;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.server.SocialUserServicesExtendedBackEndImpl;
import org.kie.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialTimelinePersistenceAPI;
import org.kie.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public abstract class SocialTimelineCachePersistence implements SocialTimelinePersistenceAPI {

    public static final String SOCIAL_FILES = "social-files";

    Map<SocialEventType, List<SocialActivitiesEvent>> typeEventsTimelineCache = new HashMap<SocialEventType, List<SocialActivitiesEvent>>();
    Map<SocialEventType, List<SocialActivitiesEvent>> typeEventsFreshEvents = new HashMap<SocialEventType, List<SocialActivitiesEvent>>();
    Map<SocialEventType, SocialCacheControl> typeEventsCacheControl = new HashMap<SocialEventType, SocialCacheControl>();

    int threshold;
    public static final String THRESHOLD_PROPERTY = "org.uberfire.social.threshold";
    private static final int DEFAULT_THRESHOLD = 100;

    IOService ioService;

    Gson gson;

    Type gsonCollectionType;

    SocialEventTypeRepositoryAPI socialEventTypeRepository;

    SocialUserPersistenceAPI socialUserPersistenceAPI;

    SocialUserServicesExtendedBackEndImpl userServicesBackend;

    @Override
    public void setup() {
        createCacheStructure();
        createCacheControl();
    }

    private void createCacheControl() {
        for ( SocialEventType type : socialEventTypeRepository.findAll() ) {
            typeEventsCacheControl.put( type, new SocialCacheControl() );
        }
        for ( String username : socialUserPersistenceAPI.getSocialUsersName() ) {
            userEventsCacheControl.put( username, new SocialCacheControl() );
        }
    }

    private void createCacheStructure() {
        for ( SocialEventType type : socialEventTypeRepository.findAll() ) {
            List<SocialActivitiesEvent> events = createOrGetTypeTimeline( type );
            typeEventsTimelineCache.put( type, events );
            typeEventsFreshEvents.put( type, new ArrayList<SocialActivitiesEvent>() );
        }
        for ( String username : socialUserPersistenceAPI.getSocialUsersName() ) {
            List<SocialActivitiesEvent> events = createOrGetUserTimeline( username );
            userEventsTimelineCache.put( username, events );
            userEventsTimelineFreshEvents.put( username, new ArrayList<SocialActivitiesEvent>() );
        }

    }

    List<SocialActivitiesEvent> createOrGetTimeline( Path timelineDir ) {
        List<SocialActivitiesEvent> events = new ArrayList<SocialActivitiesEvent>();
        try {
            if ( getIoService().exists( timelineDir ) ) {
                Integer lastFileIndex = getLastFileIndex( timelineDir );
                if ( thereIsSomethingToRead( lastFileIndex ) ) {
                    events = getTimeline( timelineDir, lastFileIndex.toString() );
                }
            } else {
                createPersistenceStructure( timelineDir );
            }
            return events;
        } catch ( Exception e ) {
            throw new ErrorAccessingTimeline( e );
        }
    }

    private List<SocialActivitiesEvent> getTimeline( Path timelineDir,
                                                     String fileIndex ) {
        List<SocialActivitiesEvent> events;
        Path fileTimeline = timelineDir.resolve( fileIndex );
        String numberOfEvents = getItemsMetadata( timelineDir, fileIndex );
        SocialFile socialFile = new SocialFile( fileTimeline, ioService, gson );
        events = socialFile.readSocialEvents(Integer.valueOf( numberOfEvents ));
        return events;
    }

    private boolean thereIsSomethingToRead( Integer lastFileIndex ) {
        return lastFileIndex >= 0;
    }

    Integer getLastFileIndex( Path timelineDir ) {
        Path resolve = timelineDir.resolve( Constants.LAST_FILE_INDEX.name() );
        String index = getIoService().readAllString( resolve );
        Integer lastIndex = Integer.valueOf( index );
        return lastIndex;
    }

    private void createPersistenceStructure( Path timelineDir ) {
        String lastIndex = "-1";
        updateLastIndexFile( timelineDir, lastIndex );
    }

    private void updateLastIndexFile( Path directory,
                                      String lastIndex ) {
        Path lastFileIndex = directory.resolve( Constants.LAST_FILE_INDEX.name() );
        getIoService().write( lastFileIndex, lastIndex );
    }

    private String persistEvents( List<SocialActivitiesEvent> newEvents,
                                  Path timeLineDir ) {
        Integer lastFileIndex = getLastFileIndex( timeLineDir );
        lastFileIndex = lastFileIndex + 1;
        Path timelineFile = timeLineDir.resolve( lastFileIndex.toString() );
        writeItems( timelineFile, newEvents );
        writeItemsMetadata( timeLineDir, lastFileIndex.toString(), newEvents.size() );
        updateLastIndexFile( timeLineDir, lastFileIndex.toString() );
        return lastFileIndex.toString();
    }

    private void writeItemsMetadata( Path timeLineDir,
                                     String originalFilename,
                                     int size ) {
        String metadataFileName = originalFilename + Constants.METADATA;
        Path timelineFile = timeLineDir.resolve( metadataFileName );
        ioService.write( timelineFile, size + "" );
    }

    private String getItemsMetadata( Path timeLineDir,
                                     String originalFilename ) {
        String metadataFileName = originalFilename + Constants.METADATA;
        Path timelineFile = timeLineDir.resolve( metadataFileName );
        if(ioService.exists( timelineFile )){
            String itemsMetadata = ioService.readAllString( timelineFile );
            return itemsMetadata;
        }
        //return json
        return "-1";
    }

    private void writeItems( Path timeLineFile,
                             List<SocialActivitiesEvent> newEvents ) {
        SocialFile socialFile = new SocialFile( timeLineFile, ioService, gson );

        try {
            socialFile.write( newEvents );
        } catch ( IOException e ) {
            throw new ErrorAccessingTimeline( e );
        }

    }

    IOService getIoService() {
        return ioService;
    }

    protected class SocialCacheControl {

        private int sizeOfcache;

        public SocialCacheControl() {
            reset();
        }

        public void registerNewEvent() {
            sizeOfcache = sizeOfcache + 1;
        }

        public boolean needToPersist() {
            return sizeOfcache > getThreshold();
        }

        public void reset() {
            sizeOfcache = 0;
        }
    }

    int getThreshold() {
        if ( threshold == 0 ) {
            String property = System.getProperty( THRESHOLD_PROPERTY );
            if ( property != null && !property.isEmpty() ) {
                threshold = Integer.valueOf( property );
            } else {
                threshold = DEFAULT_THRESHOLD;
            }
        }
        return threshold;
    }

    private class ErrorAccessingTimeline extends RuntimeException {

        public ErrorAccessingTimeline( Exception e ) {
            e.printStackTrace();
        }
    }

    enum Constants {
        LAST_FILE_INDEX, USER_TIMELINE, METADATA
    }

    //TYPE STUFF

    List<SocialActivitiesEvent> createOrGetTypeTimeline( SocialEventType type ) {
        Path timelineDir = userServicesBackend.buildPath( SOCIAL_FILES, type.name() );
        return createOrGetTimeline( timelineDir );
    }

    @Override
    public List<SocialActivitiesEvent> getLastEvents( SocialEventType key ) {
        List<SocialActivitiesEvent> socialActivitiesEvents = new ArrayList<SocialActivitiesEvent>();
        socialActivitiesEvents.addAll( typeEventsTimelineCache.get( key ) );
        socialActivitiesEvents.addAll( typeEventsFreshEvents.get( key ) );
        return socialActivitiesEvents;
    }

    List<SocialActivitiesEvent> storeTimeLineInFile( SocialEventType type ) {
        List<SocialActivitiesEvent> socialActivitiesEvents = typeEventsFreshEvents.get( type );
        persistEvents( type, socialActivitiesEvents );
        refreshCache( type, socialActivitiesEvents );
        return socialActivitiesEvents;
    }

    void refreshCache( SocialEventType type,
                       List<SocialActivitiesEvent> socialActivitiesEvents ) {
        typeEventsFreshEvents.put( type, new ArrayList<SocialActivitiesEvent>() );
        typeEventsTimelineCache.put( type, socialActivitiesEvents );
    }

    private void persistEvents( SocialEventType type,
                                List<SocialActivitiesEvent> newEvents ) {
        Path timeLineDir = userServicesBackend.buildPath( SOCIAL_FILES, type.name() );
        persistEvents( newEvents, timeLineDir );

    }

    @Override
    public Integer numberOfPages( SocialEventType type ) {
        Path timelineDir = userServicesBackend.buildPath( SOCIAL_FILES, type.name() );
        Integer lastFileIndex = getLastFileIndex( timelineDir );
        return lastFileIndex;
    }

    @Override
    public List<SocialActivitiesEvent> getRecentEvents( SocialEventType type ) {
        List<SocialActivitiesEvent> socialActivitiesEvents = new ArrayList<SocialActivitiesEvent>();
        List<SocialActivitiesEvent> typeEvents = typeEventsFreshEvents.get( type );
        if ( typeEvents != null ) {
            socialActivitiesEvents.addAll( typeEvents );
        }
        return socialActivitiesEvents;
    }

    @Override
    public Integer getTypeMostRecentFileIndex( SocialEventType type ) {
        Path timelineDir = userServicesBackend.buildPath( SOCIAL_FILES, type.name() );
        Integer lastFileIndex = getLastFileIndex( timelineDir );
        return lastFileIndex;
    }

    @Override
    public List<SocialActivitiesEvent> getTimeline( SocialEventType type,
                                                    String timelineFile ) {
        Path timelineDir = userServicesBackend.buildPath( SOCIAL_FILES, type.name() );
        List<SocialActivitiesEvent> timeline = getTimeline( timelineDir, timelineFile );
        return timeline;
    }

    //USER STUFF

    Map<String, List<SocialActivitiesEvent>> userEventsTimelineCache = new HashMap<String, List<SocialActivitiesEvent>>();
    Map<String, List<SocialActivitiesEvent>> userEventsTimelineFreshEvents = new HashMap<String, List<SocialActivitiesEvent>>();
    Map<String, SocialCacheControl> userEventsCacheControl = new HashMap<String, SocialCacheControl>();

    List<SocialActivitiesEvent> createOrGetUserTimeline( String userName ) {
        Path timelineDir = getRootUserTimelineDirectory();
        Path userFile = timelineDir.resolve( userName );
        return createOrGetTimeline( userFile );
    }

    private String persistEvents( SocialUser user,
                                  List<SocialActivitiesEvent> newEvents ) {
        Path userDir = getUserDirectory( user.getUserName() );
        if ( !ioService.exists( userDir ) ) {
            createPersistenceStructure( userDir );
        }
        String fileName = persistEvents( newEvents, userDir );
        return fileName;
    }

    private Path getUserDirectory( String userName ) {
        Path directory = getRootUserTimelineDirectory();
        return directory.resolve( userName );
    }

    @Override
    public List<SocialActivitiesEvent> getLastEvents( SocialUser user ) {

        List<SocialActivitiesEvent> socialActivitiesEvents = new ArrayList<SocialActivitiesEvent>();
        List<SocialActivitiesEvent> userEvents = userEventsTimelineCache.get( user.getUserName() );
        if ( userEvents == null ) {
            createCacheStructureForNewUsers( user );
        }
        socialActivitiesEvents.addAll( userEventsTimelineCache.get( user.getUserName() ) );
        socialActivitiesEvents.addAll( userEventsTimelineFreshEvents.get( user.getUserName() ) );
        return socialActivitiesEvents;
    }

    @Override
    public List<SocialActivitiesEvent> getRecentEvents( SocialUser user ) {
        List<SocialActivitiesEvent> socialActivitiesEvents = new ArrayList<SocialActivitiesEvent>();
        List<SocialActivitiesEvent> userEvents = userEventsTimelineFreshEvents.get( user.getUserName() );
        if ( userEvents == null ) {
            createCacheStructureForNewUsers( user );
        }
        socialActivitiesEvents.addAll( userEventsTimelineFreshEvents.get( user.getUserName() ) );
        return socialActivitiesEvents;
    }

    private void createCacheStructureForNewUsers( SocialUser user ) {
        userEventsTimelineCache.put( user.getUserName(), new ArrayList<SocialActivitiesEvent>() );
        userEventsTimelineFreshEvents.put( user.getUserName(), new ArrayList<SocialActivitiesEvent>() );
    }

    List<SocialActivitiesEvent> storeTimeLineInFile( SocialUser user ) {
        List<SocialActivitiesEvent> socialActivitiesEvents = userEventsTimelineFreshEvents.get( user.getUserName() );
        persistEvents( user, socialActivitiesEvents );
        refreshCache( user, socialActivitiesEvents );
        return socialActivitiesEvents;
    }

    void refreshCache( SocialUser user,
                       List<SocialActivitiesEvent> socialActivitiesEvents ) {
        userEventsTimelineFreshEvents.put( user.getUserName(), new ArrayList<SocialActivitiesEvent>() );
        userEventsTimelineCache.put( user.getUserName(), socialActivitiesEvents );
    }

    Path getRootUserTimelineDirectory() {
        Path path = userServicesBackend.buildPath( SOCIAL_FILES, Constants.USER_TIMELINE.name() );
        return path;
    }

    @Override
    public Integer getUserMostRecentFileIndex( SocialUser user ) {
        Path timelineDir = getUserDirectory( user.getUserName() );
        Integer lastFileIndex = getLastFileIndex( timelineDir );
        return lastFileIndex;
    }

    @Override
    public List<SocialActivitiesEvent> getTimeline( SocialUser socialUser,
                                                    String timelineFile ) {
        Path userDirectory = getUserDirectory( socialUser.getUserName() );
        List<SocialActivitiesEvent> timeline = getTimeline( userDirectory, timelineFile );
        return timeline;
    }

    void saveAllUserTimelines() {
        for ( String userName : userEventsTimelineFreshEvents.keySet() ) {
            List<SocialActivitiesEvent> socialActivitiesEvents = userEventsTimelineFreshEvents.get( userName );
            if ( !socialActivitiesEvents.isEmpty() ) {
                SocialUser socialUser = socialUserPersistenceAPI.getSocialUser( userName );
                storeTimeLineInFile( socialUser );
            }
        }
    }

    void saveAllTypeEvents() {
        for ( SocialEventType socialEventType : typeEventsFreshEvents.keySet() ) {
            List<SocialActivitiesEvent> socialActivitiesEvents = typeEventsFreshEvents.get( socialEventType );
            if ( !socialActivitiesEvents.isEmpty() ) {
                storeTimeLineInFile( socialEventType );
            }
        }
    }

    @Override
    public Integer getNumberOfEventsOnFile( SocialEventType type,
                                            String originalFilename ) {
        Path typedir = userServicesBackend.buildPath( SOCIAL_FILES, type.name() );
        return getNumberOfEventsOnPath( originalFilename, typedir );
    }

    Integer getNumberOfEventsOnPath( String originalFilename,
                                     Path path ) {
        String itemsMetadata = getItemsMetadata( path, originalFilename );
        try {
            return Integer.parseInt( itemsMetadata );
        } catch ( NumberFormatException e ) {
            return -1;
        }
    }

    @Override
    public Integer getNumberOfEventsOnFile( SocialUser socialUser,
                                            String originalFilename ) {
        Path userDirectory = getUserDirectory( socialUser.getUserName() );
        return getNumberOfEventsOnPath( originalFilename, userDirectory );
    }
}
