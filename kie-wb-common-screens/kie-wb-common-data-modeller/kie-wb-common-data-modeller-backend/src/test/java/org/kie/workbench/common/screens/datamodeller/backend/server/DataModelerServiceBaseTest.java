/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.util.Map;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.jboss.weld.environment.se.Weld;
import org.junit.After;
import org.junit.Before;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.Visibility;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

public class DataModelerServiceBaseTest {

    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    protected Weld weld;
    protected BeanManager beanManager;
    protected Paths paths;
    protected DataModelerService dataModelService;
    protected KieProjectService projectService;
    protected Map<String, AnnotationDefinition> systemAnnotations = null;

    @Before
    public void setUp() throws Exception {
        // disable git and ssh daemons as they are not needed for the tests
        System.setProperty( "org.uberfire.nio.git.daemon.enabled", "false" );
        System.setProperty( "org.uberfire.nio.git.ssh.enabled", "false" );
        System.setProperty( "org.uberfire.sys.repo.monitor.disabled", "true" );
        //Bootstrap WELD container
        weld = new Weld();
        beanManager = weld.initialize().getBeanManager();

        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
        final CreationalContext pathsCContext = beanManager.createCreationalContext( pathsBean );
        paths = (Paths) beanManager.getReference( pathsBean,
                Paths.class,
                pathsCContext );

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();

        //Create DataModelerService bean
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelerService.class ).iterator().next();
        final CreationalContext dataModelerCContext = beanManager.createCreationalContext( dataModelServiceBean );
        dataModelService = (DataModelerService ) beanManager.getReference( dataModelServiceBean,
                DataModelerService.class,
                dataModelerCContext );

        //Create ProjectServiceBean
        final Bean projectServiceBean = (Bean) beanManager.getBeans( KieProjectService.class ).iterator().next();
        final CreationalContext projectServiceCContext = beanManager.createCreationalContext( projectServiceBean );
        projectService = (KieProjectService ) beanManager.getReference( projectServiceBean,
                KieProjectService.class,
                projectServiceCContext );

        systemAnnotations = dataModelService.getAnnotationDefinitions();

    }

    public DataObject createDataObject( String packageName,
            String name,
            String superClassName ) {
        DataObject dataObject = new DataObjectImpl( packageName, name );
        dataObject.setSuperClassName( superClassName );
        return dataObject;
    }

    public ObjectProperty addProperty( DataObject dataObject,
            String name,
            String className,
            boolean baseType,
            boolean multiple,
            String bag ) {
        //todo set modifiers.
        ObjectProperty property = new ObjectPropertyImpl( name, className, multiple,bag, Visibility.PUBLIC, false, false );
        dataObject.addProperty( property );
        return property;
    }

    public Annotation createAnnotation( Map<String, AnnotationDefinition> systemAnnotations,
            String name,
            String className,
            String memberName,
            Object value ) {
        AnnotationDefinition annotationDefinition = systemAnnotations.get( className );

        Annotation annotation = new AnnotationImpl( annotationDefinition );
        if ( memberName != null ) {
            annotation.setValue( memberName, value );
        }

        return annotation;
    }

    @After
    public void tearDown() {
        if (weld != null && beanManager != null) {
            weld.shutdown();
        }
    }

}