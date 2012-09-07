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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.PathImpl;
import org.uberfire.backend.workbench.WorkbenchServices;
import org.uberfire.client.workbench.model.PerspectiveDefinition;

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

    private DateFormat sdf = SimpleDateFormat.getDateTimeInstance();

    public void save(final PerspectiveDefinition perspective) {
        //This only works if you have been to the File Explorer first. See ShowcaseEntryPoint for an example.
        //TODO {manstis} Need to convert PerspectiveDefinition into a String
        vfsService.write( new PathImpl( "jgit:///guvnorng-playground/.metadata/.perspectives/test" ),
                          "Saved [" + perspective.getName() + "] on " + sdf.format( Calendar.getInstance().getTime() ) );
        System.out.println( "---> Saving perspective (with VFS)" );
    };

}
