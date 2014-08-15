package org.uberfire.client.exporter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;


@ApplicationScoped
public class VFSJSExporter  implements UberfireJSExporter{

    @Inject
    Caller<VFSService> vfsServices;

    @Override
    public void export() {
        publish( this );
    }

    private native void publish( VFSJSExporter js )/*-{
        $wnd.$vfs_write = function (uri, content, callback) {
            js.@org.uberfire.client.exporter.VFSJSExporter::write(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(uri, content, callback)
        }
        $wnd.$vfs_readAllString = function (uri, callback) {
            js.@org.uberfire.client.exporter.VFSJSExporter::readAllString(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(uri, callback)
        }
    }-*/;



    public void write( final String uri,
                       final String content,
                       final JavaScriptObject callback ) {

        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                vfsServices.call( new RemoteCallback<Path>() {
                    @Override
                    public void callback( final Path response ) {
                        executeNativeCallback( callback, true );
                    }
                } ).write( path, content );
            }
        } ).get( uri );

    }

    public void readAllString( final String uri,
                               final JavaScriptObject callback ) {
        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path o ) {
                vfsServices.call( new RemoteCallback<String>() {
                    @Override
                    public void callback( final String response ) {
                        executeNativeCallback( callback, response );
                    }
                } ).readAllString( o );
            }
        } ).get( uri );
    }

    private native void executeNativeCallback( JavaScriptObject callback,
                                               Object param ) /*-{
        callback(param);
    }-*/;

}
