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

package org.eclipse.emf.ecore.xmi.resource.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.util.ElementHandler;
import org.eclipse.emf.ecore.xmi.util.GwtDOMHandler;
import org.eclipse.emf.ecore.xmi.util.XMLString;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.eclipse.emf.ecore.xml.type.ProcessingInstruction;
import org.eclipse.emf.ecore.xml.type.SimpleAnyType;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.ecore.xml.type.internal.DataValue.XMLChar;

import static org.kie.workbench.common.stunner.bpmn.client.emf.Bpmn2Marshalling.logError;

/**
 * This implements the XML serializer, possibly using an XMLMap
 * if one is provided as a save option.
 */
public class XMLSave {

    private static final int MAX_UTF_MAPPABLE_CODEPOINT = 0x10FFFF;
    private static final int MAX_LATIN1_MAPPABLE_CODEPOINT = 0xFF;
    private static final int MAX_ASCII_MAPPABLE_CODEPOINT = 0x7F;

    protected static final int INDEX_LOOKUP = 0;

    final StringBuffer buffer = new StringBuffer();

    protected XMLHelper helper;
    protected XMLString doc;
    protected boolean declareXSI;
    protected boolean useEncodedAttributeStyle;
    protected boolean declareXML;
    protected boolean saveTypeInfo;
    protected XMLTypeInfo xmlTypeInfo;
    protected boolean keepDefaults;
    protected Escape escape;
    protected Escape escapeURI;
    protected ResourceEntityHandler resourceEntityHandler;
    protected Lookup featureTable;
    protected String encoding;
    protected String xmlVersion;
    protected String idAttributeName = "id";
    protected String idAttributeNS = null;
    protected String processDanglingHREF;
    protected boolean declareSchemaLocation;
    protected boolean declareSchemaLocationImplementation;
    protected XMLMap map;
    protected ExtendedMetaData extendedMetaData;
    protected EClass anySimpleType;
    protected EClass anyType;
    protected Map<EObject, AnyType> eObjectToExtensionMap;
    protected EPackage xmlSchemaTypePackage = XMLTypePackage.eINSTANCE;
    protected int flushThreshold = Integer.MAX_VALUE;
    protected boolean toDOM;
    protected XMLDOMHandler handler;
    protected GwtDOMHandler gwtDocumentHandler;
    protected Node currentNode;
    protected NameInfo nameInfo;
    protected boolean useCache;
    protected EObject root;
    protected XMLResource xmlResource;
    protected List<? extends EObject> roots;
    protected ElementHandler elementHandler;

    protected static final int SKIP = 0;
    protected static final int SAME_DOC = 1;
    protected static final int CROSS_DOC = 2;

    protected static final int TRANSIENT = 0;
    protected static final int DATATYPE_SINGLE = 1;
    protected static final int DATATYPE_ELEMENT_SINGLE = 2;
    protected static final int DATATYPE_CONTENT_SINGLE = 3;
    protected static final int DATATYPE_SINGLE_NILLABLE = 4;
    protected static final int DATATYPE_MANY = 5;
    protected static final int OBJECT_CONTAIN_SINGLE = 6;
    protected static final int OBJECT_CONTAIN_MANY = 7;
    protected static final int OBJECT_HREF_SINGLE = 8;
    protected static final int OBJECT_HREF_MANY = 9;
    protected static final int OBJECT_CONTAIN_SINGLE_UNSETTABLE = 10;
    protected static final int OBJECT_CONTAIN_MANY_UNSETTABLE = 11;
    protected static final int OBJECT_HREF_SINGLE_UNSETTABLE = 12;
    protected static final int OBJECT_HREF_MANY_UNSETTABLE = 13;
    protected static final int OBJECT_ELEMENT_SINGLE = 14;
    protected static final int OBJECT_ELEMENT_SINGLE_UNSETTABLE = 15;
    protected static final int OBJECT_ELEMENT_MANY = 16;
    protected static final int OBJECT_ELEMENT_IDREF_SINGLE = 17;
    protected static final int OBJECT_ELEMENT_IDREF_SINGLE_UNSETTABLE = 18;
    protected static final int OBJECT_ELEMENT_IDREF_MANY = 19;
    protected static final int ATTRIBUTE_FEATURE_MAP = 20;
    protected static final int ELEMENT_FEATURE_MAP = 21;
    protected static final int OBJECT_ATTRIBUTE_SINGLE = 22;
    protected static final int OBJECT_ATTRIBUTE_MANY = 23;
    protected static final int OBJECT_ATTRIBUTE_IDREF_SINGLE = 24;
    protected static final int OBJECT_ATTRIBUTE_IDREF_MANY = 25;
    protected static final int DATATYPE_ATTRIBUTE_MANY = 26;

    protected static final String XML_VERSION = "1.0";

    protected static final String XSI_NIL = XMLResource.XSI_NS + ":" + XMLResource.NIL;             // xsi:nil
    protected static final String XSI_TYPE_NS = XMLResource.XSI_NS + ":" + XMLResource.TYPE;            // xsi:type
    protected static final String XSI_XMLNS = XMLResource.XML_NS + ":" + XMLResource.XSI_NS;          // xmlns:xsi
    protected static final String XSI_SCHEMA_LOCATION = XMLResource.XSI_NS + ":" + XMLResource.SCHEMA_LOCATION; // xsi:schemaLocation
    protected static final String XSI_NO_NAMESPACE_SCHEMA_LOCATION = XMLResource.XSI_NS + ":" + XMLResource.NO_NAMESPACE_SCHEMA_LOCATION; // xsi:noNamespaceSchemaLocation

    protected static final int EMPTY_ELEMENT = 1;
    protected static final int CONTENT_ELEMENT = 2;
    private String temporaryFileName = null;

    public class XMLTypeInfo {

        public boolean shouldSaveType(EClass objectType, EClassifier featureType, EStructuralFeature feature) {
            return objectType != featureType && objectType != anyType;
        }

        public boolean shouldSaveType(EClass objectType, EClass featureType, EStructuralFeature feature) {
            return objectType != featureType && (featureType.isAbstract() || feature.getEGenericType().getETypeParameter() != null);
        }
    }

    public XMLSave(XMLHelper helper) {
        this.helper = helper;
    }

    /**
     * Constructor for XMLSave.
     * @param options
     * @param helper
     * @param encoding
     */
    public XMLSave(Map<?, ?> options, XMLHelper helper, String encoding) {
        this(options, helper, encoding, "1.0");
    }

    public XMLSave(Map<?, ?> options, XMLHelper helper, String encoding, String xmlVersion) {
        this.helper = helper;
        init(helper.getResource(), options);
        this.encoding = encoding;
        this.xmlVersion = xmlVersion;
    }

