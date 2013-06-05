package org.kie.workbench.common.screens.home.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.home.service.HomeService;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;

@Service
@ApplicationScoped
public class HomeServiceImpl implements HomeService {

    @Inject
    private GroupService groupService;

    @Override
    public Collection<Group> getGroups() {
        final Collection<Group> groups = new ArrayList<Group>();
        groups.addAll( groupService.getGroups() );
        return groups;
    }

}
