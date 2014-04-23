package org.kie.workbench.common.services.datamodeller.util;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.core.base.ClassTypeResolver;
import org.jboss.forge.roaster.model.Abstractable;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.Member;
import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.VisibilityScoped;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.Import;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.parser.descr.ClassOrInterfaceTypeDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.IdentifierWithTypeArgumentsDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ImportDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ModifierDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeArgumentDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeArgumentListDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeDescr;

public class DriverUtils {


    protected DriverUtils() {

    }

    public static DriverUtils getInstance() {
        return new DriverUtils();
    }

    public ClassTypeResolver createClassTypeResolver(FileDescr fileDescr, ClassLoader classLoader) {

        String packageName;
        Set<String> classImports = new HashSet<String>( );

        List<ImportDescr> fileImports = fileDescr.getImports();
        if (fileImports != null) {
            for (ImportDescr importDescr : fileDescr.getImports()) {
                classImports.add( importDescr.getName( true ) );
            }
        }

        if (fileDescr.getPackageDescr() != null) {
            packageName = fileDescr.getPackageDescr().getPackageName();
        } else {
            packageName = null;
        }
        //add current package too, if not added, the class type resolver don't resolve current package classes.
        if (packageName != null && !"".equals( packageName )) classImports.add( packageName + ".*" );

        return new ClassTypeResolver( classImports, classLoader );
    }

    public ClassTypeResolver createClassTypeResolver(JavaClassSource javaClassSource, ClassLoader classLoader) {

        String packageName;
        Set<String> classImports = new HashSet<String>( );

        List<Import> imports = javaClassSource.getImports();
        if (imports != null) {
            for (Import currentImport : imports) {
                String importName = currentImport.getQualifiedName();
                //TODO, check static imports
                if (currentImport.isWildcard()) {
                    importName = importName + ".*";
                }
                classImports.add( importName );
            }
        }

        packageName = javaClassSource.getPackage();
        //add current package too, if not added, the class type resolver don't resolve current package classes.
        if (packageName != null && !"".equals( packageName )) classImports.add( packageName + ".*" );

        return new ClassTypeResolver( classImports, classLoader );
    }

    public boolean isPrimitiveType(TypeDescr typeDescr) {
        return typeDescr.isPrimitiveType();
    }

    public boolean isSimpleClass(TypeDescr typeDescr) {
        if (!typeDescr.isClassOrInterfaceType()) return false;

        ClassOrInterfaceTypeDescr classOrInterfaceTypeDescr = typeDescr.getClassOrInterfaceType();
        List<IdentifierWithTypeArgumentsDescr> identifierWithTypeArgumentsList = classOrInterfaceTypeDescr.getIdentifierWithTypeArguments();

        if (identifierWithTypeArgumentsList == null || identifierWithTypeArgumentsList.size() == 0) return false;

        for (IdentifierWithTypeArgumentsDescr identifierWithTypeArguments : identifierWithTypeArgumentsList) {
            if (identifierWithTypeArguments.getArguments() != null) return false;
        }
        return true;
    }

    public Object[] isSimpleGeneric(TypeDescr typeDescr) {
        Object[] result = new Object[3];
        result[0] = false;
        result[1] = null;
        result[2] = null;

        if (!typeDescr.isClassOrInterfaceType()) return result;

        ClassOrInterfaceTypeDescr classOrInterfaceTypeDescr = typeDescr.getClassOrInterfaceType();
        List<IdentifierWithTypeArgumentsDescr> identifierWithTypeArgumentsList = classOrInterfaceTypeDescr.getIdentifierWithTypeArguments();

        if (identifierWithTypeArgumentsList == null || identifierWithTypeArgumentsList.size() == 0) return result;

        int i = 0;
        StringBuilder outerClassName = new StringBuilder( );
        for (IdentifierWithTypeArgumentsDescr identifierWithTypeArguments : identifierWithTypeArgumentsList) {
            i++;
            if (i > 1) {
                outerClassName.append( "." );
            }
            outerClassName.append( identifierWithTypeArguments.getIdentifier().getIdentifier() );

            if (identifierWithTypeArguments.getArguments() != null) {
                if (identifierWithTypeArgumentsList.size() > i) return result;
                TypeArgumentListDescr typeArgumentList = identifierWithTypeArguments.getArguments();
                List<TypeArgumentDescr> typeArguments = typeArgumentList != null ? typeArgumentList.getArguments() : null;
                if (typeArguments == null || typeArguments.size() != 1) return result;

                TypeDescr type;
                if( (type = typeArguments.get( 0 ).getType()) != null && isSimpleClass( type )) {
                    result[0] = true;
                    result[1] = outerClassName.toString();
                    result[2] = type;
                    return result;
                }
            }
        }
        return result;
    }

