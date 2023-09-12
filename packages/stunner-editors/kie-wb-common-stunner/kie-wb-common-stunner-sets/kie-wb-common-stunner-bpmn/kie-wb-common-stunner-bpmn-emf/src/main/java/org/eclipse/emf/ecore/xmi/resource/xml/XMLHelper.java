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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xml.type.SimpleAnyType;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.kie.workbench.common.stunner.bpmn.client.emf.Bpmn2Marshalling;

/**
 * This class handles the package to use when there is no XML
 * namespace in an XML file.
 */
public class XMLHelper {

    /**
     * These are the kinds of features that are important
     * when loading XMI files.
     */
    public static final int DATATYPE_SINGLE = 1;
    public static final int DATATYPE_IS_MANY = 2;
    public static final int IS_MANY_ADD = 3;
    public static final int IS_MANY_MOVE = 4;
    public static final int OTHER = 5;

    protected static final Integer INTEGER_DATATYPE_IS_MANY = DATATYPE_IS_MANY;
    protected static final Integer INTEGER_DATATYPE_SINGLE = DATATYPE_SINGLE;
    protected static final Integer INTEGER_IS_MANY_ADD = IS_MANY_ADD;
    protected static final Integer INTEGER_IS_MANY_MOVE = IS_MANY_MOVE;
    protected static final Integer INTEGER_OTHER = OTHER;

    protected EPackage noNamespacePackage;
    protected XMLMap xmlMap;
    protected ExtendedMetaData extendedMetaData;
    protected boolean laxFeatureProcessing;
    protected EPackage.Registry packageRegistry;
    protected XMLResource resource;
    protected URI resourceURI;
    protected boolean deresolve;
    protected Map<EPackage, String> packages;
    protected Map<EStructuralFeature, Integer> featuresToKinds;
    protected String processDanglingHREF;
    protected DanglingHREFException danglingHREFException;
    protected EMap<String, String> prefixesToURIs;
    protected Map<String, List<String>> urisToPrefixes;
    protected Map<String, String> anyPrefixesToURIs;
    protected NamespaceSupport namespaceSupport;
    protected EClass anySimpleType;
    // true if seen xmlns="" declaration
    protected boolean seenEmptyStringMapping;
    protected EPackage xmlSchemaTypePackage = XMLTypePackage.eINSTANCE;
    protected List<String> allPrefixToURI;
    protected boolean checkForDuplicates;
    protected boolean mustHavePrefix;
    protected URIHandler uriHandler;
    protected List<? extends EObject> roots;
    protected String[] fragmentPrefixes;

    private EPackage previousPackage;
    private String previousNS;

    interface ManyReference {

        EObject getObject();

        EStructuralFeature getFeature();

        Object[] getValues();

        int[] getPositions();

        int getLineNumber();

        int getColumnNumber();
    }

    public XMLHelper() {
        super();
        packages = new HashMap<>();
        featuresToKinds = new HashMap<>();
        prefixesToURIs =
                new BasicEMap<String, String>() {
                    private static final long serialVersionUID = 1L;

                    protected List<String> getPrefixes(String uri) {
                        List<String> result = urisToPrefixes.get(uri);
                        if (result == null) {
                            urisToPrefixes.put(uri, result = new ArrayList<>());
                        }
                        return result;
                    }

                    @Override
                    protected void didAdd(Entry<String, String> entry) {
                        getPrefixes(entry.getValue()).add(entry.getKey());
                    }

                    @Override
                    protected void didClear(BasicEList<Entry<String, String>>[] oldEntryData) {
                        urisToPrefixes.clear();
                    }

                    @Override
                    protected void didModify(Entry<String, String> entry, String oldValue) {
                        String key = entry.getKey();
                        getPrefixes(oldValue).remove(key);
                        getPrefixes(entry.getValue()).add(key);
                    }

                    @Override
                    protected void didRemove(Entry<String, String> entry) {
                        getPrefixes(entry.getValue()).add(entry.getKey());
                    }
                };

        urisToPrefixes = new HashMap<>();

        anyPrefixesToURIs = new HashMap<>();
        allPrefixToURI = new ArrayList<>();
        namespaceSupport = new NamespaceSupport();
    }

    public XMLHelper(XMLResource resource) {
        this();
        setResource(resource);
    }

    public void setOptions(Map<?, ?> options) {
        laxFeatureProcessing = Boolean.TRUE.equals(options.get(XMLResource.OPTION_LAX_FEATURE_PROCESSING));
        uriHandler = (URIHandler) options.get(XMLResource.OPTION_URI_HANDLER);
        if (uriHandler != null) {
            uriHandler.setBaseURI(resourceURI);
        }
        @SuppressWarnings("unchecked")
        List<? extends EObject> roots = (List<? extends EObject>) options.get(XMLResource.OPTION_ROOT_OBJECTS);
        if (roots != null) {
            this.roots = roots;
            fragmentPrefixes = new String[roots.size()];
            int count = 0;
            for (EObject root : roots) {
                InternalEObject internalEObject = (InternalEObject) root;
                List<String> uriFragmentPath = new ArrayList<>();
                for (InternalEObject container = internalEObject.eInternalContainer(); container != null; container = internalEObject.eInternalContainer()) {
                    uriFragmentPath.add(container.eURIFragmentSegment(internalEObject.eContainingFeature(), internalEObject));
                    internalEObject = container;
                    Resource resource = container.eDirectResource();
                    if (resource != null) {
                        int index = resource.getContents().indexOf(container);
                        uriFragmentPath.add(index != 0 ? Integer.toString(index) : "");
                        break;
                    }
                }

                StringBuilder result = new StringBuilder("/");
                for (int i = uriFragmentPath.size() - 1; i >= 1; --i) {
                    result.append(uriFragmentPath.get(i));
                    result.append('/');
                }
                fragmentPrefixes[count++] = result.toString();
            }
        }
    }

