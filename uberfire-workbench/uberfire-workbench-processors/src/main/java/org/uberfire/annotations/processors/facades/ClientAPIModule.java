package org.uberfire.annotations.processors.facades;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.processors.exceptions.GenerationException;

/**
 * A facade for uberfire-client-api.
 * Due to a bug in Eclipse annotation processor and inner projects dependencies,
 * this class handle with the dependencies of uberfire-client-api.
 */
public class ClientAPIModule {


    private static final Logger logger = LoggerFactory.getLogger( ClientAPIModule.class );

    public static final String IDENTIFIER = "identifier";
    public static final String IS_DEFAULT = "isDefault";
    private static Class<? extends Annotation> workbenchSplashScreen;
    private static Class<? extends Annotation> workbenchPerspective;
    private static Class<? extends Annotation> workbenchPopup;
    private static Class<? extends Annotation> workbenchScreen;
    private static Class<? extends Annotation> workbenchContext;
    private static Class<? extends Annotation> workbenchEditor;
    private static Class<? extends Annotation> defaultPosition;
    private static Class<? extends Annotation> workbenchPartTitle;
    private static Class<? extends Annotation> workbenchContextId;
    private static Class<? extends Annotation> workbenchPartTitleDecoration;
    private static Class<? extends Annotation> workbenchPartView;
    private static Class<? extends Annotation> workbenchMenu;
    private static Class<? extends Annotation> workbenchToolBar;
    private static Class<? extends Annotation> perspective;
    private static Class<? extends Annotation> splashFilter;
    private static Class<? extends Annotation> splashBodySize;
    private static Class<? extends Annotation> intercept;

    private ClientAPIModule() {}
    
    
    static {

        try {
            workbenchSplashScreen = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchSplashScreen" );
            workbenchPerspective = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchPerspective" );
            workbenchPopup = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchPopup" );
            workbenchScreen = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchScreen" );
            workbenchContext = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchContext" );
            workbenchEditor = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchEditor" );
            defaultPosition = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.DefaultPosition" );
            workbenchPartTitle = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchPartTitle" );
            workbenchContextId = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchContextId" );
            workbenchPartTitleDecoration = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchPartTitleDecoration" );
            workbenchPartView = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchPartView" );
            workbenchMenu = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchMenu" );
            workbenchToolBar = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.WorkbenchToolBar" );
            perspective = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.Perspective" );
            splashFilter = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.SplashFilter" );
            splashBodySize = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.SplashBodySize" );
            intercept = (Class<? extends Annotation>) Class.forName( "org.uberfire.client.annotations.Intercept" );

        } catch ( ClassNotFoundException e ) {
            logger.error(e.getMessage());
        }
    }



    public static Class<? extends Annotation> getWorkbenchScreenClass() {
        return workbenchScreen;
    }

    public static Class<? extends Annotation> getSplashFilterClass() {
        return splashFilter;
    }

    public static Class<? extends Annotation> getSplashBodySizeClass() {
        return splashBodySize;
    }

    public static Class<? extends Annotation> getInterceptClass() {
        return intercept;
    }

    public static Class<? extends Annotation> getPerspectiveClass() {
        return perspective;
    }

    public static Class<? extends Annotation> getWorkbenchToolBarClass() {
        return workbenchToolBar;
    }

    public static Class<? extends Annotation> getWorkbenchMenuClass() {
        return workbenchMenu;
    }

    public static Class<? extends Annotation> getWorkbenchPartViewClass() {
        return workbenchPartView;
    }

    public static Class<? extends Annotation> getWorkbenchPartTitleDecorationsClass() {
        return workbenchPartTitleDecoration;
    }

    public static Class<? extends Annotation> getWorkbenchContextIdClass() {
        return workbenchContextId;
    }

    public static Class<? extends Annotation> getWorkbenchPartTitleClass() {
        return workbenchPartTitle;
    }

    public static Class<? extends Annotation> getDefaultPositionClass() {
        return defaultPosition;
    }

    public static Class<? extends Annotation> getWorkbenchContextClass() {
        return workbenchContext;
    }

    public static Class<? extends Annotation> getWorkbenchEditorClass() {
        return workbenchEditor;
    }

    public static Class<? extends Annotation> getWorkbenchPopupClass() {
        return workbenchPopup;
    }

    public static Class<? extends Annotation> getWorkbenchSplashScreenClass() {
        return workbenchSplashScreen;
    }

    public static Class<? extends Annotation> getWorkbenchPerspectiveClass() {
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
        String bool = ( getAnnotationIdentifierValueOnClass( classElement, workbenchPerspective.getName(), IS_DEFAULT ) );
        return Boolean.valueOf( bool );
    }

    public static String getWbPerspectiveScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchPerspective.getName(), IDENTIFIER );
    }

    public static String getWbPopupScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchPopup.getName(), IDENTIFIER );
    }

    public static String getWbSplashScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchSplashScreen.getName(), IDENTIFIER );
    }

    public static String getWbScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchScreen.getName(), IDENTIFIER );
    }

    public static String getWbContextIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchContext.getName(), IDENTIFIER );
    }
}
