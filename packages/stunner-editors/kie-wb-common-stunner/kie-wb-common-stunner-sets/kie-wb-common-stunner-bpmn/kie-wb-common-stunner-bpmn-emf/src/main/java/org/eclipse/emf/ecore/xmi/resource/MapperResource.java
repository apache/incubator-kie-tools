/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.eclipse.emf.ecore.xmi.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.shared.GWT;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.Callback;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.map.Mapper;

public abstract class MapperResource extends ResourceImpl {

    protected static final Map<EObject, String> DETACHED_EOBJECT_TO_ID_MAP = new HashMap<EObject, String>();
    private Map<String, EObject> idToEObjectMap;
    private Map<EObject, String> eObjectToIDMap;

    public MapperResource(URI uri) {
        super(uri);
    }

    protected abstract Mapper getMapper();

    @Override
    public void load(final Map<?, ?> options, final Callback<Resource> callback) throws IOException {
        if (isLoaded && callback != null) {
            callback.onSuccess(this);
            return;
        }

        Map<?, ?> response = options == null ? null : (Map<?, ?>) options.get(URIConverter.OPTION_RESPONSE);
        if (response == null) {
            response = new HashMap<Object, Object>();
        }

        if (GWT.isClient()) {
            isLoading = true;

            final URIConverter uriConverter = getURIConverter();
            final Map<Object, Object> effectiveOptions = new HashMap<Object, Object>();
            effectiveOptions.put(URIConverter.OPTION_RESPONSE, response);

            uriConverter.createInputStream(getURI(),
                                           effectiveOptions,
                                           new LoadStreamCallback(options, effectiveOptions, callback));
        } else {

            // If an input stream can't be created, ensure that the resource is still considered loaded after the failure,
            // and do all the same processing we'd do if we actually were able to create a valid input stream.
            //

            URIConverter uriConverter = getURIConverter();
            InputStream inputStream = null;
            Map<Object, Object> effectiveOptions = new HashMap<Object, Object>();
            effectiveOptions.put(URIConverter.OPTION_RESPONSE, response);
            try {
                inputStream = uriConverter.createInputStream(getURI(), effectiveOptions);
            } catch (IOException exception) {
                Notification notification = setLoaded(true);
                if (errors != null) {
                    errors.clear();
                }
                if (warnings != null) {
                    warnings.clear();
                }
                isLoading = false;
                if (notification != null) {
                    eNotify(notification);
                }
                setModified(false);

                throw exception;
            }

            try {
                load(inputStream, options);
            } finally {
                inputStream.close();
                handleLoadResponse(response, effectiveOptions);
            }
        }
    }

    private void closeStream(InputStream inputStream, Callback<?> callback) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException exception) {
            if (callback != null) {
                callback.onFailure(exception);
            }
        }
    }

    private void setAsLoaded(Notification notification) {
        isLoading = false;
        setModified(false);

        if (notification != null) {
            eNotify(notification);
        }
    }

    protected void doLoad(InputStream inputStream, Map<?, ?> options, final Callback<Resource> callback) {
        if (options == null) {
            options = Collections.<String, Object>emptyMap();
        }

        getMapper().parse(this, inputStream, options, new Callback<Resource>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Resource result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
        if (options == null) {
            options = Collections.<String, Object>emptyMap();
        }

        getMapper().write(this, outputStream, options);
    }

    @Override
    protected boolean isAttachedDetachedHelperRequired() {
        return useIDs() || super.isAttachedDetachedHelperRequired();
    }

    protected boolean useIDs() {
        return eObjectToIDMap != null || idToEObjectMap != null || useUUIDs();
    }

    protected boolean useIDAttributes() {
        return true;
    }

    protected boolean useUUIDs() {
        return false;
    }

    public String getID(EObject eObject) {
        if (eObjectToIDMap == null) {
            return null;
        } else {
            return eObjectToIDMap.get(eObject);
        }
    }

    @Override
    protected EObject getEObjectByID(String id) {
        if (idToEObjectMap != null) {
            EObject eObject = idToEObjectMap.get(id);

            if (eObject != null) {
                return eObject;
            }
        }
        return super.getEObjectByID(id);
    }

    @Override
    public String getURIFragment(EObject eObject) {
        String id = getID(eObject);

        if (id != null) {
            return id;
        } else {
            return super.getURIFragment(eObject);
        }
    }

    public void setID(EObject eObject, String id) {
        String oldID = id != null ? getEObjectToIDMap().put(eObject, id) :
                getEObjectToIDMap().remove(eObject);

        if (oldID != null) {
            getIDToEObjectMap().remove(oldID);
        }

        if (id != null) {
            getIDToEObjectMap().put(id, eObject);
        }
    }

    @Override
    protected void attachedHelper(EObject eObject) {
        super.attachedHelper(eObject);

        String id = getID(eObject);

        if (id == null) {
            if (!isLoading()) {
                id = DETACHED_EOBJECT_TO_ID_MAP.remove(eObject);
                if (id == null) {
                    id = EcoreUtil.generateUUID();
                }
                setID(eObject, id);
            }
        } else {
            getIDToEObjectMap().put(id, eObject);
        }
    }

    public Map<String, EObject> getIDToEObjectMap() {
        if (idToEObjectMap == null) {
            idToEObjectMap = new HashMap<>();
        }
        return idToEObjectMap;
    }

    public Map<EObject, String> getEObjectToIDMap() {
        if (eObjectToIDMap == null) {
            eObjectToIDMap = new HashMap<>();
        }
        return eObjectToIDMap;
    }

    private class LoadStreamCallback implements Callback<Map<?, ?>> {

        private final Callback<Resource> callback;
        private final Map<?, ?> options;
        private final Map<Object, Object> effectiveOptions;

        public LoadStreamCallback(final Map<?, ?> options, Map<Object, Object> effectiveOptions, Callback<Resource> callback) {
            this.callback = callback;
            this.options = options;
            this.effectiveOptions = effectiveOptions;
        }

        @Override
        public void onFailure(Throwable caught) {
            Notification notification = setLoaded(true);
            if (errors != null) {
                errors.clear();
            }
            if (warnings != null) {
                warnings.clear();
            }
            setAsLoaded(notification);
            callback.onFailure(caught);
        }

        @Override
        public void onSuccess(Map<?, ?> result) {
            final Map<?, ?> response = (Map<?, ?>) result.get(URIConverter.OPTION_RESPONSE);
            final InputStream inputStream = (InputStream) response.get(URIConverter.RESPONSE_RESULT);

            final Notification notification = setLoaded(true);
            if (errors != null) {
                errors.clear();
            }
            if (warnings != null) {
                warnings.clear();
            }

            doLoad(inputStream, options, new Callback<Resource>() {
                @Override
                public void onFailure(Throwable caught) {
                    setAsLoaded(notification);
                    handleLoadResponse(response, effectiveOptions);
                    closeStream(inputStream, callback);
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(Resource result) {
                    closeStream(inputStream, callback);
                    setAsLoaded(notification);
                    handleLoadResponse(response, effectiveOptions);
                    callback.onSuccess(result);
                }
            });
        }
    }
}
