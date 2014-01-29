package org.uberfire.annotations.processors.facades;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.uberfire.annotations.processors.exceptions.GenerationException;

/**
 * A facade for uberfire-client-api.
 * Due to a bug in Eclipse annotation processor and inner projects dependencies,
 * this class handle with the dependencies of uberfire-client-api.
 */
public class ClientAPIModule {

    private final Class<? extends Annotation> workbenchSplashScreen;
    private final Class<? extends Annotation> workbenchPerspective;
    private final Class<? extends Annotation> workbenchPopup;
    private final Class<? extends Annotation> workbenchScreen;
    private final Class<? extends Annotation> workbenchContext;
    private final Class<? extends Annotation> workbenchEditor;
    private final Class<? extends Annotation> defaultPosition;
    private final Class<? extends Annotation> workbenchPartTitle;
    private final Class<? extends Annotation> workbenchContextId;
    private final Class<? extends Annotation> workbenchPartTitleDecoration;
    private final Class<? extends Annotation> workbenchPartView;
    private final Class<? extends Annotation> workbenchMenu;
    private final Class<? extends Annotation> workbenchToolBar;
    private final Class<? extends Annotation> perspective;
    private final Class<? extends Annotation> splashFilter;
    private final Class<? extends Annotation> splashBodySize;
    private final Class<? extends Annotation> intercept;

    public ClientAPIModule() throws GenerationException {

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
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
    }



    public Class<? extends Annotation> getWorkbenchScreenClass() {
        return workbenchScreen;
    }

    public Class<? extends Annotation> getSplashFilterClass() {
        return splashFilter;
    }

    public Class<? extends Annotation> getSplashBodySizeClass() {
        return splashBodySize;
    }

    public Class<? extends Annotation> getInterceptClass() {
        return intercept;
    }

    public Class<? extends Annotation> getPerspectiveClass() {
        return perspective;
    }

    public Class<? extends Annotation> getWorkbenchToolBarClass() {
        return workbenchToolBar;
    }

    public Class<? extends Annotation> getWorkbenchMenuClass() {
        return workbenchMenu;
    }

    public Class<? extends Annotation> getWorkbenchPartViewClass() {
        return workbenchPartView;
    }

    public Class<? extends Annotation> getWorkbenchPartTitleDecorationsClass() {
        return workbenchPartTitleDecoration;
    }

    public Class<? extends Annotation> getWorkbenchContextIdClass() {
        return workbenchContextId;
    }

    public Class<? extends Annotation> getWorkbenchPartTitleClass() {
        return workbenchPartTitle;
    }

    public Class<? extends Annotation> getDefaultPositionClass() {
        return defaultPosition;
    }

    public Class<? extends Annotation> getWorkbenchContextClass() {
        return workbenchContext;
    }

    public Class<? extends Annotation> getWorkbenchEditorClass() {
        return workbenchEditor;
    }

    public Class<? extends Annotation> getWorkbenchPopupClass() {
        return workbenchPopup;
    }

    public Class<? extends Annotation> getWorkbenchSplashScreenClass() {
        return workbenchSplashScreen;
    }

    public Class<? extends Annotation> getWorkbenchPerspectiveClass() {
        return workbenchPerspective;
    }

    private String getAnnotationIdentifierValueOnClass( TypeElement o,
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

    public Boolean getWbPerspectiveScreenIsDefaultValueOnClass( TypeElement classElement ) throws GenerationException {
        String bool = ( getAnnotationIdentifierValueOnClass( classElement, workbenchPerspective.getName(), "isDefault" ) );
        return Boolean.valueOf( bool );
    }

    public String getWbPerspectiveScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchPerspective.getName(), "identifier" );
    }

    public String getWbPopupScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchPopup.getName(), "identifier" );
    }

    public String getWbSplashScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchSplashScreen.getName(), "identifier" );
    }

    public String getWbScreenIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchScreen.getName(), "identifier" );
    }

    public String getWbContextIdentifierValueOnClass( TypeElement classElement ) throws GenerationException {
        return getAnnotationIdentifierValueOnClass( classElement, workbenchContext.getName(), "identifier" );
    }
}