    public Object[] isSimpleGeneric(Type type, ClassTypeResolver classTypeResolver) throws ModelDriverException {
        Object[] result = new Object[3];
        result[0] = false;
        result[1] = null;
        result[2] = null;

        if ( type.isArray() ||
                type.isPrimitive() ||
                !type.isParameterized() ||
                ( type.isParameterized() && type.getTypeArguments().size() != 1 ) ) {
            return result;
        }

        Type<?> argument =  (( List<Type> ) type.getTypeArguments() ).get( 0 );
        if (!isSimpleClass( argument )) return result;

        try {
            String outerClass = classTypeResolver.getFullTypeName( type.getName() );
            String argumentClass = classTypeResolver.getFullTypeName( type.getName() );

            result[0] = true;
            result[1] = outerClass;
            result[2] = argumentClass;
            return result;

        } catch (ClassNotFoundException e) {
            throw new ModelDriverException("Class could not be resolved for name: " + type.getName() + ". " + e.getMessage(), e );
        }
    }

    public boolean isArray(TypeDescr typeDescr) {
        return typeDescr.getDimensionsCount() > 0;
    }

    /**
     * @return Return true if the given type can be managed by the driver, and subsequently by the UI.
     *
     * E.g. of managed types are:
     *              int, Integer, java.lang.Integer, org.kie.SomeClass, List<Integer>, java.util.List<org.kie.SomeClass>
     *
     * e.g. of not manged types are:
     *              int[], java.util.List<List<String>>, List<Map<String, org.kie.SomeClass>>
     *
     */
    public boolean isManagedType(TypeDescr typeDescr, ClassTypeResolver classTypeResolver) throws ModelDriverException {

        DriverUtils driverUtils = DriverUtils.getInstance();

        if ( driverUtils.isArray( typeDescr ) ) return false;

        if ( driverUtils.isPrimitiveType( typeDescr ) || driverUtils.isSimpleClass( typeDescr ) ) return true;

        Object[] simpleGenerics = driverUtils.isSimpleGeneric( typeDescr );

        if (Boolean.FALSE.equals( simpleGenerics[0] )) {
            return false;
        } else {
            //try to guess if we have something in the form Collection<SomeClass>
            String collectionCandidate = simpleGenerics[1].toString();
            try {
                Class collectionCandidateClass = classTypeResolver.resolveType( collectionCandidate );
                return Collection.class.isAssignableFrom( collectionCandidateClass );
            } catch (ClassNotFoundException e) {
                throw new ModelDriverException("Class could not be resolved for name: " + collectionCandidate + ". " + e.getMessage(), e );
            }
        }
    }

    /**
     * @return Return true if the given type can be managed by the driver, and subsequently by the UI.
     *
     * E.g. of managed types are:
     *              int, Integer, java.lang.Integer, org.kie.SomeClass, List<Integer>, java.util.List<org.kie.SomeClass>
     *
     * e.g. of not manged types are:
     *              int[], java.util.List<List<String>>, List<Map<String, org.kie.SomeClass>>
     *
     */
    public boolean isManagedType(Type type, ClassTypeResolver classTypeResolver) throws ModelDriverException {

        //quickest checks first.
        if (type.isPrimitive()) return true;

        if (type.isArray()) return false;

        if ( type.isParameterized() && type.getTypeArguments().size() > 1 ) return false;

        try {
            if (type.isParameterized()) {
                Class<?> bag = classTypeResolver.resolveType( type.getName() );
                if (!Collection.class.isAssignableFrom( bag )) return false;

                return isSimpleClass( ( ( List<Type> ) type.getTypeArguments() ).get( 0 ) );
            }

            return true;

        } catch (ClassNotFoundException e) {
            throw new ModelDriverException("Class could not be resolved for name: " + type.getName() + ". " + e.getMessage(), e );
        }
    }

    public boolean isSimpleClass(Type<?> type) {
        return !type.isArray() && !type.isPrimitive() && !type.isParameterized();
    }

    public boolean equalsType(TypeDescr type, String fullClassName, boolean multiple, String fullBagClassName, ClassTypeResolver classTypeResolver) throws ClassNotFoundException {

        String currentClassName;
        String currentBag;

        if (isArray( type )) return false;

        if (type.isPrimitiveType()) {
            return !multiple && fullClassName.equals( type.getPrimitiveType().getName() );
        }

        if (isSimpleClass( type )) {
            currentClassName = classTypeResolver.getFullTypeName( type.getClassOrInterfaceType().getClassName() );
            return !multiple && fullClassName.equals( currentClassName );
        }

        Object[] simpleGenerics = isSimpleGeneric( type );

        if (Boolean.TRUE.equals( simpleGenerics[0] ) && multiple) {

            currentBag = (String)simpleGenerics[1];
            currentBag = classTypeResolver.getFullTypeName( currentBag );

            currentClassName = ((TypeDescr)simpleGenerics[2]).getClassOrInterfaceType().getClassName();
            currentClassName = classTypeResolver.getFullTypeName( currentClassName );

            return fullBagClassName.equals( currentBag ) && fullClassName.equals(  currentClassName );
        }

        return false;
    }

