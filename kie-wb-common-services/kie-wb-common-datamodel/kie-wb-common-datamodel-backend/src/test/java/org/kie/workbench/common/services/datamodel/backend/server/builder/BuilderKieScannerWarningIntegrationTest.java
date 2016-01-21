/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server.builder;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUProjectDataModelOracleCache;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;

public class BuilderKieScannerWarningIntegrationTest extends AbstractWeldBuilderIntegrationTest {

    @Test
    //https://bugzilla.redhat.com/show_bug.cgi?id=1161577
    public void testBuilderKieScannerWarning() throws URISyntaxException {
        final URL resourceUrl = this.getClass().getResource( "/BuilderKieScannerRepo/src/main/resources/update.drl" );
        final org.uberfire.java.nio.file.Path nioResourcePath = fs.getPath( resourceUrl.toURI() );
        final Path resourcePath = paths.convert( nioResourcePath );

        //Build and look for warnings..
        final KieProject project = projectService.resolveProject( resourcePath );
        final BuildResults buildResults = buildService.build( project );
        assertNotNull( buildResults );
        assertEquals( 0,
                      buildResults.getErrorMessages().size() );
        assertEquals( 1,
                      buildResults.getInformationMessages().size() );
    }

}
