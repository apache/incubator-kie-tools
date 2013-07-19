package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import org.drools.core.util.asm.ClassFieldInspector;
import org.guvnor.common.services.project.builder.model.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.AnnotationUtils;
import org.kie.workbench.common.services.datamodel.model.Annotation;
import org.kie.workbench.common.services.datamodel.model.ClassToGenericClassConverter;
import org.kie.workbench.common.services.datamodel.model.FieldAccessorsAndMutators;
import org.kie.workbench.common.services.datamodel.model.MethodInfo;
import org.kie.workbench.common.services.datamodel.model.ModelField;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracleImpl;

/**
 * Builder for Fact Types originating from a .class
 */
public class ClassFactBuilder extends BaseFactBuilder {

    private final ClassToGenericClassConverter typeSystemConverter = new JavaTypeSystemTranslator();

    private final Map<String, List<MethodInfo>> methodInformation = new HashMap<String, List<MethodInfo>>();
    private final Map<String, String> fieldParametersType = new HashMap<String, String>();

    private final String superType;
    private final Set<Annotation> annotations = new LinkedHashSet<Annotation>();
    private final Map<String, Set<Annotation>> fieldAnnotations = new HashMap<String, Set<Annotation>>();

    public ClassFactBuilder( final ProjectDataModelOracleBuilder builder,
                             final Class<?> clazz,
                             final boolean isEvent,
                             final TypeSource typeSource ) throws IOException {
        super( builder,
               clazz,
               isEvent,
               typeSource );
        this.superType = getSuperType( clazz );
        this.annotations.addAll( getAnnotations( clazz ) );
        this.fieldAnnotations.putAll( getFieldsAnnotations( clazz ) );
        loadClassFields( clazz );
    }

    @Override
    public void build( final ProjectDataModelOracleImpl oracle ) {
        super.build( oracle );
        oracle.addMethodInformation( methodInformation );
        oracle.addFieldParametersType( fieldParametersType );
        oracle.addSuperTypes( buildSuperTypes() );
        oracle.addTypeAnnotations( buildTypeAnnotations() );
        oracle.addTypeFieldsAnnotations( buildTypeFieldsAnnotations() );
    }

    protected String getSuperType( final Class<?> clazz ) {
        final Class<?> superType = clazz.getSuperclass();
        return ( superType == null || Object.class.equals( superType ) ? null : superType.getName() );
    }

    protected Set<Annotation> getAnnotations( final Class<?> clazz ) {
        final Set<Annotation> dmoAnnotations = new LinkedHashSet<Annotation>();
        final java.lang.annotation.Annotation annotations[] = clazz.getAnnotations();
        for ( java.lang.annotation.Annotation a : annotations ) {
            final Annotation dmoa = new Annotation( a.annotationType().getName() );
            for ( Method m : a.annotationType().getDeclaredMethods() ) {
                final String methodName = m.getName();
                dmoa.addAttribute( methodName,
                                   AnnotationUtils.getAnnotationAttributeValue( a,
                                                                                methodName ) );
            }
            dmoAnnotations.add( dmoa );
        }
        return dmoAnnotations;
    }

    private Map<String, Set<Annotation>> getFieldsAnnotations( final Class<?> clazz ) {
        final Field[] fields = clazz.getDeclaredFields();
        final Map<String, Set<Annotation>> fieldsAnnotations = new HashMap<String, Set<Annotation>>();
        for ( Field field : fields ) {
            final String fieldName = field.getName();
            final Set<Annotation> fieldAnnotations = getFieldAnnotations( field );
            if ( fieldAnnotations.size() > 0 ) {
                fieldsAnnotations.put( fieldName,
                                       fieldAnnotations );
            }
        }
        return fieldsAnnotations;
    }

    private Set<Annotation> getFieldAnnotations( final Field field ) {
        final java.lang.annotation.Annotation[] annotations = field.getDeclaredAnnotations();
        final Set<Annotation> fieldAnnotations = new LinkedHashSet<Annotation>();
        for ( java.lang.annotation.Annotation a : annotations ) {
            final Annotation fieldAnnotation = new Annotation( a.annotationType().getName() );
            for ( Method m : a.annotationType().getDeclaredMethods() ) {
                final String methodName = m.getName();
                fieldAnnotation.addAttribute( methodName,
                                              AnnotationUtils.getAnnotationAttributeValue( a,
                                                                                           methodName ) );
            }
            fieldAnnotations.add( fieldAnnotation );
        }
        return fieldAnnotations;
    }

