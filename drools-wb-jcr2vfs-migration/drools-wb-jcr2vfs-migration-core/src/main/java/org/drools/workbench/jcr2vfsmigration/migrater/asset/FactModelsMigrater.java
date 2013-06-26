package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.repository.AssetItem;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.io.IOService;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.context.Project;
import org.drools.workbench.screens.factmodel.service.FactModelService;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import com.google.gwt.user.client.rpc.SerializationException;

@ApplicationScoped
public class FactModelsMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(FactModelsMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    protected FactModelService vfsFactModelService;

    @Inject
    protected MigrationPathManager migrationPathManager;
    
    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    
    @Inject
    private Paths paths;
    
    @Inject
    PackageImportHelper packageImportHelper;

    @Inject
    private ProjectService projectService;
    
    @Inject
    private DataModelerService modelerService;    

    private Map <String, String> orderedBaseTypes = new TreeMap<String, String>();
    private Map<String, AnnotationDefinitionTO> annotationDefinitions;
    
    public void migrate(Module jcrModule, AssetItem jcrAssetItem) {
        if (!AssetFormats.DRL_MODEL.equals(jcrAssetItem.getFormat())) {
            throw new IllegalArgumentException("The jcrAsset (" + jcrAssetItem.getName()
                    + ") has the wrong format (" + jcrAssetItem.getFormat() + ").");
        }
        
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAssetItem);   
        Project project = projectService.resolveProject(path);
        
        initBasePropertyTypes();
        initAnnotationDefinitions();        
        
        if(project == null) {
        	Path projectRootPath = migrationPathManager.generatePathForModule(jcrModule);
        	//Quick hack to pass mock values for pomPath etc, to make Project constructor happy. We only use projectRootPath anyway
        	project = new Project( projectRootPath,
        			projectRootPath,
        			projectRootPath,
        			projectRootPath,
                    "" );
        }
   
        try {
            Asset jcrAsset = jcrRepositoryAssetService.loadRuleAsset(jcrAssetItem.getUUID());
            
            FactModels factModels = ((FactModels) jcrAsset.getContent());
            DataModelTO dataModelTO  = new DataModelTO();
            
            String packageName = getPackageName(jcrModule);
            
            for ( FactMetaModel factMetaModel : factModels.models ) {
                DataObjectTO dataObjectTO = createDataObject(packageName, factMetaModel.getName(), factMetaModel.getSuperType());
            	List<AnnotationMetaModel> annotationMetaModel = factMetaModel.getAnnotations();                
            	addAnnotations(dataObjectTO, annotationMetaModel);
                List<FieldMetaModel> fields = factMetaModel.getFields();
                
                for(FieldMetaModel fieldMetaModel : fields) {
                	String filedName = fieldMetaModel.name;
                	String fildType = fieldMetaModel.type;
                	//Guvnor 5.5 (and earlier) does not have MultipleType
                	boolean isMultiple = false;
                	boolean isBaseType = isBaseType(fildType);
                    ObjectPropertyTO property = new ObjectPropertyTO(filedName,
                    		fildType,
                            isMultiple,
                            isBaseType);              
                	//field has no annotation in Guvnor 5.5 (and earlier)
                    dataObjectTO.getProperties().add(property);
                }
                
                dataModelTO.getDataObjects().add(dataObjectTO);
            }
            
            modelerService.saveModel(dataModelTO, project);

        } catch (SerializationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     }

    //The JCR Module name also contains the project name. This code attempts to create a package name
    //from the full JCR Module name (assuming they're formatted "projectName.subModule1.subModule2" etc
    private String getPackageName(Module jcrModule) {
        String packageName = jcrModule.getName();
        int dotIndex = packageName.indexOf( "." );
        if(dotIndex==-1) {
            packageName="";
        } else {
            packageName = packageName.substring( dotIndex +1 );
        }
        return packageName;
    }
    
    private void initBasePropertyTypes() {
    	List<PropertyTypeTO> baseTypes = modelerService.getBasePropertyTypes();
        if (baseTypes != null) {
            for (PropertyTypeTO type : baseTypes) {
                orderedBaseTypes.put(type.getName(), type.getClassName());
            }
        }
    }
    
    public Boolean isBaseType(String type) {
        return orderedBaseTypes.containsValue(type);
    }
    
    private void initAnnotationDefinitions() {
    	annotationDefinitions = modelerService.getAnnotationDefinitions();
    }
    
    private DataObjectTO createDataObject(String packageName, String name, String superClass) {
        DataObjectTO dataObject = new DataObjectTO(name, packageName, superClass);
        return dataObject;
    }
    
    private void addAnnotations(DataObjectTO dataObject, List<AnnotationMetaModel> annotationMetaModelList) {
    	for(AnnotationMetaModel annotationMetaModel : annotationMetaModelList) {
    		String name = annotationMetaModel.name;
    		Map<String, String> values = annotationMetaModel.values;
 
    		String key = AnnotationDefinitionTO.VALUE_PARAM;
    		String value = "";
    		
    		if(values.size() > 0) {
    			key = values.keySet().iterator().next();
    			value = values.values().iterator().next();
    		}
    		
    		if("Role".equals(name)) {
                dataObject.addAnnotation(annotationDefinitions.get(AnnotationDefinitionTO.ROLE_ANNOTATION), key, value);		
    		} else if("Position".equals(name)) {
                dataObject.addAnnotation(annotationDefinitions.get(AnnotationDefinitionTO.POSITION_ANNOTATON), key, value);
	   		} else if("Equals".equals(name)) {
                dataObject.addAnnotation(annotationDefinitions.get(AnnotationDefinitionTO.EQUALS_ANNOTATION), key, value);	
    		}
    	}    		
    }

}
