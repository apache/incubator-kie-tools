/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.mocks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.common.base.Defaults;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

public class CallerProxy implements java.lang.reflect.InvocationHandler {

    private Object target;
    private RemoteCallback<Object> successCallBack;
    private ErrorCallback<Object> errorCallBack;

    private CallerProxy(final Object target) {
        this.target = target;
    }

    static Object newInstance(final Object target) {
        return java.lang.reflect.Proxy.newProxyInstance(target.getClass().getClassLoader(),
                                                        target
                                                                .getClass().getInterfaces(),
                                                        new CallerProxy(target));
    }

    public void setSuccessCallBack(final RemoteCallback<Object> successCallBack) {
        this.successCallBack = successCallBack;
    }

    public void setErrorCallBack(final ErrorCallback<Object> errorCallBack) {
        this.errorCallBack = errorCallBack;
    }

    public Object invoke(final Object proxy,
                         final Method m,
                         final Object[] args) throws Throwable {
        Object result = null;
        try {
            result = m.invoke(target,
                              args);
        } catch (Exception e) {
            if (errorCallBack != null) {
                if (e instanceof InvocationTargetException) {
                    errorCallBack.error(result,
                                        ((InvocationTargetException) e).getTargetException());
                } else {
                    errorCallBack.error(result,
                                        e);
                }
            }
            if (m.getReturnType().isPrimitive()) {
                return Defaults.defaultValue(m.getReturnType());
            } else {
                return result;
            }
        }
        if (successCallBack != null) {
            successCallBack.callback(result);
        }
        return result;
    }
}
