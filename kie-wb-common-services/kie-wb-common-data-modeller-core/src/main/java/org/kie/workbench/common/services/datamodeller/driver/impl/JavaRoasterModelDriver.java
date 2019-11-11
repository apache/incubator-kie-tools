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

package org.kie.workbench.common.services.datamodeller.driver.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jboss.forge.roaster.ParserException;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.Method;
import org.jboss.forge.roaster.model.Parameter;
import org.jboss.forge.roaster.model.SyntaxError;
import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.AnnotationTargetSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.Import;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.ParameterSource;
import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationContext;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationEngine;
import org.kie.workbench.common.services.datamodeller.codegen.GenerationTools;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.Visibility;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ImportImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.JavaClassImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.JavaEnumImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.JavaTypeInfoImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.MethodImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ModelFactoryImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.kie.workbench.common.services.datamodeller.driver.AnnotationDriver;
import org.kie.workbench.common.services.datamodeller.driver.FilterHolder;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverListener;
import org.kie.workbench.common.services.datamodeller.driver.TypeInfoResult;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.CommonAnnotations;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceResponse;
import org.kie.workbench.common.services.datamodeller.driver.model.DriverError;
import org.kie.workbench.common.services.datamodeller.driver.model.DriverResult;
import org.kie.workbench.common.services.datamodeller.driver.model.ModelDriverResult;
import org.kie.workbench.common.services.datamodeller.util.DataModelUtils;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.datamodeller.util.FileUtils;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.commons.data.Pair;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.BOOLEAN;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.BYTE;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.CHAR;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.DOUBLE;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.FLOAT;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.INT;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.LONG;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.SHORT;

public class JavaRoasterModelDriver implements ModelDriver {

    private static final Logger logger = LoggerFactory.getLogger(JavaRoasterModelDriver.class);

    private IOService ioService;

    private Path javaRootPath;

    private ClassLoader classLoader;

    private List<AnnotationDefinition> configuredAnnotations = new ArrayList<AnnotationDefinition>();

    private Map<String, AnnotationDefinition> configuredAnnotationsIndex = new HashMap<String, AnnotationDefinition>();

    private Map<String, AnnotationDriver> annotationDrivers = new HashMap<String, AnnotationDriver>();

    private FilterHolder filterHolder;

    private static final String DATA_OBJECT_LOAD_ERROR = "It was not possible to create or load DataObject: \"{0}\" .";

    private static final String ANNOTATION_LOAD_ERROR = "It was not possible to create or load a DataObject or Field annotation for annotation class name: \"{0}\" .";

    private static final String DATA_OBJECT_FIELD_LOAD_ERROR = "It was not possible to create or load field: \"{0}\" for DataObject: \"{1}\" .";

    private static final String MODEL_LOAD_GENERIC_ERROR = "Unexpected error was produced when a DataModel was being loaded from the following path: \"{0}\" .";

    private static final String GENERIC_ERROR = "Unexpected error was produced.";

    public JavaRoasterModelDriver() {
        configuredAnnotations.addAll(CommonAnnotations.getCommonAnnotations());
        for (AnnotationDefinition annotationDefinition : configuredAnnotations) {
            annotationDrivers.put(annotationDefinition.getClassName(),
                                  new DefaultJavaRoasterModelAnnotationDriver());
            configuredAnnotationsIndex.put(annotationDefinition.getClassName(),
                                           annotationDefinition);
        }
    }

    public JavaRoasterModelDriver(FilterHolder filterHolder) {
        this();
        this.filterHolder = filterHolder;
    }

    public JavaRoasterModelDriver(IOService ioService,
                                  Path javaRootPath,
                                  ClassLoader classLoader,
                                  FilterHolder filterHolder) {
        this();
        this.ioService = ioService;
        this.javaRootPath = javaRootPath;
        this.classLoader = classLoader;
        this.filterHolder = filterHolder;
    }

    @Override
    public List<AnnotationDefinition> getConfiguredAnnotations() {
        return configuredAnnotations;
    }

    @Override
    public AnnotationDefinition getConfiguredAnnotation(String annotationClassName) {
        return configuredAnnotationsIndex.get(annotationClassName);
    }

    @Override
    public AnnotationDriver getAnnotationDriver(String annotationClassName) {
        return annotationDrivers.get(annotationClassName);
    }

    @Override
    public void generateModel(DataModel dataModel,
                              ModelDriverListener generationListener) throws Exception {
        //This driver type do not generate the model.
    }

    @Override
    public ModelDriverResult loadModel() throws ModelDriverException {

        ModelDriverResult result = new ModelDriverResult();
        DataModel dataModel;
        String fileContent;
        dataModel = createModel();
        result.setDataModel(dataModel);

        List<Path> rootPaths = new ArrayList<Path>();
        rootPaths.add(javaRootPath);

        Collection<FileUtils.ScanResult> scanResults = FileUtils.getInstance().scan(ioService,
                                                                                    rootPaths,
                                                                                    ".java",
                                                                                    true);
        if (scanResults != null) {
            for (FileUtils.ScanResult scanResult : scanResults) {

                logger.debug("Starting file loading into model, file: " + scanResult.getFile());
                fileContent = ioService.readAllString(scanResult.getFile());
                if (fileContent == null || "".equals(fileContent)) {
                    logger.debug("file: " + scanResult.getFile() + " is empty.");
                    result.addError(new DriverError("File has no content",
                                                    Paths.convert(scanResult.getFile())));
                    continue;
                }
                try {
                    JavaType<?> javaType = Roaster.parse(fileContent);
                    final boolean isManaged = isManagedJavaType(javaType);
                    final boolean vetoed = (isManaged ? isVetoed(javaType) : false);
                    if (isManaged && !vetoed) {
                        if (javaType.getSyntaxErrors() != null && !javaType.getSyntaxErrors().isEmpty()) {
                            //if a file has parsing errors it will be skipped.
                            addSyntaxErrors(result,
                                            scanResult.getFile(),
                                            javaType.getSyntaxErrors());
                        } else if (javaType.isEnum()) {
                            loadFromJavaEnum((JavaEnumSource) javaType,
                                             scanResult.getFile(),
                                             dataModel,
                                             result);
                        } else {
                            loadFromJavaClass((JavaClassSource) javaType,
                                              scanResult.getFile(),
                                              dataModel,
                                              result);
                        }
                    } else if (vetoed) {
                        logger.debug("The class, {}, in the file, {}, was vetoed and will be skipped.",
                                     javaType.getQualifiedName(),
                                     scanResult.getFile());
                    } else {
                        logger.debug("File: " + scanResult.getFile() + " do not contain a managed java type, it will be skipped.");
                    }
                } catch (ParserException e) {
                    result.addError(new DriverError(e.getMessage(),
                                                    Paths.convert(scanResult.getFile())));
                } catch (Exception e) {
                    //Unexpected error.
                    logger.error(errorMessage(MODEL_LOAD_GENERIC_ERROR,
                                              javaRootPath.toUri()),
                                 e);
                    throw new ModelDriverException(errorMessage(MODEL_LOAD_GENERIC_ERROR,
                                                                javaRootPath.toUri()),
                                                   e);
                }
            }
        }
        return result;
    }

    private boolean isVetoed(final JavaType<?> javaType) {
        return filterHolder.getSourceFilters().stream().anyMatch(filter -> filter.veto(javaType));
    }

    private boolean isAccepted(final JavaType<?> nestedClass) {
        return filterHolder.getNestedClassFilters().stream().anyMatch(filter -> filter.accept(nestedClass));
    }

    private boolean isAccepted(final Method<?, ?> method) {
        return filterHolder.getMethodFilters().stream().anyMatch(filter -> filter.accept(method));
    }

    private boolean isManagedJavaType(final JavaType<?> javaType) {
        return javaType.isClass() || javaType.isEnum();
    }

    private void loadFromJavaClass(JavaClassSource javaClassSource,
                                   Path file,
                                   DataModel dataModel,
                                   ModelDriverResult result) {
        try {
            Pair<DataObject, List<ObjectProperty>> pair = parseDataObject(javaClassSource);
            if (pair.getK1() != null) {
                dataModel.addDataObject(pair.getK1());
                result.setClassPath(pair.getK1().getClassName(),
                                    Paths.convert(file));
                result.setUnmanagedProperties(pair.getK1().getClassName(),
                                              pair.getK2());
            }
        } catch (ModelDriverException e) {
            logger.error("An error was produced when file: " + file + " was being loaded into a DataObject.",
                         e);
            addModelDriverError(result,
                                file,
                                e);
        }
    }

    private void loadFromJavaEnum(JavaEnumSource javaEnumSource,
                                  Path file,
                                  DataModel dataModel,
                                  ModelDriverResult result) {

        String className = javaEnumSource.getName();
        String packageName = javaEnumSource.getPackage();

        Visibility visibility = DriverUtils.buildVisibility(javaEnumSource.getVisibility());

        JavaEnumImpl javaEnum = new JavaEnumImpl(packageName,
                                                 className,
                                                 visibility);

        dataModel.addJavaEnum(javaEnum);
        result.setClassPath(javaEnum.getClassName(),
                            Paths.convert(file));
    }