    private void loadClassFields( final Class<?> clazz ) throws IOException {
        if ( clazz == null ) {
            return;
        }

        final String factType = getType( clazz );

        //Get all getters and setters for the class. This does not handle delegated properties
        final ClassFieldInspector inspector = new ClassFieldInspector( clazz );
        final Set<String> fieldNamesSet = new TreeSet<String>( inspector.getFieldNames().keySet() );
        final List<String> fieldNames = removeIrrelevantFields( fieldNamesSet );

        //Consolidate methods into those with getters or setters
        final Method[] methods = clazz.getMethods();
        final Map<String, MethodSignature> methodSignatures = removeIrrelevantMethods( getMethodSignatures( factType,
                                                                                                            methods ) );

        //Add Fields from ClassFieldInspector which provides a list of "reasonable" methods
        for ( final String fieldName : fieldNames ) {
            final String qualifiedName = factType + "." + fieldName;
            final Field f = inspector.getFieldTypesField().get( fieldName );
            if ( f == null ) {

                //If a Field cannot be found is is really a delegated property so use the Method return type
                if ( methodSignatures.containsKey( qualifiedName ) ) {
                    final MethodSignature m = methodSignatures.get( qualifiedName );
                    addParametricTypeForField( factType,
                                               fieldName,
                                               m.genericType );

                    final Class<?> returnType = m.returnType;
                    final String genericReturnType = typeSystemConverter.translateClassToGenericType( returnType );

                    addField( new ModelField( fieldName,
                                              returnType.getName(),
                                              ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                              ModelField.FIELD_ORIGIN.DELEGATED,
                                              methodSignatures.get( qualifiedName ).accessorAndMutator,
                                              genericReturnType ) );

                    addEnumsForField( factType,
                                      fieldName,
                                      returnType );

                }
            } else {

                //Otherwise we can use the results of ClassFieldInspector
                final Field field = inspector.getFieldTypesField().get( fieldName );
                addParametricTypeForField( factType,
                                           fieldName,
                                           field.getGenericType() );

                Field[] _declaredClassFields = clazz.getDeclaredFields();
                Collection declaredClassFields = _declaredClassFields != null ? Arrays.asList(_declaredClassFields) : Collections.EMPTY_LIST;

                final Class<?> returnType = field.getType();
                final String genericReturnType = typeSystemConverter.translateClassToGenericType( returnType );

                addField( new ModelField( fieldName,
                                          returnType.getName(),
                                          ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                          declaredClassFields.contains(field) ? ModelField.FIELD_ORIGIN.DECLARED : ModelField.FIELD_ORIGIN.INHERITED,
                                          methodSignatures.get( qualifiedName ).accessorAndMutator,
                                          genericReturnType ) );

                addEnumsForField( factType,
                                  fieldName,
                                  returnType );
            }

        }

        //Methods for use in ActionCallMethod's
        ClassMethodInspector methodInspector = new ClassMethodInspector( clazz,
                                                                         typeSystemConverter );

        final List<MethodInfo> methodInformation = methodInspector.getMethodInfos();
        for ( final MethodInfo mi : methodInformation ) {
            final String genericType = mi.getParametricReturnType();
            if ( genericType != null ) {
                final String qualifiedFactFieldName = factType + "#" + mi.getNameWithParameters();
                this.fieldParametersType.put( qualifiedFactFieldName,
                                              genericType );
            }
        }
        this.methodInformation.put( factType,
                                    methodInformation );
    }

    // Remove the unneeded "fields" that come from java.lang.Object
    private List<String> removeIrrelevantFields( final Collection<String> fields ) {
        final List<String> result = new ArrayList<String>();
        for ( String field : fields ) {
            //clone, empty, iterator, listIterator, size, toArray
            if ( !( field.equals( "class" ) || field.equals( "hashCode" ) || field.equals( "toString" ) ) ) {
                result.add( field );
            }
        }
        return result;
    }

    // Remove the unneeded "methods" that come from java.lang.Object
    private Map<String, MethodSignature> removeIrrelevantMethods( final Map<String, MethodSignature> methods ) {
        final Map<String, MethodSignature> result = new HashMap<String, MethodSignature>();
        for ( Map.Entry<String, MethodSignature> methodSignature : methods.entrySet() ) {
            String methodName = methodSignature.getKey();
            methodName = methodName.substring( methodName.lastIndexOf( "." ) + 1 );
            if ( !methodName.equals( "class" ) ) {
                result.put( methodSignature.getKey(),
                            methodSignature.getValue() );
            }
        }
        return result;
    }

