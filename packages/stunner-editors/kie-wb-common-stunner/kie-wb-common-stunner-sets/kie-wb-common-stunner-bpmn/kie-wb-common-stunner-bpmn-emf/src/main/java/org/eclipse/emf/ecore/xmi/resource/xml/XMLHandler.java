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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.eclipse.emf.ecore.xml.type.SimpleAnyType;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeUtil;

/**
 * This class is a generic interface for loading XML files and
 * creating EObjects from them. Its subclasses include the SAXXMLHandler
 * class, which wraps this class in a SAX default handler.
 */
public abstract class XMLHandler {

    protected static final String ERROR_TYPE = "error";
    protected static final String OBJECT_TYPE = "object";
    protected static final String UNKNOWN_FEATURE_TYPE = "unknownFeature";
    protected static final String DOCUMENT_ROOT_TYPE = "documentRoot";

    protected final static String TYPE_ATTRIB = XMLResource.XSI_NS + ":" + XMLResource.TYPE;
    protected final static String NIL_ATTRIB = XMLResource.XSI_NS + ":" + XMLResource.NIL;
    protected final static String SCHEMA_LOCATION_ATTRIB = XMLResource.XSI_NS + ":" + XMLResource.SCHEMA_LOCATION;
    protected final static String NO_NAMESPACE_SCHEMA_LOCATION_ATTRIB = XMLResource.XSI_NS + ":" + XMLResource.NO_NAMESPACE_SCHEMA_LOCATION;

    protected final static boolean DEBUG_DEMANDED_PACKAGES = false;

    protected static class MyStack<E> extends BasicEList<E> {

        private static final long serialVersionUID = 1L;

        public MyStack() {
            super();
        }

        @SuppressWarnings("unchecked")
        public final E peek() {
            return size == 0 ? null : (E) data[size - 1];
        }

        public final void push(E o) {
            grow(size + 1);
            data[size++] = o;
        }

        @SuppressWarnings("unchecked")
        public final E pop() {
            return size == 0 ? null : (E) data[--size];
        }
    }

    protected static class MyEObjectStack extends MyStack<EObject> {

        private static final long serialVersionUID = 1L;

        protected EObject[] eObjectData;

        public MyEObjectStack() {
            super();
        }

        @Override
        protected final Object[] newData(int capacity) {
            return eObjectData = new EObject[capacity];
        }

        public final EObject peekEObject() {
            return size == 0 ? null : eObjectData[size - 1];
        }

        public final EObject popEObject() {
            return size == 0 ? null : eObjectData[--size];
        }

        @Override
        public void clear() {
            eObjectData = null;
            super.clear();
        }
    }

    /**
     * For unresolved forward references, the line number where the incorrect id
     * appeared in an XML resource is needed, so the Value for the forward reference
     * and the line number where the forward reference occurred must be saved until
     * the end of the XML resource is encountered.
     */
    protected static class SingleReference {

        private EObject object;
        private EStructuralFeature feature;
        private Object value;
        private int position;
        private int lineNumber;
        private int columnNumber;

        public SingleReference(EObject object,
                               EStructuralFeature feature,
                               Object value,
                               int position,
                               int lineNumber,
                               int columnNumber) {
            this.object = object;
            this.feature = feature;
            this.value = value;
            this.position = position;
            this.lineNumber = lineNumber;
            this.columnNumber = columnNumber;
        }

        public EObject getObject() {
            return object;
        }

        public EStructuralFeature getFeature() {
            return feature;
        }

        public Object getValue() {
            return value;
        }

        public int getPosition() {
            return position;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public int getColumnNumber() {
            return columnNumber;
        }
    }

    protected static class ManyReference implements XMLHelper.ManyReference {

        private EObject object;
        private EStructuralFeature feature;
        private Object[] values;
        private int[] positions;
        private int lineNumber;
        private int columnNumber;

        public ManyReference(EObject object,
                             EStructuralFeature feature,
                             Object[] values,
                             int[] positions,
                             int lineNumber,
                             int columnNumber) {
            this.object = object;
            this.feature = feature;
            this.values = values;
            this.positions = positions;
            this.lineNumber = lineNumber;
            this.columnNumber = columnNumber;
        }

        public EObject getObject() {
            return object;
        }

        public EStructuralFeature getFeature() {
            return feature;
        }

        public Object[] getValues() {
            return values;
        }

        public int[] getPositions() {
            return positions;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public int getColumnNumber() {
            return columnNumber;
        }
    }

    protected XMLResource xmlResource;
    protected XMLHelper helper;
    protected MyStack<String> elements;
    protected MyEObjectStack objects;
    protected MyStack<Object> types;
    protected MyStack<FeatureMap> mixedTargets;
    protected Map<String, EFactory> prefixesToFactories;
    protected Map<String, URI> urisToLocations;
    protected Map<String, URI> externalURIToLocations;
    protected boolean processSchemaLocations;
    protected InternalEList<EObject> extent;
    protected List<EObject> deferredExtent;
    protected ResourceSet resourceSet;
    protected EPackage.Registry packageRegistry;
    protected URI resourceURI;
    protected boolean resolve;
    protected boolean oldStyleProxyURIs;
    protected boolean disableNotify;
    protected StringBuffer text;
    protected boolean isIDREF;
    protected boolean isSimpleFeature;
    protected List<InternalEObject> sameDocumentProxies;
    protected List<SingleReference> forwardSingleReferences;
    protected List<ManyReference> forwardManyReferences;
    protected Object[] identifiers;
    protected int[] positions;
    protected static final int ARRAY_SIZE = 64;
    protected static final int REFERENCE_THRESHOLD = 5;
    protected int capacity;
    protected Set<String> notFeatures;
    protected String idAttribute;
    protected String hrefAttribute;
    protected XMLMap xmlMap;
    protected ExtendedMetaData extendedMetaData;
    protected EClass anyType;
    protected EClass anySimpleType;
    protected boolean recordUnknownFeature;
    protected boolean useNewMethods;
    protected boolean recordAnyTypeNSDecls;
    protected Map<EObject, AnyType> eObjectToExtensionMap;
    protected EStructuralFeature contextFeature;
    protected EPackage xmlSchemaTypePackage = XMLTypePackage.eINSTANCE;
    protected boolean deferIDREFResolution;
    protected boolean processAnyXML;
    //protected EcoreBuilder ecoreBuilder;
    protected boolean isRoot;
    protected Attributes attribs;
    protected Map<EStructuralFeature, Integer> featuresToKinds;
    protected boolean useConfigurationCache;
    protected boolean needsPushContext;
    protected ResourceEntityHandler resourceEntityHandler;
    protected URIHandler uriHandler;
    protected EObject documentRoot;
    protected boolean usedNullNamespacePackage;
    protected boolean isNamespaceAware;
    protected boolean suppressDocumentRoot;
    protected boolean laxWildcardProcessing;

    /**
     *
     */
    public XMLHandler(XMLResource xmlResource, XMLHelper helper, Map<?, ?> options) {
        this.xmlResource = xmlResource;
        this.helper = helper;
        elements = new MyStack<>();
        objects = new MyEObjectStack();
        mixedTargets = new MyStack<>();

        types = new MyStack<>();
        prefixesToFactories = new HashMap<>();
        forwardSingleReferences = new ArrayList<>();
        forwardManyReferences = new ArrayList<>();
        sameDocumentProxies = new ArrayList<>();
        identifiers = new Object[ARRAY_SIZE];
        positions = new int[ARRAY_SIZE];
        capacity = ARRAY_SIZE;
        resourceSet = xmlResource.getResourceSet();
        packageRegistry = resourceSet == null ? EPackage.Registry.INSTANCE : resourceSet.getPackageRegistry();
        resourceURI = xmlResource.getURI();
        extent = (InternalEList<EObject>) xmlResource.getContents();
        if (Boolean.TRUE.equals(options.get(XMLResource.OPTION_DEFER_ATTACHMENT))) {
            deferredExtent = new ArrayList<>();
        }
        resolve = resourceURI != null && resourceURI.isHierarchical() && !resourceURI.isRelative();

        eObjectToExtensionMap = xmlResource.getEObjectToExtensionMap();
        eObjectToExtensionMap.clear();

        helper.setOptions(options);

        if (Boolean.TRUE.equals(options.get(XMLResource.OPTION_DISABLE_NOTIFY))) {
            disableNotify = true;
        }

        notFeatures = new HashSet<>();
        notFeatures.add(TYPE_ATTRIB);
        notFeatures.add(SCHEMA_LOCATION_ATTRIB);
        notFeatures.add(NO_NAMESPACE_SCHEMA_LOCATION_ATTRIB);

        xmlMap = (XMLMap) options.get(XMLResource.OPTION_XML_MAP);
        helper.setXMLMap(xmlMap);
        if (xmlMap != null) {
            idAttribute = xmlMap.getIDAttributeName();
        }

        Object extendedMetaDataOption = options.get(XMLResource.OPTION_EXTENDED_META_DATA);
        setExtendedMetaDataOption(extendedMetaDataOption);

        recordUnknownFeature = Boolean.TRUE.equals(options.get(XMLResource.OPTION_RECORD_UNKNOWN_FEATURE));
        if (recordUnknownFeature && extendedMetaData == null) {
            setExtendedMetaDataOption(Boolean.TRUE);
        }

        useNewMethods = Boolean.FALSE.equals(options.get(XMLResource.OPTION_USE_DEPRECATED_METHODS));

        XMLOptions xmlOptions = (XMLOptions) options.get(XMLResource.OPTION_XML_OPTIONS);
        if (xmlOptions != null) {
            processSchemaLocations = xmlOptions.isProcessSchemaLocations();
            externalURIToLocations = xmlOptions.getExternalSchemaLocations();

            if (processSchemaLocations || externalURIToLocations != null) {
                if (extendedMetaData == null) {
                    setExtendedMetaDataOption(Boolean.TRUE);
                }
        /*ecoreBuilder = xmlOptions.getEcoreBuilder();
        if (ecoreBuilder == null)
        {
          ecoreBuilder = createEcoreBuilder(options, extendedMetaData);
        }
        else
        {
          ecoreBuilder.setExtendedMetaData(extendedMetaData);
        }*/
            }
            processAnyXML = xmlOptions.isProcessAnyXML();
            if (processAnyXML && extendedMetaData == null) {
                setExtendedMetaDataOption(Boolean.TRUE);
            }
        }

        if (extendedMetaData != null) {
            AnyType anyType = XMLTypeFactory.eINSTANCE.createAnyType();
            mixedTargets.push(anyType.getMixed());
            text = new StringBuffer();
        }

        anyType = (EClass) options.get(XMLResource.OPTION_ANY_TYPE);
        anySimpleType = (EClass) options.get(XMLResource.OPTION_ANY_SIMPLE_TYPE);

        if (anyType == null) {
            anyType = XMLTypePackage.eINSTANCE.getAnyType();
            anySimpleType = XMLTypePackage.eINSTANCE.getSimpleAnyType();
        }

        helper.setAnySimpleType(anySimpleType);

        @SuppressWarnings("unchecked")
        Map<EClassFeatureNamePair, EStructuralFeature> newEClassFeatureNamePairToEStructuralFeatureMap =
                (Map<EClassFeatureNamePair, EStructuralFeature>) options.get(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP);
        eClassFeatureNamePairToEStructuralFeatureMap = newEClassFeatureNamePairToEStructuralFeatureMap;
        if (eClassFeatureNamePairToEStructuralFeatureMap == null) {
            eClassFeatureNamePairToEStructuralFeatureMap = new HashMap<>();
        } else {
            isOptionUseXMLNameToFeatureSet = true;
        }

        recordAnyTypeNSDecls = Boolean.TRUE.equals(options.get(XMLResource.OPTION_RECORD_ANY_TYPE_NAMESPACE_DECLARATIONS));

        hrefAttribute = XMLResource.HREF;

        if (Boolean.TRUE.equals(options.get(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE))) {
            hrefAttribute = null;
        }

        if (Boolean.TRUE.equals(options.get(XMLResource.OPTION_DEFER_IDREF_RESOLUTION))) {
            helper.setCheckForDuplicates(deferIDREFResolution = true);
        }

        if (Boolean.TRUE.equals(options.get(XMLResource.OPTION_CONFIGURATION_CACHE))) {
            useConfigurationCache = true;
        }

        // The entity handler is the best place to resolve and deresolve URIs since it can do it there just once to produce the entity.
        // So most often the entity handler will be a URI handler as well and when used as a URI handler will be an identity handler.
        //
        uriHandler = (URIHandler) options.get(XMLResource.OPTION_URI_HANDLER);
        resourceEntityHandler = (ResourceEntityHandler) options.get(XMLResource.OPTION_RESOURCE_ENTITY_HANDLER);
        if (resourceEntityHandler != null) {
            resourceEntityHandler.reset();
            if (uriHandler == null && resourceEntityHandler instanceof URIHandler) {
                uriHandler = (URIHandler) resourceEntityHandler;
                uriHandler.setBaseURI(resourceURI);
            }
        }

        if (Boolean.TRUE.equals(options.get(XMLResource.OPTION_SUPPRESS_DOCUMENT_ROOT))) {
            suppressDocumentRoot = true;
        }

        if (Boolean.TRUE.equals(options.get(XMLResource.OPTION_LAX_WILDCARD_PROCESSING))) {
            laxWildcardProcessing = true;
        }
    }

