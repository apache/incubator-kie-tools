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

package org.eclipse.emf.ecore.xmi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.map.XmlMapper;
import org.eclipse.emf.ecore.xmi.resource.xml.XMLDOMHandler;
import org.eclipse.emf.ecore.xmi.resource.xml.XMLHelper;
import org.eclipse.emf.ecore.xmi.resource.xml.XMLLoad;
import org.eclipse.emf.ecore.xmi.resource.xml.XMLSave;
import org.eclipse.emf.ecore.xml.type.AnyType;

/**
 * This class implements the XMLResource interface. It overloads the
 * doLoad method to invoke the XML deserializer rather than using the
 * default XMI loader.
 */
public class XMLResource extends ResourceImpl {

    public static final String OPTION_ELEMENT_HANDLER = "ELEMENT_HANDLER";

    public static String OPTION_USE_PARSER_POOL = "USE_PARSER_POOL";

    public static String OPTION_USE_XML_NAME_TO_FEATURE_MAP = "USE_XML_NAME_TO_FEATURE_MAP";

    public static String OPTION_USE_CACHED_LOOKUP_TABLE = "USE_CACHED_LOOKUP_TABLE";

    public static String OPTION_USE_DEPRECATED_METHODS = "USE_DEPRECATED_METHODS";

    public static String OPTION_CONFIGURATION_CACHE = "CONFIGURATION_CACHE";

    public static String OPTION_SAVE_TYPE_INFORMATION = "SAVE_TYPE_INFORMATION";

    public static String OPTION_PARSER_FEATURES = "PARSER_FEATURES";

    public static String OPTION_PARSER_PROPERTIES = "PARSER_PROPERTIES";

    public static String OPTION_DOM_USE_NAMESPACES_IN_SCOPE = "DOM_USE_NAMESPACES_IN_SCOPE";

    public static String OPTION_EXTENDED_META_DATA = "EXTENDED_META_DATA";

    public static String OPTION_ANY_TYPE = "ANY_TYPE";

    public static String OPTION_ANY_SIMPLE_TYPE = "ANY_SIMPLE_TYPE";

    public static String OPTION_XML_MAP = "XML_MAP";

    public static String OPTION_USE_ENCODED_ATTRIBUTE_STYLE = "USE_ENCODED_ATTRIBUTE_STYLE";

    public static String OPTION_FORMATTED = "FORMATTED";

    public static String OPTION_LINE_WIDTH = "LINE_WIDTH";

    public static String OPTION_DECLARE_XML = "DECLARE_XML";

    public static String OPTION_KEEP_DEFAULT_CONTENT = "KEEP_DEFAULT_CONTENT";

    public static String OPTION_SAVE_DOCTYPE = "SAVE_DOCTYPE";

    public static String OPTION_RESOURCE_ENTITY_HANDLER = "RESOURCE_ENTITY_HANDLER";

    public static String OPTION_SUPPRESS_DOCUMENT_ROOT = "SUPPRESS_DOCUMENT_ROOT";

    public static String OPTION_ESCAPE_USING_CDATA = "ESCAPE_USING_CDATA";

    public static String OPTION_SKIP_ESCAPE = "SKIP_ESCAPE";

    public static String OPTION_SKIP_ESCAPE_URI = "SKIP_ESCAPE_URI";

    public static String OPTION_PROCESS_DANGLING_HREF = "PROCESS_DANGLING_HREF";
    public static String OPTION_PROCESS_DANGLING_HREF_THROW = "THROW";
    public static String OPTION_PROCESS_DANGLING_HREF_DISCARD = "DISCARD";
    public static String OPTION_PROCESS_DANGLING_HREF_RECORD = "RECORD";

    public static String OPTION_RECORD_UNKNOWN_FEATURE = "RECORD_UNKNOWN_FEATURE";

    public static String OPTION_LAX_FEATURE_PROCESSING = "LAX_FEATURE_PROCESSING";

    public static String OPTION_LAX_WILDCARD_PROCESSING = "LAX_WILDCARD_PROCESSING";

    public static String OPTION_XML_OPTIONS = "XML_OPTIONS";

    public static String OPTION_DISABLE_NOTIFY = "DISABLE_NOTIFY";

    public static String OPTION_SCHEMA_LOCATION = "SCHEMA_LOCATION";

    public static String OPTION_SCHEMA_LOCATION_IMPLEMENTATION = "SCHEMA_LOCATION_IMPLEMENTATION";

    public static String OPTION_ENCODING = "ENCODING";

    public static String OPTION_XML_VERSION = "XML_VERSION";

    public static String OPTION_RECORD_ANY_TYPE_NAMESPACE_DECLARATIONS = "RECORD_ANY_TYPE_NAMESPACE_DECLARATIONS";

    public static String OPTION_FLUSH_THRESHOLD = "FLUSH_THRESHOLD";

    public static String OPTION_USE_FILE_BUFFER = "USE_FILE_BUFFER";

    public static String OPTION_DEFER_IDREF_RESOLUTION = "DEFER_IDREF_RESOLUTION";

