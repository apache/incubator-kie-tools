package org.uberfire.backend.organizationalunit;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.repositories.Repository;

@Remote
public interface OrganizationalUnitService {

    OrganizationalUnit getOrganizationalUnit( final String name );

    Collection<OrganizationalUnit> getOrganizationalUnits();

    OrganizationalUnit createOrganizationalUnit( final String name,
                                                 final String owner );

    OrganizationalUnit createOrganizationalUnit( final String name,
                                                 final String owner,
                                                 final Collection<Repository> repositories );

    void updateOrganizationalUnitOwner( final String name,
                                        final String owner );

    void addRepository( final OrganizationalUnit organizationalUnit,
                        final Repository repository );

    void removeRepository( final OrganizationalUnit organizationalUnit,
                           final Repository repository );

    void addRole( final OrganizationalUnit organizationalUnit,
                  final String role );

    void removeRole( final OrganizationalUnit organizationalUnit,
                     final String role );

    void removeOrganizationalUnit( final String name );

}
