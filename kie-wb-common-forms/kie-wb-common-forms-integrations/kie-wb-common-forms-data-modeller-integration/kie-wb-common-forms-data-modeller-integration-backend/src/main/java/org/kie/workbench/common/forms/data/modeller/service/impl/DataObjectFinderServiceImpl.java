/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;

@Service
@Dependent
public class DataObjectFinderServiceImpl implements DataObjectFinderService {

    private KieProjectService projectService;

    private DataModelerService dataModelerService;

    @Inject
    public DataObjectFinderServiceImpl( KieProjectService projectService,
                                        DataModelerService dataModelerService ) {
        this.projectService = projectService;
        this.dataModelerService = dataModelerService;
    }

    @Override
    public DataObject getDataObject( String typeName, Path path ) {
        DataModel dataModel = dataModelerService.loadModel( projectService.resolveProject( path ) );

        return dataModel.getDataObject( typeName );
    }

    @Override
    public List<ObjectProperty> getDataObjectProperties( String typeName, Path path ) {
        return getDataObject( typeName, path ).getProperties();
    }

    @Override
    public List<DataObjectFormModel> getAvailableDataObjects( Path path ) {
        DataModel dataModel = dataModelerService.loadModel( projectService.resolveProject( path ) );

        List<DataObjectFormModel> formModels = new ArrayList<>();

        dataModel.getDataObjects().forEach( dataObject -> {
            String modelName = dataObject.getName().substring( 0, 1 ).toLowerCase() + dataObject.getName().substring( 1 );
            formModels.add( new DataObjectFormModel( modelName, dataObject.getClassName() ) );
        } );

        return formModels;
    }
}
