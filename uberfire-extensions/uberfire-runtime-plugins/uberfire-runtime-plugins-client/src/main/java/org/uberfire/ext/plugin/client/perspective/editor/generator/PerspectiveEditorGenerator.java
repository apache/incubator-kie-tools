package org.uberfire.ext.plugin.client.perspective.editor.generator;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
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
import org.uberfire.ext.layout.editor.api.LayoutServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutEditor;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;

import static org.jboss.errai.ioc.client.QualifierUtil.*;

@ApplicationScoped
public class PerspectiveEditorGenerator {

    private SyncBeanManagerImpl beanManager;
    private ActivityBeansCache activityBeansCache;
    private PlaceManager placeManager;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private Caller<LayoutServices> layoutServices;

    @PostConstruct
    public void setup() {
        beanManager = (SyncBeanManagerImpl) IOC.getBeanManager();
        activityBeansCache = beanManager.lookupBean( ActivityBeansCache.class ).getInstance();
        placeManager = beanManager.lookupBean( PlaceManager.class ).getInstance();

    }

    @AfterInitialization
    public void loadPerspectives() {
        pluginServices.call( new RemoteCallback<Collection<LayoutEditorModel>>() {
            @Override
            public void callback( final Collection<LayoutEditorModel> response ) {
                for ( LayoutEditorModel layoutEditorModel : response ) {
                    generatePerspective( layoutEditorModel );
                }
            }
        } ).listLayoutEditor( PluginType.PERSPECTIVE_LAYOUT );
    }

    //

    private void generatePerspective( LayoutEditorModel model ) {

        layoutServices.call( new RemoteCallback<LayoutEditor>() {
            @Override
            public void callback( final LayoutEditor perspective ) {
                if ( perspective != null ) {
                    generate( perspective );
                }
            }
        } ).convertLayoutFromString( model.getLayoutEditorModel() );

    }

    public void generate( LayoutEditor layoutEditor ) {
        if ( isANewPerspective( layoutEditor ) ) {
            DefaultPerspectiveEditorScreenActivity screen = createNewScreen( layoutEditor );
            createNewPerspective( layoutEditor, screen );
        } else {
            DefaultPerspectiveEditorScreenActivity screen = updateScreen( layoutEditor );
            updatePerspective( layoutEditor, screen );
        }
    }

    private void updatePerspective( LayoutEditor editor,
                                    DefaultPerspectiveEditorScreenActivity screen ) {
        final IOCBeanDef<Activity> activity = activityBeansCache.getActivity( editor.getName() );
        final DefaultPerspectiveEditorActivity perspectiveEditorActivity = (DefaultPerspectiveEditorActivity) activity.getInstance();
        perspectiveEditorActivity.update( editor, screen );
    }

    private DefaultPerspectiveEditorScreenActivity updateScreen( LayoutEditor editor ) {
        final IOCBeanDef<Activity> activity = activityBeansCache.getActivity( editor.getName() + DefaultPerspectiveEditorScreenActivity.screenSufix() );
        final DefaultPerspectiveEditorScreenActivity screenActivity = (DefaultPerspectiveEditorScreenActivity) activity.getInstance();
        screenActivity.build( editor );
        return screenActivity;
    }

    private void createNewPerspective( LayoutEditor perspective,
                                       DefaultPerspectiveEditorScreenActivity screen ) {
        final DefaultPerspectiveEditorActivity activity = new DefaultPerspectiveEditorActivity( perspective, screen );

        beanManager.addBean( (Class) PerspectiveActivity.class, DefaultPerspectiveEditorActivity.class, null, activity, DEFAULT_QUALIFIERS, perspective.getName(), true, null );

        activityBeansCache.addNewPerspectiveActivity( beanManager.lookupBeans( perspective.getName() ).iterator().next() );

    }

    private DefaultPerspectiveEditorScreenActivity createNewScreen( LayoutEditor perspective ) {
        DefaultPerspectiveEditorScreenActivity activity = new DefaultPerspectiveEditorScreenActivity( perspective, placeManager );

        beanManager.addBean( (Class) Activity.class, DefaultPerspectiveEditorScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, activity.getName(), true, null );
        beanManager.addBean( (Class) WorkbenchScreenActivity.class, DefaultPerspectiveEditorScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, activity.getName(), true, null );
        beanManager.addBean( (Class) DefaultPerspectiveEditorScreenActivity.class, DefaultPerspectiveEditorScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, activity.getName(), true, null );

        activityBeansCache.addNewScreenActivity( beanManager.lookupBeans( activity.getName() ).iterator().next() );
        return activity;
    }

    private boolean isANewPerspective( LayoutEditor editor ) {
        final IOCBeanDef<Activity> activity = activityBeansCache.getActivity( editor.getName() );
        return activity == null;
    }

}
