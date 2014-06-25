package org.kie.workbench.common.screens.home.service;

import java.util.Collection;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.bus.server.annotations.Remote;

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