    public Document save(XMLResource resource, Document doc, Map<?, ?> options, XMLDOMHandler handler) {
        toDOM = true;
        gwtDocumentHandler = new GwtDOMHandler(doc);
        this.handler = handler;
        this.xmlResource = resource;

        init(resource, options);
        @SuppressWarnings("unchecked")
        List<? extends EObject> contents = roots = (List<? extends EObject>) options.get(XMLResource.OPTION_ROOT_OBJECTS);
        if (contents == null) {
            contents = resource.getContents();
        }
        traverse(contents);

        try {
            endSave(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        xmlResource = null;
        return gwtDocumentHandler.getDocument();
    }

    protected void endSave(List<? extends EObject> contents) {
        if (extendedMetaData != null && contents.size() >= 1) {
            EObject root = contents.get(0);
            EClass eClass = root.eClass();

            EReference xmlnsPrefixMapFeature = extendedMetaData.getXMLNSPrefixMapFeature(eClass);
            if (xmlnsPrefixMapFeature != null) {
                @SuppressWarnings("unchecked")
                EMap<String, String> xmlnsPrefixMap = (EMap<String, String>) root.eGet(xmlnsPrefixMapFeature);
                for (Map.Entry<String, String> entry : helper.getPrefixToNamespaceMap()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String currentValue = xmlnsPrefixMap.get(key);
                    if (currentValue == null ? value != null : !currentValue.equals(value)) {
                        xmlnsPrefixMap.put(key, value);
                    }
                }
            }
        }

        if (processDanglingHREF == null ||
                XMLResource.OPTION_PROCESS_DANGLING_HREF_THROW.equals(processDanglingHREF)) {
            DanglingHREFException exception = helper.getDanglingHREFException();

            if (exception != null) {
                helper = null;
                throw new RuntimeException(exception);
            }
        }

        if (useCache) {
      /*if (doc != null)
      {
        ConfigurationCache.INSTANCE.releasePrinter(doc);
      }
      if (escape != null)
      {
        ConfigurationCache.INSTANCE.releaseEscape(escape);
      }*/
        }
        featureTable = null;
        doc = null;
        helper = null;
    }

    protected void init(XMLResource resource, Map<?, ?> options) {
        useCache = Boolean.TRUE.equals(options.get(XMLResource.OPTION_CONFIGURATION_CACHE));

        nameInfo = new NameInfo();
        declareXSI = false;
        keepDefaults = Boolean.TRUE.equals(options.get(XMLResource.OPTION_KEEP_DEFAULT_CONTENT));
        useEncodedAttributeStyle = Boolean.TRUE.equals(options.get(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE));
        declareSchemaLocationImplementation = Boolean.TRUE.equals(options.get(XMLResource.OPTION_SCHEMA_LOCATION_IMPLEMENTATION));
        declareSchemaLocation = declareSchemaLocationImplementation || Boolean.TRUE.equals(options.get(XMLResource.OPTION_SCHEMA_LOCATION));

        Object saveTypeInfoOption = options.get(XMLResource.OPTION_SAVE_TYPE_INFORMATION);
        if (saveTypeInfoOption instanceof Boolean) {
            saveTypeInfo = saveTypeInfoOption.equals(Boolean.TRUE);
            if (saveTypeInfo) {
                xmlTypeInfo =
                        new XMLTypeInfo() {
                            public boolean shouldSaveType(EClass objectType, EClassifier featureType, EStructuralFeature feature) {
                                return objectType != anyType;
                            }

                            public boolean shouldSaveType(EClass objectType, EClass featureType, EStructuralFeature feature) {
                                return true;
                            }
                        };
            }
        } else {
            saveTypeInfo = saveTypeInfoOption != null;
            if (saveTypeInfo) {
                xmlTypeInfo = (XMLTypeInfo) saveTypeInfoOption;
            }
        }

        anyType = (EClass) options.get(XMLResource.OPTION_ANY_TYPE);
        anySimpleType = (EClass) options.get(XMLResource.OPTION_ANY_SIMPLE_TYPE);
        if (anyType == null) {
            anyType = XMLTypePackage.eINSTANCE.getAnyType();
            anySimpleType = XMLTypePackage.eINSTANCE.getSimpleAnyType();
        }

        Object extendedMetaDataOption = options.get(XMLResource.OPTION_EXTENDED_META_DATA);
        if (extendedMetaDataOption instanceof Boolean) {
            if (extendedMetaDataOption.equals(Boolean.TRUE)) {
                extendedMetaData =
                        resource == null || resource.getResourceSet() == null ?
                                ExtendedMetaData.INSTANCE :
                                new BasicExtendedMetaData(resource.getResourceSet().getPackageRegistry());
            }
        } else {
            extendedMetaData = (ExtendedMetaData) options.get(XMLResource.OPTION_EXTENDED_META_DATA);
        }

        // set serialization options
        if (!toDOM) {
            declareXML = !Boolean.FALSE.equals(options.get(XMLResource.OPTION_DECLARE_XML));

            if (options.get(XMLResource.OPTION_FLUSH_THRESHOLD) instanceof Integer) {
                flushThreshold = (Integer) options.get(XMLResource.OPTION_FLUSH_THRESHOLD);
            }

            Integer lineWidth = (Integer) options.get(XMLResource.OPTION_LINE_WIDTH);
            int effectiveLineWidth = lineWidth == null ? Integer.MAX_VALUE : lineWidth;
            String publicId = null, systemId = null;
            if (resource != null && Boolean.TRUE.equals(options.get(XMLResource.OPTION_SAVE_DOCTYPE))) {
                publicId = resource.getPublicId();
                systemId = resource.getSystemId();
            }
            if (useCache) {
        /*doc = ConfigurationCache.INSTANCE.getPrinter();
        doc.reset(publicId, systemId, effectiveLineWidth, temporaryFileName);
        escape = Boolean.TRUE.equals(options.get(XMLResource.OPTION_SKIP_ESCAPE)) ? null : ConfigurationCache.INSTANCE.getEscape();*/
            } else {
                doc = new XMLString(effectiveLineWidth, publicId, systemId, temporaryFileName);
                escape = Boolean.TRUE.equals(options.get(XMLResource.OPTION_SKIP_ESCAPE)) ? null : new Escape();
            }

            if (Boolean.FALSE.equals(options.get(XMLResource.OPTION_FORMATTED))) {
                doc.setUnformatted(true);
            }

            escapeURI = Boolean.FALSE.equals(options.get(XMLResource.OPTION_SKIP_ESCAPE_URI)) ? escape : null;

            if (options.containsKey(XMLResource.OPTION_ENCODING)) {
                encoding = (String) options.get(XMLResource.OPTION_ENCODING);
            } else if (resource != null) {
                encoding = resource.getEncoding();
            }

            if (options.containsKey(XMLResource.OPTION_XML_VERSION)) {
                xmlVersion = (String) options.get(XMLResource.OPTION_XML_VERSION);
            } else if (resource != null) {
                xmlVersion = resource.getXMLVersion();
            }

            if (escape != null) {
                int maxSafeChar = MAX_UTF_MAPPABLE_CODEPOINT;
                if (encoding != null) {
                    if (encoding.equalsIgnoreCase("ASCII") || encoding.equalsIgnoreCase("US-ASCII")) {
                        maxSafeChar = MAX_ASCII_MAPPABLE_CODEPOINT;
                    } else if (encoding.equalsIgnoreCase("ISO-8859-1")) {
                        maxSafeChar = MAX_LATIN1_MAPPABLE_CODEPOINT;
                    }
                }

                escape.setMappingLimit(maxSafeChar);
                if (!"1.0".equals(xmlVersion)) {
                    escape.setAllowControlCharacters(true);
                }

                escape.setUseCDATA(Boolean.TRUE.equals(options.get(XMLResource.OPTION_ESCAPE_USING_CDATA)));
            }

            resourceEntityHandler = (ResourceEntityHandler) options.get(XMLResource.OPTION_RESOURCE_ENTITY_HANDLER);
            if (resourceEntityHandler instanceof URIHandler && !options.containsKey(XMLResource.OPTION_URI_HANDLER)) {
                Map<Object, Object> newOptions = new LinkedHashMap<Object, Object>(options);
                newOptions.put(XMLResource.OPTION_URI_HANDLER, resourceEntityHandler);
                options = newOptions;
            }
        } else {
            // DOM serialization
            if (handler instanceof XMLDOMHandler) {
                handler.setExtendedMetaData(extendedMetaData);
            }
        }
        processDanglingHREF = (String) options.get(XMLResource.OPTION_PROCESS_DANGLING_HREF);
        helper.setProcessDanglingHREF(processDanglingHREF);

        map = (XMLMap) options.get(XMLResource.OPTION_XML_MAP);
        if (map != null) {
            helper.setXMLMap(map);

            if (map.getIDAttributeName() != null) {
                idAttributeName = map.getIDAttributeName();
            }
        }

        if (resource != null) {
            eObjectToExtensionMap = resource.getEObjectToExtensionMap();
            if (eObjectToExtensionMap.isEmpty()) {
                eObjectToExtensionMap = null;
            } else if (extendedMetaData == null) {
                extendedMetaData =
                        resource.getResourceSet() == null ?
                                ExtendedMetaData.INSTANCE :
                                new BasicExtendedMetaData(resource.getResourceSet().getPackageRegistry());
            }
        }

        if (extendedMetaData != null) {
            helper.setExtendedMetaData(extendedMetaData);
            if (resource != null && resource.getContents().size() >= 1) {
                EObject root = resource.getContents().get(0);
                EClass eClass = root.eClass();

                EReference xmlnsPrefixMapFeature = extendedMetaData.getXMLNSPrefixMapFeature(eClass);
                if (xmlnsPrefixMapFeature != null) {
                    @SuppressWarnings("unchecked")
                    EMap<String, String> xmlnsPrefixMap = (EMap<String, String>) root.eGet(xmlnsPrefixMapFeature);
                    helper.setPrefixToNamespaceMap(xmlnsPrefixMap);
                }
            }
        }

        elementHandler = (ElementHandler) options.get(XMLResource.OPTION_ELEMENT_HANDLER);

        @SuppressWarnings("unchecked")
        List<Object> lookup = (List<Object>) options.get(XMLResource.OPTION_USE_CACHED_LOOKUP_TABLE);
        if (lookup != null) {
            // caching turned on by the user
            if (lookup.isEmpty()) {
                featureTable = new Lookup(map, extendedMetaData, elementHandler);
                lookup.add(featureTable);
            } else {
                featureTable = (Lookup) lookup.get(INDEX_LOOKUP);
            }
        } else {
            //no caching
            featureTable = new Lookup(map, extendedMetaData, elementHandler);
        }

        helper.setOptions(options);
    }

    public void traverse(List<? extends EObject> contents) {
        if (!toDOM && declareXML) {
            doc.add("<?xml version=\"" + xmlVersion + "\" encoding=\"" + encoding + "\"?>");
            doc.addLine();
        }

        int size = contents.size();

        // Reserve a place to insert xmlns declarations after we know what they all are.
        //
        Object mark;

        if (size == 1) {
            mark = writeTopObject(contents.get(0));
        } else {
            mark = writeTopObjects(contents);
        }
        if (!toDOM) {
            // Go back and add all the XMLNS stuff.
            //
            doc.resetToMark(mark);
        } else {
            currentNode = getGwtDOMHandler().getDocument().getDocumentElement();
        }
        addNamespaceDeclarations();
        addDoctypeInformation();
    }

    /*
     * INTERNAL: this is a specialized method to add attributes for a top/root element
     */
    protected void writeTopAttributes(EObject top) {
        if (useEncodedAttributeStyle) {
            InternalEObject container = ((InternalEObject) top).eInternalContainer();
            if (container != null) {
                EReference containmentReference = top.eContainmentFeature();
                EReference containerReference = containmentReference.getEOpposite();
                if (containerReference != null && !containerReference.isTransient()) {
                    saveEObjectSingle(top, containerReference);
                }
            }
        }
    }

    protected boolean writeTopElements(EObject top) {
        if (!useEncodedAttributeStyle) {
            InternalEObject container = ((InternalEObject) top).eInternalContainer();
            if (container != null) {
                EReference containmentReference = top.eContainmentFeature();
                EReference containerReference = containmentReference.getEOpposite();
                if (containerReference != null && !containerReference.isTransient()) {
                    saveHref(container, containerReference);
                    return true;
                }
            }
        }
        return false;
    }

    protected Object writeTopObject(EObject top) {
        EClass eClass = top.eClass();
        if (!toDOM) {
            if (extendedMetaData == null || featureTable.getDocumentRoot(eClass.getEPackage()) != eClass) {
                EStructuralFeature rootFeature = null;
                if (elementHandler != null) {
                    EClassifier eClassifier =
                            eClass == anySimpleType ?
                                    ((SimpleAnyType) top).getInstanceType() :
                                    eClass;
                    rootFeature = featureTable.getRoot(eClassifier);
                }
                String name =
                        rootFeature != null ?
                                helper.getQName(rootFeature) :
                                extendedMetaData != null && roots != null && top.eContainmentFeature() != null ?
                                        helper.getQName(top.eContainmentFeature()) :
                                        helper.getQName(eClass);
                doc.startElement(name);
                Object mark = doc.mark();
                root = top;
                saveElementID(top);
                return mark;
            } else {
                doc.startElement(null);
                root = top;
                saveFeatures(top);
                return null;
            }
        } else {
            Document document = gwtDocumentHandler.getDocument();
            if (extendedMetaData == null || featureTable.getDocumentRoot(eClass.getEPackage()) != eClass) {
                EStructuralFeature rootFeature = null;
                if (elementHandler != null) {
                    EClassifier eClassifier =
                            eClass == anySimpleType ?
                                    ((SimpleAnyType) top).getInstanceType() :
                                    eClass;
                    rootFeature = featureTable.getRoot(eClassifier);
                }
                if (rootFeature != null) {
                    helper.populateNameInfo(nameInfo, rootFeature);
                } else if (extendedMetaData != null && roots != null && top.eContainmentFeature() != null) {
                    helper.populateNameInfo(nameInfo, top.eContainmentFeature());
                } else {
                    helper.populateNameInfo(nameInfo, eClass);
                }
                if (document.getLastChild() == null) {
                    currentNode = getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                    currentNode = getGwtDOMHandler().getDocument().appendChild(currentNode);
                } else {
                    currentNode = currentNode.appendChild(getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName()));
                }
                handler.recordValues(currentNode, null, null, top);
                root = top;
                saveElementID(top);
                return null;
            } else {
                root = top;
                currentNode = document;
                saveFeatures(top);
                return null;
            }
        }
    }

    protected Object writeTopObjects(List<? extends EObject> contents) {
        return writeTopObject(contents.get(0));
    }

    protected void addNamespaceDeclarations() {
        EPackage noNamespacePackage = helper.getNoNamespacePackage();
        EPackage[] packages = helper.packages();
        buffer.setLength(0);
        StringBuffer xsiSchemaLocation = buffer;
        String xsiNoNamespaceSchemaLocation = null;
        if (declareSchemaLocation) {
            Map<String, String> handledBySchemaLocationMap = Collections.emptyMap();

            if (extendedMetaData != null) {
                Resource resource = helper.getResource();
                if (resource != null && resource.getContents().size() >= 1) {
                    EObject root = getSchemaLocationRoot(resource.getContents().get(0));
                    EClass eClass = root.eClass();

                    EReference xsiSchemaLocationMapFeature = extendedMetaData.getXSISchemaLocationMapFeature(eClass);
                    if (xsiSchemaLocationMapFeature != null) {
                        @SuppressWarnings("unchecked")
                        EMap<String, String> xsiSchemaLocationMap = (EMap<String, String>) root.eGet(xsiSchemaLocationMapFeature);
                        if (!xsiSchemaLocationMap.isEmpty()) {
                            handledBySchemaLocationMap = xsiSchemaLocationMap.map();
                            declareXSI = true;
                            for (Map.Entry<String, String> entry : xsiSchemaLocationMap.entrySet()) {
                                String namespace = entry.getKey();
                                URI location = URI.createURI(entry.getValue());
                                if (namespace == null) {
                                    xsiNoNamespaceSchemaLocation = helper.deresolve(location).toString();
                                } else {
                                    if (xsiSchemaLocation.length() > 0) {
                                        xsiSchemaLocation.append(' ');
                                    }
                                    xsiSchemaLocation.append(namespace);
                                    xsiSchemaLocation.append(' ');
                                    xsiSchemaLocation.append(helper.deresolve(location).toString());
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < packages.length; i++) {
                EPackage ePackage = packages[i];

                String javaImplementationLocation = null;
                if (declareSchemaLocationImplementation) {
                    // First try to see if this package's implementation class has an eInstance.
                    //
                    try {
//            Field field = ePackage.getClass().getField("eINSTANCE");
//            javaImplementationLocation = "java://" + field.getDeclaringClass().getName();
                    } catch (Exception exception) {
                        // If there is no field, then we can't do this.
                    }
                }

                if (noNamespacePackage == ePackage) {
                    if (ePackage.eResource() != null && !handledBySchemaLocationMap.containsKey(null)) {
                        declareXSI = true;
                        if (javaImplementationLocation != null) {
                            xsiNoNamespaceSchemaLocation = javaImplementationLocation;
                        } else {
                            xsiNoNamespaceSchemaLocation = helper.getHREF(ePackage);
                            if (xsiNoNamespaceSchemaLocation != null && xsiNoNamespaceSchemaLocation.endsWith("#/")) {
                                xsiNoNamespaceSchemaLocation = xsiNoNamespaceSchemaLocation.substring(0, xsiNoNamespaceSchemaLocation.length() - 2);
                            }
                        }
                    }
                } else {
                    Resource resource = ePackage.eResource();
                    if (resource != null) {
                        String nsURI = extendedMetaData == null ? ePackage.getNsURI() : extendedMetaData.getNamespace(ePackage);
                        if (!handledBySchemaLocationMap.containsKey(nsURI)) {
                            URI uri = resource.getURI();
                            if (javaImplementationLocation != null || (uri == null ? nsURI != null : !uri.toString().equals(nsURI))) {
                                declareXSI = true;
                                if (xsiSchemaLocation.length() > 0) {
                                    xsiSchemaLocation.append(' ');
                                }
                                xsiSchemaLocation.append(nsURI);
                                xsiSchemaLocation.append(' ');

                                String location = javaImplementationLocation == null ? helper.getHREF(ePackage) : javaImplementationLocation;
                                location = convertURI(location);
                                if (location.endsWith("#/")) {
                                    location = location.substring(0, location.length() - 2);
                                    if (uri != null && uri.hasFragment()) {
                                        location += "#" + uri.fragment();
                                    }
                                }
                                xsiSchemaLocation.append(location);
                            }
                        }
                    }
                }
            }
        }

        if (declareXSI) {
            if (!toDOM) {
                doc.addAttribute(XSI_XMLNS, XMLResource.XSI_URI);
            } else {
                getGwtDOMHandler().setAttributeNS(currentNode, ExtendedMetaData.XMLNS_URI, XSI_XMLNS, XMLResource.XSI_URI);
            }
        }

        for (int i = 0; i < packages.length; i++) {
            EPackage ePackage = packages[i];
            if (ePackage != noNamespacePackage &&
                    ePackage != XMLNamespacePackage.eINSTANCE &&
                    !ExtendedMetaData.XMLNS_URI.equals(ePackage.getNsURI())) {
                String nsURI = extendedMetaData == null ? ePackage.getNsURI() : extendedMetaData.getNamespace(ePackage);
                if (ePackage == xmlSchemaTypePackage) {
                    nsURI = XMLResource.XML_SCHEMA_URI;
                }
                if (nsURI != null && !isDuplicateURI(nsURI)) {
                    List<String> nsPrefixes = helper.getPrefixes(ePackage);
                    for (String nsPrefix : nsPrefixes) {
                        if (!toDOM) {
                            if (nsPrefix != null && nsPrefix.length() > 0) {
                                if (!declareXSI || !"xsi".equals(nsPrefix)) {
                                    doc.addAttributeNS(XMLResource.XML_NS, nsPrefix, nsURI);
                                }
                            } else {
                                doc.addAttribute(XMLResource.XML_NS, nsURI);
                            }
                        } else {
                            if (nsPrefix != null && nsPrefix.length() > 0) {
                                if (!declareXSI || !"xsi".equals(nsPrefix)) {
                                    getGwtDOMHandler().setAttributeNS(currentNode, ExtendedMetaData.XMLNS_URI, XMLResource.XML_NS + ":" + nsPrefix, nsURI);
                                }
                            } else {
                                getGwtDOMHandler().setAttributeNS(currentNode, ExtendedMetaData.XMLNS_URI, XMLResource.XML_NS, nsURI);
                            }
                        }
                    }
                }
            }
        }

        if (xsiSchemaLocation.length() > 0) {
            if (!toDOM) {
                doc.addAttribute(XSI_SCHEMA_LOCATION, xsiSchemaLocation.toString());
            } else {
                getGwtDOMHandler().setAttributeNS(currentNode, XMLResource.XSI_URI, XSI_SCHEMA_LOCATION, xsiSchemaLocation.toString());
            }
        }

        if (xsiNoNamespaceSchemaLocation != null) {
            if (!toDOM) {
                doc.addAttribute(XSI_NO_NAMESPACE_SCHEMA_LOCATION, xsiNoNamespaceSchemaLocation);
            } else {
                getGwtDOMHandler().setAttributeNS(currentNode, XMLResource.XSI_URI, XSI_NO_NAMESPACE_SCHEMA_LOCATION, xsiNoNamespaceSchemaLocation);
            }
        }
    }

    protected void addDoctypeInformation() {
        if (resourceEntityHandler != null) {
            if (!toDOM) {
                for (Map.Entry<String, String> entry : resourceEntityHandler.getNameToValueMap().entrySet()) {
                    doc.addEntity(entry.getKey(), entry.getValue());
                }
            } else {
                // Entities aren't supported for DOM.
            }
        }
    }

    protected EObject getSchemaLocationRoot(EObject eObject) {
        return eObject;
    }

    public boolean isDuplicateURI(String nsURI) {
        return false;
    }

    public char[] toChar() {
        int size = doc.getLength();
        char[] output = new char[size];
        doc.getChars(output, 0);
        return output;
    }

    protected void saveElement(InternalEObject o, EStructuralFeature f) {
        if (o.eDirectResource() != null || o.eIsProxy()) {
            saveHref(o, f);
        } else {
            saveElement((EObject) o, f);
        }
    }

    protected void saveElement(EObject o, EStructuralFeature f) {
        EClass eClass = o.eClass();
        EClassifier eType = f.getEType();

        if (extendedMetaData != null && eClass != eType) {
            // Check if it's an anonymous type.
            //
            String name = extendedMetaData.getName(eClass);
            if (name.endsWith("_._type")) {
                String elementName = name.substring(0, name.indexOf("_._"));
                String prefix = helper.getPrefix(eClass.getEPackage());
                if (!"".equals(prefix)) {
                    elementName = prefix + ":" + elementName;
                }
                if (!toDOM) {
                    doc.startElement(elementName);
                } else {
                    currentNode = currentNode.appendChild(getGwtDOMHandler().createElementNS(helper.getNamespaceURI(prefix), elementName));
                    handler.recordValues(currentNode, o.eContainer(), f, o);
                }
                saveElementID(o);
                return;
            }
        }

        if (map != null) {
            XMLInfo info = map.getInfo(eClass);
            if (info != null && info.getXMLRepresentation() == XMLInfo.ELEMENT) {
                if (!toDOM) {
                    String elementName = helper.getQName(eClass);
                    doc.startElement(elementName);
                } else {
                    helper.populateNameInfo(nameInfo, eClass);
                    if (currentNode == null) {
                        currentNode = getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                        getGwtDOMHandler().getDocument().appendChild(currentNode);
                        handler.recordValues(currentNode, o.eContainer(), f, o);
                    } else {
                        currentNode = currentNode.appendChild(getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName()));
                        handler.recordValues(currentNode, o.eContainer(), f, o);
                    }
                }
                saveElementID(o);
                return;
            }
        }
        boolean isAnyType = false;
        if (o instanceof AnyType) {
            isAnyType = true;
            helper.pushContext();
            for (FeatureMap.Entry entry : ((AnyType) o).getAnyAttribute()) {
                if (ExtendedMetaData.XMLNS_URI.equals(extendedMetaData.getNamespace(entry.getEStructuralFeature()))) {
                    String uri = (String) entry.getValue();
                    helper.addPrefix(extendedMetaData.getName(entry.getEStructuralFeature()), uri == null ? "" : uri);
                }
            }
        }
        boolean shouldSaveType =
                saveTypeInfo ?
                        xmlTypeInfo.shouldSaveType(eClass, eType, f) :
                        eClass != eType &&
                                (eClass != anyType ||
                                        extendedMetaData == null ||
                                        eType != EcorePackage.Literals.EOBJECT ||
                                        extendedMetaData.getFeatureKind(f) == ExtendedMetaData.UNSPECIFIED_FEATURE);
        EDataType eDataType = null;
        if (shouldSaveType) {
            EClassifier eClassifier =
                    eClass == anySimpleType ?
                            eDataType = ((SimpleAnyType) o).getInstanceType() :
                            eClass;
            if (elementHandler != null) {
                EStructuralFeature substitutionGroup = featureTable.getSubstitutionGroup(f, eClassifier);
                if (substitutionGroup != null) {
                    f = substitutionGroup;
                    shouldSaveType = substitutionGroup.getEType() != eClassifier;
                }
            }
        }

        if (!toDOM) {
            String featureName = helper.getQName(f);
            doc.startElement(featureName);
        } else {
            helper.populateNameInfo(nameInfo, f);
            if (currentNode == null) {
                // this is a root element
                currentNode = getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                getGwtDOMHandler().getDocument().appendChild(currentNode);
                handler.recordValues(currentNode, o.eContainer(), f, o);
            } else {
                currentNode = currentNode.appendChild(getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName()));
                handler.recordValues(currentNode, o.eContainer(), f, o);
            }
        }

        if (shouldSaveType) {
            if (eDataType != null) {
                saveTypeAttribute(eDataType);
            } else {
                saveTypeAttribute(eClass);
            }
        }

        saveElementID(o);
        if (isAnyType) {
            helper.popContext();
        }
    }

    protected void saveTypeAttribute(EClass eClass) {
        declareXSI = true;
        if (!toDOM) {
            doc.addAttribute(XSI_TYPE_NS, helper.getQName(eClass));
        } else {
            helper.populateNameInfo(nameInfo, eClass);
            getGwtDOMHandler().setAttributeNS(currentNode, ExtendedMetaData.XSI_URI, XSI_TYPE_NS, nameInfo.getQualifiedName());
        }
    }

    protected void saveTypeAttribute(EDataType eDataType) {
        declareXSI = true;
        if (!toDOM) {
            doc.addAttribute(XSI_TYPE_NS, helper.getQName(eDataType));
        } else {
            helper.populateNameInfo(nameInfo, eDataType);
            getGwtDOMHandler().setAttributeNS(currentNode, XMLResource.XSI_URI, XSI_TYPE_NS, nameInfo.getQualifiedName());
        }
    }

    protected boolean shouldSaveFeature(EObject o, EStructuralFeature f) {
        try {
            return o.eIsSet(f) || keepDefaults && f.getDefaultValueLiteral() != null;
        } catch (Exception e) {
            logError("Cannot save feature (XMLSave#shouldSaveFeature).", e);
            return false;
        }
    }

    protected boolean saveFeatures(EObject o) {
        EClass eClass = o.eClass();
        int contentKind = extendedMetaData == null ? ExtendedMetaData.UNSPECIFIED_CONTENT : extendedMetaData.getContentKind(eClass);
        if (!toDOM) {
            switch (contentKind) {
                case ExtendedMetaData.MIXED_CONTENT:
                case ExtendedMetaData.SIMPLE_CONTENT: {
                    doc.setMixed(true);
                    break;
                }
            }
        }

        if (o == root) {
            writeTopAttributes(root);
        }

        EStructuralFeature[] features = featureTable.getFeatures(eClass);
        int[] featureKinds = featureTable.getKinds(eClass, features);
        int[] elementFeatures = null;
        int elementCount = 0;

        String content = null;

        // Process XML attributes
        LOOP:
        for (int i = 0; i < features.length; i++) {
            int kind = featureKinds[i];
            EStructuralFeature f = features[i];
            if (kind != TRANSIENT && shouldSaveFeature(o, f)) {
                switch (kind) {
                    case DATATYPE_ELEMENT_SINGLE: {
                        if (contentKind == ExtendedMetaData.SIMPLE_CONTENT) {
                            content = getDataTypeElementSingleSimple(o, f);
                            continue LOOP;
                        }
                        break;
                    }
                    case DATATYPE_SINGLE: {
                        saveDataTypeSingle(o, f);
                        continue LOOP;
                    }
                    case DATATYPE_SINGLE_NILLABLE: {
                        if (!isNil(o, f)) {
                            saveDataTypeSingle(o, f);
                            continue LOOP;
                        }
                        break;
                    }
                    case OBJECT_ATTRIBUTE_SINGLE: {
                        saveEObjectSingle(o, f);
                        continue LOOP;
                    }
                    case OBJECT_ATTRIBUTE_MANY: {
                        saveEObjectMany(o, f);
                        continue LOOP;
                    }
                    case OBJECT_ATTRIBUTE_IDREF_SINGLE: {
                        saveIDRefSingle(o, f);
                        continue LOOP;
                    }
                    case OBJECT_ATTRIBUTE_IDREF_MANY: {
                        saveIDRefMany(o, f);
                        continue LOOP;
                    }
                    case OBJECT_HREF_SINGLE_UNSETTABLE: {
                        if (isNil(o, f)) {
                            break;
                        }
                        // it's intentional to keep going
                    }
                    case OBJECT_HREF_SINGLE: {
                        if (useEncodedAttributeStyle) {
                            saveEObjectSingle(o, f);
                            continue LOOP;
                        } else {
                            switch (sameDocSingle(o, f)) {
                                case SAME_DOC: {
                                    saveIDRefSingle(o, f);
                                    continue LOOP;
                                }
                                case CROSS_DOC: {
                                    break;
                                }
                                default: {
                                    continue LOOP;
                                }
                            }
                        }
                        break;
                    }
                    case OBJECT_HREF_MANY_UNSETTABLE: {
                        if (isEmpty(o, f)) {
                            saveManyEmpty(o, f);
                            continue LOOP;
                        }
                        // It's intentional to keep going.
                    }
                    case OBJECT_HREF_MANY: {
                        if (useEncodedAttributeStyle) {
                            saveEObjectMany(o, f);
                            continue LOOP;
                        } else {
                            switch (sameDocMany(o, f)) {
                                case SAME_DOC: {
                                    saveIDRefMany(o, f);
                                    continue LOOP;
                                }
                                case CROSS_DOC: {
                                    break;
                                }
                                default: {
                                    continue LOOP;
                                }
                            }
                        }
                        break;
                    }
                    case OBJECT_ELEMENT_SINGLE_UNSETTABLE:
                    case OBJECT_ELEMENT_SINGLE: {
                        if (contentKind == ExtendedMetaData.SIMPLE_CONTENT) {
                            content = getElementReferenceSingleSimple(o, f);
                            continue LOOP;
                        }
                        break;
                    }
                    case OBJECT_ELEMENT_MANY: {
                        if (contentKind == ExtendedMetaData.SIMPLE_CONTENT) {
                            content = getElementReferenceManySimple(o, f);
                            continue LOOP;
                        }
                        break;
                    }
                    case OBJECT_ELEMENT_IDREF_SINGLE_UNSETTABLE:
                    case OBJECT_ELEMENT_IDREF_SINGLE: {
                        if (contentKind == ExtendedMetaData.SIMPLE_CONTENT) {
                            content = getElementIDRefSingleSimple(o, f);
                            continue LOOP;
                        }
                        break;
                    }
                    case OBJECT_ELEMENT_IDREF_MANY: {
                        if (contentKind == ExtendedMetaData.SIMPLE_CONTENT) {
                            content = getElementIDRefManySimple(o, f);
                            continue LOOP;
                        }
                        break;
                    }
                    case DATATYPE_ATTRIBUTE_MANY: {
                        break;
                    }
                    case OBJECT_CONTAIN_MANY_UNSETTABLE:
                    case DATATYPE_MANY: {
                        if (isEmpty(o, f)) {
                            saveManyEmpty(o, f);
                            continue LOOP;
                        }
                        break;
                    }
                    case OBJECT_CONTAIN_SINGLE_UNSETTABLE:
                    case OBJECT_CONTAIN_SINGLE:
                    case OBJECT_CONTAIN_MANY:
                    case ELEMENT_FEATURE_MAP: {
                        break;
                    }
                    case ATTRIBUTE_FEATURE_MAP: {
                        saveAttributeFeatureMap(o, f);
                        continue LOOP;
                    }
                    default: {
                        continue LOOP;
                    }
                }

                // We only get here if we should do this.
                //
                if (elementFeatures == null) {
                    elementFeatures = new int[features.length];
                }
                elementFeatures[elementCount++] = i;
            }
        }

        processAttributeExtensions(o);

        if (elementFeatures == null) {
            if (content == null) {
                content = getContent(o, features);
            }

            if (content == null) {
                if (o == root && writeTopElements(root)) {
                    endSaveFeatures(o, 0, null);
                    return true;
                } else {
                    endSaveFeatures(o, EMPTY_ELEMENT, null);
                    return false;
                }
            } else {
                endSaveFeatures(o, CONTENT_ELEMENT, content);
                return true;
            }
        }

        if (o == root) {
            writeTopElements(root);
        }

        // Process XML elements
        for (int i = 0; i < elementCount; i++) {
            int kind = featureKinds[elementFeatures[i]];
            EStructuralFeature f = features[elementFeatures[i]];
            switch (kind) {
                case DATATYPE_SINGLE_NILLABLE: {
                    saveNil(o, f);
                    break;
                }
                case ELEMENT_FEATURE_MAP: {
                    saveElementFeatureMap(o, f);
                    break;
                }
                case DATATYPE_MANY: {
                    saveDataTypeMany(o, f);
                    break;
                }
                case DATATYPE_ATTRIBUTE_MANY: {
                    saveDataTypeAttributeMany(o, f);
                    break;
                }
                case DATATYPE_ELEMENT_SINGLE: {
                    saveDataTypeElementSingle(o, f);
                    break;
                }
                case OBJECT_CONTAIN_SINGLE_UNSETTABLE: {
                    if (isNil(o, f)) {
                        saveNil(o, f);
                        break;
                    }
                    // it's intentional to keep going
                }
                case OBJECT_CONTAIN_SINGLE: {
                    saveContainedSingle(o, f);
                    break;
                }
                case OBJECT_CONTAIN_MANY_UNSETTABLE:
                case OBJECT_CONTAIN_MANY: {
                    saveContainedMany(o, f);
                    break;
                }
                case OBJECT_HREF_SINGLE_UNSETTABLE: {
                    if (isNil(o, f)) {
                        saveNil(o, f);
                        break;
                    }
                    // it's intentional to keep going
                }
                case OBJECT_HREF_SINGLE: {
                    saveHRefSingle(o, f);
                    break;
                }
                case OBJECT_HREF_MANY_UNSETTABLE:
                case OBJECT_HREF_MANY: {
                    saveHRefMany(o, f);
                    break;
                }
                case OBJECT_ELEMENT_SINGLE_UNSETTABLE: {
                    if (isNil(o, f)) {
                        saveNil(o, f);
                        break;
                    }
                    // it's intentional to keep going
                }
                case OBJECT_ELEMENT_SINGLE: {
                    saveElementReferenceSingle(o, f);
                    break;
                }
                case OBJECT_ELEMENT_MANY: {
                    saveElementReferenceMany(o, f);
                    break;
                }
                case OBJECT_ELEMENT_IDREF_SINGLE_UNSETTABLE: {
                    if (isNil(o, f)) {
                        saveNil(o, f);
                        break;
                    }
                    // it's intentional to keep going
                }
                case OBJECT_ELEMENT_IDREF_SINGLE: {
                    saveElementIDRefSingle(o, f);
                    break;
                }
                case OBJECT_ELEMENT_IDREF_MANY: {
                    saveElementIDRefMany(o, f);
                    break;
                }
            }
        }
        endSaveFeatures(o, 0, null);
        return true;
    }

    protected void endSaveFeatures(EObject o, int elementType, String content) {
        if (processElementExtensions(o)) {
            if (!toDOM) {
                doc.endElement();
            }
        } else {
            switch (elementType) {
                case EMPTY_ELEMENT: {
                    if (!toDOM) {
                        doc.endEmptyElement();
                    }
                    break;
                }
                case CONTENT_ELEMENT: {
                    if (!toDOM) {
                        doc.endContentElement(content);
                    }
                    break;
                }
                default: {
                    if (!toDOM) {
                        doc.endElement();
                    }
                    break;
                }
            }
        }
        if (toDOM) {
            currentNode = currentNode.getParentNode();
        }
    }

    /**
     * Returns true if there were extensions for the specified object.
     */
    protected boolean processElementExtensions(EObject object) {
        if (eObjectToExtensionMap != null) {
            AnyType anyType = eObjectToExtensionMap.get(object);
            return anyType != null && saveElementFeatureMap(anyType, XMLTypePackage.eINSTANCE.getAnyType_Mixed());
        } else {
            return false;
        }
    }

    /**
     *
     */
    protected void processAttributeExtensions(EObject object) {
        if (eObjectToExtensionMap != null) {
            AnyType anyType = eObjectToExtensionMap.get(object);
            if (anyType != null) {
                saveAttributeFeatureMap(anyType, XMLTypePackage.eINSTANCE.getAnyType_AnyAttribute());
            }
        }
    }

    protected void saveDataTypeSingle(EObject o, EStructuralFeature f) {
        Object value = helper.getValue(o, f);
        String svalue = getDatatypeValue(value, f, true);
        if (svalue != null) {
            if (!toDOM) {
                doc.addAttribute(helper.getQName(f), svalue);
            } else {
                helper.populateNameInfo(nameInfo, f);
                Attr attr = getGwtDOMHandler().createAttributeNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                attr.setNodeValue(svalue);
                getGwtDOMHandler().setAttributeNodeNS(currentNode, attr);
                handler.recordValues(attr, o, f, value);
            }
        }
    }

    protected boolean isNil(EObject o, EStructuralFeature f) {
        return helper.getValue(o, f) == null;
    }

    protected boolean isEmpty(EObject o, EStructuralFeature f) {
        return ((List<?>) helper.getValue(o, f)).isEmpty();
    }

    protected void saveNil(EObject o, EStructuralFeature f) {
        if (!toDOM) {
            saveNil(f);
        } else {
            declareXSI = true;
            helper.populateNameInfo(nameInfo, f);
            Element elem = getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
            getGwtDOMHandler().setAttributeNS(elem, ExtendedMetaData.XSI_URI, XSI_NIL, "true");
            currentNode.appendChild(elem);
            handler.recordValues(currentNode.getLastChild(), o, f, null);
        }
    }

    protected void saveNil(EStructuralFeature f) {
        declareXSI = true;
        doc.saveNilElement(helper.getQName(f));
    }

    protected void saveManyEmpty(EObject o, EStructuralFeature f) {
        if (!toDOM) {
            saveManyEmpty(f);
        } else {
            helper.populateNameInfo(nameInfo, f);
            Attr attr = getGwtDOMHandler().createAttributeNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
            getGwtDOMHandler().setAttributeNodeNS(currentNode, attr);
            handler.recordValues(attr, o, f, null);
        }
    }

    protected void saveManyEmpty(EStructuralFeature f) {
        doc.addAttribute(helper.getQName(f), "");
    }

    protected void saveDataTypeMany(EObject o, EStructuralFeature f) {
        List<?> values = (List<?>) helper.getValue(o, f);
        int size = values.size();
        if (size > 0) {
            // for performance reasons saveNil and saveElement are not used
            if (!toDOM) {
                EDataType d = (EDataType) f.getEType();
                EPackage ePackage = d.getEPackage();
                EFactory fac = ePackage.getEFactoryInstance();
                String name = helper.getQName(f);
                for (int i = 0; i < size; ++i) {
                    Object value = values.get(i);
                    if (value == null) {
                        doc.startElement(name);
                        doc.addAttribute(XSI_NIL, "true");
                        doc.endEmptyElement();
                        declareXSI = true;
                    } else {
                        String svalue = helper.convertToString(fac, d, value);
                        if (escape != null) {
                            svalue = escape.convert(svalue);
                        }
                        doc.saveDataValueElement(name, svalue);
                    }
                }
            } else {
                EDataType d = (EDataType) f.getEType();
                EPackage ePackage = d.getEPackage();
                EFactory fac = ePackage.getEFactoryInstance();
                helper.populateNameInfo(nameInfo, f);
                for (int i = 0; i < size; ++i) {
                    Object value = values.get(i);
                    if (value == null) {
                        Element elem = getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                        getGwtDOMHandler().setAttributeNS(elem, XMLResource.XSI_URI, XSI_NIL, "true");
                        currentNode.appendChild(elem);
                        handler.recordValues(elem, o, f, null);
                        declareXSI = true;
                    } else {
                        Element elem = getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                        Node text = getGwtDOMHandler().createTextNode(helper.convertToString(fac, d, value));
                        elem.appendChild(text);
                        currentNode.appendChild(elem);
                        handler.recordValues(elem, o, f, value);
                        handler.recordValues(text, o, f, value);
                    }
                }
            }
        }
    }

    protected void saveDataTypeAttributeMany(EObject o, EStructuralFeature f) {
        List<?> values = (List<?>) helper.getValue(o, f);
        int size = values.size();
        if (size > 0) {
            EDataType d = (EDataType) f.getEType();
            EPackage ePackage = d.getEPackage();
            EFactory fac = ePackage.getEFactoryInstance();
            StringBuffer stringValues = new StringBuffer();
            for (int i = 0; i < size; ++i) {
                Object value = values.get(i);
                if (value != null) {
                    String svalue = helper.convertToString(fac, d, value);
                    if (escape != null) {
                        svalue = escape.convert(svalue);
                    }
                    if (i > 0) {
                        stringValues.append(' ');
                    }
                    stringValues.append(svalue);
                }
            }
            if (!toDOM) {
                String name = helper.getQName(f);
                doc.startAttribute(name);
                doc.addAttributeContent(stringValues.toString());
                doc.endAttribute();
            } else {
                helper.populateNameInfo(nameInfo, f);
                String value = stringValues.toString();
                Attr attr = getGwtDOMHandler().createAttributeNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                attr.setNodeValue(value);
                getGwtDOMHandler().setAttributeNodeNS(currentNode, attr);
                handler.recordValues(attr, o, f, value);
            }
        }
    }

    protected void saveEObjectSingle(EObject o, EStructuralFeature f) {
        EObject value = (EObject) helper.getValue(o, f);
        if (value != null) {
            String id = helper.getHREF(value);
            if (id != null) {
                id = convertURI(id);
                buffer.setLength(0);
                if (!id.startsWith("#")) {
                    EClass eClass = value.eClass();
                    EClass expectedType = (EClass) f.getEType();
                    if (saveTypeInfo ? xmlTypeInfo.shouldSaveType(eClass, expectedType, f) : eClass != expectedType && (expectedType.isAbstract() || f.getEGenericType().getETypeParameter() != null)) {
                        buffer.append(helper.getQName(eClass));
                        buffer.append(' ');
                    }
                }
                buffer.append(id);
                if (!toDOM) {
                    String name = helper.getQName(f);
                    doc.startAttribute(name);
                    doc.addAttributeContent(buffer.toString());
                    doc.endAttribute();
                } else {
                    helper.populateNameInfo(nameInfo, f);
                    Attr attr = getGwtDOMHandler().createAttributeNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                    attr.setNodeValue(buffer.toString());
                    getGwtDOMHandler().setAttributeNodeNS(currentNode, attr);
                    handler.recordValues(attr, o, f, value);
                }
            }
        }
    }

    protected void saveEObjectMany(EObject o, EStructuralFeature f) {
        @SuppressWarnings("unchecked")
        InternalEList<? extends EObject> values = (InternalEList<? extends EObject>) helper.getValue(o, f);

        if (!values.isEmpty()) {
            buffer.setLength(0);
            boolean failure = false;
            for (Iterator<? extends EObject> i = values.basicIterator(); ; ) {
                EObject value = i.next();
                String id = helper.getHREF(value);
                if (id == null) {
                    failure = true;
                    if (!i.hasNext()) {
                        break;
                    }
                } else {
                    id = convertURI(id);
                    if (!id.startsWith("#")) {
                        EClass eClass = value.eClass();
                        EClass expectedType = (EClass) f.getEType();
                        if (saveTypeInfo ? xmlTypeInfo.shouldSaveType(eClass, expectedType, f) : eClass != expectedType && (expectedType.isAbstract() || f.getEGenericType().getETypeParameter() != null)) {
                            buffer.append(helper.getQName(eClass));
                            buffer.append(' ');
                        }
                    }
                    buffer.append(id);
                    if (i.hasNext()) {
                        buffer.append(' ');
                    } else {
                        break;
                    }
                }
            }

            String string = buffer.toString();
            if (!failure || (string = string.trim()).length() != 0) {
                if (!toDOM) {
                    String name = helper.getQName(f);
                    doc.startAttribute(name);
                    doc.addAttributeContent(string);
                    doc.endAttribute();
                } else {
                    helper.populateNameInfo(nameInfo, f);
                    Attr attr = getGwtDOMHandler().createAttributeNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                    attr.setNodeValue(string);
                    getGwtDOMHandler().setAttributeNodeNS(currentNode, attr);
                    handler.recordValues(attr, o, f, values);
                }
            }
        }
    }

    protected void saveIDRefSingle(EObject o, EStructuralFeature f) {
        EObject value = (EObject) helper.getValue(o, f);
        if (value != null) {
            String id = helper.getIDREF(value);
            if (id != null) {
                if (!toDOM) {
                    String name = helper.getQName(f);
                    doc.addAttribute(name, id);
                } else {
                    helper.populateNameInfo(nameInfo, f);
                    Attr attr = getGwtDOMHandler().createAttributeNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                    attr.setNodeValue(id);
                    getGwtDOMHandler().setAttributeNodeNS(currentNode, attr);
                    handler.recordValues(attr, o, f, value);
                }
            }
        }
    }

    protected void saveIDRefMany(EObject o, EStructuralFeature f) {
        @SuppressWarnings("unchecked")
        InternalEList<? extends EObject> values = (InternalEList<? extends EObject>) helper.getValue(o, f);
        if (!values.isEmpty()) {
            buffer.setLength(0);
            StringBuffer ids = buffer;
            boolean failure = false;
            for (Iterator<? extends EObject> i = values.basicIterator(); ; ) {
                EObject value = i.next();
                String id = helper.getIDREF(value);
                if (id == null) {
                    failure = true;
                    if (!i.hasNext()) {
                        break;
                    }
                } else {
                    ids.append(id);
                    if (i.hasNext()) {
                        ids.append(' ');
                    } else {
                        break;
                    }
                }
            }
            String idsString = ids.toString();
            if (!failure || (idsString = idsString.trim()).length() != 0) {
                if (!toDOM) {
                    String name = helper.getQName(f);
                    doc.addAttribute(name, idsString);
                } else {
                    helper.populateNameInfo(nameInfo, f);
                    Attr attr = getGwtDOMHandler().createAttributeNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                    attr.setNodeValue(idsString);
                    getGwtDOMHandler().setAttributeNodeNS(currentNode, attr);
                    handler.recordValues(attr, o, f, values);
                }
            }
        }
    }

    protected void saveElementReference(EObject remote, EStructuralFeature f) {
        String href = helper.getHREF(remote);
        if (href != null) {
            href = convertURI(href);
            EClass eClass = remote.eClass();
            EClass expectedType = (EClass) f.getEType();
            boolean shouldSaveType =
                    saveTypeInfo ?
                            xmlTypeInfo.shouldSaveType(eClass, expectedType, f) :
                            eClass != expectedType && (expectedType.isAbstract() || f.getEGenericType().getETypeParameter() != null);
            if (elementHandler != null && shouldSaveType) {
                EStructuralFeature substitutionGroup = featureTable.getSubstitutionGroup(f, eClass);
                if (substitutionGroup != null) {
                    f = substitutionGroup;
                    shouldSaveType = substitutionGroup.getEType() != eClass;
                }
            }
            if (!toDOM) {
                doc.startElement(helper.getQName(f));
            } else {
                helper.populateNameInfo(nameInfo, f);
                Element elem = getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getNamespaceURI() + ":" + nameInfo.getQualifiedName());
                Node text = getGwtDOMHandler().createTextNode(href);
                elem.appendChild(text);
                currentNode = currentNode.appendChild(elem);
                handler.recordValues(elem, remote.eContainer(), f, remote);
                handler.recordValues(text, remote.eContainer(), f, remote);
            }
            if (shouldSaveType) {
                saveTypeAttribute(eClass);
            }
            if (!toDOM) {
                doc.endContentElement(href);
            } else {
                currentNode = currentNode.getParentNode();
            }
        }
    }

    protected void saveElementReferenceSingle(EObject o, EStructuralFeature f) {
        EObject value = (EObject) helper.getValue(o, f);
        if (value != null) {
            saveElementReference(value, f);
        }
    }

    protected void saveElementReferenceMany(EObject o, EStructuralFeature f) {
        @SuppressWarnings("unchecked")
        InternalEList<? extends EObject> values = (InternalEList<? extends EObject>) helper.getValue(o, f);
        int size = values.size();
        for (int i = 0; i < size; i++) {
            saveElementReference(values.basicGet(i), f);
        }
    }

    protected String getElementReferenceSingleSimple(EObject o, EStructuralFeature f) {
        EObject value = (EObject) helper.getValue(o, f);
        String svalue = helper.getHREF(value);
        if (svalue != null) {
            svalue = convertURI(svalue);

            if (toDOM) {
                Node text = getGwtDOMHandler().createTextNode(svalue);
                currentNode.appendChild(text);
                handler.recordValues(text, o, f, value);
            }
        }
        return svalue;
    }

    protected String getElementReferenceManySimple(EObject o, EStructuralFeature f) {
        @SuppressWarnings("unchecked")
        InternalEList<? extends EObject> values = (InternalEList<? extends EObject>) helper.getValue(o, f);
        buffer.setLength(0);
        StringBuffer result = buffer;
        int size = values.size();
        String href = null;
        boolean failure = false;
        for (int i = 0; i < size; i++) {
            href = helper.getHREF(values.basicGet(i));
            if (href == null) {
                failure = true;
            } else {
                href = convertURI(href);
                result.append(href);
                result.append(' ');
            }
        }
        String svalue = result.substring(0, result.length() - 1);
        if (failure && (svalue = svalue.trim()).length() == 0) {
            return null;
        } else {
            if (toDOM) {
                Node text = getGwtDOMHandler().createTextNode(svalue);
                currentNode.appendChild(text);
                handler.recordValues(text, o, f, values);
            }
            return svalue;
        }
    }

    protected void saveElementIDRef(EObject o, EObject target, EStructuralFeature f) {
        if (!toDOM) {
            saveElementIDRef(target, f);
        } else {
            String id = helper.getIDREF(target);
            if (id != null) {
                helper.populateNameInfo(nameInfo, f);
                Element elem = getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                Node text = getGwtDOMHandler().createTextNode(id);
                elem.appendChild(text);
                currentNode.appendChild(elem);
                handler.recordValues(elem, o, f, target);
                handler.recordValues(text, o, f, target);
            }
        }
    }

    protected void saveElementIDRef(EObject target, EStructuralFeature f) {
        String name = helper.getQName(f);
        String id = helper.getIDREF(target);
        if (id != null) {
            doc.saveDataValueElement(name, id);
        }
    }

    protected void saveElementIDRefSingle(EObject o, EStructuralFeature f) {
        EObject value = (EObject) helper.getValue(o, f);
        if (value != null) {
            saveElementIDRef(o, value, f);
        }
    }

    protected void saveElementIDRefMany(EObject o, EStructuralFeature f) {
        @SuppressWarnings("unchecked")
        InternalEList<? extends EObject> values = (InternalEList<? extends EObject>) helper.getValue(o, f);
        int size = values.size();
        for (int i = 0; i < size; i++) {
            saveElementIDRef(o, values.basicGet(i), f);
        }
    }

    protected String getElementIDRefSingleSimple(EObject o, EStructuralFeature f) {
        EObject value = (EObject) helper.getValue(o, f);
        String svalue = helper.getIDREF(value);
        if (svalue == null) {
            return null;
        } else {
            if (toDOM) {
                Node text = getGwtDOMHandler().createTextNode(svalue);
                currentNode.appendChild(text);
                handler.recordValues(text, o, f, value);
            }
            return svalue;
        }
    }

    protected String getElementIDRefManySimple(EObject o, EStructuralFeature f) {
        @SuppressWarnings("unchecked")
        InternalEList<? extends EObject> values = (InternalEList<? extends EObject>) helper.getValue(o, f);
        buffer.setLength(0);
        StringBuffer result = buffer;
        boolean failure = false;
        for (int i = 0, size = values.size(); i < size; i++) {
            String idref = helper.getIDREF(values.basicGet(i));
            if (idref == null) {
                failure = true;
            } else {
                result.append(idref);
                result.append(' ');
            }
        }
        String svalue = result.substring(0, result.length() - 1);
        if (failure && (svalue = svalue.trim()).length() == 0) {
            return null;
        } else {
            if (toDOM) {
                Node text = getGwtDOMHandler().createTextNode(svalue);
                currentNode.appendChild(text);
                handler.recordValues(text, o, f, values);
            }
            return svalue;
        }
    }

    protected void saveHref(EObject remote, EStructuralFeature f) {
        String href = helper.getHREF(remote);
        if (href != null) {
            href = convertURI(href);
            EClass eClass = remote.eClass();
            EClass expectedType = (EClass) f.getEType();
            boolean shouldSaveType =
                    saveTypeInfo ?
                            xmlTypeInfo.shouldSaveType(eClass, expectedType, f) :
                            eClass != expectedType && (expectedType.isAbstract() || f.getEGenericType().getETypeParameter() != null);
            if (elementHandler != null) {
                EStructuralFeature substitutionGroup = featureTable.getSubstitutionGroup(f, eClass);
                if (substitutionGroup != null) {
                    f = substitutionGroup;
                    shouldSaveType = substitutionGroup.getEType() != eClass;
                }
            }
            if (!toDOM) {
                String name = helper.getQName(f);
                doc.startElement(name);
            } else {
                helper.populateNameInfo(nameInfo, f);
                Element elem = getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                currentNode = currentNode.appendChild(elem);
                handler.recordValues(elem, remote.eContainer(), f, remote);
            }
            if (shouldSaveType) {
                saveTypeAttribute(eClass);
            }
            if (!toDOM) {
                doc.addAttribute(XMLResource.HREF, href);
                if (eObjectToExtensionMap != null) {
                    processAttributeExtensions(remote);
                    if (processElementExtensions(remote)) {
                        doc.endElement();
                    } else {
                        doc.endEmptyElement();
                    }
                } else {
                    doc.endEmptyElement();
                }
            } else {
                getGwtDOMHandler().setAttributeNS(currentNode, null, XMLResource.HREF, href);
                if (eObjectToExtensionMap != null) {
                    processAttributeExtensions(remote);
                    processElementExtensions(remote);
                }
                currentNode = currentNode.getParentNode();
            }
        }
    }

    protected void saveHRefSingle(EObject o, EStructuralFeature f) {
        EObject value = (EObject) helper.getValue(o, f);
        if (value != null) {
            saveHref(value, f);
        }
    }

    protected void saveHRefMany(EObject o, EStructuralFeature f) {
        @SuppressWarnings("unchecked")
        InternalEList<? extends EObject> values = (InternalEList<? extends EObject>) helper.getValue(o, f);
        int size = values.size();
        for (int i = 0; i < size; i++) {
            saveHref(values.basicGet(i), f);
        }
    }

    protected void saveContainedSingle(EObject o, EStructuralFeature f) {
        InternalEObject value = (InternalEObject) helper.getValue(o, f);
        if (value != null) {
            saveElement(value, f);
        }
    }

    protected void saveContainedMany(EObject o, EStructuralFeature f) {
        @SuppressWarnings("unchecked")
        List<? extends InternalEObject> values =
                ((InternalEList<? extends InternalEObject>) helper.getValue(o, f)).basicList();
        int size = values.size();
        for (int i = 0; i < size; i++) {
            InternalEObject value = values.get(i);
            if (value != null) {
                saveElement(value, f);
            }
        }
    }

    protected void saveFeatureMapElementReference(EObject o, EReference f) {
        saveElementReference(o, f);
    }

    protected boolean saveElementFeatureMap(EObject o, EStructuralFeature f) {
        @SuppressWarnings("unchecked")
        List<? extends FeatureMap.Entry> values = (List<? extends FeatureMap.Entry>) helper.getValue(o, f);
        int size = values.size();
        for (int i = 0; i < size; i++) {
            FeatureMap.Entry entry = values.get(i);
            EStructuralFeature entryFeature = entry.getEStructuralFeature();
            Object value = entry.getValue();
            if (entryFeature == XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__PROCESSING_INSTRUCTION) {
                ProcessingInstruction pi = (ProcessingInstruction) value;
                String target = pi.getTarget();
                String data = pi.getData();
                if (escape != null && data != null) {
                    data = escape.convertLines(data);
                }
                if (!toDOM) {
                    doc.addProcessingInstruction(target, data);
                } else {
                    currentNode.appendChild(getGwtDOMHandler().createProcessingInstruction(target, data));
                }
            } else if (entryFeature instanceof EReference) {
                if (value == null) {
                    saveNil(o, entryFeature);
                } else {
                    EReference referenceEntryFeature = (EReference) entryFeature;
                    if (referenceEntryFeature.isContainment()) {
                        saveElement((InternalEObject) value, entryFeature);
                    } else if (referenceEntryFeature.isResolveProxies()) {
                        saveFeatureMapElementReference((EObject) value, referenceEntryFeature);
                    } else {
                        saveElementIDRef(o, (EObject) value, entryFeature);
                    }
                }
            } else {
                if (entryFeature == XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__TEXT) {
                    String svalue = value.toString();
                    if (escape != null) {
                        svalue = escape.convertText(svalue);
                    }
                    if (!toDOM) {
                        doc.addText(svalue);
                    } else {
                        Node text = getGwtDOMHandler().createTextNode(svalue);
                        currentNode.appendChild(text);
                        handler.recordValues(text, o, f, entry);
                    }
                } else if (entryFeature == XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__CDATA) {
                    String stringValue = value.toString();
                    if (escape != null) {
                        stringValue = escape.convertLines(stringValue);
                    }
                    if (!toDOM) {
                        doc.addCDATA(stringValue);
                    } else {
                        Node cdata = getGwtDOMHandler().createCDATASection(stringValue);
                        currentNode.appendChild(cdata);
                        handler.recordValues(cdata, o, f, entry);
                    }
                } else if (entryFeature == XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__COMMENT) {
                    String stringValue = value.toString();
                    if (escape != null) {
                        stringValue = escape.convertLines(stringValue);
                    }
                    if (!toDOM) {
                        doc.addComment(stringValue);
                    } else {
                        currentNode.appendChild(getGwtDOMHandler().createComment(stringValue));
                    }
                } else {
                    saveElement(o, value, entryFeature);
                }
            }
        }
        return size > 0;
    }

    protected void saveAttributeFeatureMap(EObject o, EStructuralFeature f) {
        @SuppressWarnings("unchecked")
        List<? extends FeatureMap.Entry> values = (List<? extends FeatureMap.Entry>) helper.getValue(o, f);
        int size = values.size();
        Set<EReference> repeats = null;
        for (int i = 0; i < size; i++) {
            FeatureMap.Entry entry = values.get(i);
            EStructuralFeature entryFeature = entry.getEStructuralFeature();
            if (entryFeature instanceof EReference) {
                EReference referenceEntryFeature = (EReference) entryFeature;
                if (referenceEntryFeature.isMany()) {
                    if (repeats == null) {
                        repeats = new HashSet<EReference>();
                    } else if (repeats.contains(referenceEntryFeature)) {
                        continue;
                    }

                    repeats.add(referenceEntryFeature);

                    if (referenceEntryFeature.isResolveProxies()) {
                        saveEObjectMany(o, entryFeature);
                    } else {
                        saveIDRefMany(o, entryFeature);
                    }
                } else {
                    if (referenceEntryFeature.isResolveProxies()) {
                        saveEObjectSingle(o, entryFeature);
                    } else {
                        saveIDRefSingle(o, entryFeature);
                    }
                }
            } else {
                Object value = entry.getValue();
                String svalue = getDatatypeValue(value, entryFeature, true);
                if (!toDOM) {
                    doc.addAttribute(helper.getQName(entryFeature), svalue);
                } else {
                    helper.populateNameInfo(nameInfo, entryFeature);
                    Attr attr = getGwtDOMHandler().createAttributeNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                    attr.setNodeValue(svalue);
                    getGwtDOMHandler().setAttributeNodeNS(currentNode, attr);
                    handler.recordValues(attr, o, f, value);
                }
            }
        }
    }

    protected int sameDocSingle(EObject o, EStructuralFeature f) {
        InternalEObject value = (InternalEObject) helper.getValue(o, f);
        if (value == null) {
            return SKIP;
        } else if (value.eIsProxy()) {
            return CROSS_DOC;
        } else {
            Resource res = value.eResource();
            return res == helper.getResource() || res == null ? SAME_DOC : CROSS_DOC;
        }
    }

    protected int sameDocMany(EObject o, EStructuralFeature f) {
        @SuppressWarnings("unchecked")
        InternalEList<? extends InternalEObject> values = (InternalEList<? extends InternalEObject>) helper.getValue(o, f);
        if (values.isEmpty()) {
            return SKIP;
        }

        for (Iterator<? extends InternalEObject> i = values.basicIterator(); i.hasNext(); ) {
            InternalEObject value = i.next();
            if (value.eIsProxy()) {
                return CROSS_DOC;
            } else {
                Resource resource = value.eResource();
                if (resource != helper.getResource() && resource != null) {
                    return CROSS_DOC;
                }
            }
        }

        return SAME_DOC;
    }

    protected String getContent(EObject o, EStructuralFeature[] features) {
        if (map == null) {
            return null;
        }

        for (int i = 0; i < features.length; i++) {
            EStructuralFeature feature = features[i];
            XMLInfo info = map.getInfo(feature);
            if (info != null && info.getXMLRepresentation() == XMLInfo.CONTENT) {
                Object value = helper.getValue(o, feature);
                String svalue = getDatatypeValue(value, feature, false);
                if (toDOM) {
                    Node text = getGwtDOMHandler().createTextNode(svalue);
                    currentNode.appendChild(text);
                    handler.recordValues(text, o, feature, value);
                }
                return svalue;
            }
        }
        return null;
    }

    protected void saveDataTypeElementSingle(EObject o, EStructuralFeature f) {
        saveElement(o, helper.getValue(o, f), f);
    }

    protected String getDataTypeElementSingleSimple(EObject o, EStructuralFeature f) {
        Object value = helper.getValue(o, f);
        String svalue = getDatatypeValue(value, f, false);
        if (toDOM) {
            Node text = getGwtDOMHandler().createTextNode(svalue);
            currentNode.appendChild(text);
            handler.recordValues(text, o, f, value);
        }
        return svalue;
    }

    private static boolean isIdNeeded(EObject o) {
        return !(
                o instanceof Documentation
                        || o instanceof LaneSet
                        || o instanceof InputOutputSpecification
                        || o instanceof InputSet
                        || o instanceof OutputSet
                        || o instanceof DataInputAssociation
                        || o instanceof DataOutputAssociation
                        || o instanceof Assignment
                        || o instanceof TimerEventDefinition
                        || o instanceof CompensateEventDefinition
                        || o instanceof SignalEventDefinition
                        || o instanceof ErrorEventDefinition
                        || o instanceof TerminateEventDefinition
                        || o instanceof MessageEventDefinition
                        || o instanceof EscalationEventDefinition
                        || o instanceof ConditionalEventDefinition
                        || o instanceof MultiInstanceLoopCharacteristics
                        || o instanceof Relationship
                        || o instanceof FormalExpression
        );
    }

    protected void saveElementID(EObject o) {
        String id = helper.getID(o);

        // Kogito customization clause
        // Ensure all object have an id set, if not yet,
        // otherwise references to those are not being properly resolved.
        // See also XMLHelper#getHREF(EObject)
        if (null == id && o instanceof BaseElement) {
            String eId = ((BaseElement) o).getId();
            if (null == eId && isIdNeeded(o)) {
                id = EcoreUtil.generateUUID();
                ((BaseElement) o).setId(id);
            }
        }
        if (id != null) {
            if (!toDOM) {
                doc.addAttribute(idAttributeName, id);
            } else {
                Attr attr = getGwtDOMHandler().createAttributeNS(idAttributeNS, idAttributeName);
                attr.setNodeValue(id);
                getGwtDOMHandler().setAttributeNodeNS(currentNode, attr);
                handler.recordValues(attr, o, null, o);
            }
        }
        saveFeatures(o);
    }

    protected static class Lookup {

        protected static final int SHIFT = 10;
        protected static final int SIZE = 1 << SHIFT; // 2^N
        protected static final int MASK = SIZE - 1; // 2^N-1

        protected EClass[] classes;
        protected EStructuralFeature[][] features;
        protected int[][] featureKinds;
        protected XMLMap map;
        protected ExtendedMetaData extendedMetaData;
        protected ArrayList<EObject> docRoots = new ArrayList<EObject>();
        protected ElementHandler elementHandler;

        protected static final class FeatureClassifierPair {

            EStructuralFeature eStructuralFeature;
            EClassifier eClassifier;

            @Override
            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                } else if (o instanceof FeatureClassifierPair) {
                    FeatureClassifierPair featureClassifierPair = (FeatureClassifierPair) o;
                    return eStructuralFeature == featureClassifierPair.eStructuralFeature && eClassifier == featureClassifierPair.eClassifier;
                } else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                return eStructuralFeature.hashCode() ^ eClassifier.hashCode();
            }
        }

        protected FeatureClassifierPair featureClassifierPair;
        protected Map<FeatureClassifierPair, EStructuralFeature> substitutionGroupMap;
        protected static final EStructuralFeature NULL_FEATURE = EcoreFactory.eINSTANCE.createEAttribute();

        public Lookup(XMLMap map) {
            this(map, null, null);
        }

        public Lookup(XMLMap map, ExtendedMetaData extendedMetaData) {
            this(map, extendedMetaData, null);
        }

        public Lookup(XMLMap map, ExtendedMetaData extendedMetaData, ElementHandler elementHandler) {
            this.map = map;
            this.extendedMetaData = extendedMetaData;
            this.elementHandler = elementHandler;
            classes = new EClass[SIZE];
            features = new EStructuralFeature[SIZE][];
            featureKinds = new int[SIZE][];
            if (elementHandler != null) {
                featureClassifierPair = new FeatureClassifierPair();
                substitutionGroupMap = new HashMap<FeatureClassifierPair, EStructuralFeature>();
            }
        }

        public EClass getDocumentRoot(EPackage epackage) {
            for (int i = 0; i < docRoots.size(); i += 2) {
                if (docRoots.get(i) == epackage) {
                    return (EClass) docRoots.get(++i);
                }
            }
            docRoots.add(epackage);
            EClass docRoot = extendedMetaData.getDocumentRoot(epackage);
            docRoots.add(docRoot);
            return docRoot;
        }

        public EStructuralFeature[] getFeatures(EClass cls) {
            int index = getIndex(cls);
            EClass c = classes[index];

            if (c == cls) {
                return features[index];
            }

            EStructuralFeature[] featureList = listFeatures(cls);
            if (c == null) {
                classes[index] = cls;
                features[index] = featureList;
                featureKinds[index] = listKinds(featureList);
            }
            return featureList;
        }

        public int[] getKinds(EClass cls, EStructuralFeature[] featureList) {
            int index = getIndex(cls);
            EClass c = classes[index];

            if (c == cls) {
                return featureKinds[index];
            }

            int[] kindsList = listKinds(featureList);
            if (c == null) {
                classes[index] = cls;
                features[index] = featureList;
                featureKinds[index] = kindsList;
            }
            return kindsList;
        }

        public EStructuralFeature getSubstitutionGroup(EStructuralFeature eStructuralFeature, EClassifier eClassifier) {
            if (elementHandler == null) {
                return null;
            } else {
                featureClassifierPair.eStructuralFeature = eStructuralFeature;
                featureClassifierPair.eClassifier = eClassifier;
                EStructuralFeature result = substitutionGroupMap.get(featureClassifierPair);
                if (result == NULL_FEATURE) {
                    result = null;
                } else {
                    result = elementHandler.getSubstitutionGroup(extendedMetaData, eStructuralFeature, eClassifier);
                    substitutionGroupMap.put(featureClassifierPair, result == null ? NULL_FEATURE : result);
                    featureClassifierPair = new FeatureClassifierPair();
                }
                return result;
            }
        }

        public EStructuralFeature getRoot(EClassifier eClassifier) {
            if (elementHandler == null) {
                return null;
            } else {
                featureClassifierPair.eStructuralFeature = NULL_FEATURE;
                featureClassifierPair.eClassifier = eClassifier;
                EStructuralFeature result = substitutionGroupMap.get(featureClassifierPair);
                if (result == NULL_FEATURE) {
                    result = null;
                } else {
                    result = elementHandler.getRoot(extendedMetaData, eClassifier);
                    substitutionGroupMap.put(featureClassifierPair, result == null ? NULL_FEATURE : result);
                    featureClassifierPair = new FeatureClassifierPair();
                }
                return result;
            }
        }

        protected int getIndex(EClass cls) {
            String name = cls.getInstanceClassName();
            int index = 0;
            if (name != null) {
                index = name.hashCode() & MASK;
            } else {
                index = cls.hashCode() >> SHIFT & MASK;
            }
            return index;
        }

        protected EStructuralFeature[] listFeatures(EClass cls) {
            if (extendedMetaData != null) {
                List<EStructuralFeature> f = new ArrayList<EStructuralFeature>();
                f.addAll(cls.getEAllStructuralFeatures());
                List<EStructuralFeature> orderedElements = extendedMetaData.getAllElements(cls);
                f.removeAll(orderedElements);
                f.addAll(orderedElements);
                return f.toArray(new EStructuralFeature[f.size()]);
            } else {
                List<EStructuralFeature> f = map == null ? cls.getEAllStructuralFeatures() : map.getFeatures(cls);
                return f.toArray(new EStructuralFeature[f.size()]);
            }
        }

        protected int[] listKinds(EStructuralFeature[] featureList) {
            int[] kinds = new int[featureList.length];
            for (int i = featureList.length - 1; i >= 0; i--) {
                kinds[i] = featureKind(featureList[i]);
            }

            return kinds;
        }

        protected int featureKind(EStructuralFeature f) {
            if (f.isTransient()) {
                return TRANSIENT;
            }

            boolean isMany = f.isMany();
            boolean isUnsettable = f.isUnsettable();

            if (f instanceof EReference) {
                EReference r = (EReference) f;
                if (r.isContainment()) {
                    return
                            isMany ?
                                    isUnsettable ? OBJECT_CONTAIN_MANY_UNSETTABLE : OBJECT_CONTAIN_MANY :
                                    isUnsettable ? OBJECT_CONTAIN_SINGLE_UNSETTABLE : OBJECT_CONTAIN_SINGLE;
                }
                EReference opposite = r.getEOpposite();
                if (opposite != null && opposite.isContainment()) {
                    return TRANSIENT;
                }

                if (map != null) {
                    XMLInfo info = map.getInfo(f);
                    if (info != null && info.getXMLRepresentation() == XMLInfo.ELEMENT) {
                        return
                                isMany ?
                                        OBJECT_ELEMENT_MANY :
                                        r.isUnsettable() ?
                                                OBJECT_ELEMENT_SINGLE_UNSETTABLE :
                                                OBJECT_ELEMENT_SINGLE;
                    }
                }

                if (extendedMetaData != null) {
                    switch (extendedMetaData.getFeatureKind(f)) {
                        case ExtendedMetaData.ATTRIBUTE_FEATURE: {
                            return
                                    r.isResolveProxies() ?
                                            isMany ?
                                                    OBJECT_ATTRIBUTE_MANY :
                                                    OBJECT_ATTRIBUTE_SINGLE :
                                            isMany ?
                                                    OBJECT_ATTRIBUTE_IDREF_MANY :
                                                    OBJECT_ATTRIBUTE_IDREF_SINGLE;
                        }
                        case ExtendedMetaData.SIMPLE_FEATURE: {
                            if (f == XMLTypePackage.Literals.SIMPLE_ANY_TYPE__INSTANCE_TYPE) {
                                return TRANSIENT;
                            }
                        }
                        case ExtendedMetaData.ELEMENT_FEATURE: {
                            return
                                    r.isResolveProxies() ?
                                            isMany ?
                                                    OBJECT_ELEMENT_MANY :
                                                    r.isUnsettable() ?
                                                            OBJECT_ELEMENT_SINGLE_UNSETTABLE :
                                                            OBJECT_ELEMENT_SINGLE :
                                            isMany ?
                                                    OBJECT_ELEMENT_IDREF_MANY :
                                                    r.isUnsettable() ?
                                                            OBJECT_ELEMENT_IDREF_SINGLE_UNSETTABLE :
                                                            OBJECT_ELEMENT_IDREF_SINGLE;
                        }
                    }
                }

                return
                        isMany ?
                                isUnsettable ? OBJECT_HREF_MANY_UNSETTABLE : OBJECT_HREF_MANY :
                                isUnsettable ? OBJECT_HREF_SINGLE_UNSETTABLE : OBJECT_HREF_SINGLE;
            } else {
                // Attribute
                EDataType d = (EDataType) f.getEType();
                if (!d.isSerializable() && d != EcorePackage.Literals.EFEATURE_MAP_ENTRY) {
                    return TRANSIENT;
                }

                if (d.getInstanceClass() == FeatureMap.Entry.class) {
                    return
                            extendedMetaData != null && extendedMetaData.getFeatureKind(f) == ExtendedMetaData.ATTRIBUTE_WILDCARD_FEATURE ?
                                    ATTRIBUTE_FEATURE_MAP :
                                    ELEMENT_FEATURE_MAP;
                }

                if (extendedMetaData != null) {
                    switch (extendedMetaData.getFeatureKind(f)) {
                        case ExtendedMetaData.SIMPLE_FEATURE: {
                            return DATATYPE_ELEMENT_SINGLE;
                        }
                        case ExtendedMetaData.ELEMENT_FEATURE: {
                            return f.isMany() ? DATATYPE_MANY : DATATYPE_ELEMENT_SINGLE;
                        }
                        case ExtendedMetaData.ATTRIBUTE_FEATURE: {
                            return f.isMany() ? DATATYPE_ATTRIBUTE_MANY : DATATYPE_SINGLE;
                        }
                    }
                }

                if (isMany) {
                    return DATATYPE_MANY;
                }

                if (isUnsettable && map == null) {
                    return DATATYPE_SINGLE_NILLABLE;
                }

                if (map == null) {
                    return DATATYPE_SINGLE;
                } else {
                    XMLInfo info = map.getInfo(f);

                    if (info != null && info.getXMLRepresentation() == XMLInfo.ELEMENT) {
                        return DATATYPE_ELEMENT_SINGLE;
                    } else if (info != null && info.getXMLRepresentation() == XMLInfo.CONTENT) {
                        return DATATYPE_CONTENT_SINGLE;
                    } else {
                        if (isUnsettable) {
                            return DATATYPE_SINGLE_NILLABLE;
                        } else {
                            return DATATYPE_SINGLE;
                        }
                    }
                }
            }
        }
    }

    protected String getDatatypeValue(Object value, EStructuralFeature f, boolean isAttribute) {
        if (value == null) {
            return null;
        }
        EDataType d = (EDataType) f.getEType();
        EPackage ePackage = d.getEPackage();
        EFactory fac = ePackage.getEFactoryInstance();
        String svalue = helper.convertToString(fac, d, value);
        if (escape != null) {
            if (isAttribute) {
                svalue = escape.convert(svalue);
            } else {
                svalue = escape.convertText(svalue);
            }
        }
        return svalue;
    }

    protected void saveElement(EObject o, Object value, EStructuralFeature f) {
        if (value == null) {
            saveNil(o, f);
        } else {
            String svalue = getDatatypeValue(value, f, false);
            if (!toDOM) {
                doc.saveDataValueElement(helper.getQName(f), svalue);
            } else {
                helper.populateNameInfo(nameInfo, f);
                Element elem = getGwtDOMHandler().createElementNS(nameInfo.getNamespaceURI(), nameInfo.getQualifiedName());
                Node text = getGwtDOMHandler().createTextNode(svalue);
                elem.appendChild(text);
                currentNode.appendChild(elem);
                handler.recordValues(elem, o, f, value);
                handler.recordValues(text, o, f, value);
            }
        }
    }

    protected String convertURI(String uri) {
        if (resourceEntityHandler != null) {
            int index = uri.indexOf('#');
            if (index > 0) {
                String baseURI = uri.substring(0, index);
                String fragment = uri.substring(index + 1);
                String entityName = resourceEntityHandler.getEntityName(baseURI);
                if (entityName != null) {
                    return "&" + entityName + ";#" + (escapeURI == null ? fragment : escapeURI.convert(fragment));
                }
            }
        }
        return escapeURI != null ? escapeURI.convert(uri) : uri;
    }

    protected static class Escape {

        protected char[] value;
        protected int mappableLimit;
        protected boolean allowControlCharacters;
        protected boolean useCDATA;

        protected final char[] NUL = {'&', '#', 'x', '0', ';'};
        protected final char[] SOH = {'&', '#', 'x', '1', ';'};
        protected final char[] STX = {'&', '#', 'x', '2', ';'};
        protected final char[] ETX = {'&', '#', 'x', '3', ';'};
        protected final char[] EOT = {'&', '#', 'x', '4', ';'};
        protected final char[] ENQ = {'&', '#', 'x', '5', ';'};
        protected final char[] ACK = {'&', '#', 'x', '6', ';'};
        protected final char[] BEL = {'&', '#', 'x', '7', ';'};
        protected final char[] BS = {'&', '#', 'x', '8', ';'};
        protected final char[] TAB = {'&', '#', 'x', '9', ';'};
        protected final char[] LF = {'&', '#', 'x', 'A', ';'};
        protected final char[] VT = {'&', '#', 'x', 'B', ';'};
        protected final char[] FF = {'&', '#', 'x', 'C', ';'};
        protected final char[] CR = {'&', '#', 'x', 'D', ';'};
        protected final char[] SO = {'&', '#', 'x', 'E', ';'};
        protected final char[] SI = {'&', '#', 'x', 'F', ';'};
        protected final char[] DLE = {'&', '#', 'x', '1', '0', ';'};
        protected final char[] DC1 = {'&', '#', 'x', '1', '1', ';'};
        protected final char[] DC2 = {'&', '#', 'x', '1', '2', ';'};
        protected final char[] DC3 = {'&', '#', 'x', '1', '3', ';'};
        protected final char[] DC4 = {'&', '#', 'x', '1', '4', ';'};
        protected final char[] NAK = {'&', '#', 'x', '1', '5', ';'};
        protected final char[] SYN = {'&', '#', 'x', '1', '6', ';'};
        protected final char[] ETB = {'&', '#', 'x', '1', '7', ';'};
        protected final char[] CAN = {'&', '#', 'x', '1', '8', ';'};
        protected final char[] EM = {'&', '#', 'x', '1', '9', ';'};
        protected final char[] SUB = {'&', '#', 'x', '1', 'A', ';'};
        protected final char[] ESC = {'&', '#', 'x', '1', 'B', ';'};
        protected final char[] FS = {'&', '#', 'x', '1', 'C', ';'};
        protected final char[] GS = {'&', '#', 'x', '1', 'D', ';'};
        protected final char[] RS = {'&', '#', 'x', '1', 'E', ';'};
        protected final char[] US = {'&', '#', 'x', '1', 'F', ';'};

        protected final char[][] CONTROL_CHARACTERS =
                new char[][]
                        {
                                NUL,
                                SOH,
                                STX,
                                ETX,
                                EOT,
                                ENQ,
                                ACK,
                                BEL,
                                BS,
                                TAB,
                                LF,
                                VT,
                                FF,
                                CR,
                                SO,
                                SI,
                                DLE,
                                DC1,
                                DC2,
                                DC3,
                                DC4,
                                NAK,
                                SYN,
                                ETB,
                                CAN,
                                EM,
                                SUB,
                                ESC,
                                FS,
                                GS,
                                RS,
                                US,
                        };

        protected final char[] AMP = {'&', 'a', 'm', 'p', ';'};
        protected final char[] LESS = {'&', 'l', 't', ';'};
        protected final char[] GREATER = {'&', 'g', 't', ';'};
        protected final char[] QUOTE = {'&', 'q', 'u', 'o', 't', ';'};
        protected final char[] LINE_FEED = System.getProperty("line.separator", "\n").toCharArray();

        public Escape() {
            value = new char[100];
        }

        public void setMappingLimit(int mappingLimit) {
            mappableLimit = mappingLimit;
        }

        public void setAllowControlCharacters(boolean allowControlCharacters) {
            this.allowControlCharacters = allowControlCharacters;
        }

        public void setUseCDATA(boolean useCDATA) {
            this.useCDATA = useCDATA;
        }

        /*
         *  Convert attribute values:
         *  & to &amp;
         *  < to &lt;
         *  " to &quot;
         *  \t to &#x9;
         *  \n to &#xA;
         *  \r to &#xD;
         */
        public String convert(String input) {
            boolean changed = false;
            int inputLength = input.length();
            grow(inputLength);
            int outputPos = 0;
            int inputPos = 0;
            char ch = 0;
            while (inputLength-- > 0) {
                ch = input.charAt(inputPos++); // value[outputPos];
                switch (ch) {
                    case 0x1:
                    case 0x2:
                    case 0x3:
                    case 0x4:
                    case 0x5:
                    case 0x6:
                    case 0x7:
                    case 0x8:
                    case 0xB:
                    case 0xC:
                    case 0xE:
                    case 0xF:
                    case 0x10:
                    case 0x11:
                    case 0x12:
                    case 0x13:
                    case 0x14:
                    case 0x15:
                    case 0x16:
                    case 0x17:
                    case 0x18:
                    case 0x19:
                    case 0x1A:
                    case 0x1B:
                    case 0x1C:
                    case 0x1D:
                    case 0x1E:
                    case 0x1F: {
                        if (allowControlCharacters) {
                            outputPos = replaceChars(outputPos, CONTROL_CHARACTERS[ch], inputLength);
                            changed = true;
                        } else {
                            throw new RuntimeException("An invalid XML character (Unicode: 0x" + Integer.toHexString(ch) + ") was found in the element content:" + input);
                        }
                        break;
                    }
                    case '&': {
                        outputPos = replaceChars(outputPos, AMP, inputLength);
                        changed = true;
                        break;
                    }
                    case '<': {
                        outputPos = replaceChars(outputPos, LESS, inputLength);
                        changed = true;
                        break;
                    }
                    case '"': {
                        outputPos = replaceChars(outputPos, QUOTE, inputLength);
                        changed = true;
                        break;
                    }
                    case '\n': {
                        outputPos = replaceChars(outputPos, LF, inputLength);
                        changed = true;
                        break;
                    }
                    case '\r': {
                        outputPos = replaceChars(outputPos, CR, inputLength);
                        changed = true;
                        break;
                    }
                    case '\t': {
                        outputPos = replaceChars(outputPos, TAB, inputLength);
                        changed = true;
                        break;
                    }
                    default: {
                        if (!XMLChar.isValid(ch)) {
                            if (XMLChar.isHighSurrogate(ch)) {
                                char high = ch;
                                if (inputLength-- > 0) {
                                    ch = input.charAt(inputPos++);
                                    if (XMLChar.isLowSurrogate(ch)) {
                                        if (mappableLimit == MAX_UTF_MAPPABLE_CODEPOINT) {
                                            // Every codepoint is supported!
                                            value[outputPos++] = high;
                                            value[outputPos++] = ch;
                                        } else {
                                            // Produce the supplemental character as an entity
                                            outputPos = replaceChars(outputPos, ("&#x" + Integer.toHexString(XMLChar.supplemental(high, ch)) + ";").toCharArray(), inputLength);
                                            changed = true;
                                        }
                                        break;
                                    }
                                    throw new RuntimeException("An invalid low surrogate character (Unicode: 0x" + Integer.toHexString(ch) + ") was found in the element content:" + input);
                                } else {
                                    throw new RuntimeException("An unpaired high surrogate character (Unicode: 0x" + Integer.toHexString(ch) + ") was found in the element content:" + input);
                                }
                            } else {
                                throw new RuntimeException("An invalid XML character (Unicode: 0x" + Integer.toHexString(ch) + ") was found in the element content:" + input);
                            }
                        } else {
                            // Normal (BMP) unicode code point. See if we know for a fact that the encoding supports it:
                            if (ch <= mappableLimit) {
                                value[outputPos++] = ch;
                            } else {
                                // We not sure the encoding supports this code point, so we write it as a character entity reference.
                                outputPos = replaceChars(outputPos, ("&#x" + Integer.toHexString(ch) + ";").toCharArray(), inputLength);
                                changed = true;
                            }
                        }
                        break;
                    }
                }
            }
            return changed ? new String(value, 0, outputPos) : input;
        }

        /*
         *  Convert element values:
         *  & to &amp;
         *  < to &lt;
         *  " to &quot;
         *  \n to line separator
         *  \r should be escaped to &xD;
         */
        public String convertText(String input) {
            boolean changed = false;
            boolean cdataCloseBracket = false;
            int inputLength = input.length();
            grow(inputLength);
            int outputPos = 0;
            int inputPos = 0;
            char ch;
            while (inputLength-- > 0) {
                ch = input.charAt(inputPos++); // value[outputPos];
                switch (ch) {
                    case 0x1:
                    case 0x2:
                    case 0x3:
                    case 0x4:
                    case 0x5:
                    case 0x6:
                    case 0x7:
                    case 0x8:
                    case 0xB:
                    case 0xC:
                    case 0xE:
                    case 0xF:
                    case 0x10:
                    case 0x11:
                    case 0x12:
                    case 0x13:
                    case 0x14:
                    case 0x15:
                    case 0x16:
                    case 0x17:
                    case 0x18:
                    case 0x19:
                    case 0x1A:
                    case 0x1B:
                    case 0x1C:
                    case 0x1D:
                    case 0x1E:
                    case 0x1F: {
                        if (allowControlCharacters) {
                            outputPos = replaceChars(outputPos, CONTROL_CHARACTERS[ch], inputLength);
                            changed = true;
                        } else {
                            throw new RuntimeException("An invalid XML character (Unicode: 0x" + Integer.toHexString(ch) + ") was found in the element content:" + input);
                        }
                        break;
                    }
                    case '&': {
                        outputPos = replaceChars(outputPos, AMP, inputLength);
                        changed = true;
                        break;
                    }
                    case '<': {
                        outputPos = replaceChars(outputPos, LESS, inputLength);
                        changed = true;
                        break;
                    }
                    case '"': {
                        outputPos = replaceChars(outputPos, QUOTE, inputLength);
                        changed = true;
                        break;
                    }
                    case '\n': {
                        outputPos = replaceChars(outputPos, LINE_FEED, inputLength);
                        changed = true;
                        break;
                    }
                    case '\r': {
                        outputPos = replaceChars(outputPos, CR, inputLength);
                        changed = true;
                        break;
                    }
                    case '>': {
                        if (inputPos >= 3 && input.charAt(inputPos - 2) == ']' && input.charAt(inputPos - 3) == ']') {
                            outputPos = replaceChars(outputPos, GREATER, inputLength);
                            cdataCloseBracket = true;
                            changed = true;
                            break;
                        }

                        // continue with default processing
                    }
                    default: {
                        if (!XMLChar.isValid(ch)) {
                            if (XMLChar.isHighSurrogate(ch)) {
                                char high = ch;
                                if (inputLength-- > 0) {
                                    ch = input.charAt(inputPos++);
                                    if (XMLChar.isLowSurrogate(ch)) {
                                        if (mappableLimit == MAX_UTF_MAPPABLE_CODEPOINT) {
                                            // Every codepoint is supported!
                                            value[outputPos++] = high;
                                            value[outputPos++] = ch;
                                        } else {
                                            // Produce the supplemental character as an entity
                                            outputPos = replaceChars(outputPos, ("&#x" + Integer.toHexString(XMLChar.supplemental(high, ch)) + ";").toCharArray(), inputLength);
                                            changed = true;
                                        }
                                        break;
                                    }
                                    throw new RuntimeException("An invalid low surrogate character (Unicode: 0x" + Integer.toHexString(ch) + ") was found in the element content:" + input);
                                } else {
                                    throw new RuntimeException("An unpaired high surrogate character (Unicode: 0x" + Integer.toHexString(ch) + ") was found in the element content:" + input);
                                }
                            } else {
                                throw new RuntimeException("An invalid XML character (Unicode: 0x" + Integer.toHexString(ch) + ") was found in the element content:" + input);
                            }
                        } else {
                            // Normal (BMP) unicode code point. See if we know for a fact that the encoding supports it:
                            if (ch <= mappableLimit) {
                                value[outputPos++] = ch;
                            } else {
                                // We not sure the encoding supports this code point, so we write it as a character entity reference.
                                outputPos = replaceChars(outputPos, ("&#x" + Integer.toHexString(ch) + ";").toCharArray(), inputLength);
                                changed = true;
                            }
                        }
                        break;
                    }
                }
            }
            return changed ? !useCDATA || cdataCloseBracket ? new String(value, 0, outputPos) : "<![CDATA[" + input + "]]>" : input;
        }

        /*
         *  Convert:
         *  \n to line separator
         */
        public String convertLines(String input) {
            boolean changed = false;
            int inputLength = input.length();
            grow(inputLength);
            int outputPos = 0;
            int inputPos = 0;
            char ch;
            while (inputLength-- > 0) {
                ch = input.charAt(inputPos++);
                switch (ch) {
                    case '\n': {
                        outputPos = replaceChars(outputPos, LINE_FEED, inputLength);
                        changed = true;
                        break;
                    }
                    default: {
                        value[outputPos++] = ch;
                        break;
                    }
                }
            }
            return changed ? new String(value, 0, outputPos) : input;
        }

        protected int replaceChars(int pos, char[] replacement, int inputLength) {
            int rlen = replacement.length;
            int newPos = pos + rlen;
            grow(newPos + inputLength);
            System.arraycopy(replacement, 0, value, pos, rlen);
            return newPos;
        }

        /**
         * @see #replaceChars(int, char[], int)
         * @deprecated since 2.2
         */
        @Deprecated
        protected int replace(int pos, char[] replacement, int inputLength) {
            int rlen = replacement.length;
            int newPos = pos + rlen;
            grow(newPos + inputLength);
            System.arraycopy(value, pos + 1, value, newPos, inputLength);
            System.arraycopy(replacement, 0, value, pos, rlen);
            return newPos;
        }

        protected void grow(int newSize) {
            int vlen = value.length;
            if (vlen < newSize) {
                char[] newValue = new char[newSize + newSize / 2];
                System.arraycopy(value, 0, newValue, 0, vlen);
                value = newValue;
            }
        }
    }

    private GwtDOMHandler getGwtDOMHandler() {
        return gwtDocumentHandler;
    }
}