    protected void setExtendedMetaDataOption(Object extendedMetaDataOption) {
        if (extendedMetaDataOption instanceof Boolean) {
            if (extendedMetaDataOption.equals(Boolean.TRUE)) {
                extendedMetaData =
                        resourceSet == null ?
                                ExtendedMetaData.INSTANCE :
                                new BasicExtendedMetaData(resourceSet.getPackageRegistry());
                if (xmlResource != null) {
                    xmlResource.getDefaultSaveOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
                }
            } else {
                extendedMetaData = null;
            }
        } else {
            extendedMetaData = (ExtendedMetaData) extendedMetaDataOption;
        }

        helper.setExtendedMetaData(extendedMetaData);
    }

    public void prepare(XMLResource resource, XMLHelper helper, Map<?, ?> options) {
        this.xmlResource = resource;
        this.helper = helper;
        resourceSet = xmlResource.getResourceSet();
        packageRegistry = resourceSet == null ? EPackage.Registry.INSTANCE : resourceSet.getPackageRegistry();
        resourceURI = xmlResource.getURI();
        extent = (InternalEList<EObject>) xmlResource.getContents();
        if (Boolean.TRUE.equals(options.get(XMLResource.OPTION_DEFER_ATTACHMENT))) {
            deferredExtent = new ArrayList<>();
        }
        resolve = resourceURI != null && resourceURI.isHierarchical() && !resourceURI.isRelative();
        eObjectToExtensionMap = xmlResource.getEObjectToExtensionMap();
        eObjectToExtensionMap.clear();
        setExtendedMetaDataOption(options.get(XMLResource.OPTION_EXTENDED_META_DATA));
        helper.setOptions(options);
        if (extendedMetaData != null) {
      /*if (ecoreBuilder != null)
      {
        ecoreBuilder.setExtendedMetaData(extendedMetaData);
      }*/
            AnyType anyType = XMLTypeFactory.eINSTANCE.createAnyType();
            mixedTargets.push(anyType.getMixed());
            text = new StringBuffer();
        }

        // bug #126072
        @SuppressWarnings("unchecked")
        Map<EClassFeatureNamePair, EStructuralFeature> newEClassFeatureNamePairToEStructuralFeatureMap =
                (Map<EClassFeatureNamePair, EStructuralFeature>) options.get(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP);
        eClassFeatureNamePairToEStructuralFeatureMap =
                newEClassFeatureNamePairToEStructuralFeatureMap;
        if (eClassFeatureNamePairToEStructuralFeatureMap == null) {
            eClassFeatureNamePairToEStructuralFeatureMap = new HashMap<>();
            isOptionUseXMLNameToFeatureSet = false;
        } else {
            isOptionUseXMLNameToFeatureSet = true;
            if (helper instanceof XMLHelper && featuresToKinds != null) {
                ((XMLHelper) helper).featuresToKinds = featuresToKinds;
            }
        }

        // The entity handler is the best place to resolve and deresolve URIs since it can do it there just once to produce the entity.
        // So most often the entity handler will be a URI handler as well and when used as a URI handler will be an identity handler.
        //
        uriHandler = (URIHandler) options.get(XMLResource.OPTION_URI_HANDLER);
        resourceEntityHandler = (ResourceEntityHandler) options.get(XMLResource.OPTION_RESOURCE_ENTITY_HANDLER);
        if (resourceEntityHandler != null) {
            resourceEntityHandler.reset();
            if (uriHandler == null && resourceEntityHandler instanceof URIHandler) {
                uriHandler = (URIHandler) resourceEntityHandler;
                uriHandler.setBaseURI(resourceURI);
            }
        }
    }

    public void reset() {
        this.xmlResource = null;
        this.extendedMetaData = null;
        // bug #126072
        eClassFeatureNamePair.eClass = null;
        eClassFeatureNamePairToEStructuralFeatureMap = null;
        if (isOptionUseXMLNameToFeatureSet && helper instanceof XMLHelper) {
            featuresToKinds = helper.featuresToKinds;
        } else {
            featuresToKinds = null;
        }
    
    /*if (ecoreBuilder != null)
    {
        this.ecoreBuilder.setExtendedMetaData(null);
    }*/
        this.helper = null;
        elements.clear();
        objects.clear();
        mixedTargets.clear();
        contextFeature = null;
        eObjectToExtensionMap = null;
        // external schema locations should only be processed once, i.e. in the subsequent parse
        // there is no need to process those again.
        externalURIToLocations = null;

        types.clear();
        prefixesToFactories.clear();
        forwardSingleReferences.clear();
        forwardManyReferences.clear();
        sameDocumentProxies.clear();
        for (int i = 0; i < identifiers.length; i++) {
            identifiers[i] = null;
        }
        for (int i = 0; i < positions.length; i++) {
            positions[i] = 0;
        }
        capacity = ARRAY_SIZE;
        resourceSet = null;
        packageRegistry = null;
        resourceURI = null;
        extent = null;
        deferredExtent = null;
        attribs = null;
//    locator = null;
        urisToLocations = null;
        resourceEntityHandler = null;
        uriHandler = null;
        documentRoot = null;
        usedNullNamespacePackage = false;
        isNamespaceAware = false;
    }

    //
    // Overwrite DefaultHandler methods
    //

    public void ignorableWhitespace(char[] ch, int start, int length) throws XMLParseException {
        // Do nothing.
    }

    public void skippedEntity(String name) throws XMLParseException {
        // Do nothing.
    }

//  protected XMIException toXMIException(SAXParseException e)
//  {
//    XMIException xmiException =
//      new XMIException
//        (e.getException() == null ? e : e.getException(),
//         e.getSystemId() == null ? getLocation() : e.getSystemId(),
//         e.getLineNumber(),
//         e.getColumnNumber());
//    return xmiException;
//  }
//
//  @Override
//  public void warning(SAXParseException e) throws XMLParseException
//  {
//    warning(toXMIException(e));
//  }
//
//  @Override
//  public void error(SAXParseException e) throws XMLParseException
//  {
//    error(toXMIException(e));
//  }
//
//  @Override
//  public void fatalError(SAXParseException e) throws XMLParseException
//  {
//    fatalError(toXMIException(e));
//    throw e;
//  }
//
//  @Override
//  public void setDocumentLocator(Locator locator)
//  {
//    setLocator(locator);
//  }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws XMLParseException {
        setAttributes(attributes);
        startElement(uri, localName, qName);
    }

    //
    // Implement LexicalHandler methods
    //

    public void startEntity(String name) {
        if (resourceEntityHandler != null) {
            text = new StringBuffer();
        }
    }

    public void endEntity(String name) {
        if (resourceEntityHandler != null) {
            resourceEntityHandler.handleEntity(name, text.toString());
        }
    }

    public void comment(char[] ch, int start, int length) // throws XMLParseException
    {
        if (mixedTargets.peek() != null) {
            if (text != null) {
                handleMixedText();
            }

            handleComment(new String(ch, start, length));
        }
    }

    public void startCDATA() {
        if (mixedTargets.peek() != null) {
            if (text != null) {
                handleMixedText();
            }
            text = new StringBuffer();
        }
    }

