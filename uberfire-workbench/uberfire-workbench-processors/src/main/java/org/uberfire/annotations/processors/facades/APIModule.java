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
 * A facade for api module.
 * Due to a bug in Eclipse annotation processor and inner projects dependencies,
 * this class handle with the dependencies of uberfire-client-api.
 */
public class APIModule {

    private static final Logger logger = LoggerFactory.getLogger( APIModule.class );

    private static  Class<? extends Annotation> placeRequest;
    private static  Class<? extends Annotation> isDirty;
    private static  Class<? extends Annotation> onClose;
    private static  Class<? extends Annotation> onFocus;
    private static  Class<? extends Annotation> onLostFocus;
    private static  Class<? extends Annotation> onMayClose;
    private static  Class<? extends Annotation> onStartup;
    private static  Class<? extends Annotation> onOpen;
    private static  Class<? extends Annotation> onShutdown;
    private static  Class<? extends Annotation> onSave;
    private static  Class<? extends Annotation> onContextAttach;
    private static  Class<? extends Annotation> panelDefinition;
    private static  Class<? extends Annotation> position;

    private APIModule() {}

    static {

        try {
            panelDefinition = (Class<? extends Annotation>) Class.forName( "org.uberfire.workbench.model.PanelDefinition" );
            position = (Class<? extends Annotation>) Class.forName( "org.uberfire.workbench.model.Position" );
            placeRequest = (Class<? extends Annotation>) Class.forName( "org.uberfire.mvp.PlaceRequest" );
            isDirty = (Class<? extends Annotation>) Class.forName( "org.uberfire.lifecycle.IsDirty" );
            onClose = (Class<? extends Annotation>) Class.forName( "org.uberfire.lifecycle.OnClose" );
            onFocus = (Class<? extends Annotation>) Class.forName( "org.uberfire.lifecycle.OnFocus" );
            onLostFocus = (Class<? extends Annotation>) Class.forName( "org.uberfire.lifecycle.OnLostFocus" );
            onMayClose = (Class<? extends Annotation>) Class.forName( "org.uberfire.lifecycle.OnMayClose" );
            onOpen = (Class<? extends Annotation>) Class.forName( "org.uberfire.lifecycle.OnOpen" );
            onSave = (Class<? extends Annotation>) Class.forName( "org.uberfire.lifecycle.OnSave" );
            onShutdown = (Class<? extends Annotation>) Class.forName( "org.uberfire.lifecycle.OnShutdown" );
            onStartup = (Class<? extends Annotation>) Class.forName( "org.uberfire.lifecycle.OnStartup" );
            onContextAttach = (Class<? extends Annotation>) Class.forName( "org.uberfire.lifecycle.OnContextAttach" );

        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    public static Class<? extends Annotation> getPanelDefinitionClass() {
        return panelDefinition;
    }

    public static Class<? extends Annotation> getPositionClass() {
        return position;
    }

    public static Class<? extends Annotation> getOnContextAttachClass() {
        return onContextAttach;
    }

    public static  Class<? extends Annotation> getPlaceRequestClass() {
        return placeRequest;
    }

    public static Class<? extends Annotation> getIsDirtyClass() {
        return isDirty;
    }

    public static Class<? extends Annotation> getOnCloseClass() {
        return onClose;
    }

    public static Class<? extends Annotation> getOnShutdownlass() {
        return onShutdown;
    }

    public static Class<? extends Annotation> getOnFocusClass() {
        return onFocus;
    }

    public static Class<? extends Annotation> getOnLostFocusClass() {
        return onLostFocus;
    }

    public static Class<? extends Annotation> getOnMayCloseClass() {
        return onMayClose;
    }

    public static Class<? extends Annotation> getOnStartupClass() {
        return onStartup;
    }

    public static Class<? extends Annotation> getOnOpenClass() {
        return onOpen;
    }

    public static Class<? extends Annotation> getOnSaveClass() {
        return onSave;
    }

}