    private Map<String, MethodSignature> getMethodSignatures( final String factType,
                                                              final Method[] methods ) {

        Map<String, MethodSignature> methodSignatures = new HashMap<String, MethodSignature>();

        //Determine accessors for methods
        for ( Method method : methods ) {
            String name = method.getName();
            if ( method.getParameterTypes().length > 0 ) {

                //Strip bare mutator name
                if ( name.startsWith( "set" ) ) {
                    name = Introspector.decapitalize( name.substring( 3 ) );
                } else {
                    name = Introspector.decapitalize( name );
                }

                final String factField = factType + "." + name;
                if ( !methodSignatures.containsKey( factField ) ) {
                    methodSignatures.put( factField,
                                          new MethodSignature( FieldAccessorsAndMutators.MUTATOR,
                                                               void.class.getGenericSuperclass(),
                                                               void.class ) );
                } else if ( methodSignatures.get( factField ).accessorAndMutator == FieldAccessorsAndMutators.ACCESSOR ) {
                    MethodSignature signature = methodSignatures.get( factField );
                    signature.accessorAndMutator = FieldAccessorsAndMutators.BOTH;
                }

            } else if ( !method.getReturnType().getName().equals( "void" ) ) {

                //Strip bare accessor name
                if ( name.startsWith( "get" ) ) {
                    name = Introspector.decapitalize( name.substring( 3 ) );
                } else if ( name.startsWith( "is" ) ) {
                    name = Introspector.decapitalize( name.substring( 2 ) );
                } else {
                    name = Introspector.decapitalize( name );
                }

                final String factField = factType + "." + name;
                if ( !methodSignatures.containsKey( factField ) ) {
                    methodSignatures.put( factField,
                                          new MethodSignature( FieldAccessorsAndMutators.ACCESSOR,
                                                               method.getGenericReturnType(),
                                                               method.getReturnType() ) );
                } else if ( methodSignatures.get( factField ).accessorAndMutator == FieldAccessorsAndMutators.MUTATOR ) {
                    MethodSignature signature = methodSignatures.get( factField );
                    signature.accessorAndMutator = FieldAccessorsAndMutators.BOTH;
                    signature.genericType = method.getGenericReturnType();
                    signature.returnType = method.getReturnType();
                }
            }
        }
        return methodSignatures;
    }

    private static class MethodSignature {

        private MethodSignature( final FieldAccessorsAndMutators accessorAndMutator,
                                 final Type genericType,
                                 final Class<?> returnType ) {
            this.accessorAndMutator = accessorAndMutator;
            this.genericType = genericType;
            this.returnType = returnType;
        }

        private FieldAccessorsAndMutators accessorAndMutator;
        private Type genericType;
        private Class<?> returnType;

    }

    private void addEnumsForField( final String className,
                                   final String fieldName,
                                   final Class<?> fieldClazz ) {
        if ( fieldClazz.isEnum() ) {
            final Field[] enumFields = fieldClazz.getDeclaredFields();
            final List<String> enumValues = new ArrayList<String>();
            for ( final Field enumField : enumFields ) {
                if ( enumField.isEnumConstant() ) {
                    final String shortName = fieldClazz.getName().substring( fieldClazz.getName().lastIndexOf( "." ) + 1 ) + "." + enumField.getName();
                    enumValues.add( shortName + "=" + shortName );
                }
            }
            final String a[] = new String[ enumValues.size() ];
            enumValues.toArray( a );
            getDataModelBuilder().addEnum( className,
                                           fieldName,
                                           a );
        }
    }

    private void addParametricTypeForField( final String className,
                                            final String fieldName,
                                            final Type type ) {
        final String qualifiedFactFieldName = className + "#" + fieldName;
        final String parametricType = getParametricType( type );
        if ( parametricType != null ) {
            fieldParametersType.put( qualifiedFactFieldName,
                                     parametricType );
        }
    }

    private String getParametricType( final Type type ) {
        if ( type instanceof ParameterizedType ) {
            final ParameterizedType pt = (ParameterizedType) type;
            Type parameter = null;
            for ( final Type t : pt.getActualTypeArguments() ) {
                parameter = t;
            }
            if ( parameter != null ) {
                if ( parameter instanceof Class<?> ) {
                    return ( (Class<?>) parameter ).getName();
                }
                return null;
            } else {
                return null;
            }
        }
        return null;
    }

    private Map<String, String> buildSuperTypes() {
        final Map<String, String> loadableSuperTypes = new HashMap<String, String>();
        loadableSuperTypes.put( getType(),
                                superType );
        return loadableSuperTypes;
    }

    private Map<String, Set<Annotation>> buildTypeAnnotations() {
        final Map<String, Set<Annotation>> loadableTypeAnnotations = new HashMap<String, Set<Annotation>>();
        loadableTypeAnnotations.put( getType(),
                                     annotations );
        return loadableTypeAnnotations;
    }

    private Map<String, Map<String, Set<Annotation>>> buildTypeFieldsAnnotations() {
        final Map<String, Map<String, Set<Annotation>>> loadableTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();
        loadableTypeFieldsAnnotations.put( getType(),
                                           fieldAnnotations );
        return loadableTypeFieldsAnnotations;
    }

}
