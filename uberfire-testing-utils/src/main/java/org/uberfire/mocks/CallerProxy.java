package org.uberfire.mocks;

import java.lang.reflect.Method;

import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

public class CallerProxy implements java.lang.reflect.InvocationHandler {

    private Object target;
    private RemoteCallback<Object> successCallBack;
    private ErrorCallback<Object> errorCallBack;

    static Object newInstance( Object target,
                               RemoteCallback<Object> successCallBack,
                               ErrorCallback<Object> errorCallBack ) {
        return java.lang.reflect.Proxy.newProxyInstance( target.getClass().getClassLoader(), target
                .getClass().getInterfaces(), new CallerProxy( target, successCallBack, errorCallBack ) );
    }

    private CallerProxy( Object target,
                         RemoteCallback successCallBack,
                         ErrorCallback errorCallBack ) {
        this.target = target;
        this.successCallBack = successCallBack;
        this.errorCallBack = errorCallBack;
    }

    public Object invoke( Object proxy,
                          Method m,
                          Object[] args ) throws Throwable {
        Object result = null;
        try {
            result = m.invoke( target, args );
        } catch ( Exception e ) {
            if ( errorCallBack != null ) {
                errorCallBack.error( result, e );
            }
            return result;
        }
        if ( successCallBack != null ) {
            successCallBack.callback( result );
        }
        return result;
    }

}
