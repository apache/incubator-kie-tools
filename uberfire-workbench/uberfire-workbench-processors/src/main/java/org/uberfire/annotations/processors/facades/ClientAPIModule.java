package org.uberfire.annotations.processors.facades;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.uberfire.annotations.processors.GeneratorUtils;

/**
 * A collection of type names in the UberFire Client API module.
 * Due to a bug in Eclipse annotation processor dependencies, we refer to all UberFire type names using Strings,
 * Elements, and TypeMirrors. We cannot refer to the annotation types as types themselves.
 */
public class ClientAPIModule {

    public static final String IDENTIFIER = "identifier";
    public static final String OWNING_PERSPECTIVE = "owningPerspective";
    public static final String IS_DEFAULT = "isDefault";
    public static final String IS_TEMPLATE = "isTemplate";
    public static final String VALUE = "value";

    private ClientAPIModule() {}

    public static final String workbenchSplashScreen =  "org.uberfire.client.annotations.WorkbenchSplashScreen" ;
    public static final String workbenchPerspective =  "org.uberfire.client.annotations.WorkbenchPerspective" ;
    public static final String workbenchPopup =  "org.uberfire.client.annotations.WorkbenchPopup" ;
    public static final String workbenchScreen =  "org.uberfire.client.annotations.WorkbenchScreen" ;
    public static final String workbenchContext =  "org.uberfire.client.annotations.WorkbenchContext" ;
    public static final String workbenchEditor =  "org.uberfire.client.annotations.WorkbenchEditor" ;
    public static final String defaultPosition =  "org.uberfire.client.annotations.DefaultPosition" ;
    public static final String workbenchPartTitle =  "org.uberfire.client.annotations.WorkbenchPartTitle" ;
    public static final String workbenchContextId =  "org.uberfire.client.annotations.WorkbenchContextId" ;
    public static final String workbenchPartTitleDecoration =  "org.uberfire.client.annotations.WorkbenchPartTitleDecoration" ;
    public static final String workbenchPartView =  "org.uberfire.client.annotations.WorkbenchPartView" ;
    public static final String workbenchMenu =  "org.uberfire.client.annotations.WorkbenchMenu" ;
    public static final String workbenchToolBar =  "org.uberfire.client.annotations.WorkbenchToolBar" ;
    public static final String perspective =  "org.uberfire.client.annotations.Perspective" ;
    public static final String splashFilter =  "org.uberfire.client.annotations.SplashFilter" ;
    public static final String splashBodyHeight =  "org.uberfire.client.annotations.SplashBodyHeight" ;
    public static final String intercept =  "org.uberfire.client.annotations.Intercept" ;
    public static final String workbenchPart = "org.uberfire.client.annotations.WorkbenchPart";
    public static final String workbenchParts = "org.uberfire.client.annotations.WorkbenchParts";
    public static final String workbenchPanel = "org.uberfire.client.annotations.WorkbenchPanel";
    public static final String parameterMapping = "org.uberfire.client.annotations.ParameterMapping";

    public static String getWorkbenchScreenClass() {
        return workbenchScreen;
    }

    public static String getSplashFilterClass() {
        return splashFilter;
    }

    public static String getSplashBodyHeightClass() {
        return splashBodyHeight;
    }

    public static String getInterceptClass() {
        return intercept;
    }

    public static String getPerspectiveClass() {
        return perspective;
    }

    public static String getWorkbenchToolBarClass() {
        return workbenchToolBar;
    }

    public static String getWorkbenchMenuClass() {
        return workbenchMenu;
    }

    public static String getWorkbenchPartViewClass() {
        return workbenchPartView;
    }

    public static String getWorkbenchPartTitleDecorationsClass() {
        return workbenchPartTitleDecoration;
    }

    public static String getWorkbenchContextIdClass() {
        return workbenchContextId;
    }

    public static String getWorkbenchPartTitleClass() {
        return workbenchPartTitle;
    }

    public static String getDefaultPositionClass() {
        return defaultPosition;
    }

    public static String getWorkbenchContextClass() {
        return workbenchContext;
    }

