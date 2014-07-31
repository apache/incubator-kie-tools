package org.uberfire.client;

import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;

public class UberfireJSAPIExporter {

    @Inject
    Caller<VFSService> vfsServices;

    private native void executeNativeCallback( JavaScriptObject callback,
                                               Object param ) /*-{
        callback(param);
    }-*/;

    public void export() {
        publishVFSAPIs( this );
    }

    private native void publishVFSAPIs( UberfireJSAPIExporter js )/*-{
        $wnd.$vfs_write = function (uri, content, callback) {
            js.@org.uberfire.client.UberfireJSAPIExporter::write(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(uri, content, callback)
        }
        $wnd.$vfs_readAllString = function (uri, callback) {
            js.@org.uberfire.client.UberfireJSAPIExporter::readAllString(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(uri, callback)
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

}
