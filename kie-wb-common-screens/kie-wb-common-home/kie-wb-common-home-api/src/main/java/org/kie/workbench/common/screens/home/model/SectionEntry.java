package org.kie.workbench.common.screens.home.model;

import java.util.ArrayList;
import java.util.Collection;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.RuntimeResource;

import static java.util.Collections.emptyList;

/**
 * A Section Entry within a Section on the Home Page
 */
public class SectionEntry implements RuntimeResource {

    private final String caption;
    private final Command onClickCommand;
    private Collection<String> roles = new ArrayList<String>();

    public SectionEntry( final String caption,
                         final Command onClickCommand ) {
        this.caption = PortablePreconditions.checkNotNull( "caption",
                                                           caption );
        this.onClickCommand = PortablePreconditions.checkNotNull( "onClickCommand",
                                                                  onClickCommand );
    }

    public String getCaption() {
        return caption;
    }

    public Command getOnClickCommand() {
        return onClickCommand;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = PortablePreconditions.checkNotNull("roles", roles);
    }

    @Override
    public String getSignatureId() {
        return getClass().getName() + "#" + caption;
    }

    @Override
    public Collection<String> getRoles() {
        return roles;
    }
    @Override
    public Collection<String> getTraits() {
        return emptyList();
    }
}
