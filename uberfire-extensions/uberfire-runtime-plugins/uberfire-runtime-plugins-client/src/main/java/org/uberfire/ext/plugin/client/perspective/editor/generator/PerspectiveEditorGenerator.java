package org.uberfire.ext.plugin.client.perspective.editor.generator;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.ext.plugin.editor.NewPerspectiveEditorEvent;
import org.uberfire.ext.plugin.editor.PerspectiveEditor;
import org.uberfire.ext.plugin.model.PerspectiveEditorModel;
import org.uberfire.ext.plugin.service.PluginServices;

import static org.jboss.errai.ioc.client.QualifierUtil.*;

@ApplicationScoped
public class PerspectiveEditorGenerator {

    private SyncBeanManagerImpl beanManager;
    private ActivityBeansCache activityBeansCache;
    private PlaceManager placeManager;

    @Inject
    private Caller<PluginServices> pluginServices;

    @PostConstruct
    public void setup() {
        beanManager = (SyncBeanManagerImpl) IOC.getBeanManager();
        activityBeansCache = beanManager.lookupBean( ActivityBeansCache.class ).getInstance();
        placeManager = beanManager.lookupBean( PlaceManager.class ).getInstance();

    }

    @AfterInitialization
    public void loadPerspectives() {
        pluginServices.call( new RemoteCallback<Collection<PerspectiveEditorModel>>() {
            @Override
            public void callback( final Collection<PerspectiveEditorModel> response ) {
                for ( PerspectiveEditorModel perspectiveEditorModel : response ) {
                    generate( perspectiveEditorModel.getPerspectiveModel() );
                }
            }
        } ).listPerspectiveEditor();
    }

    private void observeNewPerspectives( @Observes NewPerspectiveEditorEvent event ) {
        generate( event.getPerspectiveContent() );
    }

    private void generate( PerspectiveEditor editor ) {

        if ( shouldGenerate( editor ) ) {
            if ( isANewPerspective( editor ) ) {
                DefaultPerspectiveEditorScreenActivity screen = createNewScreen( editor );
                createNewPerspective( editor, screen );
            } else {
                DefaultPerspectiveEditorScreenActivity screen = updateScreen( editor );
                updatePerspective( editor, screen );
            }
        }

    }

    private boolean shouldGenerate( PerspectiveEditor editor ) {
        return editor != null && editor.isAValidPerspective();
    }

    private void updatePerspective( PerspectiveEditor editor,
                                    DefaultPerspectiveEditorScreenActivity screen ) {
        final IOCBeanDef<Activity> activity = activityBeansCache.getActivity( editor.getName() );
        final DefaultPerspectiveEditorActivity perspectiveEditorActivity = (DefaultPerspectiveEditorActivity) activity.getInstance();
        perspectiveEditorActivity.update( editor, screen );
    }

    private DefaultPerspectiveEditorScreenActivity updateScreen( PerspectiveEditor editor ) {
        final IOCBeanDef<Activity> activity = activityBeansCache.getActivity( editor.getName() + DefaultPerspectiveEditorScreenActivity.screenSufix() );
        final DefaultPerspectiveEditorScreenActivity screenActivity = (DefaultPerspectiveEditorScreenActivity) activity.getInstance();
        screenActivity.build( editor );
        return screenActivity;
    }

    private boolean isANewPerspective( PerspectiveEditor editor ) {
        final IOCBeanDef<Activity> activity = activityBeansCache.getActivity( editor.getName() );
        return activity == null;
    }

    private DefaultPerspectiveEditorScreenActivity createNewScreen( PerspectiveEditor editor
                                                                  ) {
        DefaultPerspectiveEditorScreenActivity activity = new DefaultPerspectiveEditorScreenActivity( editor, placeManager );

        beanManager.addBean( (Class) Activity.class, DefaultPerspectiveEditorScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, activity.getName(), true, null );
        beanManager.addBean( (Class) WorkbenchScreenActivity.class, DefaultPerspectiveEditorScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, activity.getName(), true, null );
        beanManager.addBean( (Class) DefaultPerspectiveEditorScreenActivity.class, DefaultPerspectiveEditorScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, activity.getName(), true, null );

        activityBeansCache.addNewScreenActivity( beanManager.lookupBeans( activity.getName() ).iterator().next() );
        return activity;
    }

    private void createNewPerspective( PerspectiveEditor editor,
                                       DefaultPerspectiveEditorScreenActivity screen ) {
        final DefaultPerspectiveEditorActivity activity = new DefaultPerspectiveEditorActivity( editor, screen );

        beanManager.addBean( (Class) PerspectiveActivity.class, DefaultPerspectiveEditorActivity.class, null, activity, DEFAULT_QUALIFIERS, editor.getName(), true, null );

        activityBeansCache.addNewPerspectiveActivity( beanManager.lookupBeans( editor.getName() ).iterator().next() );
    }

}