    public ModelDriverResult loadDataObject(final String source,
                                            final Path path) throws ModelDriverException {

        ModelDriverResult result = new ModelDriverResult();
        DataModel dataModel = createModel();
        result.setDataModel(dataModel);

        if (source == null || "".equals(source)) {
            logger.debug("source: " + source + " is empty.");
            result.addError(new DriverError("Source is empty",
                                            Paths.convert(path)));
            return result;
        }

        try {
            JavaType<?> javaType = Roaster.parse(source);
            if (javaType.isClass()) {
                if (javaType.getSyntaxErrors() != null && !javaType.getSyntaxErrors().isEmpty()) {
                    //if a file has parsing errors it will be skipped.
                    addSyntaxErrors(result,
                                    path,
                                    javaType.getSyntaxErrors());
                } else {
                    try {
                        //try to load the data object.
                        Pair<DataObject, List<ObjectProperty>> pair = parseDataObject((JavaClassSource) javaType);
                        dataModel.addDataObject(pair.getK1());
                        result.setClassPath(pair.getK1().getClassName(),
                                            Paths.convert(path));
                        result.setUnmanagedProperties(pair.getK1().getClassName(),
                                                      pair.getK2());
                    } catch (ModelDriverException e) {
                        logger.error("An error was produced when source: " + source + " was being loaded into a DataObject.",
                                     e);
                        addModelDriverError(result,
                                            path,
                                            e);
                    }
                }
            } else {
                logger.debug("No Class definition was found for source: " + source + ", it will be skipped.");
            }
        } catch (ParserException e) {
            result.addError(new DriverError(e.getMessage(),
                                            Paths.convert(path)));
        } catch (Exception e) {
            //Unexpected exception.
            logger.error(errorMessage(MODEL_LOAD_GENERIC_ERROR,
                                      javaRootPath.toUri()),
                         e);
            throw new ModelDriverException(errorMessage(MODEL_LOAD_GENERIC_ERROR,
                                                        javaRootPath.toUri()),
                                           e);
        }

        return result;
    }

    public TypeInfoResult loadJavaTypeInfo(final String source) throws ModelDriverException {

        TypeInfoResult result = new TypeInfoResult();

        if (source == null || "".equals(source)) {
            logger.debug("source: " + source + " is empty.");
            result.addError(new DriverError("Source is empty"));
            return result;
        }

        try {
            JavaType<?> javaType = Roaster.parse(source);
            if (javaType.getSyntaxErrors() != null && !javaType.getSyntaxErrors().isEmpty()) {
                addSyntaxErrors(result,
                                null,
                                javaType.getSyntaxErrors());
            } else {
                JavaTypeInfoImpl typeInfo = new JavaTypeInfoImpl();
                result.setTypeInfo(typeInfo);

                typeInfo.setName(javaType.getName());
                typeInfo.setPackageName(javaType.getPackage());
                typeInfo.setAnnotation(javaType.isAnnotation());
                typeInfo.setClass(javaType.isClass());
                typeInfo.setEnum(javaType.isEnum());
                typeInfo.setInterface(javaType.isInterface());
                typeInfo.setPackagePrivate(javaType.isPackagePrivate());
                typeInfo.setPrivate(javaType.isPrivate());
                typeInfo.setProtected(javaType.isProtected());
                typeInfo.setPublic(javaType.isPublic());
            }
        } catch (ParserException e) {
            result.addError(new DriverError(e.getMessage()));
        } catch (Exception e) {
            //Unexpected parsing o model loading exception.
            logger.error(errorMessage(GENERIC_ERROR,
                                      e));
            throw new ModelDriverException(errorMessage(MODEL_LOAD_GENERIC_ERROR),
                                           e);
        }

        return result;
    }

    public AnnotationSourceResponse resolveSourceRequest(AnnotationSourceRequest sourceRequest) {
        AnnotationSourceResponse sourceResponse = new AnnotationSourceResponse();

        for (Annotation annotation : sourceRequest.getAnnotations()) {
            sourceResponse.withAnnotationSource(annotation.getClassName(),
                                                resolveAnnotationSource(annotation));
        }
        return sourceResponse;
    }

    public org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource resolveAnnotationSource(Annotation annotation) {
        org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource annotationSource =
                new org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource();

        //TODO this method can be optimized and likely migrated to Roaster. Should be reviewed when we evaluate
        //the removal of Velocity.

        GenerationTools generationTools = new GenerationTools();
        AnnotationDefinition annotationDefinition;

        StringBuilder annotationCode = new StringBuilder();
        annotationCode.append("@");
        annotationCode.append(annotation.getClassName());

        if ((annotationDefinition = annotation.getAnnotationDefinition()) != null) {

            if (!annotationDefinition.isMarker()) {
                annotationCode.append(generationTools.resolveAnnotationType(annotation));
            }

            if (annotationDefinition.getValuePairs() != null) {
                Object value;
                String valuePairCode;
                for (AnnotationValuePairDefinition valuePairDefinition : annotationDefinition.getValuePairs()) {
                    if ((value = annotation.getValue(valuePairDefinition.getName())) != null) {
                        valuePairCode = generationTools.resolveMemberTypeExpression(valuePairDefinition,
                                                                                    value);
                    } else {
                        valuePairCode = null;
                    }
                    annotationSource.withValuePairSource(valuePairDefinition.getName(),
                                                         valuePairCode);
                }
            }
        }

        annotationSource.withSource(annotationCode.toString());
        return annotationSource;
    }

    private void addModelDriverError(ModelDriverResult result,
                                     Path file,
                                     ModelDriverException e) {
        DriverError error;

        StringBuilder message = new StringBuilder();
        message.append(e.getMessage());
        Throwable cause = e.getCause();
        while (cause != null) {
            message.append(" : ");
            message.append(cause.getMessage());
            if (cause instanceof ModelDriverException) {
                cause = cause.getCause();
            } else {
                cause = null;
            }
        }
        error = new DriverError(message.toString(),
                                Paths.convert(file));
        result.addError(error);
    }

    private void addSyntaxErrors(DriverResult result,
                                 Path file,
                                 List<SyntaxError> syntaxErrors) {
        DriverError error;
        for (SyntaxError syntaxError : syntaxErrors) {
            error = new DriverError(syntaxError.getDescription(),
                                    Paths.convert(file));
            error.setLine(syntaxError.getLine());
            error.setColumn(syntaxError.getColumn());
            result.addError(error);
        }
    }

    @Override
    public DataModel createModel() {
        return ModelFactoryImpl.getInstance().newModel();
    }

