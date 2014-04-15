package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.repository.AssetItem;
import org.drools.workbench.jcr2vfsmigration.migrater.PackageImportHelper;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.workbench.common.screens.datamodeller.model.*;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationMemberDefinition;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.PositionAnnotationDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.StandardCopyOption;

@ApplicationScoped
public class FactModelsMigrater extends BaseAssetMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(FactModelsMigrater.class);

    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    
    @Inject
    PackageImportHelper packageImportHelper;

    @Inject
    private ProjectService projectService;
    
    @Inject
    private DataModelerService modelerService;    

    private Map <String, String> orderedBaseTypes = new TreeMap<String, String>();
    private Map<String, AnnotationDefinitionTO> annotationDefinitions;
    
    public Path migrate(Module jcrModule, AssetItem jcrAssetItem, Path previousVersionPath) {
        if (!AssetFormats.DRL_MODEL.equals(jcrAssetItem.getFormat())) {
            throw new IllegalArgumentException("The jcrAsset (" + jcrAssetItem.getName()
                    + ") has the wrong format (" + jcrAssetItem.getFormat() + ").");
        }
        
        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAssetItem);   
        //The asset was renamed in this version. We move this asset first.
        if(previousVersionPath != null && !previousVersionPath.equals(path)) {
             ioService.move(Paths.convert( previousVersionPath ), Paths.convert( path ), StandardCopyOption.REPLACE_EXISTING);
        }
        
        Project project = projectService.resolveProject(path);
        
        initBasePropertyTypes();
        initAnnotationDefinitions();        
        
        if(project == null) {
        	Path projectRootPath = migrationPathManager.generatePathForModule(jcrModule.getName());
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
            packageName = migrationPathManager.normalizePackageName(packageName);
            AnnotationDefinitionTO positionAnnotationDef = getPositionAnnotationDefinition();

            for ( FactMetaModel factMetaModel : factModels.models ) {
                DataObjectTO dataObjectTO = createDataObject(packageName, factMetaModel.getName(), factMetaModel.getSuperType());
            	List<AnnotationMetaModel> annotationMetaModel = factMetaModel.getAnnotations();                
            	addAnnotations(dataObjectTO, annotationMetaModel);
                List<FieldMetaModel> fields = factMetaModel.getFields();

                int position = 0;
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
                    property.addAnnotation(positionAnnotationDef, AnnotationDefinitionTO.VALUE_PARAM, position+"" );
                    position++;
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
        
        return path;
     }


    private AnnotationDefinitionTO getPositionAnnotationDefinition() {
        AnnotationDefinition positionAnnotationDef = PositionAnnotationDefinition.getInstance();
        AnnotationDefinitionTO positionAnnotationDefTO = new AnnotationDefinitionTO(positionAnnotationDef.getName(), positionAnnotationDef.getClassName(), positionAnnotationDef.getShortDescription(), positionAnnotationDef.getDescription(), positionAnnotationDef.isObjectAnnotation(), positionAnnotationDef.isPropertyAnnotation());
        AnnotationMemberDefinitionTO memberDefinitionTO;
        for (AnnotationMemberDefinition memberDefinition : positionAnnotationDef.getAnnotationMembers()) {
            memberDefinitionTO = new AnnotationMemberDefinitionTO(memberDefinition.getName(), memberDefinition.getClassName(), memberDefinition.isPrimitiveType(), memberDefinition.isEnum(), memberDefinition.defaultValue(), memberDefinition.getShortDescription(), memberDefinition.getDescription());
            positionAnnotationDefTO.addMember(memberDefinitionTO);
        }
        return positionAnnotationDefTO;
    }

    //The JCR Module name also contains the project name. This code attempts to create a package name
    //from the full JCR Module name (assuming they're formatted "projectName.subModule1.subModule2" etc
    private String getPackageName(Module jcrModule) {
        String packageName = jcrModule.getName();
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
                dataObject.addAnnotation(annotationDefinitions.get(AnnotationDefinitionTO.POSITION_ANNOTATION), key, value);
	   		} else if("Equals".equals(name)) {
                dataObject.addAnnotation(annotationDefinitions.get(AnnotationDefinitionTO.KEY_ANNOTATION), key, value);
    		}
    	}    		
    }

}