    public static String OPTION_ROOT_OBJECTS = "ROOT_OBJECTS";

    public static String OPTION_RESOURCE_HANDLER = "RESOURCE_HANDLER";

    public static String OPTION_DEFER_ATTACHMENT = "DEFER_ATTACHMENT";

    public static String OPTION_URI_HANDLER = "URI_HANDLER";

    public static String HREF = "href";
    public static String NIL = "nil";
    public static String TYPE = "type";
    public static String SCHEMA_LOCATION = "schemaLocation";
    public static String NO_NAMESPACE_SCHEMA_LOCATION = "noNamespaceSchemaLocation";

    public static String XML_NS = ExtendedMetaData.XMLNS_PREFIX;
    public static String XSI_NS = ExtendedMetaData.XSI_PREFIX;
    public static String XSI_URI = ExtendedMetaData.XSI_URI;
    public static String XML_SCHEMA_URI = ExtendedMetaData.XML_SCHEMA_URI;

    /**
     * The map from {@link #getID ID} to {@link EObject}.
     * It is used to store IDs during a load or if the user
     * sets the ID of an object.
     */
    protected Map<String, EObject> idToEObjectMap;

    /**
     * The map from {@link EObject} to {@link #getID ID}.
     * It is used to store IDs during a load or if the user
     * sets the ID of an object.
     */
    protected Map<EObject, String> eObjectToIDMap;

    protected Map<EObject, AnyType> eObjectToExtensionMap;

    protected String encoding;
    protected String xmlVersion;
    protected boolean useZip;
    protected String publicId;
    protected String systemId;
    protected XMLDOMHandler xmlDOMHandler;

    /**
     * The map from {@link EObject} to {@link #getID ID}. It is used to store
     * IDs for objects that have been detached.
     */
    protected static final Map<EObject, String> DETACHED_EOBJECT_TO_ID_MAP = new HashMap<>();//Collections.synchronizedMap(new WeakHashMap<EObject, String>());

    /**
     * Constructor for XMLResourceImpl.
     */
    public XMLResource() {
        super();
        init();
    }

    /**
     * Constructor for XMLResourceImpl.
     * @param uri
     */
    public XMLResource(URI uri) {
        super(uri);
        init();
    }

