package org.kie.uberfire.social.activities.drools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.kie.uberfire.social.activities.model.PagedSocialQuery;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialAdapterRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialTimeLineRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialTimelineRulesQueryAPI;
import org.kie.uberfire.social.activities.service.SocialTypeTimelinePagedRepositoryAPI;

@Service
@ApplicationScoped
public class SocialTimelineRulesQuery implements SocialTimelineRulesQueryAPI {

    @Inject
    private SocialTimeLineRepositoryAPI socialTimeLineRepositoryAPI;

    @Inject
    private SocialTypeTimelinePagedRepositoryAPI socialTypeTimelinePagedRepositoryAPI;

    @Inject
    private SocialAdapterRepositoryAPI socialAdapterRepositoryAPI;

    @Override
    public List<SocialActivitiesEvent> executeAllRules() {

        List<SocialActivitiesEvent> events = new ArrayList<SocialActivitiesEvent>();
        try {

            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();

            KieSession kSession = kContainer.newKieSession( "social-session" );
            List<SocialActivitiesEvent> socialEvents = new ArrayList<SocialActivitiesEvent>();
            kSession.setGlobal( "socialEvents", socialEvents );
            kSession.setGlobal( "queryAPI", this );
            kSession.fireAllRules();

            events = (List<SocialActivitiesEvent>) kSession.getGlobal( "socialEvents" );

        } catch ( Exception e ) {
            throw new RulesExecutionQueryException( e );
        }
        return events;
    }

    @Override
    public List<SocialActivitiesEvent> executeSpecificRule( Map<String, String> globals,
                                                            final String drlName ) {

        List<SocialActivitiesEvent> events = new ArrayList<SocialActivitiesEvent>();
        try {

            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();

            KieSession kSession = kContainer.newKieSession( "social-session" );
            List<SocialActivitiesEvent> socialEvents = new ArrayList<SocialActivitiesEvent>();
            kSession.setGlobal( "socialEvents", socialEvents );
            kSession.setGlobal( "queryAPI", this );
            for ( String key : globals.keySet() ) {
                kSession.setGlobal( key, globals.get( key ) );
            }

            kSession.fireAllRules( new AgendaFilter() {
                @Override
                public boolean accept( Match match ) {
                    String rulename = match.getRule().getName();

                    if ( rulename.equals( drlName ) ) {
                        return true;
                    }

                    return false;
                }
            } );

            events = (List<SocialActivitiesEvent>) kSession.getGlobal( "socialEvents" );

        } catch ( Exception e ) {
            throw new RulesExecutionQueryException( e );
        }
        return events;

    }

    @Override
    public List<SocialActivitiesEvent> getAllCached() {

        List<SocialActivitiesEvent> events = new ArrayList<SocialActivitiesEvent>();
        Map<Class, SocialAdapter> socialAdapters = socialAdapterRepositoryAPI.getSocialAdapters();

        for ( SocialAdapter adapter : socialAdapters.values() ) {
            events.addAll( socialTimeLineRepositoryAPI.getLastEventTimeline( adapter, new HashMap() ) );
        }

        return events;
    }

    @Override
    public List<SocialActivitiesEvent> getTypeCached( String... typeNames ) {

        List<SocialActivitiesEvent> events = new ArrayList<SocialActivitiesEvent>();

        for ( String type : typeNames ) {
            events.addAll( socialTimeLineRepositoryAPI.getLastEventTimeline( type, new HashMap() ) );
        }

        return events;
    }

    @Override
    public List<SocialActivitiesEvent> getNEventsFromEachType( int numberOfEvents,
                                                               String... typeNames ) {
        List<SocialActivitiesEvent> events = new ArrayList<SocialActivitiesEvent>();

        for ( String type : typeNames ) {
            PagedSocialQuery query = socialTypeTimelinePagedRepositoryAPI.getEventTimeline( type, new SocialPaged( numberOfEvents ), new HashMap() );
            events.addAll( query.socialEvents() );
        }

        return events;
    }

    class RulesExecutionQueryException extends RuntimeException {

        private final Exception exception;

        public RulesExecutionQueryException( Exception e ) {
            this.exception = e;
        }
    }
}