    public void endCDATA() {
        if (mixedTargets.peek() != null && text != null) {
            handleCDATA();
        }
    }

    //
    // Implement DTDHandler methods
    //
    public void startDTD(String name, String publicId, String systemId) {
        xmlResource.setDoctypeInfo(publicId, systemId);
    }

    public void endDTD() {
        // Do nothing.
    }

    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws XMLParseException {
        // Do nothing.
    }

    public void notationDecl(String name, String publicId, String systemId) throws XMLParseException {
        // Do nothing.
    }
//
//  //
//  // Implement EntityResolver methods
//  //
//  @Override
//  public InputSource resolveEntity(String publicId, String systemId) throws XMLParseException
//  {
//    try
//    {
//      Map<Object, Object> options = new HashMap<Object, Object>();
//      options.put("publicId", publicId);
//      options.put("systemId", systemId);
//      options.put("baseLocation", resourceURI == null ? null : resourceURI.toString());
//      URI uri = URI.createURI(systemId);
//      if (resolve && uri.isRelative() && uri.hasRelativePath())
//      {
//        uri = helper.resolve(uri, resourceURI);
//      }
//      InputStream inputStream = getURIConverter().createInputStream(uri, options);
//      InputSource result = new InputSource(inputStream);
//      result.setPublicId(publicId);
//      result.setSystemId(systemId);
//      return result;
//    }
//    catch (IOException exception)
//    {
//      throw new XMLParseException(exception);
//    }
//  }
//

    /**
     * Returns the xsi type attribute's value.
     */
    protected abstract String getXSIType();

    /**
     * Process the XML attributes for the newly created object.
     */
    protected abstract void handleObjectAttribs(EObject obj);

    /**
     * Process the XML namespace declarations.
     * @deprecated since 2.2
     */
    @Deprecated
    protected void handleNamespaceAttribs() {
        for (int i = 0, size = attribs.getLength(); i < size; ++i) {
            String attrib = attribs.getQName(i);
            if (attrib.startsWith(XMLResource.XML_NS)) {
                handleXMLNSAttribute(attrib, attribs.getValue(i));
            } else if (SCHEMA_LOCATION_ATTRIB.equals(attrib)) {
                handleXSISchemaLocation(attribs.getValue(i));
            } else if (NO_NAMESPACE_SCHEMA_LOCATION_ATTRIB.equals(attrib)) {
                handleXSINoNamespaceSchemaLocation(attribs.getValue(i));
            }
        }
    }

    protected void handleSchemaLocation() {
        String xsiSchemLocation = attribs.getValue(ExtendedMetaData.XSI_URI, XMLResource.SCHEMA_LOCATION);
        if (xsiSchemLocation != null) {
            handleXSISchemaLocation(xsiSchemLocation);
        }

        String xsiNoNamespaceSchemLocation = attribs.getValue(ExtendedMetaData.XSI_URI, XMLResource.NO_NAMESPACE_SCHEMA_LOCATION);
        if (xsiNoNamespaceSchemLocation != null) {
            handleXSINoNamespaceSchemaLocation(xsiNoNamespaceSchemLocation);
        }
    }

    /**
     * Returns true if the xsi:nil attribute is in the list of attributes.
     */
    protected boolean isNull() {
        String value = isNamespaceAware ? attribs.getValue(ExtendedMetaData.XSI_URI, XMLResource.NIL) : attribs.getValue(NIL_ATTRIB);
        if (value != null) {
            try {
                return XMLTypeFactory.eINSTANCE.createBoolean(value);
            } catch (RuntimeException exception) {
                error(new XMIException(exception));
            }
        }
        return false;
    }

    /**
     * Sets the current attributes and returns the old ones.
     */
    protected Object setAttributes(Object attributes) {
        Object oldAttribs = attribs;
        this.attribs = (Attributes) attributes;
        return oldAttribs;
    }

    /**
     * Sets the object that might be used for determining the line and
     * column number.
     */
//  protected void setLocator(Object locator)
//  {
//    this.locator = (Locator)locator;
//  }
//
//  protected void recordHeaderInformation()
//  {
//    if (locator != null)
//    {
//      Class<?> locatorClass = locator.getClass();
//      try
//      {
//        Method encodingMethod = locatorClass.getMethod("getEncoding");
//        String encoding = (String)encodingMethod.invoke(locator);
//        if (encoding != null)
//        {
//          this.xmlResource.setEncoding(encoding);
//        }
//
//        Method versionMethod = locatorClass.getMethod("getXMLVersion");
//        String version = (String)versionMethod.invoke(locator);
//        if (version != null)
//        {
//          this.xmlResource.setXMLVersion(version);
//        }
//      }
//      catch (NoSuchMethodException e)
//      {
//        // Ignore.
//      }
//      catch (IllegalAccessException e)
//      {
//        // Ignore.
//      }
//      catch (InvocationTargetException e)
//      {
//        // Ignore.
//      }
//    }
//  }
    public void startDocument() {
        isRoot = true;
        helper.pushContext();
        needsPushContext = true;
    }

    /**
     * This method determines whether to make an object or not, then makes an
     * object based on the XML attributes and the metamodel.
     */
    public void startElement(String uri, String localName, String name) {
        if (needsPushContext) {
            helper.pushContext();
        }
        needsPushContext = true;
        if (text != null && text.length() > 0) {
            if (mixedTargets.peek() != null) {
                handleMixedText();
            } else {
                text = null;
            }
        }

        elements.push(name);
        String prefix = "";

        if (useNewMethods) {
            if (isRoot) {
                handleSchemaLocation();
            }
            prefix = helper.getPrefix((uri.length() == 0) ? null : uri);
            prefix = (prefix == null) ? "" : prefix;
        } else {
            handleNamespaceAttribs();
            int index = name.indexOf(':', 0);
            localName = name;
            if (index != -1) {
                prefix = name.substring(0, index);
                localName = name.substring(index + 1);
            }
        }
        processElement(name, prefix, localName);
    }

    protected void processElement(String name, String prefix, String localName) {
        if (isRoot) {
            isRoot = false;
//      recordHeaderInformation();
        }
        if (isError()) {
            types.push(ERROR_TYPE);
        } else {
            if (objects.isEmpty()) {
                createTopObject(prefix, localName);
            } else {
                handleFeature(prefix, localName);
            }
        }
    }

    protected void handleForwardReferences() {
        handleForwardReferences(false);
    }

    /**
     * Check if the values of the forward references have been set (they may
     * have been set due to a bidirectional reference being set).  If not, set them.
     * If this is called during end document processing, errors should be diagnosed.
     * If it is called in the middle of a document,
     * we need to clean up the forward reference lists to avoid processing resolved references again later.
     */
    protected void handleForwardReferences(boolean isEndDocument) {
        // Handle the same document proxies, which may have problems resulting from the
        // other end of a bidirectional reference being handled as an IDREF rather than as a proxy.
        // When we are done with these, we know that funny proxies are now resolved as if they were handled as IDREFs.
        //
        for (Iterator<InternalEObject> i = sameDocumentProxies.iterator(); i.hasNext(); ) {
            InternalEObject proxy = i.next();

            // Look through all the references...
            //
            LOOP:
            for (EReference eReference : proxy.eClass().getEAllReferences()) {
                // And find the one that holds this proxy.
                //
                EReference oppositeEReference = eReference.getEOpposite();
                if (oppositeEReference != null && oppositeEReference.isChangeable() && proxy.eIsSet(eReference)) {
                    // Try to resolve the proxy locally.
                    //
                    EObject resolvedEObject = xmlResource.getEObject(proxy.eProxyURI().fragment());
                    if (resolvedEObject != null) {
                        // We won't need to process this again later.
                        //
                        if (!isEndDocument) {
                            i.remove();
                        }

                        // Compute the holder of the proxy
                        //
                        EObject proxyHolder = (EObject) (eReference.isMany() ? ((List<?>) proxy.eGet(eReference)).get(0) : proxy.eGet(eReference));

                        // If the proxy holder can hold many values,
                        // it may contain a duplicate that resulted when the other end was processed as an IDREF
                        // and hence did both sides of the bidirectional relation.
                        //
                        if (oppositeEReference.isMany()) {
                            // So if the resolved object is also present...
                            //
                            InternalEList<?> holderContents = (InternalEList<?>) proxyHolder.eGet(oppositeEReference);
                            int resolvedEObjectIndex = holderContents.basicIndexOf(resolvedEObject);
                            if (resolvedEObjectIndex != -1) {
                                // Move the resolved object to the right place, remove the proxy, and we're done.
                                //
                                int proxyIndex = holderContents.basicIndexOf(proxy);
                                holderContents.move(proxyIndex, resolvedEObjectIndex);
                                holderContents.remove(proxyIndex > resolvedEObjectIndex ? proxyIndex - 1 : proxyIndex + 1);
                                break LOOP;
                            }
                        }

                        if (!oppositeEReference.getEType().isInstance(resolvedEObject)) {
                            error
                                    (new IllegalValueException
                                             (proxyHolder,
                                              oppositeEReference,
                                              resolvedEObject,
                                              null,
                                              getLocation(),
                                              getLineNumber(),
                                              getColumnNumber()));
                            break LOOP;
                        }

                        // If the resolved object doesn't contain a reference to the proxy holder as it should.
                        //
                        if (eReference.isMany() ?
                                !((InternalEList<?>) resolvedEObject.eGet(eReference)).basicContains(proxyHolder) :
                                resolvedEObject.eGet(eReference) != proxyHolder) {
                            // The proxy needs to be replaced in a way that updates both ends of the reference.
                            //
                            if (oppositeEReference.isMany()) {
                                @SuppressWarnings("unchecked")
                                InternalEList<EObject> proxyHolderList = (InternalEList<EObject>) proxyHolder.eGet(oppositeEReference);
                                proxyHolderList.setUnique(proxyHolderList.basicIndexOf(proxy), resolvedEObject);
                            } else {
                                proxyHolder.eSet(oppositeEReference, resolvedEObject);
                            }
                        }
                    }

                    break;
                }
            }
        }

        for (Iterator<SingleReference> i = forwardSingleReferences.iterator(); i.hasNext(); ) {
            SingleReference ref = i.next();
            EObject obj = xmlResource.getEObject((String) ref.getValue());

            if (obj != null) {
                // We won't need to process this again later.
                if (!isEndDocument) {
                    i.remove();
                }
                EStructuralFeature feature = ref.getFeature();
                setFeatureValue(ref.getObject(), feature, obj, ref.getPosition());
            } else if (isEndDocument) {
                error
                        (new UnresolvedReferenceException
                                 ((String) ref.getValue(),
                                  getLocation(),
                                  ref.getLineNumber(),
                                  ref.getColumnNumber()));
            }
        }

        for (Iterator<ManyReference> i = forwardManyReferences.iterator(); i.hasNext(); ) {
            ManyReference ref = i.next();
            Object[] values = ref.getValues();

            boolean failure = false;
            for (int j = 0, l = values.length; j < l; j++) {
                String id = (String) values[j];
                EObject obj = xmlResource.getEObject(id);
                values[j] = obj;

                if (obj == null) {
                    failure = true;
                    if (isEndDocument) {
                        error
                                (new UnresolvedReferenceException
                                         (id,
                                          getLocation(),
                                          ref.getLineNumber(),
                                          ref.getColumnNumber()));
                    }
                }
            }

            if (!failure) {
                if (!isEndDocument) {
                    i.remove();
                }
                setFeatureValues(ref);
            } else if (isEndDocument) {
                // At least set the references that we were able to resolve, if any.
                //
                setFeatureValues(ref);
            }
        }
    }

