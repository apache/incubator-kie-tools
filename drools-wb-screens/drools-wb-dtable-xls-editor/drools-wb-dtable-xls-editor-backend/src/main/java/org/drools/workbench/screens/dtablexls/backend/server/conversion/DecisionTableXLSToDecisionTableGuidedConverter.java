/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.dtablexls.backend.server.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.core.util.DateUtils;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DataListener;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessageType;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.drools.workbench.screens.drltext.type.DRLResourceTypeDefinition;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSConversionService;
import org.drools.workbench.screens.dtablexls.type.DecisionTableXLSResourceTypeDefinition;
import org.drools.workbench.screens.dtablexls.type.DecisionTableXLSXResourceTypeDefinition;
import org.drools.workbench.screens.factmodel.backend.server.util.FactModelPersistence;
import org.drools.workbench.screens.factmodel.model.AnnotationMetaModel;
import org.drools.workbench.screens.factmodel.model.FactMetaModel;
import org.drools.workbench.screens.factmodel.model.FactModels;
import org.drools.workbench.screens.factmodel.model.FieldMetaModel;
import org.drools.workbench.screens.globals.model.GlobalsModel;
import org.drools.workbench.screens.globals.service.GlobalsEditorService;
import org.drools.workbench.screens.globals.type.GlobalResourceTypeDefinition;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableResourceTypeDefinition;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.screens.datamodeller.model.droolsdomain.DroolsDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataModelImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Converter from a XLS Decision Table to a Guided Decision Table
 */
@ApplicationScoped
public class DecisionTableXLSToDecisionTableGuidedConverter implements DecisionTableXLSConversionService {

    private IOService ioService;
    private DRLTextEditorService drlService;
    private GuidedDecisionTableEditorService guidedDecisionTableService;
    private GlobalsEditorService globalsService;
    private KieModuleService moduleService;
    private ProjectImportsService importsService;
    private MetadataService metadataService;
    private DataModelerService modellerService;
    private DataModelService dataModelService;
    private AppConfigService appConfigService;
    private DecisionTableXLSResourceTypeDefinition xlsDTableType;
    private DecisionTableXLSXResourceTypeDefinition xlsxDTableType;
    private GuidedDTableResourceTypeDefinition guidedDTableType;
    private DRLResourceTypeDefinition drlType;
    private GlobalResourceTypeDefinition globalsType;

    private Map<String, AnnotationDefinition> annotationDefinitions;

    public DecisionTableXLSToDecisionTableGuidedConverter() {
        //Zero-parameter constructor for CDI proxy
    }

    @Inject
    public DecisionTableXLSToDecisionTableGuidedConverter(final @Named("ioStrategy") IOService ioService,
                                                          final DRLTextEditorService drlService,
                                                          final GuidedDecisionTableEditorService guidedDecisionTableService,
                                                          final GlobalsEditorService globalsService,
                                                          final KieModuleService moduleService,
                                                          final ProjectImportsService importsService,
                                                          final MetadataService metadataService,
                                                          final DataModelerService modellerService,
                                                          final DataModelService dataModelService,
                                                          final AppConfigService appConfigService,
                                                          final DecisionTableXLSResourceTypeDefinition xlsDTableType,
                                                          final DecisionTableXLSXResourceTypeDefinition xlsxDTableType,
                                                          final GuidedDTableResourceTypeDefinition guidedDTableType,
                                                          final DRLResourceTypeDefinition drlType,
                                                          final GlobalResourceTypeDefinition globalsType) {
        this.ioService = ioService;
        this.drlService = drlService;
        this.guidedDecisionTableService = guidedDecisionTableService;
        this.globalsService = globalsService;
        this.moduleService = moduleService;
        this.importsService = importsService;
        this.metadataService = metadataService;
        this.modellerService = modellerService;
        this.dataModelService = dataModelService;
        this.appConfigService = appConfigService;
        this.xlsDTableType = xlsDTableType;
        this.xlsxDTableType = xlsxDTableType;
        this.guidedDTableType = guidedDTableType;
        this.drlType = drlType;
        this.globalsType = globalsType;
    }

    @PostConstruct
    public void setup() {
        initialiseTypeConversionMetaData();
        initialiseApplicationPreferences();
    }

