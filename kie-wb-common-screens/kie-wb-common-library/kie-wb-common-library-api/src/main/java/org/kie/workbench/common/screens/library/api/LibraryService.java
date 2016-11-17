/*
 * Copyright 2016 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.api;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.services.shared.project.KieProject;

import java.util.Collection;

@Remote
public interface LibraryService {

    OrganizationalUnit getDefaultOrganizationalUnit();

    LibraryInfo getDefaultLibraryInfo();

    LibraryInfo getLibraryInfo( String selectedOuIdentifier );

    Collection<OrganizationalUnit> getOrganizationalUnits();

    KieProject newProject( String projectName, String selectOu, String baseURL );
}
