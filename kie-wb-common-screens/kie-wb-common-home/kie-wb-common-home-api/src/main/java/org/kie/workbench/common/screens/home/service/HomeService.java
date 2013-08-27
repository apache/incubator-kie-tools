package org.kie.workbench.common.screens.home.service;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;

/**
 * Handy things the Home Page needs
 */
@Remote
public interface HomeService {

    /**
     * Get a list of all OrganizationalUnits the User has access to
     * @return
     */
    Collection<OrganizationalUnit> getOrganizationalUnits();

}