    public void setNoNamespacePackage(EPackage pkg) {
        noNamespacePackage = pkg;
    }

    public EPackage getNoNamespacePackage() {
        return
                noNamespacePackage != null ?
                        noNamespacePackage :
                        extendedMetaData != null ?
                                extendedMetaData.getPackage(null) :
                                null;
    }

    public void setXMLMap(XMLMap map) {
        xmlMap = map;
        if (map != null && map.getNoNamespacePackage() != null) {
            setNoNamespacePackage(map.getNoNamespacePackage());
        }
    }

    public XMLMap getXMLMap() {
        return xmlMap;
    }

    public void setExtendedMetaData(ExtendedMetaData extendedMetaData) {
        this.extendedMetaData = extendedMetaData;
        if (extendedMetaData != null && extendedMetaData.getPackage(null) != null) {
            setNoNamespacePackage(extendedMetaData.getPackage(null));
        }
    }

    public ExtendedMetaData getExtendedMetaData() {
        return extendedMetaData;
    }

    public XMLResource getResource() {
        return resource;
    }

    public void setResource(XMLResource resource) {
        this.resource = resource;
        if (resource == null) {
            resourceURI = null;
            deresolve = false;
            packageRegistry = EPackage.Registry.INSTANCE;
        } else {
            resourceURI = resource.getURI();
            deresolve = resourceURI != null && !resourceURI.isRelative() && resourceURI.isHierarchical();
            packageRegistry = resource.getResourceSet() == null ? EPackage.Registry.INSTANCE : resource.getResourceSet().getPackageRegistry();
        }
    }

    public Object getValue(EObject obj, EStructuralFeature f) {
        return obj.eGet(f, false);
    }

    public String getQName(EClass c) {
        String name = getName(c);
        if (xmlMap != null) {
            XMLInfo clsInfo = xmlMap.getInfo(c);

            if (clsInfo != null) {
                String targetNamespace = clsInfo.getTargetNamespace();
                return getQName(targetNamespace, name);
            }
        }

        return getQName(c.getEPackage(), name);
    }

    public void populateNameInfo(NameInfo nameInfo, EClass c) {
        String name = getName(c);
        nameInfo.setLocalPart(name);
        if (xmlMap != null) {
            XMLInfo clsInfo = xmlMap.getInfo(c);

            if (clsInfo != null) {
                String targetNamespace = clsInfo.getTargetNamespace();
                nameInfo.setNamespaceURI(targetNamespace);
                nameInfo.setQualifiedName(getQName(targetNamespace, name));
                return;
            }
        }
        getQName(nameInfo, c.getEPackage(), name);
    }

    public String getQName(EDataType c) {
        String name = getName(c);
        if (xmlMap != null) {
            XMLInfo clsInfo = xmlMap.getInfo(c);

            if (clsInfo != null) {
                String targetNamespace = clsInfo.getTargetNamespace();
                return getQName(targetNamespace, name);
            }
        }

        return getQName(c.getEPackage(), name);
    }

    public void populateNameInfo(NameInfo nameInfo, EDataType eDataType) {
        String name = getName(eDataType);
        nameInfo.setLocalPart(name);
        if (xmlMap != null) {
            XMLInfo clsInfo = xmlMap.getInfo(eDataType);

            if (clsInfo != null) {
                String targetNamespace = clsInfo.getTargetNamespace();
                nameInfo.setNamespaceURI(targetNamespace);
                nameInfo.setQualifiedName(getQName(targetNamespace, name));
                return;
            }
        }
        getQName(nameInfo, eDataType.getEPackage(), name);
    }

    public String getQName(EStructuralFeature feature) {
        if (extendedMetaData != null) {
            String namespace = extendedMetaData.getNamespace(feature);
            String name = extendedMetaData.getName(feature);
            String result = name;

            // We need to be careful that we don't end up requiring the no namespace package
            // just because the feature is unqualified.
            //
            if (namespace != null) {
                // There really must be a package.
                //
                EPackage ePackage;
                if (namespace.equals(previousNS)) {
                    ePackage = previousPackage;
                } else {
                    ePackage = extendedMetaData.getPackage(namespace);
                    if (ePackage == null) {
                        ePackage = extendedMetaData.demandPackage(namespace);
                    }
                    previousPackage = ePackage;
                    previousNS = namespace;
                }

                result = getQName(ePackage, name);

                // We must have a qualifier for an attribute that needs qualified.
                //
                if (result.length() == name.length() && extendedMetaData.getFeatureKind(feature) == ExtendedMetaData.ATTRIBUTE_FEATURE) {
                    result = getQName(ePackage, name, true);
                }
            }
            return result;
        }

        String name = getName(feature);
        if (xmlMap != null) {
            XMLInfo info = xmlMap.getInfo(feature);
            if (info != null) {
                return getQName(info.getTargetNamespace(), name);
            }
        }

        return name;
    }