    /**
     * Check if the values of the forward references have been set (they may
     * have been set due to a bi-directional reference being set).  If not,
     * set them.
     */
    public void endDocument() {
        if (deferredExtent != null) {
            extent.addAll(deferredExtent);
        }

        // Pretend there is an xmlns="" because we really need to ensure that the null prefix
        // isn't used to denote something other than the null namespace.
        //
        if (usedNullNamespacePackage) {
            helper.addPrefix("", "");
        }
        helper.recordPrefixToURIMapping();
        helper.popContext();
        handleForwardReferences(true);

        if (disableNotify) {
            for (Iterator<?> i = EcoreUtil.getAllContents(xmlResource.getContents(), false); i.hasNext(); ) {
                EObject eObject = (EObject) i.next();
                eObject.eSetDeliver(true);
            }
        }

        if (extendedMetaData != null) {
            if (extent.size() == 1) {
                EObject root = extent.get(0);
                recordNamespacesSchemaLocations(root);
            }

            if (DEBUG_DEMANDED_PACKAGES) {
                // EATM temporary for debug purposes only.
                //
                Collection<EPackage> demandedPackages = EcoreUtil.copyAll(extendedMetaData.demandedPackages());
                for (EPackage ePackage : demandedPackages) {
                    ePackage.setName(ePackage.getNsURI());
                }
                extent.addAll(demandedPackages);
            }
        }
    }

    protected EMap<String, String> recordNamespacesSchemaLocations(EObject root) {
        EClass eClass = root.eClass();
        EReference xmlnsPrefixMapFeature = extendedMetaData.getXMLNSPrefixMapFeature(eClass);
        EMap<String, String> xmlnsPrefixMap = null;
        if (xmlnsPrefixMapFeature != null) {
            @SuppressWarnings("unchecked")
            EMap<String, String> newXMLNSPrefixMap = (EMap<String, String>) root.eGet(xmlnsPrefixMapFeature);
            xmlnsPrefixMap = newXMLNSPrefixMap;
            xmlnsPrefixMap.putAll(helper.getPrefixToNamespaceMap());
        }

        if (urisToLocations != null) {
            EReference xsiSchemaLocationMapFeature = extendedMetaData.getXSISchemaLocationMapFeature(eClass);
            if (xsiSchemaLocationMapFeature != null) {
                @SuppressWarnings("unchecked")
                EMap<String, String> newXSISchemaLocationMap = (EMap<String, String>) root.eGet(xsiSchemaLocationMapFeature);
                EMap<String, String> xsiSchemaLocationMap = newXSISchemaLocationMap;
                for (Map.Entry<String, URI> entry : urisToLocations.entrySet()) {
                    xsiSchemaLocationMap.put(entry.getKey(), entry.getValue().toString());
                }
            }
        }
        return xmlnsPrefixMap;
    }

    /**
     * Create an object based on the prefix and type name.
     */
    protected EObject createObjectByType(String prefix, String name, boolean top) {
        if (top) {
            handleTopLocations(prefix, name);
        }

        EFactory eFactory = getFactoryForPrefix(prefix);
        String uri = helper.getURI(prefix);
        if (eFactory == null && prefix.equals("") && uri == null) {
            EPackage ePackage = handleMissingPackage(null);
            if (ePackage == null) {
                error
                        (new PackageNotFoundException
                                 (null,
                                  getLocation(),
                                  getLineNumber(),
                                  getColumnNumber()));
            } else {
                eFactory = ePackage.getEFactoryInstance();
            }
        }

        documentRoot = createDocumentRoot(prefix, uri, name, eFactory, top);

        if (documentRoot != null) {
            return documentRoot;
        }

        EObject newObject = null;
        if (useNewMethods) {
            newObject = createObject(eFactory, helper.getType(eFactory, name), false);
        } else {
            newObject = createObjectFromFactory(eFactory, name);
        }
        newObject = validateCreateObjectFromFactory(eFactory, name, newObject, top);

        if (top) {
            processTopObject(newObject);
            // check for simple feature
            if (extendedMetaData != null && newObject != null) {
                EStructuralFeature simpleFeature = extendedMetaData.getSimpleFeature(newObject.eClass());
                if (simpleFeature != null) {
                    isSimpleFeature = true;
                    isIDREF = simpleFeature instanceof EReference;
                    objects.push(null);
                    mixedTargets.push(null);
                    types.push(simpleFeature);
                    text = new StringBuffer();
                }
            }
        }
        return newObject;
    }

    protected EObject createDocumentRoot(String prefix, String uri, String name, EFactory eFactory, boolean top) {
        if (extendedMetaData != null && eFactory != null) {
            EPackage ePackage = eFactory.getEPackage();
            EClass eClass = null;
            if (useConfigurationCache) {
        /*eClass = ConfigurationCache.INSTANCE.getDocumentRoot(ePackage);
        if (eClass == null)
        {
          eClass = extendedMetaData.getDocumentRoot(ePackage);
          ConfigurationCache.INSTANCE.putDocumentRoot(ePackage, eClass);
        }*/
            } else {
                eClass = extendedMetaData.getDocumentRoot(ePackage);
            }
            if (eClass != null) {
                // EATM Kind of hacky.
                String typeName = extendedMetaData.getName(eClass);
                @SuppressWarnings("deprecation")
                EObject newObject =
                        useNewMethods ?
                                createObject(eFactory, eClass, true) :
                                helper.createObject(eFactory, typeName);
                validateCreateObjectFromFactory(eFactory, typeName, newObject);
                if (top) {
                    if (suppressDocumentRoot) {
                        // Set up a deferred extent so the document root we create definitely will not be added to the resource.
                        //
                        List<EObject> oldDeferredExtent = deferredExtent;
                        try {
                            deferredExtent = new ArrayList<EObject>();
                            processTopObject(newObject);
                        } finally {
                            deferredExtent = oldDeferredExtent;
                        }
                        handleFeature(prefix, name);

                        // Remove the document root's information from the top of the stacks.
                        //
                        objects.remove(0);
                        mixedTargets.remove(0);
                        types.remove(0);

                        // Process the new root object if any.
                        //
                        EObject peekObject = objects.peek();
                        if (peekObject == null) {
                            // There's an EObject on the stack already.
                            //
                            if (objects.size() > 1) {
                                // Excise the new root from the document root.
                                //
                                EcoreUtil.remove(peekObject = objects.get(0));
                            } else {
                                // If there is no root object, we're dealing with an EAttribute feature instead of an EReference feature.
                                // So create an instance of simple any type and prepare it to handle the text content.
                                //
                                SimpleAnyType simpleAnyType = (SimpleAnyType) EcoreUtil.create(anySimpleType);
                                simpleAnyType.setInstanceType(((EAttribute) types.peek()).getEAttributeType());
                                objects.set(0, simpleAnyType);
                                types.set(0, XMLTypePackage.Literals.SIMPLE_ANY_TYPE__RAW_VALUE);
                                mixedTargets.set(0, simpleAnyType.getMixed());
                                peekObject = simpleAnyType;
                            }
                        } else {
                            // Excise the new root from the document root.
                            //
                            EcoreUtil.remove(peekObject);
                        }
                        // Do the extent processing that should have been done for the root but was actualljy done for the document root.
                        //
                        if (deferredExtent != null) {
                            deferredExtent.add(peekObject);
                        } else {
                            extent.addUnique(peekObject);
                        }

                        // The new root object is the actual new object since all sign of the document root will now have disappeared.
                        //
                        newObject = peekObject;
                    } else {
                        processTopObject(newObject);
                        handleFeature(prefix, name);
                    }
                }
                return newObject;
            }
        }
        return null;
    }

    protected void createTopObject(String prefix, String name) {
        createObjectByType(prefix, name, true);
    }

    /**
     * Add object to extent and call processObject.
     */
    protected void processTopObject(EObject object) {
        if (object != null) {
            if (deferredExtent != null) {
                deferredExtent.add(object);
            } else {
                extent.addUnique(object);
            }

            if (extendedMetaData != null && !mixedTargets.isEmpty()) {
                FeatureMap featureMap = mixedTargets.pop();
                EStructuralFeature target = extendedMetaData.getMixedFeature(object.eClass());
                if (target != null) {
                    FeatureMap otherFeatureMap = (FeatureMap) object.eGet(target);
                    for (FeatureMap.Entry entry : new ArrayList<FeatureMap.Entry>(featureMap)) {
                        // Ignore a whitespace only text entry at the beginning.
                        //
                        if (entry.getEStructuralFeature() != XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__TEXT ||
                                !"".equals(XMLTypeUtil.normalize(entry.getValue().toString(), true))) {
                            otherFeatureMap.add(entry.getEStructuralFeature(), entry.getValue());
                        }
                    }
                }
                text = null;
            }
        }

        processObject(object);
    }

