package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.util.Map;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

public class DataModelerServiceBaseTest {

    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    protected BeanManager beanManager;
    protected Paths paths;
    protected DataModelerService dataModelService;
    protected KieProjectService projectService;
    protected Map<String, AnnotationDefinitionTO> systemAnnotations = null;

    @Before
    public void setUp() throws Exception {
        //Bootstrap WELD container
        StartMain startMain = new StartMain( new String[ 0 ] );
        beanManager = startMain.go().getBeanManager();

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

    public DataObjectTO createDataObject( String name,
            String packageName,
            String superClassName ) {
        return new DataObjectTO( name, packageName, superClassName );
    }

    public ObjectPropertyTO addProperty( DataObjectTO dataObjectTO,
            String name,
            String className,
            boolean baseType,
            boolean multiple,
            String bag ) {
        //todo set modifiers.
        ObjectPropertyTO propertyTO = new ObjectPropertyTO( name, className, multiple, baseType, bag, -1 );
        return propertyTO;
    }

    public AnnotationTO createAnnotation( Map<String, AnnotationDefinitionTO> systemAnnotations,
            String name,
            String className,
            String memberName,
            String value ) {
        AnnotationDefinitionTO annotationDefinitionTO = systemAnnotations.get( className );

        AnnotationTO annotationTO = new AnnotationTO( annotationDefinitionTO );
        annotationTO.setName( name );
        annotationTO.setClassName( className );
        if ( memberName != null ) {
            annotationTO.setValue( memberName, value );
        }

        return annotationTO;
    }

}