    private void initialiseTypeConversionMetaData() {
        annotationDefinitions = modellerService.getAnnotationDefinitions();
    }

    private void initialiseApplicationPreferences() {
        ApplicationPreferences.setUp(appConfigService.loadPreferences());
    }

    @Override
    public ConversionResult convert(final Path path) {

        ConversionResult result = new ConversionResult();

        //Check Asset is of the correct format
        if (!(xlsDTableType.accept(path) || xlsxDTableType.accept(path))) {
            result.addMessage("Source Asset must be either a XLS or XLSX Decision Table file.",
                              ConversionMessageType.ERROR);
            return result;
        }

        final PackageDataModelOracle dmo = dataModelService.getDataModel(path);

        //Perform conversion!
        final GuidedDecisionTableGeneratorListener listener = parseAssets(path,
                                                                          result,
                                                                          dmo);

        //Root path for new resources is the same folder as the XLS file
        final Path context = Paths.convert(Paths.convert(path).getParent());

        //Add Ancillary resources
        createNewImports(context,
                         listener.getImports(),
                         result);
        createNewFunctions(context,
                           listener.getImports(),
                           listener.getFunctions(),
                           result);
        createNewQueries(context,
                         listener.getImports(),
                         listener.getQueries(),
                         result);
        makeNewJavaTypes(context,
                         listener.getTypeDeclarations(),
                         result);
        createNewGlobals(context,
                         listener.getImports(),
                         listener.getGlobals(),
                         result);

        //Add Web Guided Decision Tables
        createNewDecisionTables(context,
                                listener.getImports(),
                                listener.getGuidedDecisionTables(),
                                result);

        return result;
    }

    private GuidedDecisionTableGeneratorListener parseAssets(final Path path,
                                                             final ConversionResult result,
                                                             final PackageDataModelOracle dmo) {

        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener(result,
                                                                                                       dmo);
        listeners.add(listener);

        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream stream = ioService.newInputStream(Paths.convert(path));

        try {
            parser.parseFile(stream);
        } finally {
            try {
                stream.close();
            } catch (IOException ioe) {
                result.addMessage(ioe.getMessage(),
                                  ConversionMessageType.ERROR);
            }
        }
        return listener;
    }

    private void createNewFunctions(final Path context,
                                    final List<Import> imports,
                                    final List<String> functions,
                                    final ConversionResult result) {
        if (functions == null || functions.isEmpty()) {
            return;
        }

        //Create new assets for Functions
        for (int iCounter = 0; iCounter < functions.size(); iCounter++) {

            final String assetName = makeNewAssetName("Function " + (iCounter + 1),
                                                      drlType);
            final String drl = makeDRL(imports,
                                       functions.get(iCounter));
            drlService.create(context,
                              assetName,
                              drl,
                              "Converted from XLS Decision Table");

            result.addMessage("Created Function '" + assetName + "'",
                              ConversionMessageType.INFO);
        }
    }

    private void createNewQueries(final Path context,
                                  final List<Import> imports,
                                  final List<String> queries,
                                  final ConversionResult result) {
        if (queries == null || queries.isEmpty()) {
            return;
        }

        //Create new assets for Queries
        for (int iCounter = 0; iCounter < queries.size(); iCounter++) {

            final String assetName = makeNewAssetName("Query " + (iCounter + 1),
                                                      drlType);
            final String drl = makeDRL(imports,
                                       queries.get(iCounter));
            drlService.create(context,
                              assetName,
                              drl,
                              "Converted from XLS Decision Table");

            result.addMessage("Created Query '" + assetName + "'",
                              ConversionMessageType.INFO);
        }
    }

