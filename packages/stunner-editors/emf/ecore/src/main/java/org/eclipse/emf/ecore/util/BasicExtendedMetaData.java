/**
 * Copyright (c) 2003-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeUtil;


/**
 *  This is a basic implementation of the extended metadata API.
 */
public class BasicExtendedMetaData implements ExtendedMetaData
{
  protected String annotationURI;
  protected EPackage.Registry registry;
  protected EPackage.Registry demandRegistry;
  protected Map<EModelElement, Object> extendedMetaDataHolderCache;
  protected Map<EModelElement, EAnnotation> annotationMap;

  public BasicExtendedMetaData()
  {
    this(ANNOTATION_URI, EPackage.Registry.INSTANCE);
  }

  public BasicExtendedMetaData(EPackage.Registry registry)
  {
    this(ANNOTATION_URI, registry);
  }

  public BasicExtendedMetaData(String annotationURI, EPackage.Registry registry)
  {
    this(annotationURI, registry, null);
  }

  public BasicExtendedMetaData(String annotationURI, EPackage.Registry registry, Map<EModelElement, EAnnotation> annotationMap)
  {
    this.annotationURI = annotationURI.intern();
    this.registry = registry;
    this.demandRegistry = new org.eclipse.emf.ecore.impl.EPackageRegistryImpl();
    this.annotationMap = annotationMap;
    
    if (annotationURI != ANNOTATION_URI)
    {
      extendedMetaDataHolderCache = new HashMap<EModelElement, Object>();
    }
  }
  
  protected EAnnotation getAnnotation(EModelElement eModelElement, boolean demandCreate)
  {
    if (annotationMap != null)
    {
      EAnnotation result = annotationMap.get(eModelElement);
      if (result == null && demandCreate)
      {
        result = EcoreFactory.eINSTANCE.createEAnnotation();
        result.setSource(annotationURI);
        annotationMap.put(eModelElement, result);
      }
      return result;
    }
    else
    {
      EAnnotation result = eModelElement.getEAnnotation(annotationURI);
      if (result == null && demandCreate)
      {
        result = EcoreFactory.eINSTANCE.createEAnnotation();
        result.setSource(annotationURI);
        eModelElement.getEAnnotations().add(result);
      }
      return result;
    } 
  }

  public EClassifier getType(EPackage ePackage, String name)
  {
    return getExtendedMetaData(ePackage).getType(name);
  }

  public EPackage getPackage(String namespace)
  {
    EPackage ePackage = registry.getEPackage(namespace);
/*
    if (ePackage == null)
    {
      ePackage = demandRegistry.getEPackage(namespace);
    }
*/
    return ePackage;
  }

  public void putPackage(String namespace, EPackage ePackage)
  {
    registry.put(namespace, ePackage);
  }

  public EClass getDocumentRoot(EPackage ePackage)
  {
    return (EClass)getType(ePackage, "");
  }

  public void setDocumentRoot(EClass eClass)
  {
    setName(eClass, "");
    setContentKind(eClass, MIXED_CONTENT);
  }

  public boolean isDocumentRoot(EClass eClass)
  {
    return "".equals(getName(eClass));
  }

  public EReference getXMLNSPrefixMapFeature(EClass eClass)
  {
    if (getContentKind(eClass) == MIXED_CONTENT)
    {
      List<EReference> eAllReferences = eClass.getEAllReferences();
      for (int i = 0, size = eAllReferences.size();  i < size; ++i)
      {
        EReference eReference = eAllReferences.get(i);
        if ("xmlns:prefix".equals(getName(eReference)))
        {
          return eReference;
        }
      }
    }

    return null;
  }

  public EReference getXSISchemaLocationMapFeature(EClass eClass)
  {
    if (getContentKind(eClass) == MIXED_CONTENT)
    {
      List<EReference> eAllReferences = eClass.getEAllReferences();
      for (int i = 0, size = eAllReferences.size();  i < size; ++i)
      {
        EReference eReference = eAllReferences.get(i);
        if ("xsi:schemaLocation".equals(getName(eReference)))
        {
          return eReference;
        }
      }
    }

    return null;
  }

  public boolean isQualified(EPackage ePackage)
  {
    return getExtendedMetaData(ePackage).isQualified();
  }

  protected boolean basicIsQualified(EPackage ePackage)
  {
    EAnnotation eAnnotation = getAnnotation(ePackage, false);
    return eAnnotation == null || !"false".equals(eAnnotation.getDetails().get("qualified"));
  }

  public void setQualified(EPackage ePackage, boolean isQualified)
  {
    if (!isQualified)
    {
      EAnnotation eAnnotation = getAnnotation(ePackage, true);
      eAnnotation.getDetails().put("qualified", "false");
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(ePackage, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("qualified");
      }
    }
    getExtendedMetaData(ePackage).setQualified(isQualified);
  }

  public String getNamespace(EPackage ePackage)
  {
    if (isQualified(ePackage))
    {
      return (ePackage).getNsURI();
    }
    else
    {
      return null;
    }
  }

  public String getNamespace(EClassifier eClassifier)
  {
    return getNamespace(eClassifier.getEPackage());
  }

  public String getNamespace(EStructuralFeature eStructuralFeature)
  {
    return getExtendedMetaData(eStructuralFeature).getNamespace();
  }

  public String basicGetNamespace(EStructuralFeature eStructuralFeature)
  {
    EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
    if (eAnnotation == null)
    {
      return null;
    }
    else
    {
      String result = eAnnotation.getDetails().get("namespace");
      if ("##targetNamespace".equals(result))
      {
        return getNamespace(eStructuralFeature.getEContainingClass().getEPackage());
      }
      else
      {
        return result;
      }
    }
  }

