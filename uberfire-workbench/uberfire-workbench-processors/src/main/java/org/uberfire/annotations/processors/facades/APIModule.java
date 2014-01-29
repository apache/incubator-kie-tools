package org.uberfire.annotations.processors.facades;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.uberfire.annotations.processors.exceptions.GenerationException;

/**
 * A facade for api module.
 * Due to a bug in Eclipse annotation processor and inner projects dependencies,
 * this class handle with the dependencies of uberfire-client-api.
 */
public class APIModule {

    private final Class<? extends Annotation> placeRequest;
    private final Class<? extends Annotation> isDirty;
    private final Class<? extends Annotation> onClose;
    private final Class<? extends Annotation> onFocus;
    private final Class<? extends Annotation> onLostFocus;
    private final Class<? extends Annotation> onMayClose;
    private final Class<? extends Annotation> onStartup;
    private final Class<? extends Annotation> onOpen;
    private final Class<? extends Annotation> onShutdown;
    private final Class<? extends Annotation> onSave;
    private final Class<? extends Annotation> onContextAttach;
    private final Class<? extends Annotation> panelDefinition;
    private final Class<? extends Annotation> position;

    public APIModule() throws GenerationException {

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
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
    }

    public Class<? extends Annotation> getPanelDefinitionClass() {
        return panelDefinition;
    }

    public Class<? extends Annotation> getPositionClass() {
        return position;
    }

    public Class<? extends Annotation> getOnContextAttachClass() {
        return onContextAttach;
    }

    public Class<? extends Annotation> getPlaceRequestClass() {
        return placeRequest;
    }

    public Class<? extends Annotation> getIsDirtyClass() {
        return isDirty;
    }

    public Class<? extends Annotation> getOnCloseClass() {
        return onClose;
    }

    public Class<? extends Annotation> getOnShutdownlass() {
        return onShutdown;
    }

    public Class<? extends Annotation> getOnFocusClass() {
        return onFocus;
    }

    public Class<? extends Annotation> getOnLostFocusClass() {
        return onLostFocus;
    }

    public Class<? extends Annotation> getOnMayCloseClass() {
        return onMayClose;
    }

    public Class<? extends Annotation> getOnStartupClass() {
        return onStartup;
    }

    public Class<? extends Annotation> getOnOpenClass() {
        return onOpen;
    }

    public Class<? extends Annotation> getOnSaveClass() {
        return onSave;
    }

}