    private void makeNewJavaTypes(final Path context,
                                  final List<String> declaredTypes,
                                  final ConversionResult result) {
        if (declaredTypes == null || declaredTypes.isEmpty()) {
            return;
        }

        final KieModule module = moduleService.resolveModule(context);

        for (String declaredType : declaredTypes) {
            final FactModels factModels = FactModelPersistence.unmarshal(declaredType);
            final String packageName = factModels.getPackageName();
            final DataModel dataModel = new DataModelImpl();

            for (FactMetaModel factMetaModel : factModels.getModels()) {
                final DataObject dataObject = new DataObjectImpl(packageName,
                                                                 factMetaModel.getName());
                dataObject.setSuperClassName(factMetaModel.getSuperType());
                final List<AnnotationMetaModel> annotationMetaModel = factMetaModel.getAnnotations();
                addAnnotations(dataObject,
                               annotationMetaModel);

                final List<FieldMetaModel> fields = factMetaModel.getFields();

                for (FieldMetaModel fieldMetaModel : fields) {
                    final String fieldName = fieldMetaModel.name;
                    final String fieldType = fieldMetaModel.type;
                    //Guvnor 5.5 (and earlier) does not have MultipleType
                    boolean isMultiple = false;
                    ObjectProperty property = new ObjectPropertyImpl(fieldName,
                                                                     fieldType,
                                                                     isMultiple);

                    //field has no annotation in Guvnor 5.5 (and earlier)
                    dataObject.addProperty(property);

                    result.addMessage("Created Java Type " + getJavaTypeFQCN(dataObject),
                                      ConversionMessageType.INFO);
                }

                dataModel.getDataObjects().add(dataObject);
            }

            modellerService.saveModel(dataModel,
                                      module);
        }
    }

    private String getJavaTypeFQCN(final DataObject dataObject) {
        final String packageName = dataObject.getPackageName();
        final String className = dataObject.getClassName();
        if (packageName == null || packageName.equals("")) {
            return className;
        }
        return packageName + "." + className;
    }

    private void addAnnotations(final DataObject dataObject,
                                final List<AnnotationMetaModel> annotationMetaModelList) {
        for (AnnotationMetaModel annotationMetaModel : annotationMetaModelList) {
            final String name = annotationMetaModel.name;
            final Map<String, String> values = annotationMetaModel.values;

            Annotation annotation;
            String key = DroolsDomainAnnotations.VALUE_PARAM;
            String value = "";

            if (values.size() > 0) {
                key = values.keySet().iterator().next();
                value = values.values().iterator().next();
            }

            if ("Role".equals(name)) {
                annotation = new AnnotationImpl(annotationDefinitions.get(DroolsDomainAnnotations.ROLE_ANNOTATION));
                annotation.setValue(key,
                                    value);
                dataObject.addAnnotation(annotation);
            } else if ("Position".equals(name)) {
                annotation = new AnnotationImpl(annotationDefinitions.get(DroolsDomainAnnotations.POSITION_ANNOTATION));
                annotation.setValue(key,
                                    value);
                dataObject.addAnnotation(annotation);
            } else if ("Equals".equals(name)) {
                annotation = new AnnotationImpl(annotationDefinitions.get(DroolsDomainAnnotations.KEY_ANNOTATION));
                annotation.setValue(key,
                                    value);
                dataObject.addAnnotation(annotation);
            }
        }
    }

    private String makeDRL(final List<Import> imports,
                           final String baseDRL) {
        final StringBuilder sb = new StringBuilder();
        if (!(imports == null || imports.isEmpty())) {
            for (Import item : imports) {
                sb.append("import ").append(item.getClassName()).append(";\n");
            }
            sb.append("\n");
        }
        sb.append(baseDRL).append("\n");
        return sb.toString();
    }

    private void createNewGlobals(final Path context,
                                  final List<Import> imports,
                                  final List<Global> globals,
                                  final ConversionResult result) {
        if (globals == null || globals.isEmpty()) {
            return;
        }

        //Create new asset for Globals. All Globals can be in one file.
        final String assetName = makeNewAssetName("Global",
                                                  globalsType);
        final GlobalsModel model = makeGlobalsModel(imports,
                                                    globals,
                                                    result);
        globalsService.create(context,
                              assetName,
                              model,
                              "Converted from XLS Decision Table");

        result.addMessage("Created Globals '" + assetName + "'",
                          ConversionMessageType.INFO);
    }