  public void setNamespace(EStructuralFeature eStructuralFeature, String namespace)
  {
    String packageNamespace = getNamespace(eStructuralFeature.getEContainingClass().getEPackage());
    String convertedNamespace = namespace;
    if (namespace == null ? packageNamespace == null : namespace.equals(packageNamespace))
    {
      convertedNamespace="##targetNamespace";
    }

    if (convertedNamespace != null)
    {
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, true);
      eAnnotation.getDetails().put("namespace", convertedNamespace);
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("namespace");
      }
    }
    getExtendedMetaData(eStructuralFeature).setNamespace(namespace);
  }

  public String getName(EClassifier eClassifier)
  {
    return getExtendedMetaData(eClassifier).getName();
  }

  protected String basicGetName(EClassifier eClassifier)
  {
    EAnnotation eAnnotation = getAnnotation(eClassifier, false);
    if (eAnnotation != null)
    {
      String result = eAnnotation.getDetails().get("name");
      if (result != null)
      {
        return result;
      }
    }
    return eClassifier.getName();
  }

  public void setName(EClassifier eClassifier, String name)
  {
    EAnnotation eAnnotation = getAnnotation(eClassifier, true);
    eAnnotation.getDetails().put("name", name);
    getExtendedMetaData(eClassifier).setName(name);
    EPackage ePackage = eClassifier.getEPackage();
    if (ePackage != null)
    {
      getExtendedMetaData(ePackage).rename(eClassifier, name);
    }
  }

  public boolean isAnonymous(EClassifier eClassifier)
  {
    String name =  getExtendedMetaData(eClassifier).getName();
    return name.length() == 0 || name.indexOf("_._") != -1;
  }

  public String getName(EStructuralFeature eStructuralFeature)
  {
    return getExtendedMetaData(eStructuralFeature).getName();
  }

  protected String basicGetName(EStructuralFeature eStructuralFeature)
  {
    EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
    if (eAnnotation != null)
    {
      String result = eAnnotation.getDetails().get("name");
      if (result != null)
      {
        return result;
      }
    }
    return eStructuralFeature.getName();
  }

  public void setName(EStructuralFeature eStructuralFeature, String name)
  {
    EAnnotation eAnnotation = getAnnotation(eStructuralFeature, true);
    eAnnotation.getDetails().put("name", name);
    getExtendedMetaData(eStructuralFeature).setName(name);
  }

  protected String getQualifiedName(String defaultNamespace, EClassifier eClassifier)
  {
    String namespace = getNamespace(eClassifier);
    String name = getName(eClassifier);
    if (namespace == null)
    {
      return namespace == defaultNamespace ? name : "#" + name;
    }
    else
    {
      return namespace.equals(defaultNamespace) ? name : namespace + "#" + name;
    }
  }

  protected String getQualifiedName(String defaultNamespace, EStructuralFeature eStructuralFeature)
  {
    String namespace = getNamespace(eStructuralFeature);
    String name = getName(eStructuralFeature);
    if (namespace == null)
    {
      return namespace == defaultNamespace ? name : "#" + name;
    }
    else
    {
      return namespace.equals(defaultNamespace) ? name : namespace + "#" + name;
    }
  }

  public EClassifier getType(String namespace, String name)
  {
    EPackage ePackage = getPackage(namespace);
    return ePackage == null ? null : getType(ePackage, name);
  }

  public EStructuralFeature getAttribute(String namespace, String name)
  {
    EPackage ePackage = getPackage(namespace);
    if (ePackage != null)
    {
      EClass documentRoot = getDocumentRoot(ePackage);
      if (documentRoot != null)
      {
        return getLocalAttribute(documentRoot, namespace, name);
      }
    }

    return null;
  }

  public EStructuralFeature getElement(String namespace, String name)
  {
    EPackage ePackage = getPackage(namespace);
    if (ePackage != null)
    {
      EClass documentRoot = getDocumentRoot(ePackage);
      if (documentRoot != null)
      {
        return getLocalElement(documentRoot, namespace, name);
      }
    }

    return null;
  }

  public int getFeatureKind(EStructuralFeature eStructuralFeature)
  {
    return getExtendedMetaData(eStructuralFeature).getFeatureKind();
  }

  protected int basicGetFeatureKind(EStructuralFeature eStructuralFeature)
  {
    EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
    if (eAnnotation != null)
    {
      Object kind = eAnnotation.getDetails().get("kind");
      if (kind != null)
      {
        for (int i = 1; i < FEATURE_KINDS.length; ++i)
        {
          if (FEATURE_KINDS[i].equals(kind))
          {
            return i;
          }
        }
      }
    }

    return 0;
  }

  public void setFeatureKind(EStructuralFeature eStructuralFeature, int kind)
  {
    if (kind > 0 && kind < FEATURE_KINDS.length)
    {
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, true);
      eAnnotation.getDetails().put("kind", FEATURE_KINDS[kind]);
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("kind");
      }
    }
    getExtendedMetaData(eStructuralFeature).setFeatureKind(kind);
  }

  public int getContentKind(EClass eClass)
  {
    return getExtendedMetaData(eClass).getContentKind();
  }

  protected int basicGetContentKind(EClass eClass)
  {
    EAnnotation eAnnotation = getAnnotation(eClass, false);
    if (eAnnotation != null)
    {
      Object kind = eAnnotation.getDetails().get("kind");
      if (kind != null)
      {
        for (int i = 1; i < CONTENT_KINDS.length; ++i)
        {
          if (CONTENT_KINDS[i].equals(kind))
          {
            return i;
          }
        }
      }
    }

    return 0;
  }

  public void setContentKind(EClass eClass, int kind)
  {
    if (kind > 0 && kind < CONTENT_KINDS.length)
    {
      EAnnotation eAnnotation = getAnnotation(eClass, true);
      eAnnotation.getDetails().put("kind", CONTENT_KINDS[kind]);
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eClass, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("kind");
      }
    }
    getExtendedMetaData(eClass).setContentKind(kind);
  }

  public int getDerivationKind(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getDerivationKind();
  }

  protected int basicGetDerivationKind(EClassifier eClassifier)
  {
    EAnnotation eAnnotation = getAnnotation(eClassifier, false);
    if (eAnnotation != null)
    {
      EMap<String, String> details = eAnnotation.getDetails();
      Object kind = details.get("restriction");
      if (kind != null)
      {
        return RESTRICTION_DERIVATION;
      }
      kind = details.get("list");
      if (kind != null)
      {
        return LIST_DERIVATION;
      }
      kind = details.get("union");
      if (kind != null)
      {
        return UNION_DERIVATION;
      }
    }

    return 0;
  }

  public EDataType getBaseType(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getBaseType();
  }

  public EDataType basicGetBaseType(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    if (eAnnotation != null)
    {
      EMap<String, String> details = eAnnotation.getDetails();
      String baseType = details.get("baseType");
      if (baseType != null)
      {
        int index = baseType.lastIndexOf("#");
        EClassifier type = 
          index == -1 ?
            getType(eDataType.getEPackage(), baseType) :
            index == 0 ?
              getType((String)null, baseType.substring(1)) :
              getType(baseType.substring(0, index), baseType.substring(index + 1));
        if (type instanceof EDataType)
        {
          return (EDataType)type;
        }
      }
    }

    return null;
  }

  public void setBaseType(EDataType eDataType, EDataType baseType)
  {
    if (baseType == null)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("baseType");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("baseType", getQualifiedName(getNamespace(eDataType), baseType));
    }
    getExtendedMetaData(eDataType).setBaseType(baseType);
  }

  public EDataType getItemType(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getItemType();
  }

  protected EDataType basicGetItemType(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    if (eAnnotation != null)
    {
      EMap<String, String> details = eAnnotation.getDetails();
      String itemType = details.get("itemType");
      if (itemType != null)
      {
        int index = itemType.lastIndexOf("#");
        EClassifier type = 
          index == -1 ?
            getType(eDataType.getEPackage(), itemType) :
            index == 0 ?
              getType((String)null, itemType.substring(1)) :
              getType(itemType.substring(0, index), itemType.substring(index + 1));
        if (type instanceof EDataType)
        {
          return (EDataType)type;
        }
      }
    }

    return null;
  }

  public void setItemType(EDataType eDataType, EDataType itemType)
  {
    if (itemType == null)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("itemType");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("itemType", getQualifiedName(getNamespace(eDataType), itemType));
    }
    getExtendedMetaData(eDataType).setItemType(itemType);
  }

  public List<EDataType> getMemberTypes(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getMemberTypes();
  }

  protected List<EDataType> basicGetMemberTypes(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    if (eAnnotation != null)
    {
      String memberTypes = eAnnotation.getDetails().get("memberTypes");
      if (memberTypes != null)
      {
        List<EDataType> result = new ArrayList<EDataType>();
        for (String member : memberTypes.split("\\w"))
        {
          int index = member.lastIndexOf("#");
          EClassifier type = 
            index == -1 ?
              getType(eDataType.getEPackage(), member) :
              index == 0 ?
                getType((String)null, member.substring(1)) :
                getType(member.substring(0, index), member.substring(index + 1));
          if (type instanceof EDataType)
          {
            result.add((EDataType)type);
          }
        }
        return result;
      }
    }

    return Collections.emptyList();
  }

  public void setMemberTypes(EDataType eDataType, List<EDataType> memberTypes)
  {
    if (memberTypes.isEmpty())
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("memberTypes");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      String namespace = getNamespace(eDataType);
      StringBuffer result = new StringBuffer();
      for (int i = 0, size = memberTypes.size(); i < size; ++i)
      {
        result.append(getQualifiedName(namespace, memberTypes.get(i)));
        result.append(' ');
      }
      eAnnotation.getDetails().put("memberTypes", result.substring(0, result.length() - 1));
    }
    getExtendedMetaData(eDataType).setMemberTypes(memberTypes);
  }

  protected boolean isFeatureKindSpecific()
  {
    return true;
  }

  protected boolean isFeatureNamespaceMatchingLax()
  {
    return false;
  }

  public EStructuralFeature getLocalAttribute(EClass eClass, String namespace, String name)
  {
    EStructuralFeature result = null;
    if (isFeatureKindSpecific())
    {
      List<EStructuralFeature> allAttributes = getAllAttributes(eClass);
      for (int i = 0, size = allAttributes.size(); i < size; ++i)
      {
        EStructuralFeature eStructuralFeature = allAttributes.get(i);
        if (name.equals(getName(eStructuralFeature)))
        {
          String featureNamespace = getNamespace(eStructuralFeature);
          if (namespace == null) 
          {
            if (featureNamespace == null)
            {
              return eStructuralFeature;
            }
            else if (result == null)
            {
              result = eStructuralFeature;
            }
          }
          else if (namespace.equals(featureNamespace))
          {
            return eStructuralFeature;
          }
          else if (featureNamespace == null && result == null)
          {
            result = eStructuralFeature;
          }
        }
      }
    }
    else
    {
      for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i)
      {
        EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(i);
        switch (getFeatureKind(eStructuralFeature))
        {
          case UNSPECIFIED_FEATURE:
          case ATTRIBUTE_FEATURE:
          {
            if (name.equals(getName(eStructuralFeature)))
            {
              String featureNamespace = getNamespace(eStructuralFeature);
              if (namespace == null) 
              {
                if (featureNamespace == null)
                {
                  return eStructuralFeature;
                }
                else if (result == null)
                {
                  result = eStructuralFeature;
                }
              }
              else if (namespace.equals(featureNamespace))
              {
                return eStructuralFeature;
              }
              else if (featureNamespace == null && result == null)
              {
                result = eStructuralFeature;
              }
            }
            break;
          }
        }
      }
    }

    return isFeatureNamespaceMatchingLax() ? result : null;
  }

  public EStructuralFeature getAttribute(EClass eClass, String namespace, String name)
  {
    EStructuralFeature result = getLocalAttribute(eClass, namespace, name);
    if (result == null)
    {
      result = getAttribute(namespace, name);
      if (result != null && getAffiliation(eClass, result) == null)
      {
        return null;
      }
    }
    return result;
  }

  protected EStructuralFeature getLocalElement(EClass eClass, String namespace, String name)
  {
    EStructuralFeature result = null;
    if (isFeatureKindSpecific())
    {
      List<EStructuralFeature> allElements = getAllElements(eClass);
      for (int i = 0, size = allElements.size(); i < size; ++i)
      {
        EStructuralFeature eStructuralFeature = allElements.get(i);
        if (name.equals(getName(eStructuralFeature)))
        {
          String featureNamespace = getNamespace(eStructuralFeature);
          if (namespace == null) 
          {
            if (featureNamespace == null)
            {
              return eStructuralFeature;
            }
            else if (result == null)
            {
              result = eStructuralFeature;
            }
          }
          else if (namespace.equals(featureNamespace))
          {
            return eStructuralFeature;
          }
          else if (featureNamespace == null && result == null)
          {
            result = eStructuralFeature;
          }
        }
      }
    }
    else
    {
      for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i)
      {
        EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(i);
        switch (getFeatureKind(eStructuralFeature))
        {
          case UNSPECIFIED_FEATURE:
          case ELEMENT_FEATURE:
          {
            if (name.equals(getName(eStructuralFeature)))
            {
              String featureNamespace = getNamespace(eStructuralFeature);
              if (namespace == null) 
              {
                if (featureNamespace == null)
                {
                  return eStructuralFeature;
                }
                else if (result == null)
                {
                  result = eStructuralFeature;
                }
              }
              else if (namespace.equals(featureNamespace))
              {
                return eStructuralFeature;
              }
              else if (featureNamespace == null && result == null)
              {
                result = eStructuralFeature;
              }
            }
            break;
          }
        }
      }
    }

    return isFeatureNamespaceMatchingLax() ? result : null;
  }

  public EStructuralFeature getElement(EClass eClass, String namespace, String name)
  {
    EStructuralFeature result = getLocalElement(eClass, namespace, name);
    if (result == null)
    {
      result = getElement(namespace, name);
      if (result != null && getAffiliation(eClass, result) == null)
      {
        return null;
      }
    }
    return result;
  }
  
  public List<EStructuralFeature> getAllAttributes(EClass eClass)
  {
    List<EClass> superTypes = eClass.getESuperTypes();
    List<EStructuralFeature> result = null;
    boolean changeable = false;
    for (int i = 0, size = superTypes.size(); i < size; ++i) 
    {
      EClass eSuperType = superTypes.get(i);
      List<EStructuralFeature> allAttributes =  getAllAttributes(eSuperType);
      if (!allAttributes.isEmpty())
      {
        if (result == null)
        {
          result = allAttributes;
        }
        else
        {
          if (!changeable)
          {
            changeable = true;
            result = new UniqueEList<EStructuralFeature>(result);
          }
          result.addAll(allAttributes);
        }
      }
    }
    List<EStructuralFeature> attributes = getAttributes(eClass);
    if (!attributes.isEmpty())
    {
      if (result == null)
      {
        return attributes;
      }
      else
      {
        if (!changeable)
        {
          result = new UniqueEList<EStructuralFeature>(result);
        }
        result.addAll(attributes);
        return result;
      }
    }
    else
    {
      return result == null ? Collections.<EStructuralFeature>emptyList() : result;
    }
  }
  
  public List<EStructuralFeature> getAllElements(EClass eClass)
  {
    List<EClass> superTypes = eClass.getESuperTypes();
    List<EStructuralFeature> result = null;
    boolean changeable = false;
    for (int i = 0, size = superTypes.size(); i < size; ++i) 
    {
      EClass eSuperType = superTypes.get(i);
      List<EStructuralFeature> allElements =  getAllElements(eSuperType);
      if (!allElements.isEmpty())
      {
        if (result == null)
        {
          result = allElements;
        }
        else
        {
          if (!changeable)
          {
            changeable = true;
            result = new UniqueEList<EStructuralFeature>(result);
          }
          result.addAll(allElements);
        }
      }
    }
    List<EStructuralFeature> elements = getElements(eClass);
    if (!elements.isEmpty())
    {
      if (result == null)
      {
        return elements;
      }
      else
      {
        if (!changeable)
        {
          result = new UniqueEList<EStructuralFeature>(result);
        }
        result.addAll(elements);
        return result;
      }
    }
    else
    {
      return result == null ? Collections.<EStructuralFeature>emptyList() : result;
    }
  }

  public List<EStructuralFeature> getAttributes(EClass eClass)
  {
    List<EStructuralFeature> eStructuralFeatures = eClass.getEStructuralFeatures();
    List<EStructuralFeature> result = null;
    for (int i = 0, size = eStructuralFeatures.size(); i < size; ++i)
    {
      EStructuralFeature eStructuralFeature = eStructuralFeatures.get(i);
      switch (getFeatureKind(eStructuralFeature))
      {
        case ATTRIBUTE_FEATURE:
        case ATTRIBUTE_WILDCARD_FEATURE:
        {
          if (result == null)
          {
            result = new ArrayList<EStructuralFeature>();
          }
          result.add(eStructuralFeature);
        }
      }
    }
    return result == null ? Collections.<EStructuralFeature>emptyList() : result;
  }

  public List<EStructuralFeature> getElements(EClass eClass)
  {
    List<EStructuralFeature> eStructuralFeatures = eClass.getEStructuralFeatures();
    List<EStructuralFeature> result = null;
    for (int i = 0, size = eStructuralFeatures.size(); i < size; ++i)
    {
      EStructuralFeature eStructuralFeature = eStructuralFeatures.get(i);
      switch (getFeatureKind(eStructuralFeature))
      {
        case ELEMENT_FEATURE:
        case ELEMENT_WILDCARD_FEATURE:
        case GROUP_FEATURE:
        {
          if (result == null)
          {
            result = new ArrayList<EStructuralFeature>();
          }
          result.add(eStructuralFeature);
          break;
        }
      }
    }
    
   return result == null ? Collections.<EStructuralFeature>emptyList() : result;
  }

  public EStructuralFeature getSimpleFeature(EClass eClass)
  {
    if (getContentKind(eClass) == SIMPLE_CONTENT)
    {
      for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i)
      {
        EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(i);
        if (getFeatureKind(eStructuralFeature) == ExtendedMetaData.SIMPLE_FEATURE)
        {
          return eStructuralFeature;
        }
      }
    }

    return null;
  }

  public EAttribute getMixedFeature(EClass eClass)
  {
    switch (getContentKind(eClass))
    {
      case MIXED_CONTENT:
      case SIMPLE_CONTENT:
      {
        List<EAttribute> eAllAttributes = eClass.getEAllAttributes();
        for (int i = 0, size = eAllAttributes.size(); i < size; ++i)
        {
          EAttribute eAttribute = eAllAttributes.get(i);
          if (getFeatureKind(eAttribute) == ExtendedMetaData.ELEMENT_WILDCARD_FEATURE)
          {
            return eAttribute;
          }
        }
        break;
      }
    }

    return null;
  }

  public List<String> getWildcards(EStructuralFeature eStructuralFeature)
  {
    return getExtendedMetaData(eStructuralFeature).getWildcards();
  }

  protected List<String> basicGetWildcards(EStructuralFeature eStructuralFeature)
  {
    EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
    if (eAnnotation != null)
    {
      String wildcards = eAnnotation.getDetails().get("wildcards");
      if (wildcards != null)
      {
        List<String> result = new ArrayList<String>();
        for (String wildcard : wildcards.split("\\w"))
        {
          if (wildcard.equals("##other"))
          {
            result.add("!##" + getNamespace(eStructuralFeature.getEContainingClass().getEPackage()));
          }
          else if (wildcard.equals("##local"))
          {
            result.add(null);
          }
          else if (wildcard.equals("##targetNamespace"))
          {
            result.add(getNamespace(eStructuralFeature.getEContainingClass().getEPackage()));
          }
          else
          {
            result.add(wildcard);
          }
        }
        return result;
      }
    }

    return Collections.emptyList();
  }

  public void setWildcards(EStructuralFeature eStructuralFeature, List<String> wildcards)
  {
    if (wildcards.isEmpty())
    {
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("wildcards");
        eAnnotation.getDetails().remove("name");
      }
    }
    else
    {
      String namespace = getNamespace(eStructuralFeature.getEContainingClass().getEPackage());
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, true);
      eAnnotation.getDetails().put("wildcards", getEncodedWildcards(namespace, wildcards));
      eAnnotation.getDetails().put("name","");
    }
    getExtendedMetaData(eStructuralFeature).setWildcards(wildcards);
  }
  
  public static String getEncodedWildcards(String namespace, List<String> wildcards)
  {
    if (wildcards.isEmpty())
    {
      return ""; 
    }
    else
    {
      StringBuffer value = new StringBuffer();
      for (int i = 0, size = wildcards.size(); i < size; )
      {
        String wildcard = wildcards.get(i);
        if (wildcard == null)
        {
          if (namespace == null)
          {
            value.append("##targetNamespace");
          }
          else 
          {
            value.append("##local");
          }
        }
        else if (wildcard.startsWith("!##"))
        {
          if (namespace == null ? 
                wildcard.length() == 3 : 
                wildcard.endsWith(namespace) && wildcard.length() == namespace.length() + 3)
          {
            value.append("##other");
          }
          else
          {
            value.append(wildcard);
          }
        }
        else if (wildcard.equals(namespace))
        {
          value.append("##targetNamespace");
        }
        else
        {
          value.append(wildcard);
        }

        if (++i < size)
        {
          value.append(' ');
        }
      }
      return value.toString();
    }
  }

  public int getProcessingKind(EStructuralFeature eStructuralFeature)
  {
    return getExtendedMetaData(eStructuralFeature).getProcessingKind();
  }

  protected int basicGetProcessingKind(EStructuralFeature eStructuralFeature)
  {
    EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
    if (eAnnotation != null)
    {
      Object kind = eAnnotation.getDetails().get("processing");
      if (kind != null)
      {
        for (int i = 1; i < PROCESSING_KINDS.length; ++i)
        {
          if (PROCESSING_KINDS[i].equals(kind))
          {
            return i;
          }
        }
      }
    }

    return 0;
  }

  public void setProcessingKind(EStructuralFeature eStructuralFeature, int kind)
  {
    if (kind > 0 && kind < PROCESSING_KINDS.length)
    {
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, true);
      eAnnotation.getDetails().put("processing", PROCESSING_KINDS[kind]);
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("processing");
      }
    }
    getExtendedMetaData(eStructuralFeature).setProcessingKind(kind);
  }

  public EStructuralFeature getGroup(EStructuralFeature eStructuralFeature)
  {
    return getExtendedMetaData(eStructuralFeature).getGroup();
  }

  protected EStructuralFeature basicGetGroup(EStructuralFeature eStructuralFeature)
  {
    EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
    if (eAnnotation != null)
    {
      String qualifiedName = eAnnotation.getDetails().get("group");
      if (qualifiedName != null)
      {
        int fragmentIndex = qualifiedName.lastIndexOf('#');
        if (fragmentIndex == -1)
        {
          return 
            getElement
              (eStructuralFeature.getEContainingClass(), 
               getNamespace(eStructuralFeature.getEContainingClass().getEPackage()), 
               qualifiedName);
        }
        else if (fragmentIndex == 0)
        {
          return 
            getElement
              (eStructuralFeature.getEContainingClass(), 
               null, 
               qualifiedName.substring(1));
        }
        else
        {
          return 
            getElement
              (eStructuralFeature.getEContainingClass(), 
               qualifiedName.substring(0, fragmentIndex), 
               qualifiedName.substring(fragmentIndex + 1));
        }
      }
    }
    return null;
  }

  public void setGroup(EStructuralFeature eStructuralFeature, EStructuralFeature group)
  {
    if (group == null)
    {
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("group");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, true);
      eAnnotation.getDetails().put
        ("group", getQualifiedName(getNamespace(eStructuralFeature.getEContainingClass().getEPackage()), group));
    }
    getExtendedMetaData(eStructuralFeature).setGroup(group);
  }

  public EStructuralFeature getAffiliation(EStructuralFeature eStructuralFeature)
  {
    return getExtendedMetaData(eStructuralFeature).getAffiliation();
  }

  protected EStructuralFeature basicGetAffiliation(EStructuralFeature eStructuralFeature)
  {
    EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
    if (eAnnotation != null)
    {
      String qualifiedName = eAnnotation.getDetails().get("affiliation");
      if (qualifiedName != null)
      {
        int fragmentIndex = qualifiedName.lastIndexOf('#');
        if (fragmentIndex == -1)
        {
          return getElement(getNamespace(eStructuralFeature.getEContainingClass().getEPackage()), qualifiedName);
        }
        else if (fragmentIndex == 0)
        {
          return getElement(null, qualifiedName.substring(1));
        }
        else
        {
          return getElement(qualifiedName.substring(0, fragmentIndex), qualifiedName.substring(fragmentIndex + 1));
        }
      }
    }
    return null;
  }

  public void setAffiliation(EStructuralFeature eStructuralFeature, EStructuralFeature affiliation)
  {
    if (affiliation == null)
    {
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("affiliation");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eStructuralFeature, true);
      eAnnotation.getDetails().put
        ("affiliation", getQualifiedName(getNamespace(eStructuralFeature.getEContainingClass().getEPackage()), affiliation));
    }
    getExtendedMetaData(eStructuralFeature).setAffiliation(affiliation);
  }

  public EStructuralFeature getAffiliation(EClass eClass, EStructuralFeature eStructuralFeature)
  {
    if (eClass.getFeatureID(eStructuralFeature) >= 0) 
    {
      return eStructuralFeature;
    }

    switch (getFeatureKind(eStructuralFeature))
    {
      case ATTRIBUTE_FEATURE:
      {
        if (isDocumentRoot(eStructuralFeature.getEContainingClass()))
        {
          String namespace = getNamespace(eStructuralFeature);
          String name = getName(eStructuralFeature);
          EStructuralFeature result = getLocalAttribute(eClass, namespace, name);
          if (result != null)
          {
            return result;
          }
  
          List<EStructuralFeature> allAttributes = getAllAttributes(eClass);
          for (int i = 0, size = allAttributes.size(); i < size; ++i)
          {
            result = allAttributes.get(i);
            if (matches(getWildcards(result), namespace))
            {
              return result;
            }
          }
        }
        return null;
      }
      case ELEMENT_FEATURE:
      {
        if (isDocumentRoot(eStructuralFeature.getEContainingClass()))
        {
          for (EStructuralFeature affiliation = eStructuralFeature; affiliation != null; affiliation = getAffiliation(affiliation))
          {
            String namespace = getNamespace(affiliation);
            String name = getName(affiliation);
            EStructuralFeature result = getLocalElement(eClass, namespace, name);
            if (result != null)
            {
              return result;
            }
          }
  
          String namespace = getNamespace(eStructuralFeature);
          if (XMLTypePackage.eNS_URI.equals(namespace))
          {
            return getMixedFeature(eClass);
          }
          else
          {
            List<EStructuralFeature> allElements = getAllElements(eClass);
            for (int i = 0, size = allElements.size(); i < size; ++i)
            {
              EStructuralFeature result = allElements.get(i);
              if (matches(getWildcards(result), namespace))
              {
                return result;
              }
            }
          }
        }
        return null;
      }
      default:
      {
        return null;
      }
    }
  }

  public EStructuralFeature getAttributeWildcardAffiliation(EClass eClass, String namespace, String name)
  {
    List<EStructuralFeature> allAttributes = getAllAttributes(eClass);
    for (int i = 0, size = allAttributes.size(); i < size; ++i)
    {
      EStructuralFeature result = allAttributes.get(i);
      if (matches(getWildcards(result), namespace))
      {
        return result;
      }
    }

    return null;
  }

  public EStructuralFeature getElementWildcardAffiliation(EClass eClass, String namespace, String name)
  {
    List<EStructuralFeature> allElements = getAllElements(eClass);
    for (int i = 0, size = allElements.size(); i < size; ++i)
    {
      EStructuralFeature result = allElements.get(i);
      if (matches(getWildcards(result), namespace))
      {
        return result;
      }
    }

    return null;
  }

  public boolean matches(List<String> wildcards, String namespace)
  {
    if (!wildcards.isEmpty())
    {
      for (int i = 0, size = wildcards.size(); i < size; ++i)
      {
        String wildcard = wildcards.get(i);
        if (matches(wildcard, namespace))
        {
          return true;
        }
      }
    }

    return false;
  }

  public boolean matches(String wildcard, String namespace)
  {
    return
      wildcard == null ?
        namespace == null :
        wildcard.startsWith("!##") ?
           namespace != null &&
             (!wildcard.endsWith(namespace) || wildcard.length() != namespace.length() + 3) && 
             !XMLTypePackage.eNS_URI.equals(namespace) :
           wildcard.equals("##any") && !XMLTypePackage.eNS_URI.equals(namespace) || wildcard.equals(namespace);
  }

  public int getWhiteSpaceFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getWhiteSpaceFacet();
  }

  protected int basicGetWhiteSpaceFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    if (eAnnotation != null)
    {
      String whiteSpaceLiteral = eAnnotation.getDetails().get("whiteSpace");
      for (int i = 1; i < WHITE_SPACE_KINDS.length; ++i)
      {
        if (WHITE_SPACE_KINDS[i].equals(whiteSpaceLiteral))
        {
          return i;
        }
      }
    }
    return UNSPECIFIED_WHITE_SPACE;
  }

  public void setWhiteSpaceFacet(EDataType eDataType, int whiteSpace)
  {
    if (whiteSpace == UNSPECIFIED_WHITE_SPACE)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("whiteSpace");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("whiteSpace", WHITE_SPACE_KINDS[whiteSpace]);
    }
    getExtendedMetaData(eDataType).setWhiteSpaceFacet(whiteSpace);
  }

  public List<String> getEnumerationFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getEnumerationFacet();
  }

  protected List<String> basicGetEnumerationFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    if (eAnnotation != null)
    {
      String enumerationLiteral = eAnnotation.getDetails().get("enumeration");
      if (enumerationLiteral != null)
      {
        List<String> result = new ArrayList<String>();
        for (String item : enumerationLiteral.split("\\w"))
        {
          String enumeration = replace(replace(item, "%20", " "), "%25", "%");
          result.add(enumeration);
        }
        return result;
      }
    }
    return Collections.emptyList();
  }

  public void setEnumerationFacet(EDataType eDataType, List<String> literals)
  {
    if (literals.isEmpty())
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("enumeration");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      StringBuffer result = new StringBuffer();
      for (int i = 0, size = literals.size(); i < size; ++i)
      {
        result.append(replace(replace(literals.get(i), "%","%25"), " ", "%20"));
        result.append(' ');
      }
      eAnnotation.getDetails().put("enumeration", result.substring(0, result.length() - 1));
    }
    getExtendedMetaData(eDataType).setEnumerationFacet(literals);
  }

  public List<String> getPatternFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getPatternFacet();
  }

  protected List<String> basicGetPatternFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    if (eAnnotation != null)
    {
      String patternLiteral = eAnnotation.getDetails().get("pattern");
      if (patternLiteral != null)
      {
        List<String> result = new ArrayList<String>();
        for (String item : patternLiteral.split("\\w"))
        {
          String pattern = replace(replace(item, "%20", " "), "%25", "%");
          result.add(pattern);
        }
        return result;
      }
    }
    return Collections.emptyList();
  }

  public void setPatternFacet(EDataType eDataType, List<String> pattern)
  {
    if (pattern.isEmpty())
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("pattern");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      StringBuffer result = new StringBuffer();
      for (int i = 0, size = pattern.size(); i < size; ++i)
      {
        result.append(replace(replace(pattern.get(i), "%","%25"), " ", "%20"));
        result.append(' ');
      }
      eAnnotation.getDetails().put("pattern", result.substring(0, result.length() - 1));
    }
    getExtendedMetaData(eDataType).setPatternFacet(pattern);
  }

  public int getTotalDigitsFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getTotalDigitsFacet();
  }

  protected int basicGetTotalDigitsFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    if (eAnnotation != null)
    {
      String totalDigitsLiteral = eAnnotation.getDetails().get("totalDigits");
      if (totalDigitsLiteral != null)
      {
        return Integer.parseInt(totalDigitsLiteral);
      }
    }
    return -1;
  }

  public void setTotalDigitsFacet(EDataType eDataType, int digits)
  {
    if (digits == -1)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("totalDigits");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("totalDigits", Integer.toString(digits));
    } 
    getExtendedMetaData(eDataType).setTotalDigitsFacet(digits);
  }

  public int getFractionDigitsFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getFractionDigitsFacet();
  }

  protected int basicGetFractionDigitsFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    if (eAnnotation != null)
    {
      String fractionDigitsLiteral = eAnnotation.getDetails().get("fractionDigits");
      if (fractionDigitsLiteral != null)
      {
        return Integer.parseInt(fractionDigitsLiteral);
      }
    }
    return -1;
  }

  public void setFractionDigitsFacet(EDataType eDataType, int digits)
  {
    if (digits == -1)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("fractionDigits");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("fractionDigits", Integer.toString(digits));
    } 
    getExtendedMetaData(eDataType).setFractionDigitsFacet(digits);
  }

  public int getLengthFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getLengthFacet();
  }

  protected int basicGetLengthFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    if (eAnnotation != null)
    {
      String lengthLiteral = eAnnotation.getDetails().get("length");
      if (lengthLiteral != null)
      {
        return Integer.parseInt(lengthLiteral);
      }
    }
    return -1;
  }

  public void setLengthFacet(EDataType eDataType, int length)
  {
    if (length == -1)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("length");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("length", Integer.toString(length));
    } 
    getExtendedMetaData(eDataType).setLengthFacet(length);
  }

  public int getMinLengthFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getMinLengthFacet();
  }

  protected int basicGetMinLengthFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    if (eAnnotation != null)
    {
      String minLengthLiteral = eAnnotation.getDetails().get("minLength");
      if (minLengthLiteral != null)
      {
        return Integer.parseInt(minLengthLiteral);
      }
    }
    return -1;
  }

  public void setMinLengthFacet(EDataType eDataType, int length)
  {
    if (length == -1)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("minLength");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("minLength", Integer.toString(length));
    } 
    getExtendedMetaData(eDataType).setMinLengthFacet(length);
  }

  public int getMaxLengthFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getMaxLengthFacet();
  }

  protected int basicGetMaxLengthFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    if (eAnnotation != null)
    {
      String maxLengthLiteral = eAnnotation.getDetails().get("maxLength");
      if (maxLengthLiteral != null)
      {
        return Integer.parseInt(maxLengthLiteral);
      }
    }
    return -1;
  }

  public void setMaxLengthFacet(EDataType eDataType, int length)
  {
    if (length == -1)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("maxLength");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("maxLength", Integer.toString(length));
    } 
    getExtendedMetaData(eDataType).setMaxLengthFacet(length);
  }

  public String getMinExclusiveFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getMinExclusiveFacet();
  }

  protected String basicGetMinExclusiveFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    return
      eAnnotation == null ? 
        null : 
        (String)eAnnotation.getDetails().get("minExclusive");
  }

  public void setMinExclusiveFacet(EDataType eDataType, String literal)
  {
    if (literal == null)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("minExclusive");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("minExclusive", literal);
    } 
    getExtendedMetaData(eDataType).setMinExclusiveFacet(literal);
  }

  public String getMaxExclusiveFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getMaxExclusiveFacet();
  }

  protected String basicGetMaxExclusiveFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    return
      eAnnotation == null ? 
        null : 
        (String)eAnnotation.getDetails().get("maxExclusive");
  }

  public void setMaxExclusiveFacet(EDataType eDataType, String literal)
  {
    if (literal == null)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("maxExclusive");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("maxExclusive", literal);
    } 
    getExtendedMetaData(eDataType).setMaxExclusiveFacet(literal);
  }

  public String getMinInclusiveFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getMinInclusiveFacet();
  }

  protected String basicGetMinInclusiveFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    return
      eAnnotation == null ? 
        null : 
        (String)eAnnotation.getDetails().get("minInclusive");
  }

  public void setMinInclusiveFacet(EDataType eDataType, String literal)
  {
    if (literal == null)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("minInclusive");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("minInclusive", literal);
    } 
    getExtendedMetaData(eDataType).setMinInclusiveFacet(literal);
  }

  public String getMaxInclusiveFacet(EDataType eDataType)
  {
    return getExtendedMetaData(eDataType).getMaxInclusiveFacet();
  }

  protected String basicGetMaxInclusiveFacet(EDataType eDataType)
  {
    EAnnotation eAnnotation = getAnnotation(eDataType, false);
    return
      eAnnotation == null ? 
        null : 
        (String)eAnnotation.getDetails().get("maxInclusive");
  }

  public void setMaxInclusiveFacet(EDataType eDataType, String literal)
  {
    if (literal == null)
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, false);
      if (eAnnotation != null)
      {
        eAnnotation.getDetails().remove("maxInclusive");
      }
    }
    else
    {
      EAnnotation eAnnotation = getAnnotation(eDataType, true);
      eAnnotation.getDetails().put("maxInclusive", literal);
    } 
    getExtendedMetaData(eDataType).setMaxInclusiveFacet(literal);
  }

  public EPackage demandPackage(String namespace)
  {
    EPackage ePackage = demandRegistry.getEPackage(namespace);
    if (ePackage == null)
    {
      ePackage = EcoreFactory.eINSTANCE.createEPackage();
      ePackage.setNsURI(namespace);
      setQualified(ePackage, namespace != null);
      if (namespace != null)
      {
        ePackage.setNsPrefix
          (namespace.equals(ExtendedMetaData.XMLNS_URI) ? 
             namespace.equals(ExtendedMetaData.XML_URI) ?
               "xml" : 
               "xmlns" : 
             computePrefix(namespace));
      }
      demandRegistry.put(namespace, ePackage);

      // demandDocumentRoot(ePackage);

      EClass documentRootEClass = EcoreFactory.eINSTANCE.createEClass();
      documentRootEClass.getESuperTypes().add(XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot());
      documentRootEClass.setName("DocumentRoot");
      ePackage.getEClassifiers().add(documentRootEClass);
      setDocumentRoot(documentRootEClass);
    }
    return ePackage;
  }
  
  protected String computePrefix(String namespace)
  {
    int index = namespace.length();
    boolean containsLetter = false;
    StringBuffer prefix = new StringBuffer(index);
    while (--index >= 0)
    {
      char character = namespace.charAt(index);
      if (XMLTypeUtil.isNCNamePart(character))
      {
        prefix.append(character);
        containsLetter = Character.isLetter(character);
        break;
      }
    }
    while (--index >= 0)
    {
      char character = namespace.charAt(index);
      if (XMLTypeUtil.isNCNamePart(character))
      {
        prefix.append(character);
        if (!containsLetter)
        {
          containsLetter = Character.isLetter(character);
        }
      }
      else if (!containsLetter)
      {
        prefix.append('_');
      }
      else
      {
        break;
      }
    }
    
    int length = prefix.length();
    if (length == 0 || !XMLTypeUtil.isNCNameStart(prefix.charAt(length - 1)))
    {
      prefix.append('_');
    }
    StringBuffer result = new StringBuffer(prefix.length());
    for (int i = prefix.length() - 1; i > 0; --i)
    {
      result.append(prefix.charAt(i));
    }
    return result.toString();
  }

  public EClassifier demandType(String namespace, String name)
  {
    EPackage ePackage = demandPackage(namespace);
    EClassifier eClassifier = getType(ePackage, name);
    if (eClassifier != null)
    {
      return eClassifier;
    }
    else
    {
      EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      eClass.setName(name);
      eClass.getESuperTypes().add(XMLTypePackage.eINSTANCE.getAnyType());
      setContentKind(eClass, MIXED_CONTENT);
      ePackage.getEClassifiers().add(eClass);
      return eClass;
    }
  }

  public EStructuralFeature demandFeature(String namespace, String name, boolean isElement)
  {
    return demandFeature(namespace, name, isElement, isElement);
  }

  public EStructuralFeature demandFeature(String namespace, String name, boolean isElement, boolean isReference)
  {
    EPackage ePackage = demandPackage(namespace);
    EClass documentRootEClass = getDocumentRoot(ePackage);
    EStructuralFeature eStructuralFeature = 
      isElement ? 
        getLocalElement(documentRootEClass, namespace, name) : 
        getLocalAttribute(documentRootEClass, namespace, name);
    if (eStructuralFeature != null)
    {
      return eStructuralFeature;
    }
    else
    {
      if (isReference)
      {
        EReference eReference = EcoreFactory.eINSTANCE.createEReference();
        if (isElement)
        {
          eReference.setContainment(true);
          eReference.setResolveProxies(false);
        }
        eReference.setEType(EcorePackage.Literals.EOBJECT);
        eReference.setName(name);
        eReference.setDerived(true);
        eReference.setTransient(true);
        eReference.setVolatile(true);
        documentRootEClass.getEStructuralFeatures().add(eReference);

        setFeatureKind(eReference, isElement ? ELEMENT_FEATURE : ATTRIBUTE_FEATURE);
        setNamespace(eReference, namespace);

        // Mark the bound as unspecified so that it won't be considered many
        // but can nevertheless be recognized as being unspecified and perhaps still be treat as many.
        //
        if (isElement)
        {
          eReference.setUpperBound(ETypedElement.UNSPECIFIED_MULTIPLICITY);
        }

        return eReference;
      }
      else
      {
        EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        eAttribute.setName(name);
        eAttribute.setEType(XMLTypePackage.eINSTANCE.getAnySimpleType());
        eAttribute.setDerived(true);
        eAttribute.setTransient(true);
        eAttribute.setVolatile(true);
        documentRootEClass.getEStructuralFeatures().add(eAttribute);

        setFeatureKind(eAttribute, isElement ? ELEMENT_FEATURE : ATTRIBUTE_FEATURE);
        setNamespace(eAttribute, namespace);

        // Mark the bound as unspecified so that it won't be considered many
        // but can nevertheless be recognized as being unspecified and perhaps still be treat as many.
        //
        if (isElement)
        {
          eAttribute.setUpperBound(ETypedElement.UNSPECIFIED_MULTIPLICITY);
        }

        return eAttribute;
      }
    }
  }

  @SuppressWarnings("unchecked")
  public Collection<EPackage> demandedPackages()
  {
    return (Collection<EPackage>)(Collection<?>)demandRegistry.values();
  }





  public static interface EPackageExtendedMetaData
  {
    interface Holder
    {
      EPackageExtendedMetaData getExtendedMetaData();
      void setExtendedMetaData(EPackageExtendedMetaData ePackageExtendedMetaData);
    }

    boolean isQualified();
    void setQualified(boolean isQualified);

    EClassifier getType(String name);

    void rename(EClassifier eClassifier, String newName);
  }

  public class EPackageExtendedMetaDataImpl implements EPackageExtendedMetaData
  {
    protected EPackage ePackage;
    protected boolean isInitialized;
    protected boolean isQualified;
    protected Map<String, EClassifier> nameToClassifierMap;

    public EPackageExtendedMetaDataImpl(EPackage ePackage)
    {
      this.ePackage = ePackage;
    }

    public boolean isQualified()
    {
      if (!isInitialized)
      {
        setQualified(basicIsQualified(ePackage));
      }
      return isQualified;
    }

    public void setQualified(boolean isQualified)
    {
      this.isQualified = isQualified;
      isInitialized = true;
    }
    
    public EClassifier getType(String name)
    {
      EClassifier result = null;
      if (nameToClassifierMap != null)
      {
        result = nameToClassifierMap.get(name);
      }
      if (result == null)
      {
        List<EClassifier> eClassifiers = ePackage.getEClassifiers();
        int size = eClassifiers.size();
        if (nameToClassifierMap == null || nameToClassifierMap.size() != size)
        {
          Map<String, EClassifier> nameToClassifierMap = new HashMap<String, EClassifier>();
          if (this.nameToClassifierMap != null)
          {
            nameToClassifierMap.putAll(this.nameToClassifierMap);
          }
  
          // For demand created created packages we allow the list of classifiers to grow 
          // so this should handle those additional instances.
          //
          int originalMapSize = nameToClassifierMap.size();
          for (int i = originalMapSize; i < size; ++i)
          {
            EClassifier eClassifier = eClassifiers.get(i);
            String eClassifierName = getName(eClassifier);
            EClassifier conflictingEClassifier = nameToClassifierMap.put(eClassifierName, eClassifier);
            if (conflictingEClassifier != null && conflictingEClassifier != eClassifier)
            {
              nameToClassifierMap.put(eClassifierName, conflictingEClassifier);
            }
          }
  
          if (nameToClassifierMap.size() != size)
          {
            for (int i = 0; i < originalMapSize; ++i)
            {
              EClassifier eClassifier = eClassifiers.get(i);
              String eClassifierName = getName(eClassifier);
              EClassifier conflictingEClassifier = nameToClassifierMap.put(eClassifierName, eClassifier);
              if (conflictingEClassifier != null && conflictingEClassifier != eClassifier)
              {
                nameToClassifierMap.put(eClassifierName, conflictingEClassifier);
              }
            }
          }
          result = nameToClassifierMap.get(name);
          this.nameToClassifierMap = nameToClassifierMap;
        }
      }

      return result;
    }

    public void rename(EClassifier eClassifier, String newName)
    {
      if (nameToClassifierMap != null)
      {
        nameToClassifierMap.values().remove(eClassifier);
        nameToClassifierMap.put(newName, eClassifier);
      }
    }
  }

  protected EPackageExtendedMetaData getExtendedMetaData(EPackage ePackage)
  {
    if (extendedMetaDataHolderCache != null)
    {
      EPackageExtendedMetaData result = (EPackageExtendedMetaData)extendedMetaDataHolderCache.get(ePackage);
      if (result == null)
      {
        extendedMetaDataHolderCache.put(ePackage, result = createEPackageExtendedMetaData(ePackage));
      }
      return result;
    }
    else
    {
      EPackageExtendedMetaData.Holder holder = (EPackageExtendedMetaData.Holder)ePackage;
      EPackageExtendedMetaData result = holder.getExtendedMetaData();
      if (result == null)
      {
        holder.setExtendedMetaData(result = createEPackageExtendedMetaData(ePackage));
      }
      return result;
    } 
  }

  protected EPackageExtendedMetaData createEPackageExtendedMetaData(EPackage ePackage)
  {
    return new EPackageExtendedMetaDataImpl(ePackage);
  }


  protected static final String UNINITIALIZED_STRING = "uninitialized";
  protected static final int UNINITIALIZED_INT = -2;
  protected static final EDataType UNINITIALIZED_EDATA_TYPE = EcoreFactory.eINSTANCE.createEDataType();
  protected static final EStructuralFeature UNINITIALIZED_ESTRUCTURAL_FEATURE = EcoreFactory.eINSTANCE.createEAttribute();


  public static interface EClassifierExtendedMetaData
  {
    interface Holder
    {
      EClassifierExtendedMetaData getExtendedMetaData();
      void setExtendedMetaData(EClassifierExtendedMetaData eClassifierExtendedMetaData);
    }
    String getName();
    void setName(String name);

    int getContentKind();
    void setContentKind(int kind);

    int getDerivationKind();

    EDataType getBaseType();
    void setBaseType(EDataType baseType);

    EDataType getItemType();
    void setItemType(EDataType itemType);

    List<EDataType> getMemberTypes();
    void setMemberTypes(List<EDataType> memberTypes);

    int getWhiteSpaceFacet();
    void setWhiteSpaceFacet(int whiteSpace);

    List<String> getEnumerationFacet();
    void setEnumerationFacet(List<String> literals);

    List<String> getPatternFacet();
    void setPatternFacet(List<String> literals);

    int getTotalDigitsFacet();
    void setTotalDigitsFacet(int digits);

    int getFractionDigitsFacet();
    void setFractionDigitsFacet(int digits);

    int getLengthFacet();
    void setLengthFacet(int length);
    
    int getMinLengthFacet();
    void setMinLengthFacet(int length);
    
    int getMaxLengthFacet();
    void setMaxLengthFacet(int length);

    String getMinExclusiveFacet();
    void setMinExclusiveFacet(String literal);

    String getMaxExclusiveFacet();
    void setMaxExclusiveFacet(String literal);

    String getMinInclusiveFacet();
    void setMinInclusiveFacet(String literal);

    String getMaxInclusiveFacet();
    void setMaxInclusiveFacet(String literal);
  }

  public class EClassExtendedMetaDataImpl implements EClassifierExtendedMetaData
  {
    protected EClass eClass;
    protected String name = UNINITIALIZED_STRING;
    protected int contentKind = UNINITIALIZED_INT;

    public EClassExtendedMetaDataImpl(EClass eClass)
    {
      this.eClass = eClass;
    }

    public String getName()
    {
      if (name == UNINITIALIZED_STRING)
      {
        setName(basicGetName(eClass));
      }
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public int getContentKind()
    {
      if (contentKind == UNINITIALIZED_INT)
      {
        setContentKind(basicGetContentKind(eClass));
      }
      return contentKind;
    }

    public void setContentKind(int kind)
    {
      this.contentKind = kind;
    }

    public int getDerivationKind()
    {
      return 0;
    }

    public EDataType getBaseType()
    {
      return null;
    }

    public void setBaseType(EDataType baseType)
    {
      throw new UnsupportedOperationException("Can't set the base type of an EClass");
    }

    public EDataType getItemType()
    {
      return null;
    }

    public void setItemType(EDataType itemType)
    {
      throw new UnsupportedOperationException("Can't set the item type of an EClass");
    }

    public List<EDataType> getMemberTypes()
    {
      return Collections.emptyList();
    }

    public void setMemberTypes(List<EDataType> memberTypes)
    {
      throw new UnsupportedOperationException("Can't set the member types of an EClass");
    }

    public int getWhiteSpaceFacet()
    {
      return 0;
    }

    public void setWhiteSpaceFacet(int whiteSpace)
    {
      throw new UnsupportedOperationException("Can't set the white space of an EClass");
    }

    public List<String> getEnumerationFacet()
    {
      return Collections.emptyList();
    }

    public void setEnumerationFacet(List<String> literals)
    {
      throw new UnsupportedOperationException("Can't set the enumeration of an EClass");
    }

    public List<String> getPatternFacet()
    {
      return Collections.emptyList();
    }

    public void setPatternFacet(List<String> pattern)
    {
      throw new UnsupportedOperationException("Can't set the pattern of an EClass");
    }

    public int getTotalDigitsFacet()
    {
      return -1;
    }

    public void setTotalDigitsFacet(int digits)
    {
      throw new UnsupportedOperationException("Can't set the total digits of an EClass");
    }

    public int getFractionDigitsFacet()
    {
      return -1;
    }

    public void setFractionDigitsFacet(int digits)
    {
      throw new UnsupportedOperationException("Can't set the fraction digits of an EClass");
    }

    public int getLengthFacet()
    {
      return -1;
    }

    public void setLengthFacet(int length)
    {
      throw new UnsupportedOperationException("Can't set the length of an EClass");
    }

    public int getMinLengthFacet()
    {
      return -1;
    }

    public void setMinLengthFacet(int minLength)
    {
      throw new UnsupportedOperationException("Can't set the min length of an EClass");
    }

    public int getMaxLengthFacet()
    {
      return -1;
    }

    public void setMaxLengthFacet(int maxLength)
    {
      throw new UnsupportedOperationException("Can't set the max length of an EClass");
    }

    public String getMinExclusiveFacet()
    {
      return null;
    }

    public void setMinExclusiveFacet(String literal)
    {
      throw new UnsupportedOperationException("Can't set the min exclusive of an EClass");
    }

    public String getMaxExclusiveFacet()
    {
      return null;
    }

    public void setMaxExclusiveFacet(String literal)
    {
      throw new UnsupportedOperationException("Can't set the max exclusive of an EClass");
    }

    public String getMinInclusiveFacet()
    {
      return null;
    }

    public void setMinInclusiveFacet(String literal)
    {
      throw new UnsupportedOperationException("Can't set the min inclusive of an EClass");
    }

    public String getMaxInclusiveFacet()
    {
      return null;
    }

    public void setMaxInclusiveFacet(String literal)
    {
      throw new UnsupportedOperationException("Can't set the max inclusive of an EClass");
    }
  }

  public class EDataTypeExtendedMetaDataImpl implements EClassifierExtendedMetaData
  {
    protected EDataType eDataType;
    protected String name = UNINITIALIZED_STRING;
    protected EDataType baseType = UNINITIALIZED_EDATA_TYPE;
    protected EDataType itemType = UNINITIALIZED_EDATA_TYPE;
    protected List<EDataType> memberTypes;
    protected int derivationKind = UNINITIALIZED_INT;
    protected int whiteSpace = UNINITIALIZED_INT;
    protected List<String> enumerationLiterals;
    protected List<String> pattern;
    int totalDigits = UNINITIALIZED_INT;
    int fractionDigits = UNINITIALIZED_INT;
    int length = UNINITIALIZED_INT;
    int minLength = UNINITIALIZED_INT;
    int maxLength = UNINITIALIZED_INT;
    String minExclusive = UNINITIALIZED_STRING;
    String maxExclusive = UNINITIALIZED_STRING;
    String minInclusive = UNINITIALIZED_STRING;
    String maxInclusive = UNINITIALIZED_STRING;

    public EDataTypeExtendedMetaDataImpl(EDataType eDataType)
    {
      this.eDataType = eDataType;
    }

    public String getName()
    {
      if (name == UNINITIALIZED_STRING)
      {
        setName(basicGetName(eDataType));
      }
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public int getContentKind()
    {
      return 0;
    }

    public void setContentKind(int kind)
    {
      throw new UnsupportedOperationException("Can't set the content kind of an EDataType");
    }

    public int getDerivationKind()
    {
      if (derivationKind == UNINITIALIZED_INT)
      {
        if (getBaseType() != null)
        {
          derivationKind = RESTRICTION_DERIVATION;
        }
        else if (getItemType() != null)
        {
          derivationKind = LIST_DERIVATION;
        }
        else if (!getMemberTypes().isEmpty())
        {
          derivationKind = UNION_DERIVATION;
        }
        else
        {
          derivationKind = 0;
        }
      }
      return derivationKind;
    }

    public EDataType getBaseType()
    {
      if (baseType == UNINITIALIZED_EDATA_TYPE)
      {
        setBaseType(basicGetBaseType(eDataType));
      }
      return baseType;
    }

    public void setBaseType(EDataType baseType)
    {
      this.baseType = baseType;
      derivationKind = UNINITIALIZED_INT;
    }

    public EDataType getItemType()
    {
      if (itemType == UNINITIALIZED_EDATA_TYPE)
      {
        setItemType(basicGetItemType(eDataType));
      }
      return itemType;
    }

    public void setItemType(EDataType itemType)
    {
      this.itemType = itemType;
      derivationKind = UNINITIALIZED_INT;
    }

    public List<EDataType> getMemberTypes()
    {
      if (memberTypes == null)
      {
        setMemberTypes(basicGetMemberTypes(eDataType));
      }
      return memberTypes;
    }

    public void setMemberTypes(List<EDataType> memberTypes)
    {
      this.memberTypes = memberTypes;
      derivationKind = UNINITIALIZED_INT;
    }

    public int getWhiteSpaceFacet()
    {
      if (whiteSpace == UNINITIALIZED_INT)
      {
        setWhiteSpaceFacet(basicGetWhiteSpaceFacet(eDataType));
      }
      return whiteSpace;
    }

    public void setWhiteSpaceFacet(int whiteSpace)
    {
      this.whiteSpace = whiteSpace;
    }

    public List<String> getEnumerationFacet()
    {
      if (enumerationLiterals == null)
      {
        setEnumerationFacet(basicGetEnumerationFacet(eDataType));
      }
      return enumerationLiterals;
    }

    public void setEnumerationFacet(List<String> literals)
    {
      this.enumerationLiterals = literals;
    }

    public List<String> getPatternFacet()
    {
      if (pattern == null)
      {
        setPatternFacet(basicGetPatternFacet(eDataType));
      }
      return pattern;
    }

    public void setPatternFacet(List<String> pattern)
    {
      this.pattern = pattern;
    }

    public int getTotalDigitsFacet()
    {
      if (totalDigits == UNINITIALIZED_INT)
      {
        setTotalDigitsFacet(basicGetTotalDigitsFacet(eDataType));
      }
      return totalDigits;
    }

    public void setTotalDigitsFacet(int digits)
    {
      this.totalDigits = digits;
    }

    public int getFractionDigitsFacet()
    {
      if (fractionDigits == UNINITIALIZED_INT)
      {
        setFractionDigitsFacet(basicGetFractionDigitsFacet(eDataType));
      }
      return fractionDigits;
    }

    public void setFractionDigitsFacet(int digits)
    {
      this.fractionDigits = digits;
    }

    public int getLengthFacet()
    {
      if (length == UNINITIALIZED_INT)
      {
        setLengthFacet(basicGetLengthFacet(eDataType));
      }
      return length;
    }

    public void setLengthFacet(int length)
    {
      this.length = length;
    }

    public int getMinLengthFacet()
    {
      if (minLength == UNINITIALIZED_INT)
      {
        setMinLengthFacet(basicGetMinLengthFacet(eDataType));
      }
      return minLength;
    }

    public void setMinLengthFacet(int minLength)
    {
      this.minLength = minLength;
    }

    public int getMaxLengthFacet()
    {
      if (maxLength == UNINITIALIZED_INT)
      {
        setMaxLengthFacet(basicGetMaxLengthFacet(eDataType));
      }
      return maxLength;
    }

    public void setMaxLengthFacet(int maxLength)
    {
      this.maxLength = maxLength;
    }

    public String getMinExclusiveFacet()
    {
      if (minExclusive == UNINITIALIZED_STRING)
      {
        setMinExclusiveFacet(basicGetMinExclusiveFacet(eDataType));
      }
      return minExclusive;
    }

    public void setMinExclusiveFacet(String literal)
    {
      this.minExclusive = literal;
    }

    public String getMaxExclusiveFacet()
    {
      if (maxExclusive == UNINITIALIZED_STRING)
      {
        setMaxExclusiveFacet(basicGetMaxExclusiveFacet(eDataType));
      }
      return maxExclusive;
    }

    public void setMaxExclusiveFacet(String literal)
    {
      this.maxExclusive = literal;
    }

    public String getMinInclusiveFacet()
    {
      if (minInclusive == UNINITIALIZED_STRING)
      {
        setMinInclusiveFacet(basicGetMinInclusiveFacet(eDataType));
      }
      return minInclusive;
    }

    public void setMinInclusiveFacet(String literal)
    {
      this.minInclusive = literal;
    }

    public String getMaxInclusiveFacet()
    {
      if (maxInclusive == UNINITIALIZED_STRING)
      {
        setMaxInclusiveFacet(basicGetMaxInclusiveFacet(eDataType));
      }
      return maxInclusive;
    }

    public void setMaxInclusiveFacet(String literal)
    {
      this.maxInclusive = literal;
    }
  }

  protected EClassifierExtendedMetaData getExtendedMetaData(EClassifier eClassifier)
  {
    if (extendedMetaDataHolderCache != null)
    {
      EClassifierExtendedMetaData result = (EClassifierExtendedMetaData)extendedMetaDataHolderCache.get(eClassifier);
      if (result == null)
      {
        extendedMetaDataHolderCache.put(eClassifier, result = createEClassifierExtendedMetaData(eClassifier));
      }
      return result;
    }
    else
    {
      EClassifierExtendedMetaData.Holder holder = (EClassifierExtendedMetaData.Holder)eClassifier;
      EClassifierExtendedMetaData result = holder.getExtendedMetaData();
      if (result == null)
      {
        holder.setExtendedMetaData(result = createEClassifierExtendedMetaData(eClassifier));
      }
      return result;
    }  
  }

  protected EClassifierExtendedMetaData createEClassifierExtendedMetaData(EClassifier eClassifier)
  {
    if (eClassifier instanceof EClass)
    {
      return new EClassExtendedMetaDataImpl((EClass)eClassifier);
    }
    else
    {
      return new EDataTypeExtendedMetaDataImpl((EDataType)eClassifier);
    }
  }

  public static interface EStructuralFeatureExtendedMetaData
  {
    interface Holder
    {
      EStructuralFeatureExtendedMetaData getExtendedMetaData();
      void setExtendedMetaData(EStructuralFeatureExtendedMetaData eStructuralFeatureExtendedMetaData);
    }

    String getName();
    void setName(String name);

    String getNamespace();
    void setNamespace(String namespace);

    int getFeatureKind();
    void setFeatureKind(int kind);

    List<String> getWildcards();
    void setWildcards(List<String> wildcards);

    int getProcessingKind();
    void setProcessingKind(int kind);

    EStructuralFeature getGroup();
    void setGroup(EStructuralFeature group);

    EStructuralFeature getAffiliation();
    void setAffiliation(EStructuralFeature affiliation);

    Map<EClass, FeatureMapUtil.Validator> getValidatorMap();
  }

  public class EStructuralFeatureExtendedMetaDataImpl implements EStructuralFeatureExtendedMetaData
  {
    protected EStructuralFeature eStructuralFeature;
    protected String name = UNINITIALIZED_STRING;
    protected String namespace = UNINITIALIZED_STRING;
    protected int featureKind = UNINITIALIZED_INT;
    protected List<String> wildcards;
    protected int processingKind = UNINITIALIZED_INT;
    protected EStructuralFeature group = UNINITIALIZED_ESTRUCTURAL_FEATURE;
    protected EStructuralFeature affiliation = UNINITIALIZED_ESTRUCTURAL_FEATURE;
    protected Map<EClass, FeatureMapUtil.Validator> validatorMap;

    public EStructuralFeatureExtendedMetaDataImpl(EStructuralFeature eStructuralFeature)
    {
      this.eStructuralFeature = eStructuralFeature;
    }

    public Map<EClass, FeatureMapUtil.Validator> getValidatorMap()
    {
      if (validatorMap == null)
      {
        validatorMap = new HashMap<EClass, FeatureMapUtil.Validator>();
      }
      return validatorMap;
    }

    public String getName()
    {
      if (name == UNINITIALIZED_STRING)
      {
        setName(basicGetName(eStructuralFeature));
      }
      return name;
    }

    public void setName(String name)
    {
      this.name= name;
    }

    public String getNamespace()
    {
      if (namespace == UNINITIALIZED_STRING)
      {
        setNamespace(basicGetNamespace(eStructuralFeature));
      }
      return namespace;
    }

    public void setNamespace(String namespace)
    {
      this.namespace = namespace;
    }

    public int getFeatureKind()
    {
      if (featureKind == UNINITIALIZED_INT)
      {
        setFeatureKind(basicGetFeatureKind(eStructuralFeature));
      }
      return featureKind;
    }

    public void setFeatureKind(int kind)
    {
      this.featureKind = kind;
    }

    public List<String> getWildcards()
    {
      if (wildcards == null)
      {
        setWildcards(basicGetWildcards(eStructuralFeature));
      }
      return wildcards;
    }

    public void setWildcards(List<String> wildcards)
    {
      this.wildcards = wildcards;
    }

    public int getProcessingKind()
    {
      if (processingKind == UNINITIALIZED_INT)
      {
        setProcessingKind(basicGetProcessingKind(eStructuralFeature));
      }
      return processingKind;
    }

    public void setProcessingKind(int kind)
    {
      this.processingKind = kind;
    }

    public EStructuralFeature getGroup()
    {
      if (group == UNINITIALIZED_ESTRUCTURAL_FEATURE)
      {
        setGroup(basicGetGroup(eStructuralFeature));
      }
      return group;
    }

    public void setGroup(EStructuralFeature group)
    {
      this.group = group;
    }

    public EStructuralFeature getAffiliation()
    {
      if (affiliation == UNINITIALIZED_ESTRUCTURAL_FEATURE)
      {
        setAffiliation(basicGetAffiliation(eStructuralFeature));
      }
      return affiliation;
    }

    public void setAffiliation(EStructuralFeature affiliation)
    {
      this.affiliation = affiliation;
    }
  }

  protected EStructuralFeatureExtendedMetaData getExtendedMetaData(EStructuralFeature eStructuralFeature)
  {
    if (extendedMetaDataHolderCache != null)
    {
      EStructuralFeatureExtendedMetaData result = (EStructuralFeatureExtendedMetaData)extendedMetaDataHolderCache.get(eStructuralFeature);
      if (result == null)
      {
        extendedMetaDataHolderCache.put(eStructuralFeature, result = createEStructuralFeatureExtendedMetaData(eStructuralFeature));
      }
      return result;
    }
    else
    {
      EStructuralFeatureExtendedMetaData.Holder holder = (EStructuralFeatureExtendedMetaData.Holder)eStructuralFeature;
      EStructuralFeatureExtendedMetaData result = holder.getExtendedMetaData();
      if (result == null)
      {
        holder.setExtendedMetaData(result = createEStructuralFeatureExtendedMetaData(eStructuralFeature));
      }
      return result;
    }
  }

  protected EStructuralFeatureExtendedMetaData createEStructuralFeatureExtendedMetaData(EStructuralFeature eStructuralFeature)
  {
    return new EStructuralFeatureExtendedMetaDataImpl(eStructuralFeature);
  }

  private static String replace(String in, String oldString, String newString)
  {
    if (in == null || oldString == null)
    {
      return in;
    }
      
    int oldStringLength = oldString.length();
    if (oldStringLength == 0)
    {
      return in;
    }
    
    if (newString == null)
    {
      newString = "";
    }
    int newStringLength = newString.length();
      
    int index = -newStringLength;
    StringBuffer result = new StringBuffer(in);
    while((index = indexOf(result, oldString, index + newStringLength)) >= 0)
    {
      result.replace(index, index + oldStringLength, newString);
    }
    
    return result.toString();
  }
  
  private static int indexOf(StringBuffer in, String str, int fromIndex)
  {
    if (in == null)
    {
      return -1;
    }
    
    if (str == null)
    {
      str = "";
    }
      
    int lengthIn = in.length();
    int lengthStr = str.length();

    if (lengthIn < lengthStr)
    {
      return -1;
    }

    if (fromIndex > lengthIn)
    {
      if (lengthIn == 0 && fromIndex == 0 && lengthStr == 0)
      {
        return 0;
      }
      return -1;
    }
    
    if (fromIndex < 0)
    {
      fromIndex = 0;
    }
      
    if (lengthStr == 0)
    {
      return fromIndex;
    }
      
    int strPos = 0;
    for (int i = fromIndex; i < lengthIn; i++)
    {
      if (in.charAt(i) == str.charAt(strPos))
      {
        strPos++;
        if(strPos == lengthStr)
        {
          return i - lengthStr + 1;
        }
      }
      else
      {
        strPos = 0; 
      }
    }
    
    return -1;
  }
}