    public boolean equalsType(Type type, String fullClassName, boolean multiple, String fullBagClassName, ClassTypeResolver classTypeResolver) throws Exception {

        String currentClassName;
        String currentBag;

        if (type.isArray()) return false;

        if (type.isPrimitive()) {
            return !multiple && fullClassName.equals( type.getName() );
        }

        if (isSimpleClass( type )) {
            currentClassName = classTypeResolver.getFullTypeName( type.getName() );
            return !multiple && fullClassName.equals( currentClassName );
        }

        Object[] simpleGenerics = isSimpleGeneric( type, classTypeResolver );
        if (isManagedType( type, classTypeResolver ) && multiple && Boolean.TRUE.equals( simpleGenerics[0] )) {

            currentBag = (String)simpleGenerics[1];
            currentBag = classTypeResolver.getFullTypeName( currentBag );

            currentClassName = (String)simpleGenerics[2];
            currentClassName = classTypeResolver.getFullTypeName( currentClassName );

            return fullBagClassName.equals( currentBag ) && fullClassName.equals(  currentClassName );
        }

        return false;
    }

    public int buildModifierRepresentation( List<ModifierDescr> modifiers ) {
        int result = 0x0;
        if (modifiers != null) {
            for (ModifierDescr modifier : modifiers) {
                if ("public".equals( modifier.getName() )) result = result | Modifier.PUBLIC;
                if ("protected".equals( modifier.getName() )) result = result | Modifier.PROTECTED;
                if ("private".equals( modifier.getName() )) result = result | Modifier.PRIVATE;
                if ("abstract".equals( modifier.getName() )) result = result | Modifier.ABSTRACT;
                if ("static".equals( modifier.getName() )) result = result | Modifier.STATIC;
                if ("final".equals( modifier.getName() )) result = result | Modifier.FINAL;
                if ("transient".equals( modifier.getName() )) result = result | Modifier.TRANSIENT;
                if ("volatile".equals( modifier.getName() )) result = result | Modifier.VOLATILE;
                if ("synchronized".equals( modifier.getName() )) result = result | Modifier.SYNCHRONIZED;
                if ("native".equals( modifier.getName() )) result = result | Modifier.NATIVE;
                if ("strictfp".equals( modifier.getName() )) result = result | Modifier.STRICT;
                if ("interface".equals( modifier.getName() )) result = result | Modifier.INTERFACE;
            }
        }
        return result;
    }

    public int buildModifierRepresentation( Member<?> member) {
        int result = 0x0;
        result = addModifierRepresentation( result, member );
        result = addModifierRepresentation( result, ( VisibilityScoped ) member );
        return result;
    }

    public int buildModifierRepresentation(JavaClassSource classSource) {
        return addModifierRepresentation( 0x0, classSource );

    }

    public int addModifierRepresentation(int modifiers, Member<?> member) {
        if (member != null) {
            if (member.isStatic()) modifiers = modifiers | Modifier.STATIC;
            if (member.isFinal()) modifiers = modifiers | Modifier.FINAL;
        }
        return modifiers;
    }

    public int addModifierRepresentation(int modifiers, VisibilityScoped visibilityScoped ) {
        if (visibilityScoped != null) {
            if (visibilityScoped.isPublic()) modifiers = modifiers | Modifier.PUBLIC;
            if (visibilityScoped.isProtected()) modifiers = modifiers | Modifier.PROTECTED;
            if (visibilityScoped.isPrivate()) modifiers = modifiers | Modifier.PRIVATE;
        }
        return modifiers;
    }

    public int keyFieldsCount( DataObject dataObject ) {
        int result = 0;
        for (ObjectProperty property : dataObject.getProperties().values()) {
            if (property.getAnnotation( org.kie.api.definition.type.Key.class.getName() ) != null) {
                result++;
            }
        }
        return result;
    }

    public int assignableFieldsCount( DataObject dataObject ) {
        int result = 0;
        for (ObjectProperty property : dataObject.getProperties().values()) {
            if (!property.isStatic() && !property.isFinal()) {
                result++;
            }
        }
        return result;
    }

}