    /**
     * Pop the appropriate stacks and set features whose values are in
     * the content of XML elements.
     */
    public void endElement(String uri, String localName, String name) {
        elements.pop();
        Object type = types.pop();
        if (type == OBJECT_TYPE) {
            if (text == null) {
                objects.pop();
                mixedTargets.pop();
            } else {
                EObject object = objects.popEObject();
                if (mixedTargets.peek() != null &&
                        (object.eContainer() != null ||
                                recordUnknownFeature &&
                                        (eObjectToExtensionMap.containsValue(object) || ((InternalEObject) object).eDirectResource() != null))) {
                    handleMixedText();
                    mixedTargets.pop();
                } else {
                    if (text.length() != 0) {
                        handleProxy((InternalEObject) object, text.toString().trim());
                    }
                    mixedTargets.pop();
                    text = null;
                }
            }
        } else if (isIDREF) {
            objects.pop();
            mixedTargets.pop();
            if (text != null) {
                setValueFromId(objects.peekEObject(), (EReference) type, text.toString());
                text = null;
            }
            isIDREF = false;
        } else if (isTextFeatureValue(type)) {
            EObject eObject = objects.popEObject();
            mixedTargets.pop();
            if (eObject == null) {
                eObject = objects.peekEObject();
            }
            setFeatureValue(eObject, (EStructuralFeature) type, text == null ? null : text.toString());
            text = null;
        }

        if (isSimpleFeature) {
            types.pop();
            objects.pop();
            mixedTargets.pop();
            isSimpleFeature = false;
        }
        helper.popContext(prefixesToFactories);
    }

    protected boolean isTextFeatureValue(Object type) {
        return type != ERROR_TYPE;
    }

    public void startPrefixMapping(String prefix, String uri) {
        isNamespaceAware = true;

        if (needsPushContext) {
            helper.pushContext();
            needsPushContext = false;
        }

        //if (useNonDeprecatedMethods)
        //{
        helper.addPrefix(prefix, uri);
        prefixesToFactories.remove(prefix);
        //}
    }

    public void endPrefixMapping(String prefix) {
        // Do nothing.
    }

    public void characters(char[] ch, int start, int length) {
        if (text == null && mixedTargets.peek() != null) {
            text = new StringBuffer();
        }

        if (text != null) {
            text.append(ch, start, length);
        }
    }

    public void processingInstruction(String target, String data) {
        if (mixedTargets.peek() != null) {
            if (text != null) {
                handleMixedText();
            }

            handleProcessingInstruction(target, data);
        }
    }

    protected void handleXMLNSAttribute(String attrib, String value) {
        // Handle namespaces
        int index = attrib.indexOf(':', 0);
        String prefix = index == -1 ? "" : attrib.substring(index + 1);
        helper.addPrefix(prefix, value);
        prefixesToFactories.remove(prefix);
    }

    protected void handleXSISchemaLocation(String schemaLocations) {
        if (urisToLocations == null) {
            urisToLocations = new HashMap<String, URI>();
            xmlResource.getDefaultSaveOptions().put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        }

        String[] tokens = schemaLocations.split(" ");
        for (int i = 0; i < tokens.length; i++) {
            String key = tokens[i];
            if (i + 1 < tokens.length) {
                String value = tokens[++i];
                URI uri = URI.createURI(value);
                if (uriHandler != null) {
                    uri = uriHandler.resolve(uri);
                } else if (resolve && uri.isRelative() && uri.hasRelativePath()) {
                    uri = helper.resolve(uri, resourceURI);
                }
                urisToLocations.put(key, uri);
            }
        }
    }

    protected void handleXSINoNamespaceSchemaLocation(String noNamespaceSchemaLocation) {
        if (urisToLocations == null) {
            urisToLocations = new HashMap<String, URI>();
            xmlResource.getDefaultSaveOptions().put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        }

        URI uri = URI.createURI(noNamespaceSchemaLocation);
        if (uriHandler != null) {
            uri = uriHandler.resolve(uri);
        } else if (resolve && uri.isRelative() && uri.hasRelativePath()) {
            uri = helper.resolve(uri, resourceURI);
        }
        urisToLocations.put(null, uri);
    }

    protected void processSchemaLocations(String prefix, String name) {
    /*if (urisToLocations != null)
    {
      // If processSchemaLocations is specified, treat these as XML Schema locations
      if (processSchemaLocations)
      {
        try
        {
          ecoreBuilder.generate(urisToLocations);
        }
        catch (Exception exception)
        {
          XMIPlugin.INSTANCE.log(exception);
        }
      }
      // If externalSchemaLocations are specified, process these ones as well
      try
      {
        if (externalURIToLocations != null)
        {
          ecoreBuilder.generate(externalURIToLocations);
        }
      }
      catch (Exception exception)
      {
        XMIPlugin.INSTANCE.log(exception);
      }

      URI locationForNull = urisToLocations.get(null);
      if (locationForNull != null && helper.getNoNamespacePackage() == null)
      {
        helper.setNoNamespacePackage(getPackageForURI(locationForNull.toString()));
      }
    }
    else if (externalURIToLocations != null)
    {
      try
      {
        ecoreBuilder.generate(externalURIToLocations);
      }
      catch (Exception exception)
      {
        XMIPlugin.INSTANCE.log(exception);
      }
    }*/
    }

    protected void handleTopLocations(String prefix, String name) {
        processSchemaLocations(prefix, name);
        if (processAnyXML) {
            // Ensure that anything can be handled, even if it's not recognized.
            //
            String uri = helper.getURI(prefix);
            if (extendedMetaData.getPackage(uri) == null) {
                extendedMetaData.demandFeature(uri, name, true);
            }
        }
    }

    /**
     * The XML element represents a feature. There are two
     * cases to handle:
     * 1. The feature has a type that is a datatype.
     * 2. The feature has a type that is a class.
     */
    protected void handleFeature(String prefix, String name) {
        EObject peekObject = objects.peekEObject();

        // This happens when processing an element with simple content that has elements content even though it shouldn't.
        //
        if (peekObject == null) {
            types.push(ERROR_TYPE);
            error
                    (new FeatureNotFoundException
                             (name,
                              null,
                              getLocation(),
                              getLineNumber(),
                              getColumnNumber()));
            return;
        }

        EStructuralFeature feature = getFeature(peekObject, prefix, name, true);
        if (feature != null) {
            int kind = helper.getFeatureKind(feature);
            if (kind == XMLHelper.DATATYPE_SINGLE || kind == XMLHelper.DATATYPE_IS_MANY) {
                objects.push(null);
                mixedTargets.push(null);
                types.push(feature);
                if (!isNull()) {
                    text = new StringBuffer();
                }
            } else if (extendedMetaData != null) {
                EReference eReference = (EReference) feature;
                boolean isContainment = eReference.isContainment();
                if (!isContainment && !eReference.isResolveProxies() && extendedMetaData.getFeatureKind(feature) != ExtendedMetaData.UNSPECIFIED_FEATURE) {
                    isIDREF = true;
                    objects.push(null);
                    mixedTargets.push(null);
                    types.push(feature);
                    text = new StringBuffer();
                } else {
                    createObject(peekObject, feature);
                    EObject childObject = objects.peekEObject();
                    if (childObject != null) {
                        if (isContainment) {
                            EStructuralFeature simpleFeature = extendedMetaData.getSimpleFeature(childObject.eClass());
                            if (simpleFeature != null) {
                                isSimpleFeature = true;
                                isIDREF = simpleFeature instanceof EReference;
                                objects.push(null);
                                mixedTargets.push(null);
                                types.push(simpleFeature);
                                text = new StringBuffer();
                            }
                        } else if (!childObject.eIsProxy()) {
                            text = new StringBuffer();
                        }
                    }
                }
            } else {
                createObject(peekObject, feature);
            }
        } else {
            // Try to get a general-content feature.
            // Use a pattern that's not possible any other way.
            //
            if (xmlMap != null && (feature = getFeature(peekObject, null, "", true)) != null) {

                EFactory eFactory = getFactoryForPrefix(prefix);

                // This is for the case for a local unqualified element that has been bound.
                //
                if (eFactory == null) {
                    eFactory = feature.getEContainingClass().getEPackage().getEFactoryInstance();
                }

                EObject newObject = null;
                if (useNewMethods) {
                    newObject = createObject(eFactory, helper.getType(eFactory, name), false);
                } else {
                    newObject = createObjectFromFactory(eFactory, name);
                }
                newObject = validateCreateObjectFromFactory(eFactory, name, newObject, feature);
                if (newObject != null) {
                    setFeatureValue(peekObject, feature, newObject);
                }
                processObject(newObject);
            } else {
                // This handles the case of a substitution group.
                //
                if (xmlMap != null) {
                    EFactory eFactory = getFactoryForPrefix(prefix);
                    EObject newObject = createObjectFromFactory(eFactory, name);
                    validateCreateObjectFromFactory(eFactory, name, newObject);
                    if (newObject != null) {
                        for (EReference eReference : peekObject.eClass().getEAllReferences()) {
                            if (eReference.getEType().isInstance(newObject)) {
                                setFeatureValue(peekObject, eReference, newObject);
                                processObject(newObject);
                                return;
                            }
                        }
                    }
                }

                handleUnknownFeature(prefix, name, true, peekObject, null);
            }
        }
    }

    protected int getLineNumber() {
        return -1;
//    if (locator != null)
//    {
//      return locator.getLineNumber();
//    }
//    else
//    {
//      return -1;
//    }
    }

    protected int getColumnNumber() {
        return -1;
//    if (locator != null)
//    {
//      return locator.getColumnNumber();
//    }
//    else
//    {
//      return -1;
//    }
    }

    protected String getLocation() {
        return "";
//    return
//      locator != null && locator.getSystemId() != null ?
//        locator.getSystemId() :
//        resourceURI == null ? "" : resourceURI.toString();
    }

    protected AnyType getExtension(EObject peekObject) {
        AnyType anyType = eObjectToExtensionMap.get(peekObject);
        if (anyType == null) {
            anyType = XMLTypeFactory.eINSTANCE.createAnyType();
            eObjectToExtensionMap.put(peekObject, anyType);
        }
        return anyType;
    }

    protected void handleUnknownFeature(String prefix, String name, boolean isElement, EObject peekObject, String value) {
        if (recordUnknownFeature) {
            recordUnknownFeature(prefix, name, isElement, peekObject, value);
        } else {
            reportUnknownFeature(prefix, name, isElement, peekObject, value);
        }
    }