    public static String getWorkbenchEditorClass() {
        return workbenchEditor;
    }

    public static String getWorkbenchPopupClass() {
        return workbenchPopup;
    }

    public static String getWorkbenchSplashScreenClass() {
        return workbenchSplashScreen;
    }

    public static String getWorkbenchPerspectiveClass() {
        return workbenchPerspective;
    }

    public static String getWorkbenchPart() {
        return workbenchPart;
    }

    public static String getWorkbenchParts() {
        return workbenchParts;
    }

    public static String getParameterMapping() {
        return parameterMapping;
    }

    public static String getWorkbenchPanel() {
        return workbenchPanel;
    }

    /**
     * Returns the value of the String-valued Annotation parameter on the given type, ignoring any default value that
     * exists on the annotation. Returns an empty string if the type lacks the given annotation, or if the annotation
     * lacks the given parameter.
     */
    private static String getAnnotationStringParam( TypeElement target,
                                                               String annotationClassName,
                                                               String annotationParamName ) {
        AnnotationValue paramValue = getAnnotationParamValue( target, annotationClassName, annotationParamName );
        if ( paramValue == null ) {
            return "";
        }
        return paramValue.getValue().toString();
    }

    /**
     * Returns the value associated with the given parameter of the given annotation on the given class element,
     * ignoring any default value that exists on the annotation. Returns null if the type lacks the given annotation, or
     * if the annotation lacks the given parameter.
     */
    private static AnnotationValue getAnnotationParamValue( TypeElement target,
                                                            String annotationClassName,
                                                            String annotationName ) {
        for ( final AnnotationMirror am : target.getAnnotationMirrors() ) {
            if ( annotationClassName.equals( am.getAnnotationType().toString() ) ) {
                for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet() ) {
                    if ( annotationName.equals( entry.getKey().getSimpleName().toString() ) ) {
                        return entry.getValue();
                    }
                }
            }
        }
        return null;
    }

    public static Boolean getWbPerspectiveScreenIsDefaultValueOnClass( TypeElement classElement ) {
        String bool = ( getAnnotationStringParam( classElement, workbenchPerspective, IS_DEFAULT ) );
        return Boolean.valueOf( bool );
    }

    public static String getWbPerspectiveScreenIdentifierValueOnClass( TypeElement classElement ) {
        return getAnnotationStringParam( classElement, workbenchPerspective, IDENTIFIER );
    }

    public static String getWbPopupScreenIdentifierValueOnClass( TypeElement classElement ) {
        return getAnnotationStringParam( classElement, workbenchPopup, IDENTIFIER );
    }

    public static String getWbSplashScreenIdentifierValueOnClass( TypeElement classElement ) {
        return getAnnotationStringParam( classElement, workbenchSplashScreen, IDENTIFIER );
    }

    public static String getWbScreenIdentifierValueOnClass( TypeElement classElement ) {
        return getAnnotationStringParam( classElement, workbenchScreen, IDENTIFIER );
    }

    public static AnnotationValue getWbScreenOwningPerspective( TypeElement classElement ) {
        return getAnnotationParamValue( classElement, workbenchScreen, OWNING_PERSPECTIVE );
    }

    public static String getWbContextIdentifierValueOnClass( TypeElement classElement ) {
        return getAnnotationStringParam( classElement, workbenchContext, IDENTIFIER );
    }

    public static boolean getWbPerspectiveScreenIsATemplate( Elements elementUtils, TypeElement classElement ) {
        List<? extends Element> enclosedElements = classElement.getEnclosedElements();
        for ( Element element: enclosedElements ) {
            if (isATemplate( elementUtils, element )) {
                return true;
            }
        }
        return false;
    }

    public static boolean isATemplate( Elements elementUtils, Element element ) {
        if ( GeneratorUtils.getAnnotation( elementUtils, element, workbenchParts ) != null) return true;
        if ( GeneratorUtils.getAnnotation( elementUtils, element, workbenchPart ) != null) return true;
        if ( GeneratorUtils.getAnnotation( elementUtils, element, workbenchPanel ) != null) return true;
        return false;
    }
}
