/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.backend.server.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.PathImpl;
import org.uberfire.backend.workbench.WorkbenchServices;
import org.uberfire.client.workbench.model.PerspectiveDefinition;

import com.thoughtworks.xstream.XStream;

/**
 * Workbench services
 */
@Service
@ApplicationScoped
public class WorkbenchServicesImpl
    implements
    WorkbenchServices {

    @Inject
    private VFSService vfsService;

    @Inject @Named("fs")
    private ActiveFileSystems fileSystems;

    private XStream    xs = new XStream();

    private Path bootstrapRoot = null;

    @PostConstruct
    public void init(){
        this.bootstrapRoot = fileSystems.getBootstrapFileSystem().getRootDirectories().get(0);
    }

    public void save(final PerspectiveDefinition perspective) {
        final String xml = xs.toXML( perspective );

        final String rootURI = bootstrapRoot.toURI();

        vfsService.write( new PathImpl( rootURI + "/.metadata/.perspectives/" + perspective.getName() ), xml );
    }

    public PerspectiveDefinition load(final String perspectiveName) {
        final String rootURI = bootstrapRoot.toURI();
        final Path path = new PathImpl( rootURI + "/.metadata/.perspectives/" + perspectiveName );

        if ( vfsService.exists( path ) ){
            final String xml = vfsService.readAllString( path );
            return  (PerspectiveDefinition) xs.fromXML( xml );
        }

        return null;
    };

}