    protected void recordUnknownFeature(String prefix, String name, boolean isElement, EObject peekObject, String value) {
        if (isElement) {
            AnyType anyType = getExtension(peekObject);
            int objectsIndex = objects.size();
            objects.push(anyType);
            int mixedTargetsIndex = mixedTargets.size();
            mixedTargets.push(anyType.getAny());
            int typesIndex = types.size();
            types.push(UNKNOWN_FEATURE_TYPE);

            handleFeature(prefix, name);

            objects.remove(objectsIndex);
            mixedTargets.remove(mixedTargetsIndex);
            types.remove(typesIndex);
        } else {
            AnyType anyType = getExtension(peekObject);
            setAttribValue(anyType, prefix == null ? name : prefix + ":" + name, value);
        }
    }

    protected void reportUnknownFeature(String prefix, String name, boolean isElement, EObject peekObject, String value) {
        if (isElement) {
            types.push(ERROR_TYPE);
        }
        error
                (new FeatureNotFoundException
                         (name,
                          peekObject,
                          getLocation(),
                          getLineNumber(),
                          getColumnNumber()));
    }

    public void error(XMIException e) {
        xmlResource.getErrors().add(e);
    }

    public void warning(XMIException e) {
        xmlResource.getWarnings().add(e);
    }

    public void fatalError(XMIException e) {
        xmlResource.getErrors().add(e);
    }

    /**
     * Create an object based on the given feature and attributes.
     */
    protected void createObject(EObject peekObject, EStructuralFeature feature) {
        if (isNull()) {
            setFeatureValue(peekObject, feature, null);
            objects.push(null);
            mixedTargets.push(null);
            types.push(OBJECT_TYPE);
        } else {
            String xsiType = getXSIType();
            if (xsiType != null) {
                createObjectFromTypeName(peekObject, xsiType, feature);
            } else {
                createObjectFromFeatureType(peekObject, feature);
                // This check is redundant -- see handleFeature method (EL)
        /*if (extendedMetaData != null && !((EReference)feature).isContainment())
        {
          text = new StringBuffer();
        }*/
                if (xmlMap != null && !((EReference) feature).isContainment()) {
                    XMLInfo info = xmlMap.getInfo(feature);
                    if (info != null && info.getXMLRepresentation() == XMLInfo.ELEMENT) {
                        text = new StringBuffer();
                    }
                }
            }
        }
    }

    /**
     * Create an object from the given qualified type name.
     */
    protected EObject createObjectFromTypeName(EObject peekObject, String typeQName, EStructuralFeature feature) {
        String typeName = null;
        String prefix = "";
        int index = typeQName.indexOf(':', 0);
        if (index > 0) {
            prefix = typeQName.substring(0, index);
            typeName = typeQName.substring(index + 1);
        } else {
            typeName = typeQName;
        }

        contextFeature = feature;
        EFactory eFactory = getFactoryForPrefix(prefix);
        contextFeature = null;

        if (eFactory == null && prefix.equals("") && helper.getURI(prefix) == null) {
            contextFeature = feature;
            EPackage ePackage = handleMissingPackage(null);
            contextFeature = null;
            if (ePackage == null) {
                error(new PackageNotFoundException(null, getLocation(), getLineNumber(), getColumnNumber()));
            } else {
                eFactory = ePackage.getEFactoryInstance();
            }
        }
        EObject obj = null;
        if (useNewMethods) {
            obj = createObject(eFactory, helper.getType(eFactory, typeName), false);
        } else {
            obj = createObjectFromFactory(eFactory, typeName);
        }
        obj = validateCreateObjectFromFactory(eFactory, typeName, obj, feature);

        if (obj != null) {
            if (contextFeature == null) {
                setFeatureValue(peekObject, feature, obj);
            } else {
                contextFeature = null;
            }
        }

        processObject(obj);

        return obj;
    }

    /**
     * Create an object based on the type of the given feature.
     */
    protected EObject createObjectFromFeatureType(EObject peekObject, EStructuralFeature feature) {
        String typeName = null;
        EFactory factory = null;
        EClassifier eType = null;
        EObject obj = null;

        if (feature != null && (eType = feature.getEType()) != null) {
            if (useNewMethods) {
                if (extendedMetaData != null && eType == EcorePackage.Literals.EOBJECT && extendedMetaData.getFeatureKind(feature) != ExtendedMetaData.UNSPECIFIED_FEATURE) {
                    eType = anyType;
                    typeName = extendedMetaData.getName(anyType);
                    factory = anyType.getEPackage().getEFactoryInstance();
                } else {
                    factory = eType.getEPackage().getEFactoryInstance();
                    typeName = extendedMetaData == null ? eType.getName() : extendedMetaData.getName(eType);
                }
                obj = createObject(factory, eType, false);
            } else {

                if (extendedMetaData != null && eType == EcorePackage.Literals.EOBJECT && extendedMetaData.getFeatureKind(feature) != ExtendedMetaData.UNSPECIFIED_FEATURE) {
                    typeName = extendedMetaData.getName(anyType);
                    factory = anyType.getEPackage().getEFactoryInstance();
                } else {
                    EClass eClass = (EClass) eType;
                    typeName = extendedMetaData == null ? eClass.getName() : extendedMetaData.getName(eClass);
                    factory = eClass.getEPackage().getEFactoryInstance();
                }
                obj = createObjectFromFactory(factory, typeName);
            }
        }

        obj = validateCreateObjectFromFactory(factory, typeName, obj, feature);

        if (obj != null) {
            setFeatureValue(peekObject, feature, obj);
        }

        processObject(obj);
        return obj;
    }

    /**
     * @deprecated since 2.2
     * Create an object given a content helper, a factory, and a type name,
     * and process the XML attributes.
     */
    @Deprecated
    protected EObject createObjectFromFactory(EFactory factory, String typeName) {
        EObject newObject = null;

        if (factory != null) {
            newObject = helper.createObject(factory, typeName);

            if (newObject != null) {
                if (disableNotify) {
                    newObject.eSetDeliver(false);
                }

                handleObjectAttribs(newObject);
            }
        }

        return newObject;
    }

    protected EObject createObject(EFactory eFactory, EClassifier type, boolean documentRoot) {
        EObject newObject = helper.createObject(eFactory, type);
        if (newObject != null && !documentRoot) {
            if (disableNotify) {
                newObject.eSetDeliver(false);
            }
            handleObjectAttribs(newObject);
        }
        return newObject;
    }

    protected EObject validateCreateObjectFromFactory(EFactory factory, String typeName, EObject newObject, boolean top) {
        if (newObject == null && top && (recordUnknownFeature || processAnyXML) && factory != null && extendedMetaData != null) {
            String namespace = extendedMetaData.getNamespace(factory.getEPackage());
            if (namespace == null) {
                usedNullNamespacePackage = true;
            }
            if (useNewMethods) {
                EClassifier type = extendedMetaData.demandType(namespace, typeName);
                newObject = createObject(type.getEPackage().getEFactoryInstance(), type, false);
            } else {
                factory = extendedMetaData.demandType(namespace, typeName).getEPackage().getEFactoryInstance();
                newObject = createObjectFromFactory(factory, typeName);
            }
        }

        validateCreateObjectFromFactory(factory, typeName, newObject);
        return newObject;
    }

    protected void validateCreateObjectFromFactory(EFactory factory, String typeName, EObject newObject) {
        if (newObject == null) {
            error
                    (new EClassNotFoundException
                             (typeName,
                              factory,
                              getLocation(),
                              getLineNumber(),
                              getColumnNumber()));
        }
    }

    protected EObject validateCreateObjectFromFactory(EFactory factory, String typeName, EObject newObject, EStructuralFeature feature) {
        if (newObject != null) {
            if (extendedMetaData != null) {
                Collection<EPackage> demandedPackages = extendedMetaData.demandedPackages();
                if (!demandedPackages.isEmpty() && demandedPackages.contains(newObject.eClass().getEPackage())) {
                    if (recordUnknownFeature) {
                        EObject peekObject = objects.peekEObject();
                        if (!(peekObject instanceof AnyType)) {
                            AnyType anyType = getExtension(objects.peekEObject());
                            EStructuralFeature entryFeature =
                                    extendedMetaData.demandFeature(extendedMetaData.getNamespace(feature), extendedMetaData.getName(feature), true);
                            anyType.getAny().add(entryFeature, newObject);
                            contextFeature = entryFeature;
                        }
                        return newObject;
                    } else {
                        String namespace = extendedMetaData.getNamespace(feature);
                        String name = extendedMetaData.getName(feature);
                        EStructuralFeature wildcardFeature =
                                extendedMetaData.getElementWildcardAffiliation((objects.peekEObject()).eClass(), namespace, name);
                        if (wildcardFeature != null) {
                            int processingKind = laxWildcardProcessing ? ExtendedMetaData.LAX_PROCESSING : extendedMetaData.getProcessingKind(wildcardFeature);
                            switch (processingKind) {
                                case ExtendedMetaData.LAX_PROCESSING:
                                case ExtendedMetaData.SKIP_PROCESSING: {
                                    return newObject;
                                }
                            }
                        }
                    }

                    newObject = null;
                }
            }
        } else if (feature != null && factory != null && extendedMetaData != null) {
            // processing unknown feature with xsi:type (xmi:type)
            if (recordUnknownFeature || processAnyXML) {

                EObject result = null;
                String namespace = extendedMetaData.getNamespace(factory.getEPackage());
                if (namespace == null) {
                    usedNullNamespacePackage = true;
                }
                if (useNewMethods) {
                    EClassifier type = extendedMetaData.demandType(namespace, typeName);
                    result = createObject(type.getEPackage().getEFactoryInstance(), type, false);
                } else {
                    factory = extendedMetaData.demandType(namespace, typeName).getEPackage().getEFactoryInstance();
                    result = createObjectFromFactory(factory, typeName);
                }
                EObject peekObject = objects.peekEObject();
                if (!(peekObject instanceof AnyType)) {
                    AnyType anyType = getExtension(peekObject);
                    EStructuralFeature entryFeature =
                            extendedMetaData.demandFeature(extendedMetaData.getNamespace(feature), extendedMetaData.getName(feature), true);
                    anyType.getAny().add(entryFeature, result);
                    contextFeature = entryFeature;
                }
                return result;
            } else {
                String namespace = extendedMetaData.getNamespace(feature);
                String name = extendedMetaData.getName(feature);
                EStructuralFeature wildcardFeature =
                        extendedMetaData.getElementWildcardAffiliation((objects.peekEObject()).eClass(), namespace, name);
                if (wildcardFeature != null) {
                    int processingKind = laxWildcardProcessing ? ExtendedMetaData.LAX_PROCESSING : extendedMetaData.getProcessingKind(wildcardFeature);
                    switch (processingKind) {
                        case ExtendedMetaData.LAX_PROCESSING:
                        case ExtendedMetaData.SKIP_PROCESSING: {
                            // EATM Demand create metadata; needs to depend on processing mode...
                            String factoryNamespace = extendedMetaData.getNamespace(factory.getEPackage());
                            if (factoryNamespace == null) {
                                usedNullNamespacePackage = true;
                            }
                            if (useNewMethods) {
                                EClassifier type = extendedMetaData.demandType(factoryNamespace, typeName);
                                return createObject(type.getEPackage().getEFactoryInstance(), type, false);
                            } else {
                                factory = extendedMetaData.demandType(factoryNamespace, typeName).getEPackage().getEFactoryInstance();
                                return createObjectFromFactory(factory, typeName);
                            }
                        }
                    }
                }
            }
        }

        validateCreateObjectFromFactory(factory, typeName, newObject);

        return newObject;
    }