    public void populateNameInfo(NameInfo nameInfo, EStructuralFeature feature) {
        if (extendedMetaData != null) {
            String namespace = extendedMetaData.getNamespace(feature);
            String name = extendedMetaData.getName(feature);
            nameInfo.setNamespaceURI(namespace);
            nameInfo.setLocalPart(name);
            nameInfo.setQualifiedName(name);

            // We need to be careful that we don't end up requiring the no namespace package
            // just because the feature is unqualified.
            //
            if (namespace != null) {
                // There really must be a package.
                //
                EPackage ePackage = extendedMetaData.getPackage(namespace);
                if (ePackage == null) {
                    ePackage = extendedMetaData.demandPackage(namespace);
                }

                String result = getQName(nameInfo, ePackage, name);

                // We must have a qualifier for an attribute that needs qualified.
                //
                if (result.length() == name.length() && extendedMetaData.getFeatureKind(feature) == ExtendedMetaData.ATTRIBUTE_FEATURE) {
                    getQName(nameInfo, ePackage, name, true);
                }
            }
        } else {
            String name = getName(feature);
            nameInfo.setNamespaceURI(null);
            nameInfo.setLocalPart(name);
            if (xmlMap != null) {
                XMLInfo info = xmlMap.getInfo(feature);
                if (info != null) {
                    String targetNamespace = info.getTargetNamespace();
                    nameInfo.setNamespaceURI(targetNamespace);
                    nameInfo.setQualifiedName(getQName(targetNamespace, name));
                }
            }
            nameInfo.setQualifiedName(name);
        }
    }

    protected String getQName(NameInfo nameInfo, EPackage ePackage, String name) {
        String qname = getQName(nameInfo, ePackage, name, mustHavePrefix);
        nameInfo.setQualifiedName(qname);
        return qname;
    }

    protected String getQName(NameInfo nameInfo, EPackage ePackage, String name, boolean mustHavePrefix) {
        String nsPrefix = getPrefix(ePackage, mustHavePrefix);
        nameInfo.setNamespaceURI(getNamespaceURI(nsPrefix));
        if ("".equals(nsPrefix)) {
            return name;
        } else if (name.length() == 0) {
            return nsPrefix;
        } else {
            return nsPrefix + ":" + name;
        }
    }

    protected String getQName(EPackage ePackage, String name) {
        return getQName(ePackage, name, mustHavePrefix);
    }

    protected String getQName(EPackage ePackage, String name, boolean mustHavePrefix) {
        String nsPrefix = getPrefix(ePackage, mustHavePrefix);
        if ("".equals(nsPrefix)) {
            return name;
        } else if (name.length() == 0) {
            return nsPrefix;
        } else {
            return nsPrefix + ":" + name;
        }
    }

    public String getPrefix(EPackage ePackage) {
        return getPrefix(ePackage, mustHavePrefix);
    }

    public String getNamespaceURI(String prefix) {
        String namespaceURI = namespaceSupport.getURI(prefix);
        if (namespaceURI == null) {
            namespaceURI = prefixesToURIs.get(prefix);
        }
        return namespaceURI;
    }

