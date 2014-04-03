package org.uberfire.annotations.processors.facades;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.uberfire.annotations.processors.exceptions.GenerationException;

/**
 * A collection of type names in the UberFire Client API module.
 * Due to a bug in Eclipse annotation processor dependencies, we refer to all UberFire type names using Strings,
 * Elements, and TypeMirrors. We cannot refer to the annotation types as types themselves.
 */
public class ClientAPIModule {

    public static final String IDENTIFIER = "identifier";
    public static final String IS_DEFAULT = "isDefault";

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
    public static final String splashBodySize =  "org.uberfire.client.annotations.SplashBodySize" ;
    public static final String intercept =  "org.uberfire.client.annotations.Intercept" ;

    public static String getWorkbenchScreenClass() {
        return workbenchScreen;
    }

    public static String getSplashFilterClass() {
        return splashFilter;
    }

    public static String getSplashBodySizeClass() {
        return splashBodySize;
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

    private static String getAnnotationIdentifierValueOnClass( TypeElement o,
                                                        String className,
                                                        String annotationName ) throws GenerationException {
        try {
            String identifierValue = "";
            for ( final AnnotationMirror am : o.getAnnotationMirrors() ) {

                if ( className.equals( am.getAnnotationType().toString() ) ) {
                    for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet() ) {
                        if ( annotationName.equals( entry.getKey().getSimpleName().toString() ) ) {
                            AnnotationValue value = entry.getValue();
                            identifierValue = value.getValue().toString();
                        }
                    }
                }
            }
            return identifierValue;
        } catch ( Exception e ) {
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
    }

    public static Boolean getWbPerspectiveScreenIsDefaultValueOnClass( TypeElement classElement ) throws GenerationException {
        String bool = ( getAnnotationIdentifierValueOnClass( classElement, workbenchPerspective, IS_DEFAULT ) );
        return Boolean.valueOf( bool );
    }

    public static String getWbPerspectiveScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchPerspective, IDENTIFIER );
    }

    public static String getWbPopupScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchPopup, IDENTIFIER );
    }

    public static String getWbSplashScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchSplashScreen, IDENTIFIER );
    }

    public static String getWbScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchScreen, IDENTIFIER );
    }

    public static String getWbContextIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchContext, IDENTIFIER );
    }
}