    private Pair<DataObject, List<ObjectProperty>> parseDataObject(JavaClassSource javaClassSource) throws ModelDriverException {

        String className;
        String packageName;
        String superClass;
        String qualifiedName;
        ClassTypeResolver classTypeResolver;

        className = javaClassSource.getName();
        packageName = javaClassSource.getPackage();
        qualifiedName = NamingUtils.createQualifiedName(packageName,
                                                        className);

        if (logger.isDebugEnabled()) {
            logger.debug("Building DataObject for, packageName: " + packageName + ", className: " + className);
        }

        classTypeResolver = DriverUtils.createClassTypeResolver(javaClassSource,
                                                                classLoader);

        Visibility visibility = DriverUtils.buildVisibility(javaClassSource.getVisibility());

        DataObject dataObject = new DataObjectImpl(packageName,
                                                   className,
                                                   visibility,
                                                   javaClassSource.isAbstract(),
                                                   javaClassSource.isFinal());

        List<ObjectProperty> unmanagedProperties = new ArrayList<ObjectProperty>();

        try {
            if (javaClassSource.getSuperType() != null) {
                superClass = resolveTypeName(classTypeResolver,
                                             javaClassSource.getSuperType());
                dataObject.setSuperClassName(superClass);
            }

            List<AnnotationSource<JavaClassSource>> annotations = javaClassSource.getAnnotations();
            if (annotations != null) {
                for (AnnotationSource annotation : annotations) {
                    addJavaClassAnnotation(dataObject,
                                           annotation,
                                           classTypeResolver);
                }
            }

            List<MethodSource<JavaClassSource>> classMethods = javaClassSource.getMethods();
            if (classMethods != null) {
                for (MethodSource<JavaClassSource> classMethod : classMethods) {
                    if (isAccepted(classMethod)) {
                        addMethod(dataObject,
                                  classMethod,
                                  classTypeResolver);
                    }
                }
            }

            List<JavaSource<?>> nestedTypes = javaClassSource.getNestedTypes();
            if (nestedTypes != null) {
                for (JavaSource nestedType : nestedTypes) {
                    if (isAccepted(nestedType)) {
                        if (nestedType instanceof JavaClassSource) {
                            JavaClassImpl nestedJavaClass = new JavaClassImpl("",
                                                                              nestedType.getName(),
                                                                              DriverUtils.buildVisibility(nestedType.getVisibility()));
                            dataObject.addNestedClass(nestedJavaClass);
                            if (javaClassSource.getInterfaces() != null) {
                                for (String interfaceDefinition : ((JavaClassSource) nestedType).getInterfaces()) {
                                    nestedJavaClass.addInterface(interfaceDefinition);
                                }
                            }
                            List<AnnotationSource<JavaClassSource>> nestedClassAnnotations = nestedType.getAnnotations();
                            if (nestedClassAnnotations != null) {
                                for (AnnotationSource annotation : nestedClassAnnotations) {
                                    addJavaClassAnnotation(nestedJavaClass,
                                                           annotation,
                                                           classTypeResolver);
                                }
                            }
                            List<MethodSource<JavaClassSource>> nestedClassMethods = ((JavaClassSource) nestedType).getMethods();
                            if (nestedClassMethods != null) {
                                for (Method nestedClassMethod : nestedClassMethods) {
                                    if (isAccepted(nestedClassMethod)) {
                                        addMethod(nestedJavaClass,
                                                  nestedClassMethod,
                                                  classTypeResolver);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            List<FieldSource<JavaClassSource>> fields = javaClassSource.getFields();
            if (fields != null) {
                for (FieldSource<JavaClassSource> field : fields) {
                    if (DriverUtils.isManagedType(field.getType(),
                                                  classTypeResolver)) {
                        addProperty(dataObject,
                                    field,
                                    classTypeResolver);
                    } else {
                        logger.debug("field: " + field + "with fieldName: " + field.getName() + " won't be loaded by the diver because type: " + field.getType().getName() + " isn't a managed type.");
                        unmanagedProperties.add(new ObjectPropertyImpl(field.getName(),
                                                                       field.getType().toString(),
                                                                       false,
                                                                       DriverUtils.buildVisibility(field.getVisibility()),
                                                                       field.isStatic(),
                                                                       field.isFinal()));
                    }
                }
            }

            List<Import> imports = javaClassSource.getImports();
            if (imports != null) {
                for (Import _import : imports) {
                    dataObject.addImport(new ImportImpl(_import.getQualifiedName()));
                }
            }

            return new Pair<DataObject, List<ObjectProperty>>(dataObject,
                                                              unmanagedProperties);
        } catch (ClassNotFoundException e) {
            logger.error(errorMessage(DATA_OBJECT_LOAD_ERROR,
                                      qualifiedName),
                         e);
            throw new ModelDriverException(errorMessage(DATA_OBJECT_LOAD_ERROR,
                                                        qualifiedName),
                                           e);
        } catch (ModelDriverException e) {
            logger.error(errorMessage(DATA_OBJECT_LOAD_ERROR,
                                      qualifiedName),
                         e);
            throw new ModelDriverException(errorMessage(DATA_OBJECT_LOAD_ERROR,
                                                        qualifiedName),
                                           e);
        }
    }

    private ObjectProperty addProperty(DataObject dataObject,
                                       FieldSource<JavaClassSource> field,
                                       ClassTypeResolver classTypeResolver) throws ModelDriverException {
        ObjectProperty property = parseProperty(field,
                                                classTypeResolver);
        dataObject.addProperty(property);
        return property;
    }

    private List<org.kie.workbench.common.services.datamodeller.core.Type> resolveTypeArguments(List<Type> typeArguments) {
        List<org.kie.workbench.common.services.datamodeller.core.Type> result = new ArrayList<>();
        if (typeArguments != null) {
            resolveTypeArguments(typeArguments,
                                 result);
        }
        return result;
    }

    private void resolveTypeArguments(List<Type> typeArguments,
                                      List<org.kie.workbench.common.services.datamodeller.core.Type> resultTypeArguments) {
        if (typeArguments != null) {
            for (Type typeArgument : typeArguments) {
                org.kie.workbench.common.services.datamodeller.core.impl.TypeImpl resultType = new org.kie.workbench.common.services.datamodeller.core.impl.TypeImpl(typeArgument.getQualifiedName(),
                                                                                                                                                                     new ArrayList<>());
                resultTypeArguments.add(resultType);
                resolveTypeArguments(typeArgument.getTypeArguments(),
                                     resultType.getTypeArguments());
            }
        }
    }

    private void addMethod(org.kie.workbench.common.services.datamodeller.core.JavaClass javaClass,
                           Method method,
                           ClassTypeResolver classTypeResolver) throws ClassNotFoundException, ModelDriverException {
        List<Parameter> parameters = method.getParameters();

        List<org.kie.workbench.common.services.datamodeller.core.Parameter> modelParameters = new ArrayList<>();

        if (parameters != null) {
            for (Parameter parameter : parameters) {
                modelParameters.add(new org.kie.workbench.common.services.datamodeller.core.impl.ParameterImpl(
                        new org.kie.workbench.common.services.datamodeller.core.impl.TypeImpl(resolveTypeName(classTypeResolver,
                                                                                                              parameter.getType().getName()),
                                                                                              resolveTypeArguments(parameter.getType().getTypeArguments())),
                        parameter.getName()));
            }
        }

        org.kie.workbench.common.services.datamodeller.core.Type returnType = null;

        if (method.getReturnType() != null) {
            returnType = new org.kie.workbench.common.services.datamodeller.core.impl.TypeImpl(
                    resolveTypeName(classTypeResolver,
                                    method.getReturnType().getName()),
                    resolveTypeArguments(method.getReturnType().getTypeArguments()));
        }

        Visibility visibility = Visibility.PACKAGE_PRIVATE;

        if (method.getVisibility() != null) {
            visibility = DriverUtils.buildVisibility(method.getVisibility());
        }

        MethodImpl dataObjectMethod = new MethodImpl(method.getName(),
                                                     modelParameters,
                                                     method.getBody(),
                                                     returnType,
                                                     visibility);

        List<AnnotationSource<JavaClassSource>> annotations = method.getAnnotations();
        if (annotations != null) {
            for (AnnotationSource annotation : annotations) {
                dataObjectMethod.addAnnotation(createAnnotation(annotation,
                                                                classTypeResolver));
            }
        }

        javaClass.addMethod(dataObjectMethod);
    }

    public ObjectProperty parseProperty(FieldSource<JavaClassSource> field,
                                        ClassTypeResolver classTypeResolver) throws ModelDriverException {
        Type type;
        boolean multiple = false;
        String className;
        String bag = null;
        ObjectProperty property;

        Visibility visibility = DriverUtils.buildVisibility(field.getVisibility());

        try {
            type = field.getType();
            if (type.isPrimitive()) {
                className = type.getName();
            } else {
                if (DriverUtils.isSimpleClass(type)) {
                    className = resolveTypeName(classTypeResolver,
                                                type.getName());
                } else {
                    //if this point was reached, we know it's a Collection. Managed type check was done previous to adding the property.
                    multiple = true;
                    @SuppressWarnings("unchecked")
                    Type elementsType = ((List<Type>) type.getTypeArguments()).get(0);
                    className = resolveTypeName(classTypeResolver,
                                                elementsType.getName());
                    bag = resolveTypeName(classTypeResolver,
                                          type.getName());
                }
            }

            property = new ObjectPropertyImpl(field.getName(),
                                              className,
                                              multiple,
                                              bag,
                                              visibility,
                                              field.isStatic(),
                                              field.isFinal());

            List<AnnotationSource<JavaClassSource>> annotations = field.getAnnotations();
            if (annotations != null) {
                for (AnnotationSource annotation : annotations) {
                    addPropertyAnnotation(property,
                                          annotation,
                                          classTypeResolver);
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error(errorMessage(DATA_OBJECT_FIELD_LOAD_ERROR,
                                      field.getName(),
                                      field.getOrigin().getName()),
                         e);
            throw new ModelDriverException(errorMessage(DATA_OBJECT_FIELD_LOAD_ERROR,
                                                        field.getName(),
                                                        field.getOrigin().getName()),
                                           e);
        }

        return property;
    }

    public List<ObjectProperty> parseManagedTypesProperties(JavaClassSource javaClassSource,
                                                            ClassTypeResolver classTypeResolver) throws ModelDriverException {

        List<FieldSource<JavaClassSource>> fields = javaClassSource.getFields();
        List<ObjectProperty> properties = new ArrayList<ObjectProperty>();
        ObjectProperty property;

        for (FieldSource<JavaClassSource> field : fields) {
            if (DriverUtils.isManagedType(field.getType(),
                                          classTypeResolver)) {
                property = parseProperty(field,
                                         classTypeResolver);
                properties.add(property);
            } else {
                logger.debug("field: " + field + "with fieldName: " + field.getName() + " won't be loaded by the diver because type: " + field.getType().getName() + " isn't a managed type.");
            }
        }
        return properties;
    }

    private void addJavaClassAnnotation(org.kie.workbench.common.services.datamodeller.core.JavaClass javaClass,
                                        AnnotationSource annotationSource,
                                        ClassTypeResolver classTypeResolver) throws ModelDriverException {
        Annotation annotation = createAnnotation(annotationSource,
                                                 classTypeResolver);
        if (annotation != null) {
            javaClass.addAnnotation(annotation);
        }
    }

    private void addPropertyAnnotation(ObjectProperty property,
                                       AnnotationSource annotationSource,
                                       ClassTypeResolver classTypeResolver) throws ModelDriverException {
        Annotation annotation = createAnnotation(annotationSource,
                                                 classTypeResolver);
        if (annotation != null) {
            property.addAnnotation(annotation);
        }
    }

    private Annotation createAnnotation(AnnotationSource annotationToken,
                                        ClassTypeResolver classTypeResolver) throws ModelDriverException {

        AnnotationDefinition annotationDefinition;
        Annotation annotation = null;

        annotationDefinition = buildAnnotationDefinition(annotationToken,
                                                         classTypeResolver);
        if (annotationDefinition != null) {
            AnnotationDriver annotationDriver = new DefaultJavaRoasterModelAnnotationDriver();
            annotation = annotationDriver.buildAnnotation(annotationDefinition,
                                                          annotationToken);
        } else {
            logger.warn("Annotation: " + annotationToken.getName() + " is not configured for this ModelDriver driver.");
        }
        return annotation;
    }

    public AnnotationDefinition buildAnnotationDefinition(AnnotationSource annotationSource,
                                                          ClassTypeResolver classTypeResolver) throws ModelDriverException {
        return buildAnnotationDefinition(annotationSource.getQualifiedName(),
                                         classTypeResolver);
    }

    public AnnotationDefinition buildAnnotationDefinition(String annotationClassName,
                                                          ClassTypeResolver classTypeResolver) throws ModelDriverException {
        try {
            String resolvedClassName = resolveTypeName(classTypeResolver,
                                                       annotationClassName);
            Class annotationClass = classTypeResolver.resolveType(resolvedClassName);
            return DriverUtils.buildAnnotationDefinition(annotationClass);
        } catch (ClassNotFoundException e) {
            logger.error(errorMessage(ANNOTATION_LOAD_ERROR,
                                      annotationClassName),
                         e);
            throw new ModelDriverException(errorMessage(ANNOTATION_LOAD_ERROR,
                                                        annotationClassName),
                                           e);
        }
    }

    private String resolveTypeName(ClassTypeResolver classTypeResolver,
                                   String name) throws ClassNotFoundException {
        try {
            if (name == null) {
                return null;
            } else if (NamingUtils.isQualifiedName(name)) {
                return name;
            } else if ("void".equals(name)) {
                return name;
            } else {
                return classTypeResolver.getFullTypeName(name);
            }
        } catch (ClassNotFoundException e) {
            logger.error("Class could not be resolved for name: " + name,
                         e);
            throw e;
        }
    }

    public void updateImports(JavaClassSource javaClassSource,
                              List<org.kie.workbench.common.services.datamodeller.core.Import> dataObjectImports,
                              UpdateInfo updateInfo) {

        List<Import> imports = javaClassSource.getImports();

        if (imports != null) {
            for (Import _import : imports) {
                javaClassSource.removeImport(_import);
            }
        }

        if (dataObjectImports != null) {
            for (org.kie.workbench.common.services.datamodeller.core.Import _import : dataObjectImports) {
                javaClassSource.addImport(_import.getName());
            }
        }

        String newClassName;
        String currentPackage = javaClassSource.isDefaultPackage() ? null : javaClassSource.getPackage();

        if (imports != null) {
            for (Import currentImport : imports) {
                if (!currentImport.isWildcard() && !currentImport.isStatic()) {
                    if ((newClassName = updateInfo.getRenamedClasses().get(currentImport.getQualifiedName())) != null) {
                        javaClassSource.removeImport(currentImport);
                        if (!StringUtils.equals(currentPackage,
                                                NamingUtils.extractPackageName(newClassName))) {
                            javaClassSource.addImport(newClassName);
                        }
                    } else if (updateInfo.getDeletedClasses().contains(currentImport.getQualifiedName())) {
                        javaClassSource.removeImport(currentImport);
                    }
                }
            }
        }
    }

    public void updateImports(JavaClassSource javaClassSource,
                              Map<String, String> renamedClasses,
                              List<String> deletedClasses) {

        List<Import> imports = javaClassSource.getImports();
        String newClassName;
        String currentPackage = javaClassSource.isDefaultPackage() ? null : javaClassSource.getPackage();

        if (imports != null) {
            for (Import currentImport : imports) {
                if (!currentImport.isWildcard() && !currentImport.isStatic()) {
                    if ((newClassName = renamedClasses.get(currentImport.getQualifiedName())) != null) {
                        javaClassSource.removeImport(currentImport);
                        if (!StringUtils.equals(currentPackage,
                                                NamingUtils.extractPackageName(newClassName))) {
                            javaClassSource.addImport(newClassName);
                        }
                    } else if (deletedClasses.contains(currentImport.getQualifiedName())) {
                        javaClassSource.removeImport(currentImport);
                    }
                }
            }
        }
    }

    public boolean updatePackage(JavaClassSource javaClassSource,
                                 String packageName) {

        String oldPackageName = javaClassSource.getPackage();
        if (packageName == null) {
            javaClassSource.setDefaultPackage();
        } else {
            javaClassSource.setPackage(packageName);
        }

        return StringUtils.equals(oldPackageName,
                                  packageName);
    }

    public void updateSource(JavaClassSource javaClassSource,
                             DataObject dataObject,
                             UpdateInfo updateInfo,
                             ClassTypeResolver classTypeResolver) throws Exception {

        if (javaClassSource == null || !javaClassSource.isClass()) {
            logger.warn("A null javaClassSource or javaClassSouce is not a Class, no processing will be done. javaClassSource: " + javaClassSource + " className: " + (javaClassSource != null ? javaClassSource.getName() : null));
            return;
        }

        Map<String, FieldSource<JavaClassSource>> currentClassFields = new HashMap<String, FieldSource<JavaClassSource>>();
        List<FieldSource<JavaClassSource>> classFields = javaClassSource.getFields();
        Map<String, String> preservedFields = new HashMap<String, String>();

        //update package, class name, and super class name if needed.
        updatePackage(javaClassSource,
                      dataObject.getPackageName());
        updateImports(javaClassSource,
                      dataObject.getImports(),
                      updateInfo);
        updateAnnotations(javaClassSource,
                          dataObject.getAnnotations(),
                          classTypeResolver);
        updateMethods(javaClassSource,
                      dataObject.getMethods(),
                      classTypeResolver);
        updateClassName(javaClassSource,
                        dataObject.getName());
        updateSuperClassName(javaClassSource,
                             dataObject.getSuperClassName(),
                             classTypeResolver);

        if (classFields != null) {
            for (FieldSource<JavaClassSource> field : classFields) {
                currentClassFields.put(field.getName(),
                                       field);
            }
        }

        List<ObjectProperty> currentManagedProperties = parseManagedTypesProperties(javaClassSource,
                                                                                    classTypeResolver);
        currentManagedProperties = DataModelUtils.filterAssignableFields(currentManagedProperties);

        //prior to touch the class fields get the constructors candidates to be the current all fields, position annotated fields, and key annotated fields constructors.
        List<MethodSource<JavaClassSource>> allFieldsConstructorCandidates = findAllFieldsConstructorCandidates(javaClassSource,
                                                                                                                currentManagedProperties,
                                                                                                                classTypeResolver);
        List<MethodSource<JavaClassSource>> keyFieldsConstructorCandidates = findKeyFieldsConstructorCandidates(javaClassSource,
                                                                                                                currentManagedProperties,
                                                                                                                classTypeResolver);
        List<MethodSource<JavaClassSource>> positionFieldsConstructorCandidates = findPositionFieldsConstructorCandidates(javaClassSource,
                                                                                                                          currentManagedProperties,
                                                                                                                          classTypeResolver);

        //create new fields and update existing.
        for (ObjectProperty property : dataObject.getProperties()) {

            if (property.isFinal() || property.isStatic()) {
                preservedFields.put(property.getName(),
                                    property.getName());
                continue;
            }

            if (currentClassFields.containsKey(property.getName())) {
                updateField(javaClassSource,
                            property.getName(),
                            property,
                            classTypeResolver);
            } else {
                createField(javaClassSource,
                            property,
                            classTypeResolver);
            }

            preservedFields.put(property.getName(),
                                property.getName());
        }

        //update constructors, equals and hashCode methods.
        updateConstructors(javaClassSource,
                           dataObject,
                           allFieldsConstructorCandidates,
                           keyFieldsConstructorCandidates,
                           positionFieldsConstructorCandidates,
                           classTypeResolver);

        //delete fields from .java file that not exists in the DataObject.
        List<String> removableFields = new ArrayList<String>();
        for (FieldSource<JavaClassSource> field : currentClassFields.values()) {
            if (!preservedFields.containsKey(field.getName()) &&
                    isManagedField(field,
                                   classTypeResolver)) {
                removableFields.add(field.getName());
            }
        }
        for (String fieldName : removableFields) {
            removeField(javaClassSource,
                        fieldName,
                        classTypeResolver);
        }
        // update nested classes
        List<JavaSource<?>> nestedTypes = javaClassSource.getNestedTypes();
        if (nestedTypes != null) {
            for (JavaSource nestedJavaSource : nestedTypes) {
                if (isAccepted(nestedJavaSource)) {
                    javaClassSource.removeNestedType(nestedJavaSource);
                }
            }
        }
        GenerationEngine engine = GenerationEngine.getInstance();
        GenerationContext context = new GenerationContext(null);
        for (org.kie.workbench.common.services.datamodeller.core.JavaClass nestedJavaClass : dataObject.getNestedClasses()) {
            javaClassSource.addNestedType(engine.generateNestedClassString(context,
                                                                           nestedJavaClass,
                                                                           ""));
        }
    }

    public boolean updateClassName(JavaClassSource javaClassSource,
                                   String name) throws Exception {

        String oldName = javaClassSource.getName();
        javaClassSource.setName(name);

        return StringUtils.equals(oldName,
                                  name);
    }

    public boolean updateSuperClassName(JavaClassSource javaClassSource,
                                        String superClassName,
                                        ClassTypeResolver classTypeResolver) throws Exception {

        String oldSuperClassName = javaClassSource.getSuperType() != null ? resolveTypeName(classTypeResolver,
                                                                                            javaClassSource.getSuperType()) : null;

        if (!StringUtils.equals(oldSuperClassName,
                                superClassName)) {
            //TODO remove the extra "import packageName.SuperClassName" added by Roaster when a class name is set as superclass.
            javaClassSource.setSuperType(superClassName);
            return true;
        }
        return false;
    }

    public void updateAnnotations(AnnotationTargetSource annotationTargetSource,
                                  List<Annotation> annotations,
                                  ClassTypeResolver classTypeResolver) throws Exception {

        List<AnnotationSource<?>> currentAnnotations = annotationTargetSource.getAnnotations();
        if (currentAnnotations != null) {
            for (AnnotationSource<?> currentAnnotation : currentAnnotations) {
                annotationTargetSource.removeAnnotation(currentAnnotation);
            }
        }

        if (annotations != null) {
            for (Annotation annotation : annotations) {
                addAnnotation(annotationTargetSource,
                              annotation);
            }
        }
    }

    public AnnotationSource<?> addAnnotation(AnnotationTargetSource annotationTargetSource,
                                             Annotation annotation) {

        AnnotationSource<?> newAnnotationSource = annotationTargetSource.addAnnotation();
        newAnnotationSource.setName(annotation.getClassName());

        AnnotationDefinition annotationDefinition = annotation.getAnnotationDefinition();
        if (!annotationDefinition.isMarker()) {
            for (AnnotationValuePairDefinition memberDefinition : annotationDefinition.getValuePairs()) {
                Object value = annotation.getValue(memberDefinition.getName());
                if (value != null) {
                    addMemberValue(newAnnotationSource,
                                   memberDefinition,
                                   value);
                }
            }
        }
        return newAnnotationSource;
    }

    public void addMemberValue(AnnotationSource<?> annotationSource,
                               AnnotationValuePairDefinition valuePairDefinition,
                               Object value) {
        if (value == null) {
            return;
        }

        String encodedValue;

        if (valuePairDefinition.isEnum()) {
            if (valuePairDefinition.isArray()) {
                encodedValue = DriverUtils.encodeEnumArrayValue(valuePairDefinition,
                                                                value);
            } else {
                encodedValue = DriverUtils.encodeEnumValue(valuePairDefinition,
                                                           value);
            }
            if (encodedValue != null) {
                annotationSource.setLiteralValue(valuePairDefinition.getName(),
                                                 encodedValue);
            }
        } else if (valuePairDefinition.isString()) {
            //characters like '\r\t', \n, and " needs to be escaped due to Roaster internal implementation.
            if (valuePairDefinition.isArray()) {
                encodedValue = DriverUtils.encodeStringArrayValue(value,
                                                                  true);
            } else {
                encodedValue = DriverUtils.encodeStringValue(value,
                                                             true);
            }
            if (encodedValue != null) {
                annotationSource.setLiteralValue(valuePairDefinition.getName(),
                                                 encodedValue);
            }
        } else if (valuePairDefinition.isPrimitiveType()) {
            //primitive types are wrapped by the java.lang.type.
            if (valuePairDefinition.isArray()) {
                encodedValue = DriverUtils.encodePrimitiveArrayValue(valuePairDefinition,
                                                                     value);
            } else {
                encodedValue = DriverUtils.encodePrimitiveValue(valuePairDefinition,
                                                                value);
            }
            if (encodedValue != null) {
                annotationSource.setLiteralValue(valuePairDefinition.getName(),
                                                 encodedValue);
            }
        } else if (valuePairDefinition.isAnnotation()) {
            if (valuePairDefinition.isArray()) {
                List<Annotation> annotations = new ArrayList<Annotation>();
                if (value instanceof List) {
                    for (Object item : (List) value) {
                        if (item instanceof Annotation) {
                            annotations.add((Annotation) item);
                        }
                    }
                } else if (value instanceof Annotation) {
                    annotations.add((Annotation) value);
                }
                addAnnotationArrayMemberValue(annotationSource,
                                              valuePairDefinition,
                                              annotations);
            } else if (value instanceof Annotation) {
                addAnnotationMemberValue(annotationSource,
                                         valuePairDefinition,
                                         (Annotation) value);
            }
        } else if (valuePairDefinition.isClass()) {
            if (valuePairDefinition.isArray()) {
                encodedValue = DriverUtils.encodeClassArrayValue(value);
            } else {
                encodedValue = DriverUtils.encodeClassValue(value.toString());
            }
            if (encodedValue != null) {
                annotationSource.setLiteralValue(valuePairDefinition.getName(),
                                                 encodedValue);
            }
        }
    }

    private void addAnnotationMemberValue(AnnotationSource annotationSource,
                                          AnnotationValuePairDefinition valuePairDefinition,
                                          Annotation annotation) {
        AnnotationSource targetAnnotation = annotationSource.addAnnotationValue(valuePairDefinition.getName());
        targetAnnotation.setName(annotation.getClassName());

        if (!annotation.getAnnotationDefinition().isMarker()) {
            for (AnnotationValuePairDefinition memberDefinition : annotation.getAnnotationDefinition().getValuePairs()) {
                Object value = annotation.getValue(memberDefinition.getName());
                if (value != null) {
                    addMemberValue(targetAnnotation,
                                   memberDefinition,
                                   value);
                }
            }
        }
    }

    private void addAnnotationArrayMemberValue(AnnotationSource annotationSource,
                                               AnnotationValuePairDefinition valuePairDefinition,
                                               List<Annotation> annotations) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                addAnnotationMemberValue(annotationSource,
                                         valuePairDefinition,
                                         annotation);
            }
        }
    }

    public void updateMethods(JavaClassSource javaClassSource,
                              List<org.kie.workbench.common.services.datamodeller.core.Method> methods,
                              ClassTypeResolver classTypeResolver) throws Exception {

        List<MethodSource<JavaClassSource>> currentMethods = javaClassSource.getMethods();
        if (currentMethods != null) {
            for (MethodSource<JavaClassSource> currentMethod : currentMethods) {
                if (isAccepted(currentMethod)) {
                    javaClassSource.removeMethod(currentMethod);
                }
            }
        }

        if (methods != null) {
            for (org.kie.workbench.common.services.datamodeller.core.Method method : methods) {
                addMethod(javaClassSource,
                          method,
                          classTypeResolver);
            }
        }
    }

    private void addMethod(JavaClassSource javaClassSource,
                           org.kie.workbench.common.services.datamodeller.core.Method method,
                           ClassTypeResolver classTypeResolver) throws ClassNotFoundException {
        MethodSource<JavaClassSource> methodSource = javaClassSource.addMethod();
        methodSource.setName(method.getName());
        methodSource.setReturnType(buildMethodReturnTypeString(method.getReturnType(),
                                                               classTypeResolver));
        methodSource.setParameters(buildMethodParameterString(method.getParameters(),
                                                              classTypeResolver));
        methodSource.setBody(method.getBody());
        methodSource.setVisibility(buildVisibility(method.getVisibilty()));

        for (Annotation annotation : method.getAnnotations()) {
            addAnnotation(methodSource,
                          annotation);
        }
    }

    private String buildMethodReturnTypeString(org.kie.workbench.common.services.datamodeller.core.Type methodReturnType,
                                               ClassTypeResolver classTypeResolver) throws ClassNotFoundException {
        if (methodReturnType == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(resolveTypeName(classTypeResolver,
                                       methodReturnType.getName()));

        buildTypeArgumentsString(methodReturnType.getTypeArguments(),
                                 classTypeResolver,
                                 builder);

        return builder.toString();
    }

    private String buildMethodParameterString(List<org.kie.workbench.common.services.datamodeller.core.Parameter> methodParameters,
                                              ClassTypeResolver classTypeResolver) throws ClassNotFoundException {
        if (methodParameters == null || methodParameters.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        java.util.Iterator<org.kie.workbench.common.services.datamodeller.core.Parameter> iterator = methodParameters.iterator();

        while (iterator.hasNext()) {
            org.kie.workbench.common.services.datamodeller.core.Parameter parameter = iterator.next();

            org.kie.workbench.common.services.datamodeller.core.Type parameterType = parameter.getType();
            builder.append(resolveTypeName(classTypeResolver,
                                           parameterType.getName()));
            buildTypeArgumentsString(parameter.getType().getTypeArguments(),
                                     classTypeResolver,
                                     builder);
            builder.append(" ");
            builder.append(parameter.getName());

            if (iterator.hasNext()) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    private org.jboss.forge.roaster.model.Visibility buildVisibility(Visibility visibility) {
        if (visibility == null) {
            return org.jboss.forge.roaster.model.Visibility.PACKAGE_PRIVATE;
        }
        return DriverUtils.buildVisibility(visibility);
    }

    private void buildTypeArgumentsString(List<org.kie.workbench.common.services.datamodeller.core.Type> typeArguments,
                                          ClassTypeResolver classTypeResolver,
                                          StringBuilder builder) throws ClassNotFoundException {
        if (typeArguments == null || typeArguments.isEmpty()) {
            return;
        }
        builder.append("<");

        java.util.Iterator<org.kie.workbench.common.services.datamodeller.core.Type> iterator = typeArguments.iterator();

        while (iterator.hasNext()) {
            org.kie.workbench.common.services.datamodeller.core.Type argument = iterator.next();

            builder.append(resolveTypeName(classTypeResolver,
                                           argument.getName()));

            buildTypeArgumentsString(argument.getTypeArguments(),
                                     classTypeResolver,
                                     builder);

            if (iterator.hasNext()) {
                builder.append(",");
            }
        }

        builder.append(">");
    }

    public void createField(JavaClassSource javaClassSource,
                            ObjectProperty property,
                            ClassTypeResolver classTypeResolver) throws Exception {

        String fieldSource;
        String methodSource;
        String methodName;

        GenerationContext generationContext = new GenerationContext(null);
        GenerationEngine engine;
        GenerationTools genTools = new GenerationTools();

        try {
            engine = GenerationEngine.getInstance();

            fieldSource = genTools.indent(engine.generateCompleteFieldString(generationContext,
                                                                             property));
            javaClassSource.addField(fieldSource);

            //create getter
            methodSource = genTools.indent(engine.generateFieldGetterString(generationContext,
                                                                            property));
            methodName = genTools.toJavaGetter(property.getName(),
                                               property.getClassName());

            //remove old getter if exists
            removeMethodByParamsClassName(javaClassSource,
                                          methodName);
            //add the new getter
            javaClassSource.addMethod(methodSource);

            //create setter
            methodSource = genTools.indent(engine.generateFieldSetterString(generationContext,
                                                                            property));
            methodName = genTools.toJavaSetter(property.getName());

            //remove old setter if exists
            //TODO check collections

            //TODO aca tengo un problema cuando creo un Pojo en memoria y a su vez un field de ese tipo.
            //Porque intento resolver la clase con el classTypeResolver y el Pojo aun no ha sido creado con lo cual
            //tengo Class Not found exception.
            //Tengo que implementar el remove de otra forma para este caso, posiblemente iterando todos los metodos.
            //Cuando le cambio el tipo a un field de un pojo existente hacia un tipo de una clase creada en memoria
            //Crei que podria darse tambien esta exception pero parece que no.
            //Tengo que ver a ver porque no se da el error en este caso.

            //Class<?> fieldClass = classTypeResolver.resolveType( property.getClassName() );
            removeMethodByParamsClassName(javaClassSource,
                                          methodName,
                                          property.getClassName());
            //add the new setter
            javaClassSource.addMethod(methodSource);
        } catch (Exception e) {
            logger.error("Field: " + property.getName() + " couldn't be created.",
                         e);
            throw e;
        }
    }

    public void updateField(JavaClassSource javaClassSource,
                            String fieldName,
                            ObjectProperty property,
                            ClassTypeResolver classTypeResolver) throws Exception {

        GenerationTools genTools = new GenerationTools();
        GenerationEngine engine = GenerationEngine.getInstance();
        GenerationContext context = new GenerationContext(null);
        boolean updateAccessors = false;
        FieldSource<JavaClassSource> field;

        field = javaClassSource.getField(fieldName);
        Type oldType = field.getType();

        if (hasChangedToCollectionType(field,
                                       property,
                                       classTypeResolver)) {
            //fields that changed to a collection like java.util.List<SomeEntity>
            //needs to be removed and created again due to Roaster. Ideally it shouldn't be so.
            updateCollectionField(javaClassSource,
                                  fieldName,
                                  property,
                                  classTypeResolver);
        } else {
            //for the rest of changes is better to manage the field update without removing the field.

            if (!fieldName.equals(property.getName())) {
                field.setName(property.getName());
                //the field was renamed, accessors must be updated.
                updateAccessors = true;
            }

            if (DriverUtils.isManagedType(field.getType(),
                                          classTypeResolver) &&
                    !DriverUtils.equalsType(field.getType(),
                                            property.getClassName(),
                                            property.isMultiple(),
                                            property.getBag(),
                                            classTypeResolver)) {
                //the has type changed, and not to a collection type.

                String newClassName = property.getClassName();
                field.setType(newClassName);

                if (field.getLiteralInitializer() != null) {
                    //current field has an initializer, but the field type changed so we are not sure old initializer is
                    //valid for the new type.
                    if (NamingUtils.isPrimitiveTypeId(newClassName)) {
                        setPrimitiveTypeDefaultInitializer(field,
                                                           newClassName);
                    } else {
                        field.setLiteralInitializer(null);
                    }
                }
                updateAccessors = true;
            }

            updateAnnotations(field,
                              property.getAnnotations(),
                              classTypeResolver);

            if (updateAccessors) {

                String accessorName;
                String methodSource;
                String oldClassName;

                //remove old accessors
                //TODO check primitive types
                Class<?> oldClass = classTypeResolver.resolveType(oldType.getName());
                oldClassName = oldClass.getName();

                accessorName = genTools.toJavaGetter(fieldName,
                                                     oldClassName);
                removeMethodByParamsClass(javaClassSource,
                                          accessorName);

                accessorName = genTools.toJavaSetter(fieldName);
                removeMethodByParamsClass(javaClassSource,
                                          accessorName,
                                          oldClass);

                //and generate the new ones
                methodSource = genTools.indent(engine.generateFieldGetterString(context,
                                                                                property));
                javaClassSource.addMethod(methodSource);

                methodSource = genTools.indent(engine.generateFieldSetterString(context,
                                                                                property));
                javaClassSource.addMethod(methodSource);
            }
        }
    }

    private void updateCollectionField(JavaClassSource javaClassSource,
                                       String fieldName,
                                       ObjectProperty property,
                                       ClassTypeResolver classTypeResolver) throws Exception {

        GenerationTools genTools = new GenerationTools();
        GenerationEngine engine = GenerationEngine.getInstance();
        GenerationContext context = new GenerationContext(null);
        boolean updateAccessors = true;
        FieldSource<JavaClassSource> currentField;

        currentField = javaClassSource.getField(fieldName);
        Type currentType = currentField.getType();

        StringBuilder fieldSource = new StringBuilder();

        fieldSource.append(engine.generateCompleteFieldString(context,
                                                              property));

        javaClassSource.removeField(currentField);
        javaClassSource.addField(fieldSource.toString());

        if (updateAccessors) {

            String accessorName;
            String methodSource;
            String oldClassName;

            //remove old accessors
            //TODO check primitive types
            Class<?> oldClass = classTypeResolver.resolveType(currentType.getName());
            oldClassName = oldClass.getName();

            accessorName = genTools.toJavaGetter(fieldName,
                                                 oldClassName);
            removeMethodByParamsClass(javaClassSource,
                                      accessorName);

            accessorName = genTools.toJavaSetter(fieldName);
            removeMethodByParamsClass(javaClassSource,
                                      accessorName,
                                      oldClass);

            //and generate the new ones
            methodSource = genTools.indent(engine.generateFieldGetterString(context,
                                                                            property));
            javaClassSource.addMethod(methodSource);

            methodSource = genTools.indent(engine.generateFieldSetterString(context,
                                                                            property));
            javaClassSource.addMethod(methodSource);
        }
    }

    private boolean hasChangedToCollectionType(FieldSource<JavaClassSource> field,
                                               ObjectProperty property,
                                               ClassTypeResolver classTypeResolver) throws Exception {

        return DriverUtils.isManagedType(field.getType(),
                                         classTypeResolver)
                && !DriverUtils.equalsType(field.getType(),
                                           property.getClassName(),
                                           property.isMultiple(),
                                           property.getBag(),
                                           classTypeResolver) &&
                property.isMultiple();
    }

    public void updateConstructors(JavaClassSource javaClassSource,
                                   DataObject dataObject,
                                   List<MethodSource<JavaClassSource>> allFieldsConstructorCandidates,
                                   List<MethodSource<JavaClassSource>> keyFieldsConstructorCandidates,
                                   List<MethodSource<JavaClassSource>> positionFieldsConstructorCandidates,
                                   ClassTypeResolver classTypeResolver) throws Exception {

        GenerationContext generationContext = new GenerationContext(null);
        GenerationEngine engine = GenerationEngine.getInstance();
        GenerationTools genTools = new GenerationTools();
        JavaRoasterModelDriver modelDriver = new JavaRoasterModelDriver();

        boolean needsAllFieldsConstructor;
        boolean needsKeyFieldsConstructor;
        boolean needsPositionFieldsConstructor;
        boolean needsEmptyConstructor;

        String defaultConstructorSource;
        String allFieldsConstructorSource;
        String keyFieldsConstructorSource;
        String positionFieldsConstructorSource;
        String equalsMethodSource;
        String hashCodeMethodSource;

        //check if the candidate methods has exactly the same body of the generated by the data modeller.
        List<MethodSource<JavaClassSource>> currentAllFieldsConstructors = modelDriver.filterGeneratedConstructors(allFieldsConstructorCandidates);
        List<MethodSource<JavaClassSource>> currentKeyFieldsConstructors = modelDriver.filterGeneratedConstructors(keyFieldsConstructorCandidates);
        List<MethodSource<JavaClassSource>> currentPositionFieldsConstructors = modelDriver.filterGeneratedConstructors(positionFieldsConstructorCandidates);

        if (logger.isDebugEnabled()) {
            logger.debug("allFieldsConstructorCandidates candidates: " + allFieldsConstructorCandidates.size());
            logger.debug(allFieldsConstructorCandidates.size() > 0 ? allFieldsConstructorCandidates.get(0).toString() : "");
            logger.debug("\n\n");

            logger.debug("currentAllFieldsConstructors: " + currentAllFieldsConstructors.size());
            logger.debug(currentAllFieldsConstructors.size() > 0 ? currentAllFieldsConstructors.get(0).toString() : "");
            logger.debug("\n\n");

            logger.debug("KeyFieldsConstructorCandidates: " + keyFieldsConstructorCandidates.size());
            logger.debug(keyFieldsConstructorCandidates.size() > 0 ? keyFieldsConstructorCandidates.get(0).toString() : "");
            logger.debug("\n\n");

            logger.debug("currentKeyFieldsConstructors: " + currentKeyFieldsConstructors.size());
            logger.debug(currentKeyFieldsConstructors.size() > 0 ? currentKeyFieldsConstructors.get(0).toString() : "");
            logger.debug("\n\n");

            logger.debug("positionFieldsConstructorCandidates: " + positionFieldsConstructorCandidates.size());
            logger.debug(positionFieldsConstructorCandidates.size() > 0 ? positionFieldsConstructorCandidates.get(0).toString() : "");
            logger.debug("\n\n");

            logger.debug("currentPositionFieldsConstructors: " + currentPositionFieldsConstructors.size());
            logger.debug(currentPositionFieldsConstructors.size() > 0 ? currentPositionFieldsConstructors.get(0).toString() : "");
            logger.debug("\n\n");
        }

        //delete current data modeller generated all fields, key fields, and position fields constructors if there are any.
        for (MethodSource<JavaClassSource> constructor : currentAllFieldsConstructors) {
            javaClassSource.removeMethod(constructor);
        }
        for (MethodSource<JavaClassSource> constructor : currentKeyFieldsConstructors) {
            javaClassSource.removeMethod(constructor);
        }
        for (MethodSource<JavaClassSource> constructor : currentPositionFieldsConstructors) {
            javaClassSource.removeMethod(constructor);
        }

        //calculate the file order for the fields.
        List<FieldSource<JavaClassSource>> fields = javaClassSource.getFields();
        if (fields != null && fields.size() > 0) {
            int fileOrder = 0;
            for (FieldSource<JavaClassSource> field : fields) {
                ObjectPropertyImpl objectProperty = (ObjectPropertyImpl) dataObject.getProperty(field.getName());
                if (objectProperty != null) {
                    objectProperty.setFileOrder(fileOrder);
                }
                fileOrder++;
            }
        }

        //get the sorted list of all fields, position annotated and key annotated fields. These lists will be used
        //to identify collisions with client provided constructors.
        List<ObjectProperty> allFields = DataModelUtils.sortByFileOrder(DataModelUtils.filterAssignableFields(dataObject));
        List<ObjectProperty> positionFields = DataModelUtils.sortByPosition(DataModelUtils.filterPositionFields(dataObject));
        List<ObjectProperty> keyFields = DataModelUtils.sortByFileOrder(DataModelUtils.filterKeyFields(dataObject));

        needsEmptyConstructor = true; //we always wants to generate the default constructor.
        needsAllFieldsConstructor = allFields.size() > 0;
        needsPositionFieldsConstructor = positionFields.size() > 0 &&
                !DataModelUtils.equalsByFieldName(allFields,
                                                  positionFields) &&
                !DataModelUtils.equalsByFieldType(allFields,
                                                  positionFields);

        needsKeyFieldsConstructor = keyFields.size() > 0 &&
                !DataModelUtils.equalsByFieldName(allFields,
                                                  keyFields) &&
                !DataModelUtils.equalsByFieldType(allFields,
                                                  keyFields) &&
                !DataModelUtils.equalsByFieldName(positionFields,
                                                  keyFields) &&
                !DataModelUtils.equalsByFieldType(positionFields,
                                                  keyFields);

        List<MethodSource<JavaClassSource>> currentConstructors = new ArrayList<MethodSource<JavaClassSource>>();
        MethodSource<JavaClassSource> currentEquals = null;
        MethodSource<JavaClassSource> currentHashCode = null;
        MethodSource<JavaClassSource> newConstructor;

        //Iterate remaining methods looking for client provided constructors, hashCode and equals methods.
        List<MethodSource<JavaClassSource>> methods = javaClassSource.getMethods();
        if (methods != null) {
            for (MethodSource<JavaClassSource> method : methods) {
                if (method.isConstructor()) {
                    currentConstructors.add(method);
                    if (method.getParameters() == null || method.getParameters().size() == 0) {
                        needsEmptyConstructor = false;
                    }
                } else if (isEquals(method)) {
                    currentEquals = method;
                } else if (isHashCode(method)) {
                    currentHashCode = method;
                }
            }
        }

        //check collisions with remaining constructors first.
        needsAllFieldsConstructor = needsAllFieldsConstructor && (findMatchingConstructorsByTypes(javaClassSource,
                                                                                                  allFields,
                                                                                                  classTypeResolver).size() == 0);
        needsPositionFieldsConstructor = needsPositionFieldsConstructor && (findMatchingConstructorsByTypes(javaClassSource,
                                                                                                            positionFields,
                                                                                                            classTypeResolver).size() == 0);
        needsKeyFieldsConstructor = needsKeyFieldsConstructor && (findMatchingConstructorsByTypes(javaClassSource,
                                                                                                  keyFields,
                                                                                                  classTypeResolver).size() == 0);

        //remove current equals and hashCode methods
        if (currentEquals != null) {
            javaClassSource.removeMethod(currentEquals);
        }
        if (currentHashCode != null) {
            javaClassSource.removeMethod(currentHashCode);
        }

        //finally create the needed constructors

        if (needsEmptyConstructor) {
            defaultConstructorSource = genTools.indent(engine.generateDefaultConstructorString(generationContext,
                                                                                               dataObject));
            newConstructor = javaClassSource.addMethod(defaultConstructorSource);
            newConstructor.setConstructor(true);
        }

        if (needsAllFieldsConstructor) {
            allFieldsConstructorSource = genTools.indent(engine.generateAllFieldsConstructorString(generationContext,
                                                                                                   dataObject));
            if (allFieldsConstructorSource != null && !allFieldsConstructorSource.trim().isEmpty()) {
                newConstructor = javaClassSource.addMethod(allFieldsConstructorSource);
                newConstructor.setConstructor(true);
            }
        }

        if (needsPositionFieldsConstructor) {
            positionFieldsConstructorSource = genTools.indent(engine.generatePositionFieldsConstructorString(generationContext,
                                                                                                             dataObject));
            if (positionFieldsConstructorSource != null && !positionFieldsConstructorSource.trim().isEmpty()) {
                newConstructor = javaClassSource.addMethod(positionFieldsConstructorSource);
                newConstructor.setConstructor(true);
            }
        }
        if (needsKeyFieldsConstructor) {
            keyFieldsConstructorSource = genTools.indent(engine.generateKeyFieldsConstructorString(generationContext,
                                                                                                   dataObject));
            if (keyFieldsConstructorSource != null && !keyFieldsConstructorSource.trim().isEmpty()) {
                newConstructor = javaClassSource.addMethod(keyFieldsConstructorSource);
                newConstructor.setConstructor(true);
            }
        }
        if (keyFields.size() > 0) {
            equalsMethodSource = genTools.indent(engine.generateEqualsString(generationContext,
                                                                             dataObject));
            javaClassSource.addMethod(equalsMethodSource);

            hashCodeMethodSource = genTools.indent(engine.generateHashCodeString(generationContext,
                                                                                 dataObject));
            javaClassSource.addMethod(hashCodeMethodSource);
        }
    }

    /**
     * Takes care of field and the corresponding setter/getter removal.
     */
    public void removeField(JavaClassSource javaClassSource,
                            String fieldName,
                            ClassTypeResolver classTypeResolver) throws Exception {
        logger.debug("Removing field: " + fieldName + ", from class: " + javaClassSource.getName());

        FieldSource<JavaClassSource> field;
        GenerationTools genTools = new GenerationTools();
        String methodName;

        field = javaClassSource.getField(fieldName);
        if (field != null) {

            //check if the class has a setter/getter for the given field.
            Class<?> fieldClass = classTypeResolver.resolveType(field.getType().getName());
            methodName = genTools.toJavaGetter(fieldName,
                                               fieldClass.getName());
            removeMethodByParamsClass(javaClassSource,
                                      methodName);

            methodName = genTools.toJavaSetter(fieldName);
            removeMethodByParamsClass(javaClassSource,
                                      methodName,
                                      fieldClass);

            //finally remove the field.
            javaClassSource.removeField(field);
        } else {
            logger.debug("Field field: " + fieldName + " was not found in class: " + javaClassSource.getName());
        }
    }

    public void removeMethodByParamsClass(JavaClassSource javaClassSource,
                                          String methodName,
                                          Class<?>... paramTypes) {
        logger.debug("Removing method: " + methodName + ", form class: " + javaClassSource.getName());
        MethodSource<JavaClassSource> method = javaClassSource.getMethod(methodName,
                                                                         paramTypes);
        if (method != null) {
            javaClassSource.removeMethod(method);
            logger.debug("Method method: " + methodName + ", was removed from class: " + javaClassSource.getName());
        } else {
            logger.debug("Method method: " + methodName + " not exists for class: " + javaClassSource.getName());
        }
    }

    public void removeMethodByParamsClassName(JavaClassSource javaClassSource,
                                              String methodName,
                                              String... paramTypes) {
        logger.debug("Removing method: " + methodName + ", form class: " + javaClassSource.getName());
        MethodSource<JavaClassSource> method = javaClassSource.getMethod(methodName,
                                                                         paramTypes);
        if (method != null) {
            javaClassSource.removeMethod(method);
            logger.debug("Method method: " + methodName + ", was removed from class: " + javaClassSource.getName());
        } else {
            logger.debug("Method method: " + methodName + " not exists for class: " + javaClassSource.getName());
        }
    }

    public List<MethodSource<JavaClassSource>> findAllFieldsConstructorCandidates(JavaClassSource javaClassSource,
                                                                                  List<ObjectProperty> properties,
                                                                                  ClassTypeResolver classTypeResolver) {
        return findMatchingConstructorsByParameters(javaClassSource,
                                                    properties,
                                                    classTypeResolver);
    }

    public List<MethodSource<JavaClassSource>> findKeyFieldsConstructorCandidates(JavaClassSource javaClassSource,
                                                                                  List<ObjectProperty> properties,
                                                                                  ClassTypeResolver classTypeResolver) {
        List<ObjectProperty> keyFields = DataModelUtils.filterKeyFields(properties);
        return findMatchingConstructorsByParameters(javaClassSource,
                                                    keyFields,
                                                    classTypeResolver);
    }

    public List<MethodSource<JavaClassSource>> findPositionFieldsConstructorCandidates(JavaClassSource javaClassSource,
                                                                                       List<ObjectProperty> properties,
                                                                                       ClassTypeResolver classTypeResolver) {
        List<ObjectProperty> positionalFields = DataModelUtils.filterPositionFields(properties);
        return findMatchingConstructorsByParameters(javaClassSource,
                                                    DataModelUtils.sortByPosition(positionalFields),
                                                    classTypeResolver);
    }

    public List<MethodSource<JavaClassSource>> findMatchingConstructorsByParameters(JavaClassSource javaClassSource,
                                                                                    List<ObjectProperty> properties,
                                                                                    ClassTypeResolver classTypeResolver) {
        List<MethodSource<JavaClassSource>> result = new ArrayList<MethodSource<JavaClassSource>>();
        List<MethodSource<JavaClassSource>> constructors = getConstructors(javaClassSource);
        for (MethodSource<JavaClassSource> constructor : constructors) {
            List<ParameterSource<JavaClassSource>> parameters = constructor.getParameters();
            if (parameters == null || parameters.size() == 0 || parameters.size() != properties.size()) {
                continue;
            }
            int unmatchedParams = parameters.size();
            int paramIndex = 0;
            for (ParameterSource<JavaClassSource> param : parameters) {
                if (paramMatchesWithProperty(param,
                                             properties.get(paramIndex),
                                             classTypeResolver)) {
                    unmatchedParams--;
                    //TODO optimize to not visit all parameters, now I want to visit them all by intention
                }
                paramIndex++;
            }
            if (unmatchedParams == 0) {
                result.add(constructor);
            }
        }
        return result;
    }

    public List<MethodSource<JavaClassSource>> findMatchingConstructorsByTypes(JavaClassSource javaClassSource,
                                                                               List<ObjectProperty> properties,
                                                                               ClassTypeResolver classTypeResolver) {
        List<MethodSource<JavaClassSource>> result = new ArrayList<MethodSource<JavaClassSource>>();
        List<MethodSource<JavaClassSource>> constructors = getConstructors(javaClassSource);
        for (MethodSource<JavaClassSource> constructor : constructors) {
            List<ParameterSource<JavaClassSource>> parameters = constructor.getParameters();
            if (parameters == null || parameters.size() == 0 || parameters.size() != properties.size()) {
                continue;
            }
            int unmatchedParams = parameters.size();
            int paramIndex = 0;
            for (ParameterSource<JavaClassSource> param : parameters) {
                if (paramMatchesWithPropertyType(param,
                                                 properties.get(paramIndex),
                                                 classTypeResolver)) {
                    unmatchedParams--;
                } else {
                    break;
                }
                paramIndex++;
            }
            if (unmatchedParams == 0) {
                result.add(constructor);
            }
        }
        return result;
    }

    public boolean paramMatchesWithProperty(ParameterSource<JavaClassSource> param,
                                            ObjectProperty property,
                                            ClassTypeResolver classTypeResolver) {
        if (!param.getName().equals(property.getName())) {
            return false;
        }
        try {
            return DriverUtils.equalsType(param.getType(),
                                          property.getClassName(),
                                          property.isMultiple(),
                                          property.getBag(),
                                          classTypeResolver);
        } catch (Exception e) {
            //TODO check if we need to propagate this exception.
            logger.error("An error was produced on parameter matching test with param: " + param.getName() + " and field: " + property.getName(),
                         e);
            return false;
        }
    }

    public boolean paramMatchesWithPropertyType(ParameterSource<JavaClassSource> param,
                                                ObjectProperty property,
                                                ClassTypeResolver classTypeResolver) {
        try {
            return DriverUtils.equalsType(param.getType(),
                                          property.getClassName(),
                                          property.isMultiple(),
                                          property.getBag(),
                                          classTypeResolver);
        } catch (Exception e) {
            //TODO check if we need to propagate this exception.
            logger.error("An error was produced on parameter matching test with param: " + param.getName() + " and field: " + property.getName(),
                         e);
            return false;
        }
    }

    public List<MethodSource<JavaClassSource>> filterGeneratedConstructors(List<MethodSource<JavaClassSource>> constructors) {
        List<MethodSource<JavaClassSource>> result = new ArrayList<MethodSource<JavaClassSource>>();
        if (constructors != null) {
            for (MethodSource<JavaClassSource> constructor : constructors) {
                if (isGeneratedConstructor(constructor)) {
                    result.add(constructor);
                }
            }
        }
        return result;
    }

    /**
     * @param constructor a Constructor method to check.
     * @return true, if the given constructor was generated by the data modeler.
     */
    public boolean isGeneratedConstructor(MethodSource<JavaClassSource> constructor) {
        if (constructor.isAbstract() || constructor.isStatic() || constructor.isFinal()) {
            return false;
        }
        if (!constructor.isPublic()) {
            return false; //we only generate public constructors.
        }

        if (constructor.getAnnotations() != null && constructor.getAnnotations().size() > 0) {
            return false; //we never add annotations to constructors
        }

        List<ParameterSource<JavaClassSource>> parameters = constructor.getParameters();
        List<String> expectedLines = new ArrayList<String>();
        String expectedLine;
        if (parameters != null) {
            for (ParameterSource<JavaClassSource> param : parameters) {
                if (param.getAnnotations() != null && param.getAnnotations().size() > 0) {
                    return false; //we never add annotations to parameters
                }
                //ideally we should know if the parameter is final, but Roaster don't provide that info.
                expectedLine = "this." + param.getName() + "=" + param.getName() + ";";
                expectedLines.add(expectedLine);
            }
        }

        String body = constructor.getBody();
        if (body == null || (body = body.trim()).isEmpty()) {
            return false;
        }

        try {
            BufferedReader reader = new BufferedReader(new StringReader(body));
            String line = null;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber > expectedLines.size()) {
                    return false;
                }
                if (!line.trim().equals(expectedLines.get(lineNumber - 1))) {
                    return false;
                }
            }

            return lineNumber == expectedLines.size();
        } catch (IOException e) {
            return false;
        }
    }

    public List<MethodSource<JavaClassSource>> getConstructors(JavaClassSource javaClassSource) {
        List<MethodSource<JavaClassSource>> constructors = new ArrayList<MethodSource<JavaClassSource>>();
        List<MethodSource<JavaClassSource>> methods = javaClassSource.getMethods();
        if (methods != null) {
            for (MethodSource<JavaClassSource> method : methods) {
                if (method.isConstructor()) {
                    constructors.add(method);
                }
            }
        }
        return constructors;
    }

    public boolean isManagedField(FieldSource<JavaClassSource> field,
                                  ClassTypeResolver classTypeResolver) throws Exception {

        if (!field.isFinal() && !field.isStatic()) {
            //finally we can check if the field type is a managed type.
            //if not, the field should remain untouched
            return DriverUtils.isManagedType(field.getType(),
                                             classTypeResolver);
        }
        return false;
    }

    public boolean isEquals(MethodSource<?> method) {
        return method.getName().equals("equals") &&
                (method.getParameters() == null || method.getParameters().size() == 1) &&
                method.getReturnType() != null &&
                method.getReturnType().isPrimitive() &&
                "boolean".equals(method.getReturnType().getName());
    }

    public boolean isHashCode(MethodSource<?> method) {
        return method.getName().equals("hashCode") &&
                (method.getParameters() == null || method.getParameters().size() == 0) &&
                method.getReturnType() != null &&
                method.getReturnType().isPrimitive() &&
                "int".equals(method.getReturnType().getName());
    }

    public void setPrimitiveTypeDefaultInitializer(FieldSource<?> field,
                                                   String primitiveType) {

        if (BYTE.equals(primitiveType)) {
            field.setLiteralInitializer("0");
        }
        if (SHORT.equals(primitiveType)) {
            field.setLiteralInitializer("0");
        }
        if (INT.equals(primitiveType)) {
            field.setLiteralInitializer("0");
        }
        if (LONG.equals(primitiveType)) {
            field.setLiteralInitializer("0L");
        }
        if (FLOAT.equals(primitiveType)) {
            field.setLiteralInitializer("0.0f");
        }
        if (DOUBLE.equals(primitiveType)) {
            field.setLiteralInitializer("0.0d");
        }
        if (CHAR.equals(primitiveType)) {
            field.setLiteralInitializer("\'\\u0000\'");
        }
        if (BOOLEAN.equals(primitiveType)) {
            field.setLiteralInitializer("false");
        }
    }

    public Pair<Annotation, List<DriverError>> parseAnnotationWithValuePair(String annotationClassName,
                                                                            ElementType target,
                                                                            String valuePairName,
                                                                            String literalValue,
                                                                            ClassLoader classLoader) {

        List<DriverError> driverErrors = new ArrayList<DriverError>();
        Annotation annotation = null;

        Pair<AnnotationSource<JavaClassSource>, List<DriverError>> parseResult = parseAnnotationWithValuePair(
                annotationClassName,
                target,
                valuePairName,
                literalValue);

        driverErrors.addAll(parseResult.getK2());
        if (driverErrors.size() == 0) {
            //TODO review this, we should use DriverUtils.createClassTypeResolver( javaClassSource, classLoader ); instead

            ClassTypeResolver classTypeResolver = new ClassTypeResolver(Collections.emptySet(),
                                                                        classLoader);

            try {
                annotation = createAnnotation(parseResult.getK1(),
                                              classTypeResolver);
            } catch (Exception e) {
                driverErrors.add(new DriverError(e.getMessage()));
            }
        }
        return new Pair<>(annotation,
                          driverErrors);
    }

    public Pair<AnnotationSource<JavaClassSource>, List<DriverError>> parseAnnotationWithValuePair(String annotationClassName,
                                                                                                   ElementType target,
                                                                                                   String valuePairName,
                                                                                                   String literalValue) {

        List<DriverError> syntaxErrors = new ArrayList<DriverError>();

        String annotationStr = "@" + annotationClassName + "(" + valuePairName + "=" + literalValue + " )";
        String stub;
        AnnotationSource<JavaClassSource> annotation = null;
        if (ElementType.TYPE.equals(target)) {
            stub = annotationStr + " public class Stub { }";
        } else {
            stub = "public class Stub { " + annotationStr + " int dummy; }";
        }
        JavaClassSource temp = (JavaClassSource) Roaster.parse(JavaClass.class,
                                                               stub);
        if (temp.getSyntaxErrors() != null && temp.getSyntaxErrors().size() > 0) {
            for (org.jboss.forge.roaster.model.SyntaxError syntaxError : temp.getSyntaxErrors()) {
                syntaxErrors.add(new DriverError(syntaxError.getDescription(),
                                                 syntaxError.getLine(),
                                                 syntaxError.getColumn()));
            }
        } else if (ElementType.TYPE.equals(target)) {
            annotation = temp.getAnnotation(annotationClassName);
        } else {
            annotation = temp.getField("dummy").getAnnotation(annotationClassName);
        }

        if (annotation == null) {
            syntaxErrors.add(new DriverError("Annotation value pair could not be parsed."));
        }
        return new Pair<>(annotation,
                          syntaxErrors);
    }

    public boolean isManagedAnnotation(AnnotationSource<?> annotation,
                                       ClassTypeResolver classTypeResolver) throws Exception {
        String annotationClassName = resolveTypeName(classTypeResolver,
                                                     annotation.getName());
        return getConfiguredAnnotation(annotationClassName) != null;
    }

    private String errorMessage(String message,
                                Object... params) {
        return MessageFormat.format(message,
                                    params);
    }
}
