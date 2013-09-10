package org.uberfire.backend.organizationalunit;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RemoveOrganizationalUnitEvent {

    private OrganizationalUnit organizationalUnit;

    public RemoveOrganizationalUnitEvent() {
    }

    public RemoveOrganizationalUnitEvent( final OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit( final OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

}