    /**
     * Add object to appropriate stacks.
     */
    protected void processObject(EObject object) {
        if (recordAnyTypeNSDecls && object instanceof AnyType) {
            FeatureMap featureMap = ((AnyType) object).getAnyAttribute();
            for (Map.Entry<String, String> entry : helper.getAnyContentPrefixToURIMapping().entrySet()) {
                Object uri = entry.getValue();
                featureMap.add(extendedMetaData.demandFeature(ExtendedMetaData.XMLNS_URI, entry.getKey(), false), uri == null ? "" : uri);
            }
        }

        if (object != null) {
            objects.push(object);
            types.push(OBJECT_TYPE);

            if (extendedMetaData != null) {
                EStructuralFeature mixedFeature = extendedMetaData.getMixedFeature(object.eClass());
                if (mixedFeature != null) {
                    mixedTargets.push((FeatureMap) object.eGet(mixedFeature));
                } else {
                    mixedTargets.push(null);
                }
            } else {
                mixedTargets.push(null);
            }
        } else {
            types.push(ERROR_TYPE);
        }
    }

    protected EFactory getFactoryForPrefix(String prefix) {
        EFactory factory = prefixesToFactories.get(prefix);
        if (factory == null) {
            String uri = helper.getURI(prefix);
            EPackage ePackage = getPackageForURI(uri);

            if (ePackage == null && uri == null && prefix.equals("")) {
                ePackage = helper.getNoNamespacePackage();
            }

            if (ePackage != null) {
                factory = ePackage.getEFactoryInstance();
                prefixesToFactories.put(prefix, factory);
                if (uri == null) {
                    usedNullNamespacePackage = true;
                }
            }
        }

        return factory;
    }

    /**
     * Attempt to get the namespace for the given prefix, then return
     * ERegister.getPackage() or null.
     */
    protected EPackage getPackageForURI(String uriString) {
        if (uriString == null) {
            return null;
        }

        EPackage ePackage =
                extendedMetaData == null ?
                        packageRegistry.getEPackage(uriString) :
                        extendedMetaData.getPackage(uriString);

        if (ePackage != null && ePackage.eIsProxy()) {
            ePackage = null;
        }

        if (ePackage == null) {
            URI uri = URI.createURI(uriString);
            if (uri.scheme() == null) {
                // This only works for old globally registered things.
                for (Map.Entry<String, Object> entry : packageRegistry.entrySet()) {
                    String nsURI = entry.getKey();
                    if (nsURI != null &&
                            nsURI.length() > uriString.length() &&
                            nsURI.endsWith(uriString) &&
                            nsURI.charAt(nsURI.length() - uriString.length() - 1) == '/') {
                        oldStyleProxyURIs = true;
                        return (EPackage) entry.getValue();
                    }
                }
            }

            if (urisToLocations != null) {
                URI locationURI = urisToLocations.get(uriString);
                if (locationURI != null) {
                    uri = locationURI;
                }
            }

            String fragment = uri.fragment();
            Resource resource = null;

//      if ("java".equalsIgnoreCase(uri.scheme()) && uri.authority() != null)
//      {
//        try
//        {
//          String className = uri.authority();
//          Class<?> javaClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
//          Field field = javaClass.getField("eINSTANCE");
//          resource = ((EPackage)field.get(null)).eResource();
//        }
//        catch (Exception exception)
//        {
//          // Ignore it if we can't find it since we'll fail anyway.
//        }
//      }

            if (resource == null && resourceSet != null) {
                URI trimmedURI = uri.trimFragment();
                resource = resourceSet.getResource(trimmedURI, false);
                if (resource != null) {
                    if (!resource.isLoaded()) {
                        try {
                            resource.load(resourceSet.getLoadOptions());
                        } catch (IOException exception) {
                            // Continue with a different approach.
                        }
                    }
                } else if (!XMLResource.XML_SCHEMA_URI.equals(uriString)) {
                    throw new UnsupportedOperationException("cannot open XML schema from URI");
//          try
//          {
//            InputStream inputStream = getURIConverter().createInputStream(trimmedURI, null);
//            resource = resourceSet.createResource(trimmedURI);
//            if (resource == null)
//            {
//              inputStream.close();
//            }
//            else
//            {
//              resource.load(inputStream, resourceSet.getLoadOptions());
//            }
//          }
//          catch (IOException exception)
//          {
//            // Continue with a different approach.
//          }
                }
            }

            if (resource != null) {
                Object content = null;
                if (fragment != null) {
                    content = resource.getEObject(fragment);
                } else {
                    List<EObject> contents = resource.getContents();
                    if (!contents.isEmpty()) {
                        content = contents.get(0);
                    }
                }

                if (content instanceof EPackage) {
                    ePackage = (EPackage) content;
                    if (extendedMetaData != null) {
                        extendedMetaData.putPackage(extendedMetaData.getNamespace(ePackage), ePackage);
                    } else {
                        packageRegistry.put(ePackage.getNsURI(), ePackage);
                    }
                }
            }
        }

        if (ePackage == null) {
            ePackage = handleMissingPackage(uriString);
        }

        if (ePackage == null) {
            error
                    (new PackageNotFoundException
                             (uriString,
                              getLocation(),
                              getLineNumber(),
                              getColumnNumber()));
        }

        return ePackage;
    }

    protected EPackage handleMissingPackage(String uriString) {
        if (XMLResource.XML_SCHEMA_URI.equals(uriString)) {
            return xmlSchemaTypePackage;
        } else if (extendedMetaData != null) {
            if (recordUnknownFeature) {
                return extendedMetaData.demandPackage(uriString);
            } else if (processAnyXML && objects.isEmpty()) {
                return extendedMetaData.demandPackage(uriString);
            } else if (contextFeature != null) {
                String namespace = extendedMetaData.getNamespace(contextFeature);
                String name = extendedMetaData.getName(contextFeature);
                EStructuralFeature wildcardFeature =
                        extendedMetaData.getElementWildcardAffiliation((objects.peekEObject()).eClass(), namespace, name);
                if (wildcardFeature != null) {
                    int processingKind = laxWildcardProcessing ? ExtendedMetaData.LAX_PROCESSING : extendedMetaData.getProcessingKind(wildcardFeature);
                    switch (processingKind) {
                        case ExtendedMetaData.LAX_PROCESSING:
                        case ExtendedMetaData.SKIP_PROCESSING: {
                            return extendedMetaData.demandPackage(uriString);
                        }
                    }
                }
            }
        }

        return null;
    }

    protected URIConverter getURIConverter() {
        return resourceSet != null ? resourceSet.getURIConverter() : new ExtensibleURIConverterImpl();
    }

    protected void setFeatureValue(EObject object, EStructuralFeature feature, Object value) {
        setFeatureValue(object, feature, value, -1);
    }

    /**
     * Set the given feature of the given object to the given value.
     */
    protected void setFeatureValue(EObject object, EStructuralFeature feature, Object value, int position) {
        try {
            helper.setValue(object, feature, value, position);
        } catch (RuntimeException e) {
            error
                    (new IllegalValueException
                             (object,
                              feature,
                              value,
                              e,
                              getLocation(),
                              getLineNumber(),
                              getColumnNumber()));
        }
    }

    /**
     * Set the values for the given multi-valued forward reference.
     */
    protected void setFeatureValues(ManyReference reference) {
        List<XMIException> xmiExceptions = helper.setManyReference(reference, getLocation());

        if (xmiExceptions != null) {
            for (XMIException exception : xmiExceptions) {
                error(exception);
            }
        }
    }

    /**
     * Create a feature with the given name for the given object with the
     * given values.
     */
    protected void setAttribValue(EObject object, String name, String value) {
        int index = name.indexOf(':', 0);

        // We use null here instead of "" because an attribute without a prefix is considered to have the null target namespace...
        String prefix = null;
        String localName = name;
        if (index != -1) {
            prefix = name.substring(0, index);
            localName = name.substring(index + 1);
        }
        EStructuralFeature feature = getFeature(object, prefix, localName, false);
        if (feature == null) {
            handleUnknownFeature(prefix, localName, false, object, value);
        } else {
            int kind = helper.getFeatureKind(feature);

            if (kind == XMLHelper.DATATYPE_SINGLE || kind == XMLHelper.DATATYPE_IS_MANY) {
                setFeatureValue(object, feature, value, -2);
            } else {
                setValueFromId(object, (EReference) feature, value);
            }
        }
    }

