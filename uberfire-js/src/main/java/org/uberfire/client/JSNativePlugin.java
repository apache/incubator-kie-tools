package org.uberfire.client;

import java.util.Collection;
import java.util.Collections;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.backend.plugin.RuntimePluginsService;
import org.uberfire.mvp.PlaceRequest;

@Dependent
public class JSNativePlugin {

    @Inject
    private Caller<RuntimePluginsService> runtimePluginsService;

    private JavaScriptObject obj;
    private Element element = null;

    private static final Collection<String> ROLES = Collections.emptyList();

    private static final Collection<String> TRAITS = Collections.emptyList();

    public void build( final JavaScriptObject obj ) {
        if ( this.obj != null ) {
            throw new RuntimeException( "Can't build more than once." );
        }
        this.obj = obj;
        buildElement();
    }

    public native String getId()  /*-{
        return this.@org.uberfire.client.JSNativePlugin::obj.id;
    }-*/;

    public Element getElement() {
        return element;
    }

    public String getContextId() {
        String contextId = null;

        if ( hasMethod( obj, "context_id" ) ) {
            contextId = getContextIdFunctionResult( obj );
        } else if ( hasStringProperty( obj, "context_id" ) ) {
            contextId = getContextId( obj );
        }

        return contextId;
    }

    public String getTitle() {
        String title = null;

        if ( hasMethod( obj, "title" ) ) {
            title = getTitleFunctionResult( obj );
        } else if ( hasStringProperty( obj, "title" ) ) {
            title = getTitle( obj );
        }

        if ( title == null ) {
            title = getId();
        }

        return title;
    }

    public String getType() {
        String type = null;

        if ( hasMethod( obj, "type" ) ) {
            type = getTypeFunctionResult( obj );
        } else if ( hasStringProperty( obj, "type" ) ) {
            type = getType( obj );
        }

        if ( type == null ) {
            type = "";
        }

        return type;
    }

    public void onOpen() {
        if ( hasMethod( obj, "on_open" ) ) {
            executeOnOpen( obj );
        }
    }

    public void onClose() {
        if ( hasMethod( obj, "on_close" ) ) {
            executeOnClose( obj );
        }
    }

    public void onFocus() {
        if ( hasMethod( obj, "on_focus" ) ) {
            executeOnFocus( obj );
        }
    }

    public void onLostFocus() {
        if ( hasMethod( obj, "on_lost_focus" ) ) {
            executeOnLostFocus( obj );
        }
    }

    public boolean onMayClose() {
        if ( hasMethod( obj, "on_may_close" ) ) {
            return executeOnMayClose( obj );
        }
        return true;
    }

    public Collection<String> getRoles() {
        return ROLES;
    }

    public Collection<String> getTraits() {
        return TRAITS;
    }

    private void buildElement() {
        final String content;
        final String contentUrl;
        if ( hasMethod( obj, "templateUrl" ) ) {
            contentUrl = getTemplateUrlFunctionResult( obj );
            content = null;
        } else if ( hasMethod( obj, "template" ) ) {
            content = getTemplateFunctionResult( obj );
            contentUrl = null;
        } else if ( hasStringProperty( obj, "templateUrl" ) ) {
            contentUrl = getTemplateUrl( obj );
            content = null;
        } else if ( hasStringProperty( obj, "template" ) ) {
            content = getTemplate( obj );
            contentUrl = null;
        } else {
            content = null;
            contentUrl = null;
        }

        if ( content != null ) {
            element = new HTML( new SafeHtmlBuilder().appendHtmlConstant( content ).toSafeHtml() ).getElement();
        } else if ( contentUrl != null ) {
            runtimePluginsService.call( new RemoteCallback<String>() {
                @Override
                public void callback( String content ) {
                    element = new HTML( new SafeHtmlBuilder().appendHtmlConstant( content ).toSafeHtml() ).getElement();
                }
            } ).getTemplateContent( contentUrl );
        } else {
            element = null;
        }
    }

    static boolean hasTemplate( final JavaScriptObject obj ) {
        if ( hasMethod( obj, "template" ) || hasMethod( obj, "templateUrl" ) ) {
            return true;
        }

        return hasStringProperty( obj, "template" ) || hasStringProperty( obj, "templateUrl" );
    }

    static native boolean hasMethod( final JavaScriptObject obj,
                                     final String methodName )  /*-{
        return ((typeof obj[methodName]) === "function");
    }-*/;

    static native boolean hasStringProperty( final JavaScriptObject obj,
                                             final String propertyName )  /*-{
        return ((typeof obj[propertyName]) === "string");
    }-*/;

    private static native String getTemplateUrlFunctionResult( final JavaScriptObject o ) /*-{
        var result = o.templateUrl();
        if (typeof result === "string") {
            return result;
        }
        return null;
    }-*/;

    private static native String getTemplateFunctionResult( final JavaScriptObject o ) /*-{
        var result = o.template();
        if (typeof result === "string") {
            return result;
        }
        return null;
    }-*/;

    private static native String getContextIdFunctionResult( final JavaScriptObject o ) /*-{
        var result = o.context_Id();
        if (typeof result === "string") {
            return result;
        }
        return null;
    }-*/;

    private static native String getTypeFunctionResult( final JavaScriptObject o ) /*-{
        var result = o.type();
        if (typeof result === "string") {
            return result;
        }
        return null;
    }-*/;

    private static native String getTitleFunctionResult( final JavaScriptObject o ) /*-{
        var result = o.title();
        if (typeof result === "string") {
            return result;
        }
        return null;
    }-*/;

    private static native void executeOnOpen( final JavaScriptObject o ) /*-{
        o.on_open();
    }-*/;

    private static native void executeOnClose( final JavaScriptObject o ) /*-{
        o.on_close();
    }-*/;

    private static native void executeOnShutdown( final JavaScriptObject o ) /*-{
        o.on_shutdown();
    }-*/;

    private static native void executeOnFocus( final JavaScriptObject o ) /*-{
        o.on_focus();
    }-*/;

    private static native void executeOnLostFocus( final JavaScriptObject o ) /*-{
        o.on_lost_focus();
    }-*/;

    private static native boolean executeOnMayClose( final JavaScriptObject o ) /*-{
        var result = o.type();
        if (typeof result === "boolean") {
            return result;
        }
        return true;
    }-*/;

    private static native String getType( final JavaScriptObject o ) /*-{
        return o.type;
    }-*/;

    private static native String getTitle( final JavaScriptObject o ) /*-{
        return o.title;
    }-*/;

    private static native String getContextId( final JavaScriptObject o ) /*-{
        return o.context_id;
    }-*/;

    private static native String getTemplate( final JavaScriptObject o ) /*-{
        return o.template;
    }-*/;

    private static native String getTemplateUrl( final JavaScriptObject o ) /*-{
        return o.templateUrl;
    }-*/;

    public void onStartup() {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void onStartup( PlaceRequest place ) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void onShutdown() {
        if ( hasMethod( obj, "on_shutdown" ) ) {
            executeOnShutdown( obj );
        }
    }
}