    protected String getPrefix(EPackage ePackage, boolean mustHavePrefix) {
        String nsPrefix = packages.get(ePackage);
        if (nsPrefix == null || mustHavePrefix && nsPrefix.length() == 0) {
            String nsURI =
                    xmlSchemaTypePackage == ePackage ?
                            XMLResource.XML_SCHEMA_URI :
                            extendedMetaData == null ?
                                    ePackage.getNsURI() :
                                    extendedMetaData.getNamespace(ePackage);

            boolean found = false;
            List<String> prefixes = urisToPrefixes.get(nsURI);
            if (prefixes != null) {
                for (String prefix : prefixes) {
                    nsPrefix = prefix;
                    if (!mustHavePrefix || nsPrefix.length() > 0) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                // for any content prefix to URI mapping could be in namespace context
                nsPrefix = namespaceSupport.getPrefix(nsURI);
                if (nsPrefix != null) {
                    return nsPrefix;
                }

                if (nsURI != null) {
                    nsPrefix = xmlSchemaTypePackage == ePackage ? "xsd" : ePackage.getNsPrefix();
                }
                if (nsPrefix == null) {
                    nsPrefix = mustHavePrefix ? "_" : "";
                }

                if (prefixesToURIs.containsKey(nsPrefix)) {
                    String currentValue = prefixesToURIs.get(nsPrefix);
                    if (currentValue == null ? nsURI != null : !currentValue.equals(nsURI)) {
                        int index = 1;
                        while (prefixesToURIs.containsKey(nsPrefix + "_" + index)) {
                            ++index;
                        }
                        nsPrefix += "_" + index;
                    }
                }

                prefixesToURIs.put(nsPrefix, nsURI);
            }

            if (!packages.containsKey(ePackage)) {
                packages.put(ePackage, nsPrefix);
            }
        }

        return nsPrefix;
    }

    public List<String> getPrefixes(EPackage ePackage) {
        List<String> result = new UniqueEList<String>();
        result.add(getPrefix(ePackage));
        String namespace = extendedMetaData == null ? ePackage.getNsURI() : extendedMetaData.getNamespace(ePackage);
        List<String> prefixes = urisToPrefixes.get(namespace);
        if (prefixes != null) {
            result.addAll(prefixes);
        }
        return result;
    }

    protected String getQName(String uri, String name) {
        if (uri == null) {
            EPackage theNoNamespacePackage = getNoNamespacePackage();
            if (theNoNamespacePackage != null) {
                packages.put(theNoNamespacePackage, "");
            }

            return name;
        }

        EPackage ePackage =
                extendedMetaData == null ?
                        EPackage.Registry.INSTANCE.getEPackage(uri) :
                        extendedMetaData.getPackage(uri);
        if (ePackage == null) {
            if (extendedMetaData != null) {
                return getQName(extendedMetaData.demandPackage(uri), name);
            } else {
                // EATM this would be wrong.
                return name;
            }
        } else {
            return getQName(ePackage, name);
        }
    }

    public String getName(ENamedElement obj) {
        if (extendedMetaData != null) {
            return
                    obj instanceof EStructuralFeature ?
                            extendedMetaData.getName((EStructuralFeature) obj) :
                            extendedMetaData.getName((EClassifier) obj);
        }

        if (xmlMap != null) {
            XMLInfo info = xmlMap.getInfo(obj);
            if (info != null) {
                String result = info.getName();
                if (result != null) {
                    return result;
                }
            }
        }

        return obj.getName();
    }

    public String getID(EObject obj) {
        return resource == null ? null : resource.getID(obj);
    }

    protected String getURIFragmentQuery(Resource containingResource, EObject object) {
        return null;
    }

    protected String getURIFragment(Resource containingResource, EObject object) {
        if (roots != null && containingResource == resource && !EcoreUtil.isAncestor(roots, object)) {
            URI uriResult = handleDanglingHREF(object);
            return uriResult == null || !uriResult.hasFragment() ? null : uriResult.fragment();
        } else {
            String result = containingResource.getURIFragment(object);
            if (result.length() > 0 && result.charAt(0) != '/') {
                String query = getURIFragmentQuery(containingResource, object);
                if (query != null) {
                    result += "?" + query + "?";
                }
            } else if ("/-1".equals(result)) {
                if (object.eResource() != containingResource) {
                    URI uriResult = handleDanglingHREF(object);
                    return uriResult == null || !uriResult.hasFragment() ? null : uriResult.fragment();
                }
            } else if (fragmentPrefixes != null) {
                for (int i = 0; i < fragmentPrefixes.length; ++i) {
                    String fragmentPrefix = fragmentPrefixes[i];
                    if (result.startsWith(fragmentPrefix)) {
                        result = "/" + (i == 0 ? "" : Integer.toString(i)) + result.substring(fragmentPrefix.length() - 1);
                        break;
                    }
                }
            }
            return result;
        }
    }

    public String getIDREF(EObject obj) {
        return resource == null ? null : getURIFragment(resource, obj);
    }

    protected URI handleDanglingHREF(EObject object) {
        if (!XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD.equals(processDanglingHREF)) {
            DanglingHREFException exception = new DanglingHREFException(
                    "The object '" + object + "' is not contained in a resource.",
                    resource == null || resource.getURI() == null ? "unknown" : resource.getURI().toString(), 0, 0);

            if (danglingHREFException == null) {
                danglingHREFException = exception;
            }

            if (resource != null) {
                resource.getErrors().add(exception);
            }
        }

        return null;
    }

    public String getHREF(EObject obj) {
        InternalEObject o = (InternalEObject) obj;

        URI objectURI = o.eProxyURI();
        if (objectURI == null) {
            Resource otherResource = obj.eResource();
            if (otherResource == null) {
                if (resource != null && resource.getID(obj) != null) {
                    objectURI = getHREF(resource, obj);
                } else {
                    objectURI = handleDanglingHREF(obj);
                    if (objectURI == null) {
                        return null;
                    }
                }
            } else {
                objectURI = getHREF(otherResource, obj);
            }
        }

        // Kogito customization clause
        // If no href resolved yet, and obj is a BaseElement, it probably references a single object, so use the identifier.
        // Otherwise some BPMN references, such as Message Event's value or called element in Reusable Subprocess are not properly loaded.
        if (o instanceof BaseElement) {
            String id = ((BaseElement) o).getId();
            return id;
        }

        objectURI = deresolve(objectURI);

        return null != objectURI ? objectURI.toString() : "";
    }

    protected URI getHREF(Resource otherResource, EObject obj) {
        return null != otherResource.getURI() ?
                otherResource.getURI().appendFragment(getURIFragment(otherResource, obj)) : null;
    }

    public URI deresolve(URI uri) {
        if (uriHandler != null) {
            uri = uriHandler.deresolve(uri);
        } else if (deresolve && !uri.isRelative()) {
            URI deresolvedURI = uri.deresolve(resourceURI, true, true, false);
            if (deresolvedURI.hasRelativePath()) {
                uri = deresolvedURI;
            }
        }

        return uri;
    }

    public int getFeatureKind(EStructuralFeature feature) {
        Integer kind = featuresToKinds.get(feature);
        if (kind != null) {
            return kind;
        } else {
            computeFeatureKind(feature);
            kind = featuresToKinds.get(feature);
            if (kind != null) {
                return kind;
            } else {
                featuresToKinds.put(feature, INTEGER_OTHER);
                return OTHER;
            }
        }
    }

    public EObject createObject(EFactory eFactory, EClassifier type) {
        EObject newObject = null;
        if (eFactory != null) {
            if (extendedMetaData != null) {
                if (type == null) {
                    return null;
                } else if (type instanceof EClass) {
                    EClass eClass = (EClass) type;
                    if (!eClass.isAbstract()) {
                        newObject = eFactory.create((EClass) type);
                    }
                } else {
                    SimpleAnyType result = (SimpleAnyType) EcoreUtil.create(anySimpleType);
                    result.setInstanceType((EDataType) type);
                    newObject = result;
                }
            } else {
                if (type != null) {
                    EClass eClass = (EClass) type;
                    if (!eClass.isAbstract()) {
                        newObject = eFactory.create((EClass) type);
                    }
                }
            }
        }
        return newObject;
    }

    public EClassifier getType(EFactory eFactory, String typeName) {
        if (eFactory != null) {
            EPackage ePackage = eFactory.getEPackage();
            if (extendedMetaData != null) {
                return extendedMetaData.getType(ePackage, typeName);
            } else {
                EClass eClass = (EClass) ePackage.getEClassifier(typeName);
                if (eClass == null && xmlMap != null) {
                    return xmlMap.getClassifier(ePackage.getNsURI(), typeName);
                }
                return eClass;
            }
        }
        return null;
    }

    /**
     * @deprecated since 2.2
     */
    @Deprecated
    public EObject createObject(EFactory eFactory, String classXMIName) {
        return createObject(eFactory, getType(eFactory, classXMIName));
    }

    public EStructuralFeature getFeature(EClass eClass, String namespaceURI, String name) {
        EStructuralFeature feature = getFeatureWithoutMap(eClass, name);
        if (feature == null) {
            if (xmlMap != null) {
                feature = xmlMap.getFeature(eClass, namespaceURI, name);
                if (feature != null) {
                    computeFeatureKind(feature);
                }
            } else if (laxFeatureProcessing && extendedMetaData != null) {
                List<EStructuralFeature> structuralFeatures = eClass.getEAllStructuralFeatures();
                for (int i = 0, size = structuralFeatures.size(); i < size; ++i) {
                    EStructuralFeature eStructuralFeature = structuralFeatures.get(i);
                    if (name.equals(extendedMetaData.getName(eStructuralFeature))
                            && (namespaceURI == null ? extendedMetaData.getNamespace(eStructuralFeature) == null : namespaceURI.equals(extendedMetaData.getNamespace(eStructuralFeature)))) {
                        return eStructuralFeature;
                    }
                }
            }
        }

        return feature;
    }

    public EStructuralFeature getFeature(EClass eClass, String namespaceURI, String name, boolean isElement) {
        if (extendedMetaData != null) {
            // Once we see a lookup of an element in the null namespace, we should behave as if there has been an explicit xmlns=""
            //
            if (isElement && namespaceURI == null) {
                seenEmptyStringMapping = true;
            }
            EStructuralFeature eStructuralFeature =
                    isElement ?
                            extendedMetaData.getElement(eClass, namespaceURI, name) :
                            extendedMetaData.getAttribute(eClass, namespaceURI == "" ? null : namespaceURI, name);
            if (eStructuralFeature != null) {
                computeFeatureKind(eStructuralFeature);
            } else {
                eStructuralFeature = getFeature(eClass, namespaceURI, name);

                // Only if the feature kind is unspecified should we return a match.
                // Otherwise, we might return an attribute feature when an element is required,
                // or vice versa. This also can be controlled by XMLResource.OPTION_LAX_FEATURE_PROCESSING.
                //
                if (!laxFeatureProcessing && eStructuralFeature != null &&
                        extendedMetaData.getFeatureKind(eStructuralFeature) != ExtendedMetaData.UNSPECIFIED_FEATURE) {
                    eStructuralFeature = null;
                }
            }

            return eStructuralFeature;
        }

        return getFeature(eClass, namespaceURI, name);
    }

    protected EStructuralFeature getFeatureWithoutMap(EClass eClass, String name) {
        EStructuralFeature feature = eClass.getEStructuralFeature(name);

        if (feature != null) {
            computeFeatureKind(feature);
        }

        return feature;
    }

    protected void computeFeatureKind(EStructuralFeature feature) {
        EClassifier eClassifier = feature.getEType();

        if (eClassifier instanceof EDataType) {
            if (feature.isMany()) {
                featuresToKinds.put(feature, INTEGER_DATATYPE_IS_MANY);
            } else {
                featuresToKinds.put(feature, INTEGER_DATATYPE_SINGLE);
            }
        } else {
            if (feature.isMany()) {
                EReference reference = (EReference) feature;
                EReference opposite = reference.getEOpposite();

                if (opposite == null || opposite.isTransient() || !opposite.isMany()) {
                    featuresToKinds.put(feature, INTEGER_IS_MANY_ADD);
                } else {
                    featuresToKinds.put(feature, INTEGER_IS_MANY_MOVE);
                }
            }
        }
    }

    public String getJavaEncoding(String xmlEncoding) {
        return xmlEncoding;
    }

    public String getXMLEncoding(String javaEncoding) {
        return javaEncoding;
    }

    public EPackage[] packages() {
        Map<String, EPackage> map = new TreeMap<String, EPackage>();

        // Sort and eliminate duplicates caused by having both a regular package and a demanded package for the same nsURI.
        //
        for (EPackage ePackage : packages.keySet()) {
            String prefix = getPrefix(ePackage);
            if (prefix == null) {
                prefix = "";
            }
            EPackage conflict = map.put(prefix, ePackage);
            if (conflict != null && conflict.eResource() != null) {
                map.put(prefix, conflict);
            }
        }
        EPackage[] result = new EPackage[map.size()];
        map.values().toArray(result);
        return result;
    }

    public void setValue(EObject object, EStructuralFeature feature, Object value, int position) {
        // The underlying GWT implementation for EMF throws an UnsupportedOperationException is some feature validation fails,
        // so do not propagate the exception in that case, otherwise the process will be not loaded at all.
        // Instead, log an error for further fixing.
        try {
            doSetValue(object, feature, value, position);
        } catch (UnsupportedOperationException e) {
            Bpmn2Marshalling.logError("Cannot set the value [" + value + "] for " +
                                              "feature [" + feature.getName() + "] into the " +
                                              "object [" + object + "]",
                                      e);
        }
    }

    private void doSetValue(EObject object, EStructuralFeature feature, Object value, int position) {
        if (extendedMetaData != null) {
            EStructuralFeature targetFeature = extendedMetaData.getAffiliation(object.eClass(), feature);
            if (targetFeature != null && targetFeature != feature) {
                EStructuralFeature group = extendedMetaData.getGroup(targetFeature);
                if (group != null) {
                    targetFeature = group;
                }
                if (targetFeature.getEType() == EcorePackage.Literals.EFEATURE_MAP_ENTRY) {
                    FeatureMap featureMap = (FeatureMap) object.eGet(targetFeature);
                    EClassifier eClassifier = feature.getEType();
                    if (eClassifier instanceof EDataType) {
                        EDataType eDataType = (EDataType) eClassifier;
                        EFactory eFactory = eDataType.getEPackage().getEFactoryInstance();
                        value = createFromString(eFactory, eDataType, (String) value);
                    }
                    featureMap.add(feature, value);
                    return;
                } else {
                    // If we are substituting an EAttribute for an EReference...
                    //
                    EClassifier eType = feature.getEType();
                    if (eType instanceof EDataType && targetFeature instanceof EReference) {
                        // Create an simple any type wrapper for the attribute value and use that with the EReference.
                        //
                        SimpleAnyType simpleAnyType = (SimpleAnyType) EcoreUtil.create(anySimpleType);
                        simpleAnyType.setInstanceType((EDataType) eType);
                        simpleAnyType.setRawValue((String) value);
                        value = simpleAnyType;
                    }
                    feature = targetFeature;
                }
            }
        }

        int kind = getFeatureKind(feature);
        switch (kind) {
            case DATATYPE_SINGLE:
            case DATATYPE_IS_MANY: {
                EClassifier eClassifier = feature.getEType();
                EDataType eDataType = (EDataType) eClassifier;
                EFactory eFactory = eDataType.getEPackage().getEFactoryInstance();

                if (kind == DATATYPE_IS_MANY) {
                    @SuppressWarnings("unchecked")
                    InternalEList<Object> list = (InternalEList<Object>) object.eGet(feature);
                    if (position == -2) {
                        String[] tokens = ((String) value).split(" ");
                        for (int t = 0; t < tokens.length; t++) {
                            String token = tokens[t];
                            list.addUnique(createFromString(eFactory, eDataType, token));
                        }

                        // Make sure that the list will appear to be set to be empty.
                        //
                        if (list.isEmpty()) {
                            list.clear();
                        }
                    } else if (value == null) {
                        list.addUnique(null);
                    } else {
                        list.addUnique(createFromString(eFactory, eDataType, (String) value));
                    }
                } else if (value == null) {
                    object.eSet(feature, null);
                } else {
                    object.eSet(feature, createFromString(eFactory, eDataType, (String) value));
                }
                break;
            }
            case IS_MANY_ADD:
            case IS_MANY_MOVE: {
                @SuppressWarnings("unchecked")
                InternalEList<Object> list = (InternalEList<Object>) object.eGet(feature);

                if (position == -1) {
                    if (object == value) {
                        list.add(value);
                    } else {
                        list.addUnique(value);
                    }
                } else if (position == -2) {
                    list.clear();
                } else if (checkForDuplicates || object == value) {
                    int index = list.basicIndexOf(value);
                    if (index == -1) {
                        list.addUnique(position, value);
                    } else {
                        list.move(position, index);
                    }
                } else if (kind == IS_MANY_ADD) {
                    list.addUnique(position, value);
                } else {
                    list.move(position, value);
                }
                break;
            }
            default: {
                object.eSet(feature, value);
                break;
            }
        }
    }

    public List<XMIException> setManyReference(ManyReference reference, String location) {
        EStructuralFeature feature = reference.getFeature();
        int kind = getFeatureKind(feature);
        EObject object = reference.getObject();
        @SuppressWarnings("unchecked")
        InternalEList<Object> list = (InternalEList<Object>) object.eGet(feature);
        List<XMIException> xmiExceptions = new BasicEList<XMIException>();
        Object[] values = reference.getValues();
        int[] positions = reference.getPositions();

        if (kind == IS_MANY_ADD) {
            for (int i = 0, l = values.length; i < l; i++) {
                Object value = values[i];
                if (value != null) {
                    int position = positions[i];
                    try {
                        if (checkForDuplicates || object == value) {
                            int index = list.basicIndexOf(value);
                            if (index == -1) {
                                list.addUnique(position, value);
                            } else {
                                list.move(position, index);
                            }
                        } else {
                            list.addUnique(position, value);
                        }
                    } catch (RuntimeException e) {
                        xmiExceptions.add(new IllegalValueException
                                                  (object,
                                                   feature,
                                                   value,
                                                   e,
                                                   location,
                                                   reference.getLineNumber(),
                                                   reference.getColumnNumber()
                                                  ));
                    }
                }
            }
        } else {
            for (int i = 0, l = values.length; i < l; i++) {
                Object value = values[i];
                if (value != null) {
                    try {
                        int sourcePosition = list.basicIndexOf(value);
                        if (sourcePosition != -1) {
                            list.move(positions[i], sourcePosition);
                        } else {
                            list.addUnique(positions[i], value);
                        }
                    } catch (RuntimeException e) {
                        xmiExceptions.add(new IllegalValueException
                                                  (object,
                                                   feature,
                                                   value,
                                                   e,
                                                   location,
                                                   reference.getLineNumber(),
                                                   reference.getColumnNumber()
                                                  ));
                    }
                }
            }
        }

        if (xmiExceptions.isEmpty()) {
            return null;
        } else {
            return xmiExceptions;
        }
    }

    public void setCheckForDuplicates(boolean checkForDuplicates) {
        this.checkForDuplicates = checkForDuplicates;
    }

    public void setProcessDanglingHREF(String value) {
        processDanglingHREF = value;
    }

    public DanglingHREFException getDanglingHREFException() {
        return danglingHREFException;
    }

    public URI resolve(URI relative, URI base) {
        return uriHandler == null ? relative.resolve(base) : uriHandler.resolve(relative);
    }

    public void pushContext() {
        namespaceSupport.pushContext();
    }

    public void popContext() {
        namespaceSupport.popContext();
    }

    public void popContext(Map<String, EFactory> prefixesToFactories) {
        namespaceSupport.popContext(prefixesToFactories);
    }

    public void addPrefix(String prefix, String uri) {
        if (!"xml".equals(prefix) && !"xmlns".equals(prefix)) {
            uri = (uri.length() == 0) ? null : uri;
            namespaceSupport.declarePrefix(prefix, uri);
            allPrefixToURI.add(prefix);
            allPrefixToURI.add(uri);
        }
    }

    public String getPrefix(String namespaceURI) {
        return namespaceSupport.getPrefix(namespaceURI);
    }

    public Map<String, String> getAnyContentPrefixToURIMapping() {
        anyPrefixesToURIs.clear();
        int count = namespaceSupport.getDeclaredPrefixCount();
        int size = allPrefixToURI.size();
        while (count-- > 0) {
            String uri = allPrefixToURI.remove(--size);
            String prefix = allPrefixToURI.remove(--size);
            anyPrefixesToURIs.put(prefix, uri);
        }
        return anyPrefixesToURIs;
    }

    public String getURI(String prefix) {
        return
                "xml".equals(prefix) ?
                        "http://www.w3.org/XML/1998/namespace" :
                        "xmlns".equals(prefix) ?
                                ExtendedMetaData.XMLNS_URI :
                                namespaceSupport.getURI(prefix);
    }

    public EMap<String, String> getPrefixToNamespaceMap() {
        return prefixesToURIs;
    }

    public void recordPrefixToURIMapping() {
        for (int i = 0, size = allPrefixToURI.size(); i < size; ) {
            String prefix = allPrefixToURI.get(i++);
            String uri = allPrefixToURI.get(i++);
            String originalURI = prefixesToURIs.get(prefix);
            if (uri == null) {
                // xmlns="" declaration
                // Example #1: <a><q-name>q</q-name><b xmlns="abc"/></a>
                // Example #2: <a xmlns="abc"><b xmlns=""/><c xmlns="abc2"/></a>
                // Example #3: <a xmlns:a="abc"><b xmlns:a="abc2"/></a>

                seenEmptyStringMapping = true;
                if (originalURI != null) {
                    // since xmlns="" is default declaration, remove ""->empty_URI mapping
                    prefixesToURIs.removeKey(prefix);
                    addNSDeclaration(prefix, originalURI);
                }
                continue;
            } else if ((seenEmptyStringMapping && prefix.length() == 0)) {
                // record default ns declaration as duplicate if seen QName (#1) or seen xmlns="" (#2)
                addNSDeclaration(prefix, uri);
            } else if (originalURI != null) {
                if (!uri.equals(originalURI)) {
                    // record duplicate declaration for a given prefix (#3)
                    addNSDeclaration(prefix, uri);
                }
            } else {
                // recording a first declaration for a given prefix
                prefixesToURIs.put(prefix, uri);
            }
        }
    }

    public void setPrefixToNamespaceMap(EMap<String, String> prefixToNamespaceMap) {
        for (Map.Entry<String, String> entry : prefixToNamespaceMap) {
            String prefix = entry.getKey();
            String namespace = entry.getValue();
            EPackage ePackage = null;
            if (extendedMetaData == null) {
                ePackage = packageRegistry.getEPackage(namespace);
            } else {
                ePackage = extendedMetaData.getPackage(namespace);
                if (ePackage == null) {
                    if (XMLResource.XML_SCHEMA_URI.equals(namespace)) {
                        ePackage = xmlSchemaTypePackage;
                    } else {
                        ePackage = extendedMetaData.demandPackage(namespace);
                    }
                }
            }
            if (ePackage != null && !packages.containsKey(ePackage)) {
                packages.put(ePackage, prefix);
            }
            prefixesToURIs.put(prefix, namespace);
        }
    }

    /**
     * A helper to encode namespace prefix mappings.
     */
    protected static class NamespaceSupport {

        protected String[] namespace = new String[16 * 2];

        protected int namespaceSize = 0;

        protected int[] context = new int[8];

        protected int currentContext = -1;

        protected String[] prefixes = new String[16];

        public void pushContext() {
            // extend the array, if necessary
            if (currentContext + 1 == context.length) {
                int[] contextarray = new int[context.length * 2];
                System.arraycopy(context, 0, contextarray, 0, context.length);
                context = contextarray;
            }

            // push context
            context[++currentContext] = namespaceSize;
        }

        public void popContext() {
            namespaceSize = context[currentContext--];
        }

        public void popContext(Map<String, EFactory> prefixesToFactories) {
            int oldNamespaceSize = namespaceSize;
            for (int i = namespaceSize = context[currentContext--]; i < oldNamespaceSize; i += 2) {
                prefixesToFactories.remove(namespace[i]);
            }
        }

        /**
         * @param prefix prefix to declare
         * @param uri uri that maps to the prefix
         * @return true if the prefix existed in the current context and
         * its uri has been remapped; false if prefix does not exist in the
         * current context
         */
        public boolean declarePrefix(String prefix, String uri) {
            // see if prefix already exists in current context
            for (int i = namespaceSize; i > context[currentContext]; i -= 2) {
                if (namespace[i - 2].equals(prefix)) {
                    namespace[i - 1] = uri;
                    return true;
                }
            }

            // resize array, if needed
            if (namespaceSize == namespace.length) {
                String[] namespacearray = new String[namespaceSize * 2];
                System.arraycopy(namespace, 0, namespacearray, 0, namespaceSize);
                namespace = namespacearray;
            }

            // bind prefix to uri in current context
            namespace[namespaceSize++] = prefix;
            namespace[namespaceSize++] = uri;
            return false;
        }

        public String getURI(String prefix) {
            // find prefix in current context
            for (int i = namespaceSize; i > 0; i -= 2) {
                if (namespace[i - 2].equals(prefix)) {
                    return namespace[i - 1];
                }
            }

            // prefix not found
            return null;
        }

        public String getPrefix(String uri) {
            // find uri in current context
            for (int i = namespaceSize; i > 0; i -= 2) {
                String knownURI = namespace[i - 1];
                if ((knownURI != null) ? knownURI.equals(uri) : uri == knownURI) {
                    knownURI = getURI(namespace[i - 2]);
                    if ((knownURI != null) ? knownURI.equals(uri) : uri == knownURI) {
                        return namespace[i - 2];
                    }
                }
            }

            // uri not found
            return null;
        }

        public int getDeclaredPrefixCount() {
            return (namespaceSize - context[currentContext]) / 2;
        }

        public String getDeclaredPrefixAt(int index) {
            return namespace[context[currentContext] + index * 2];
        } // getDeclaredPrefixAt(int):String
    }// namespace context

    public void setAnySimpleType(EClass type) {
        anySimpleType = type;
    }

    public String convertToString(EFactory factory, EDataType dataType, Object value) {
        if (extendedMetaData != null) {
            if (value instanceof List<?>) {
                List<?> list = (List<?>) value;
                for (Object item : list) {
                    updateQNamePrefix(factory, dataType, item, true);
                }
                return factory.convertToString(dataType, value);
            } else {
                return updateQNamePrefix(factory, dataType, value, false);
            }
        }
        return factory.convertToString(dataType, value);
    }

    protected Object createFromString(EFactory eFactory, EDataType eDataType, String value) {
        Object obj = eFactory.createFromString(eDataType, value);
        if (extendedMetaData != null) {
            if (obj instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) obj;
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    Object replacement = updateQNameURI(item);
                    if (replacement != item) {
                        list.set(i, replacement);
                    }
                }
            } else {
                obj = updateQNameURI(obj);
            }
        }
        return obj;
    }

    protected Object updateQNameURI(Object value) {
        {
            return value;
        }
    }

    /**
     * @param factory
     * @param dataType
     * @param value a data value to be converted to string
     * @param list if the value is part of the list of values
     * @return if the value is not part of the list, return string corresponding to value,
     * otherwise return null
     */
    protected String updateQNamePrefix(EFactory factory, EDataType dataType, Object value, boolean list) {
//    if (value instanceof QName)
//    {
//      QName qName = (QName)value;
//      String namespace = qName.getNamespaceURI();
//      String localPart = qName.getLocalPart();
//      if (namespace.length() == 0)
//      {
//        if (qName.getPrefix().length() != 0)
//        {
//          throw new IllegalStateException("The null namespace cannot be bound to a non-null prefix '" + qName + "'");
//        }
//        return localPart;
//      }
//      String prefix = qName.getPrefix();
//      EPackage ePackage = extendedMetaData.getPackage(namespace);
//      if (ePackage == null)
//      {
//        int size = extendedMetaData.demandedPackages().size();
//        ePackage = extendedMetaData.demandPackage(namespace);
//        if (prefix.length() != 0 && extendedMetaData.demandedPackages().size() > size)
//        {
//          ePackage.setNsPrefix(prefix);
//        }
//      }
//      if (!namespace.equals(getNamespaceURI(prefix)))
//      {
//        prefix = getPrefix(ePackage, true);
//      }
//      return list ? null : prefix.length() == 0 ? localPart : prefix + ':' + localPart;
//    }

        return list ? null : factory.convertToString(dataType, value);
    }

    protected void addNSDeclaration(String prefix, String uri) {
        if (uri != null) {
            List<String> existingPrefixes = urisToPrefixes.get(uri);
            if (existingPrefixes == null) {
                int lowerBound = 0;
                int index = 1;
                String newPrefix;
                while (prefixesToURIs.containsKey(newPrefix = prefix + "_" + index)) {
                    lowerBound = index;
                    index <<= 1;
                }
                if (lowerBound != 0) {
                    int upperBound = index;
                    while (lowerBound + 1 < upperBound) {
                        index = (lowerBound + upperBound) >> 1;
                        if (prefixesToURIs.containsKey(prefix + "_" + index)) {
                            lowerBound = index;
                        } else {
                            upperBound = index;
                        }
                    }
                    newPrefix = prefix + "_" + (lowerBound + 1);
                }
                prefixesToURIs.put(newPrefix, uri);
            }
        }
    }

    public void setMustHavePrefix(boolean mustHavePrefix) {
        this.mustHavePrefix = mustHavePrefix;
    }
}