    /**
     * Create a ValueLine object and put it in the list
     * of references to resolve at the end of the document.
     */
    protected void setValueFromId(EObject object, EReference eReference, String ids) {
        String[] tokens = ids.split(" ");
        int t = 0;

        boolean isFirstID = true;
        boolean mustAdd = deferIDREFResolution;
        boolean mustAddOrNotOppositeIsMany = false;

        int size = 0;
        String qName = null;
        int position = 0;
        while (t < tokens.length) {
            String id = tokens[t++];
            int index = id.indexOf('#', 0);
            if (index != -1) {
                if (index == 0) {
                    id = id.substring(1);
                } else {
                    Object oldAttributes = setAttributes(null);
                    // Create a proxy in the correct way and pop it.
                    //
                    InternalEObject proxy =
                            (InternalEObject)
                                    (qName == null ?
                                            createObjectFromFeatureType(object, eReference) :
                                            createObjectFromTypeName(object, qName, eReference));
                    setAttributes(oldAttributes);
                    if (proxy != null) {
                        handleProxy(proxy, id);
                    }
                    objects.pop();
                    types.pop();
                    mixedTargets.pop();

                    qName = null;
                    ++position;
                    continue;
                }
            } else if (id.indexOf(':', 0) != -1) {
                qName = id;
                continue;
            }

            if (!deferIDREFResolution) {
                if (isFirstID) {
                    EReference eOpposite = eReference.getEOpposite();
                    if (eOpposite == null) {
                        mustAdd = true;
                        mustAddOrNotOppositeIsMany = true;
                    } else {
                        mustAdd = eOpposite.isTransient() || eReference.isMany();
                        mustAddOrNotOppositeIsMany = mustAdd || !eOpposite.isMany();
                    }
                    isFirstID = false;
                }

                if (mustAddOrNotOppositeIsMany) {
                    EObject resolvedEObject = xmlResource.getEObject(id);
                    if (resolvedEObject != null) {
                        setFeatureValue(object, eReference, resolvedEObject);
                        qName = null;
                        ++position;
                        continue;
                    }
                }
            }

            if (mustAdd) {
                if (size == capacity) {
                    growArrays();
                }

                identifiers[size] = id;
                positions[size] = position;
                ++size;
            }
            qName = null;
            ++position;
        }

        if (position == 0) {
            setFeatureValue(object, eReference, null, -2);
        } else if (size <= REFERENCE_THRESHOLD) {
            for (int i = 0; i < size; i++) {
                SingleReference ref = new SingleReference
                        (object,
                         eReference,
                         identifiers[i],
                         positions[i],
                         getLineNumber(),
                         getColumnNumber());
                forwardSingleReferences.add(ref);
            }
        } else {
            Object[] values = new Object[size];
            int[] currentPositions = new int[size];
            System.arraycopy(identifiers, 0, values, 0, size);
            System.arraycopy(positions, 0, currentPositions, 0, size);

            ManyReference ref = new ManyReference
                    (object,
                     eReference,
                     values,
                     currentPositions,
                     getLineNumber(),
                     getColumnNumber());
            forwardManyReferences.add(ref);
        }
    }

    protected void handleProxy(InternalEObject proxy, String uriLiteral) {
        URI proxyURI;
        if (oldStyleProxyURIs) {
            proxy.eSetProxyURI(proxyURI = URI.createURI(uriLiteral.startsWith("/") ? uriLiteral : "/" + uriLiteral));
        } else {
            URI uri = URI.createURI(uriLiteral);
            if (uriHandler != null) {
                uri = uriHandler.resolve(uri);
            } else if (resolve &&
                    uri.isRelative() &&
                    uri.hasRelativePath() &&
                    (extendedMetaData == null ?
                            !packageRegistry.containsKey(uri.trimFragment().toString()) :
                            extendedMetaData.getPackage(uri.trimFragment().toString()) == null)) {
                uri = helper.resolve(uri, resourceURI);
            }
            proxy.eSetProxyURI(proxyURI = uri);
        }

        // Test for a same document reference that would usually be handled as an IDREF.
        //
        if (proxyURI.trimFragment().equals(resourceURI)) {
            sameDocumentProxies.add(proxy);
        }
    }

    protected void growArrays() {
        int oldCapacity = capacity;
        capacity = capacity * 2;
        Object[] newIdentifiers = new Object[capacity];
        int[] newPositions = new int[capacity];
        System.arraycopy(identifiers, 0, newIdentifiers, 0, oldCapacity);
        System.arraycopy(positions, 0, newPositions, 0, oldCapacity);
        identifiers = newIdentifiers;
        positions = newPositions;
    }

    /**
     * Returns true if there was an error in the last XML element; false otherwise.
     */
    protected boolean isError() {
        return types.peek() == ERROR_TYPE;
    }

    static class EClassFeatureNamePair {

        public EClass eClass;
        public String featureName;
        public String namespaceURI;
        public boolean isElement;

        @Override
        public boolean equals(Object that) {
            EClassFeatureNamePair typedThat = (EClassFeatureNamePair) that;
            return
                    typedThat.eClass == eClass &&
                            typedThat.isElement == isElement &&
                            typedThat.featureName.equals(featureName) &&
                            (typedThat.namespaceURI != null ? typedThat.namespaceURI.equals(namespaceURI) : namespaceURI == null);
        }

        @Override
        public int hashCode() {
            return eClass.hashCode() ^ featureName.hashCode() ^ (namespaceURI == null ? 0 : namespaceURI.hashCode()) + (isElement ? 0 : 1);
        }
    }

    Map<EClassFeatureNamePair, EStructuralFeature> eClassFeatureNamePairToEStructuralFeatureMap;
    boolean isOptionUseXMLNameToFeatureSet;
    EClassFeatureNamePair eClassFeatureNamePair = new EClassFeatureNamePair();

    /**
     * @deprecated
     */
    @Deprecated
    protected EStructuralFeature getFeature(EObject object, String prefix, String name) {
        EClass eClass = object.eClass();
        String uri = helper.getURI(prefix);
        EStructuralFeature result = helper.getFeature(eClass, uri, name, true);
        if (result == null) {
            helper.getFeature(eClass, uri, name, false);
        }
        return result;
    }

    /**
     * Get the EStructuralFeature from the metaObject for the given object
     * and feature name.
     */
    protected EStructuralFeature getFeature(EObject object, String prefix, String name, boolean isElement) {
        String uri = helper.getURI(prefix);
        EClass eClass = object.eClass();
        eClassFeatureNamePair.eClass = eClass;
        eClassFeatureNamePair.featureName = name;
        eClassFeatureNamePair.namespaceURI = uri;
        eClassFeatureNamePair.isElement = isElement;
        EStructuralFeature result = eClassFeatureNamePairToEStructuralFeatureMap.get(eClassFeatureNamePair);
        if (result == null) {
            result = helper.getFeature(eClass, uri, name, isElement);

            if (result == null) {
                if (extendedMetaData != null) {
                    EStructuralFeature wildcardFeature =
                            isElement ?
                                    extendedMetaData.getElementWildcardAffiliation(eClass, uri, name) :
                                    extendedMetaData.getAttributeWildcardAffiliation(eClass, uri, name);
                    if (wildcardFeature != null) {
                        int processingKind = laxWildcardProcessing ? ExtendedMetaData.LAX_PROCESSING : extendedMetaData.getProcessingKind(wildcardFeature);
                        switch (processingKind) {
                            case ExtendedMetaData.LAX_PROCESSING:
                            case ExtendedMetaData.SKIP_PROCESSING: {
                                // EATM Demand create metadata.
                                result = extendedMetaData.demandFeature(uri, name, isElement);
                                break;
                            }
                        }
                    }
                } else {
                    // EATM Call the deprecated method which does the same thing
                    // but might have an override in older code.
                    result = getFeature(object, prefix, name);
                }
            }

            EClassFeatureNamePair entry = new EClassFeatureNamePair();
            entry.eClass = eClass;
            entry.featureName = name;
            entry.namespaceURI = uri;
            entry.isElement = isElement;
            eClassFeatureNamePairToEStructuralFeatureMap.put(entry, result);
        }

        return result;
    }

    /**
     * Searches the array of bytes to determine the XML
     * encoding.
     */
    public static String getXMLEncoding(byte[] bytes) {
        String javaEncoding = null;

        if (bytes.length >= 4) {
            if (((bytes[0] == -2) && (bytes[1] == -1)) ||
                    ((bytes[0] == 0) && (bytes[1] == 60))) {
                javaEncoding = "UnicodeBig";
            } else if (((bytes[0] == -1) && (bytes[1] == -2)) ||
                    ((bytes[0] == 60) && (bytes[1] == 0))) {
                javaEncoding = "UnicodeLittle";
            } else if ((bytes[0] == -17) && (bytes[1] == -69) && (bytes[2] == -65)) {
                javaEncoding = "UTF8";
            }
        }

        String header = null;

        try {
            if (javaEncoding != null) {
                header = new String(bytes, 0, bytes.length, javaEncoding);
            } else {
                header = new String(bytes, 0, bytes.length);
            }
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        if (!header.startsWith("<?xml")) {
            return "UTF-8";
        }

        int endOfXMLPI = header.indexOf("?>");
        int encodingIndex = header.indexOf("encoding", 6);

        if ((encodingIndex == -1) || (encodingIndex > endOfXMLPI)) {
            return "UTF-8";
        }

        int firstQuoteIndex = header.indexOf('"', encodingIndex);
        int lastQuoteIndex;

        if ((firstQuoteIndex == -1) || (firstQuoteIndex > endOfXMLPI)) {
            firstQuoteIndex = header.indexOf('\'', encodingIndex);
            lastQuoteIndex = header.indexOf('\'', firstQuoteIndex + 1);
        } else {
            lastQuoteIndex = header.indexOf('"', firstQuoteIndex + 1);
        }

        return header.substring(firstQuoteIndex + 1, lastQuoteIndex);
    }

    protected void handleComment(String comment) {
        FeatureMap featureMap = mixedTargets.peek();
        featureMap.add(XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__COMMENT, comment);
        text = null;
    }

    protected void handleMixedText() {
        FeatureMap featureMap = mixedTargets.peek();
        featureMap.add(XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__TEXT, text.toString());
        text = null;
    }

    protected void handleCDATA() {
        FeatureMap featureMap = mixedTargets.peek();
        featureMap.add(XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__CDATA, text.toString());
        text = null;
    }

    protected void handleProcessingInstruction(String target, String data) {
        FeatureMap featureMap = mixedTargets.peek();
        FeatureMapUtil.addProcessingInstruction(featureMap, target, data);
        text = null;
    }

  /*protected EcoreBuilder createEcoreBuilder(Map<?, ?> options, ExtendedMetaData extendedMetaData)
  {
    return new DefaultEcoreBuilder(extendedMetaData);
  }*/
}
