package org.uberfire.backend.organizationalunit;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NewOrganizationalUnitEvent {

    private OrganizationalUnit organizationalUnit;

    public NewOrganizationalUnitEvent() {
    }

    public NewOrganizationalUnitEvent( final OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit( final OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

}
