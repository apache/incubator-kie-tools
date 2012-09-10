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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
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

    private XStream    xs = new XStream();

    public void save(final PerspectiveDefinition perspective) {
        //This only works if you have been to the File Explorer first. See ShowcaseEntryPoint for an example.
        final String xml = xs.toXML( perspective );
        vfsService.write( new PathImpl( "jgit:///guvnorng-playground/.metadata/.perspectives/" + perspective.getName() ),
                          xml );
    };

    public PerspectiveDefinition load(final String perspectiveName) {
        //This only works if you have been to the File Explorer first. See ShowcaseEntryPoint for an example.
        final Path path = new PathImpl( "jgit:///guvnorng-playground/.metadata/.perspectives/" + perspectiveName );
        try {
            final String xml = vfsService.readAllString( path );
            final PerspectiveDefinition perspective = (PerspectiveDefinition) xs.fromXML( xml );
            return perspective;
        } catch ( NullPointerException npe ) {
            //TODO {manstis} There is no way to detect if the file exists, and if you try to read a non-existent file you get a NPE
        }
        return null;
    };

}
