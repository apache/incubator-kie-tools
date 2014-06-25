package org.kie.uberfire.social.activities.repository;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialAdapterRepositoryAPI;

@ApplicationScoped
public class SocialAdapterRepository implements SocialAdapterRepositoryAPI {

    private Map<Class, SocialAdapter> socialAdapters = new HashMap<Class, SocialAdapter>();

    @Inject
    @Any
    private Instance<SocialAdapter> services;

    @PostConstruct
    public void setup() {
        for ( SocialAdapter bean : services ) {
            socialAdapters.put( bean.eventToIntercept(), bean );
        }
    }

    @Override
    public Map<Class, SocialAdapter> getSocialAdapters() {
        return socialAdapters;
    }
}