    protected void init() {
        encoding = "ASCII";
        xmlVersion = "1.0";
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

    protected boolean assignIDsWhileLoading() {
        return true;
    }

    public Map<Object, Object> getDefaultSaveOptions() {
        if (defaultSaveOptions == null) {
            defaultSaveOptions = new HashMap<>();
        }
        return defaultSaveOptions;
    }

    public Map<Object, Object> getDefaultLoadOptions() {
        if (defaultLoadOptions == null) {
            defaultLoadOptions = new HashMap<>();
        }
        return defaultLoadOptions;
    }

    protected XMLHelper createXMLHelper() {
        return new XMLHelper(this);
    }

    protected XMLLoad createXMLLoad() {
        return new XMLLoad(createXMLHelper());
    }

    protected XMLSave createXMLSave() {
        return new XMLSave(createXMLHelper());
    }

    public Document save(Document doc, Map<?, ?> options, XMLDOMHandler handler) {
        XMLSave xmlSave = createXMLSave();
        xmlDOMHandler = handler;
        if (xmlDOMHandler == null) {
            xmlDOMHandler = new XMLDOMHandler();
        }
        Document document = doc;
        if (document == null) {
            try {
                document = XMLParser.createDocument();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        if (defaultSaveOptions == null || defaultSaveOptions.isEmpty()) {
            return xmlSave.save(this, document, options == null ? Collections.EMPTY_MAP : options, xmlDOMHandler);
        } else if (options == null) {
            return xmlSave.save(this, document, defaultSaveOptions, xmlDOMHandler);
        } else {
            Map<Object, Object> mergedOptions = new HashMap<Object, Object>(defaultSaveOptions);
            mergedOptions.putAll(options);
            return xmlSave.save(this, document, mergedOptions, xmlDOMHandler);
        }
    }

    @Override
    public boolean useZip() {
        return useZip;
    }

    public void setUseZip(boolean useZip) {
        this.useZip = useZip;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setDoctypeInfo(String publicId, String systemId) {
        this.publicId = publicId;
        this.systemId = systemId;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getXMLVersion() {
        return xmlVersion;
    }

    public void setXMLVersion(String version) {
        this.xmlVersion = version;
    }

    public Map<String, EObject> getIDToEObjectMap() {
        if (idToEObjectMap == null) {
            idToEObjectMap = new HashMap<String, EObject>();
        }

        return idToEObjectMap;
    }

    public Map<EObject, String> getEObjectToIDMap() {
        if (eObjectToIDMap == null) {
            eObjectToIDMap = new HashMap<EObject, String>();
        }

        return eObjectToIDMap;
    }

    public Map<EObject, AnyType> getEObjectToExtensionMap() {
        if (eObjectToExtensionMap == null) {
            eObjectToExtensionMap = new HashMap<EObject, AnyType>();
        }
        return eObjectToExtensionMap;
    }

    /*
     * Javadoc copied from interface
     */
    public String getID(EObject eObject) {
        if (eObjectToIDMap == null) {
            return null;
        } else {
            return eObjectToIDMap.get(eObject);
        }
    }

    /**
     * Sets the ID of the object.
     * This default implementation will update the {@link #eObjectToIDMap}.
     * Clients may override it to set the ID as an actual attribute object the object.
     * @param eObject the object.
     * @param id the object's ID.
     */
    public void setID(EObject eObject, String id) {
        Object oldID = id != null ? getEObjectToIDMap().put(eObject, id) : getEObjectToIDMap().remove(eObject);

        if (oldID != null) {
            getIDToEObjectMap().remove(oldID);
        }

        if (id != null) {
            getIDToEObjectMap().put(id, eObject);
        }
    }

    /*
     * Javadoc copied from interface.
     */
    @Override
    public String getURIFragment(EObject eObject) {
        String id = getID(eObject);

        if (id != null) {
            return id;
        } else {
            return super.getURIFragment(eObject);
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

        return useIDAttributes() ? super.getEObjectByID(id) : null;
    }

    protected boolean isPath(String uriFragment) {
        return uriFragment.startsWith("/");
    }

    @Override
    protected boolean isAttachedDetachedHelperRequired() {
        return useIDs() || super.isAttachedDetachedHelperRequired();
    }

    @Override
    protected void attachedHelper(EObject eObject) {
        super.attachedHelper(eObject);

        if (useIDs()) {
            String id = getID(eObject);
            if (useUUIDs() && id == null) {
                if (assignIDsWhileLoading() || !isLoading()) {
                    id = DETACHED_EOBJECT_TO_ID_MAP.remove(eObject);
                    if (id == null) {
                        id = EcoreUtil.generateUUID();
                    }
                    setID(eObject, id);
                }
            } else if (id != null) {
                getIDToEObjectMap().put(id, eObject);
            }
        }
    }

    @Override
    protected void detachedHelper(EObject eObject) {
        if (useIDs()) {
            if (useUUIDs()) {
                DETACHED_EOBJECT_TO_ID_MAP.put(eObject, getID(eObject));
            }

            if (idToEObjectMap != null && eObjectToIDMap != null) {
                setID(eObject, null);
            }
        }

        super.detachedHelper(eObject);
    }

    /**
     * Does all the work of unloading the resource. It calls doUnload in
     * ResourceImpl, then it clears {@link #idToEObjectMap} and {@link #eObjectToIDMap} as necessary.
     */
    @Override
    protected void doUnload() {
        super.doUnload();

        if (idToEObjectMap != null) {
            idToEObjectMap.clear();
        }

        if (eObjectToIDMap != null) {
            eObjectToIDMap.clear();
        }

        if (eObjectToExtensionMap != null) {
            eObjectToExtensionMap.clear();
        }
    }

    /**
     * Returns a string representation of the {@link #idToEObjectMap ID} map.
     * @return a string representation of the ID map.
     */
    @Override
    public String toKeyString() {
        StringBuffer result = new StringBuffer("Key type: ");
        result.append(getClass().toString());
        if (idToEObjectMap != null) {
            TreeMap<String, String> tree = new TreeMap<String, String>();
            for (String key : idToEObjectMap.keySet()) {
                if (key != null) {
                    tree.put(key, key);
                }
            }

            // add the key/value pairs to the output string
            for (String key : tree.values()) {
                Object value = idToEObjectMap.get(key);
                result.append("\r\n\t[Key=" + key + ", Value=" + value + "]");
            }
        }
        return result.toString();
    }

    public final void load(Node node, Map<?, ?> options) throws IOException {
        if (!isLoaded) {
            Notification notification = setLoaded(true);
            isLoading = true;

            if (errors != null) {
                errors.clear();
            }

            if (warnings != null) {
                warnings.clear();
            }

            try {
                if (defaultLoadOptions == null || defaultLoadOptions.isEmpty()) {
                    doLoad(node, options);
                } else if (options == null) {
                    doLoad(node, defaultLoadOptions);
                } else {
                    Map<Object, Object> mergedOptions = new HashMap<Object, Object>(defaultLoadOptions);
                    mergedOptions.putAll(options);

                    doLoad(node, mergedOptions);
                }
            } finally {
                isLoading = false;

                if (notification != null) {
                    eNotify(notification);
                }

                setModified(false);
            }
        }
    }

    public void doLoad(Node node, Map<?, ?> options) throws IOException {
        XMLLoad xmlLoad = createXMLLoad();

        if (options == null) {
            options = Collections.EMPTY_MAP;
        }

        xmlLoad.load(this, node, options);
    }

    @Override
    protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
        //super.doSave(outputStream, options);

        if (options == null) {
            options = Collections.<String, Object>emptyMap();
        }

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.write(this, outputStream, options);
    }
}
