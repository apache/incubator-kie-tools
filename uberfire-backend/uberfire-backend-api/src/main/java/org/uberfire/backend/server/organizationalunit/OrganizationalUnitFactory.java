package org.uberfire.backend.server.organizationalunit;

import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.server.config.ConfigGroup;

public interface OrganizationalUnitFactory {

    OrganizationalUnit newOrganizationalUnit( ConfigGroup groupConfig );
}