    private GlobalsModel makeGlobalsModel(final List<Import> imports,
                                          final List<Global> globals,
                                          final ConversionResult result) {
        final GlobalsModel model = new GlobalsModel();
        for (Global global : globals) {
            if (global.getClassName().contains(".")) {
                model.getGlobals().add(new org.drools.workbench.screens.globals.model.Global(global.getIdentifier(),
                                                                                             global.getClassName()));
            } else {
                boolean mapped = false;
                for (Import imp : imports) {
                    if (imp.getClassName().contains(".")) {
                        final String fullyQualifiedClassName = imp.getClassName();
                        final String leafClassName = fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf(".") + 1);
                        if (global.getClassName().equals(leafClassName)) {
                            model.getGlobals().add(new org.drools.workbench.screens.globals.model.Global(global.getIdentifier(),
                                                                                                         fullyQualifiedClassName));
                            mapped = true;
                            break;
                        }
                    }
                }
                if (!mapped) {
                    result.addMessage("Unable to determine Fully Qualified Class Name for Global '" + global.getIdentifier() + "'. Skipping.",
                                      ConversionMessageType.ERROR);
                }
            }
        }
        return model;
    }

    private void createNewImports(final Path context,
                                  final List<Import> imports,
                                  final ConversionResult result) {

        if (imports == null || imports.isEmpty()) {
            return;
        }

        //Get Module's project.imports path
        final KieModule module = moduleService.resolveModule(context);
        final Path externalImportsPath = module.getImportsPath();

        //Load existing PackageImports
        final ProjectImports projectImports = loadProjectImports(externalImportsPath);

        //Make collections of existing Imports so we don't duplicate them when adding the new
        List<String> existingImports = new ArrayList<String>();
        for (org.kie.soup.project.datamodel.imports.Import item : projectImports.getImports().getImports()) {
            existingImports.add(item.getType());
        }

        //Add imports
        boolean isModified = false;
        for (Import item : imports) {
            if (!existingImports.contains(item.getClassName())) {
                isModified = true;
                result.addMessage("Created Import for '" + item.getClassName() + "'.",
                                  ConversionMessageType.INFO);
                projectImports.getImports().addImport(new org.kie.soup.project.datamodel.imports.Import(item.getClassName()));
            }
        }

        //Save update
        if (isModified) {
            final Metadata metadata = metadataService.getMetadata(context);
            importsService.save(externalImportsPath,
                                projectImports,
                                metadata,
                                "Imports added during XLS conversion");
        }
    }

    ProjectImports loadProjectImports(final Path externalImportsPath) {
        final org.uberfire.java.nio.file.Path nioExternalImportsPath = Paths.convert(externalImportsPath);
        ProjectImports projectImports = new ProjectImports();
        if (Files.exists(nioExternalImportsPath)) {
            projectImports = importsService.load(externalImportsPath);
        }
        return projectImports;
    }

    private void createNewDecisionTables(final Path context,
                                         final List<Import> imports,
                                         final List<GuidedDecisionTable52> dtables,
                                         final ConversionResult result) {
        if (dtables == null || dtables.isEmpty()) {
            return;
        }

        //Create new assets for Guided Decision Tables
        for (int iCounter = 0; iCounter < dtables.size(); iCounter++) {

            //Add imports
            final GuidedDecisionTable52 dtable = dtables.get(iCounter);
            for (Import item : imports) {
                dtable.getImports().addImport(new org.kie.soup.project.datamodel.imports.Import(item.getClassName()));
            }

            //Make new resource
            final String assetName = makeNewAssetName(dtable.getTableName(),
                                                      guidedDTableType);
            guidedDecisionTableService.create(context,
                                              assetName,
                                              dtable,
                                              "Converted from XLS Decision Table");

            result.addMessage("Created Guided Decision Table '" + assetName + "'",
                              ConversionMessageType.INFO);
        }
    }

    private String makeNewAssetName(final String baseName,
                                    final ResourceTypeDefinition type) {
        Calendar now = Calendar.getInstance();
        StringBuilder sb = new StringBuilder(baseName);
        sb.append(" (converted on ");
        sb.append(DateUtils.format(now.getTime()));
        sb.append(" ");
        sb.append(now.get(Calendar.HOUR_OF_DAY));
        sb.append("-");
        sb.append(now.get(Calendar.MINUTE));
        sb.append("-");
        sb.append(now.get(Calendar.SECOND));
        sb.append(")");
        sb.append(".").append(type.getSuffix());
        return sb.toString();
    }
}
