package org.uberfire.client.exporter;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.JavaScriptObject;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.client.editor.JSEditorActivity;
import org.uberfire.client.editor.JSNativeEditor;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.workbench.type.ClientResourceType;

import static org.jboss.errai.ioc.client.QualifierUtil.*;

@ApplicationScoped
public class EditorJSExporter implements UberfireJSExporter {

    @Override
    public void export() {
        publish();
    }

    private native void publish() /*-{
        $wnd.$registerEditor = @org.uberfire.client.exporter.EditorJSExporter::registerEditor(Ljava/lang/Object;);
    }-*/;

    public static void registerEditor( final Object _obj ) {
        final JavaScriptObject obj = (JavaScriptObject) _obj;
        if ( JSNativeEditor.hasStringProperty( obj, "id" ) ) {
            final SyncBeanManager beanManager = IOC.getBeanManager();
            final ActivityBeansCache activityBeansCache = beanManager.lookupBean( ActivityBeansCache.class ).getInstance();

            final JSNativeEditor newNativeEditor = beanManager.lookupBean( JSNativeEditor.class ).getInstance();
            newNativeEditor.build( obj );

            PlaceManager placeManager = beanManager.lookupBean( PlaceManager.class ).getInstance();
            final JSEditorActivity activity = new JSEditorActivity( newNativeEditor, placeManager );

            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) Activity.class, JSEditorActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativeEditor.getId(), true );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) WorkbenchEditorActivity.class, JSEditorActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativeEditor.getId(), true );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) JSEditorActivity.class, JSEditorActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativeEditor.getId(), true );

            Class<? extends ClientResourceType> resourceTypeClass = getResourceTypeClass( beanManager, newNativeEditor );
            activityBeansCache.addNewEditorActivity( beanManager.lookupBeans( newNativeEditor.getId() ).iterator().next(), resourceTypeClass );

        }
    }

    private static Class<? extends ClientResourceType> getResourceTypeClass( SyncBeanManager beanManager,
                                                                             JSNativeEditor newNativeEditor ) {

        Collection<IOCBeanDef<ClientResourceType>> iocBeanDefs = beanManager.lookupBeans( ClientResourceType.class );
        for ( IOCBeanDef<ClientResourceType> iocBeanDef : iocBeanDefs ) {
            String beanClassName = iocBeanDef.getBeanClass().getName();
            if ( beanClassName.equalsIgnoreCase( newNativeEditor.getResourceType() ) ) {
                return (Class<? extends ClientResourceType>) iocBeanDef.getBeanClass();
            }
        }
        throw new EditorResourceTypeNotFound();
    }

    public static class EditorResourceTypeNotFound extends RuntimeException {

    }
}
