/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.impl;


import com.google.gwt.user.client.rpc.GwtTransient;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIService;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EPackage</b></em>'.
 * @extends BasicExtendedMetaData.EPackageExtendedMetaData.Holder
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EPackageImpl#getNsURI <em>Ns URI</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EPackageImpl#getNsPrefix <em>Ns Prefix</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EPackageImpl#getEFactoryInstance <em>EFactory Instance</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EPackageImpl#getEClassifiers <em>EClassifiers</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EPackageImpl#getESubpackages <em>ESubpackages</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EPackageImpl#getESuperPackage <em>ESuper Package</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EPackageImpl extends ENamedElementImpl implements EPackage, BasicExtendedMetaData.EPackageExtendedMetaData.Holder
{
  /**
   * The default value of the '{@link #getNsURI() <em>Ns URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNsURI()
   * @generated
   * @ordered
   */
  protected static final String NS_URI_EDEFAULT = null;

  /**
   * The Ecore factory.
   */
  @GwtTransient
  protected EcoreFactory ecoreFactory;

  /**
   * The Ecore factory.
   */
  @GwtTransient
  protected EcorePackage ecorePackage;

  /**
   * The map from name to 
   */
  @GwtTransient
  protected Map<String, EClassifier> eNameToEClassifierMap; 

  /**
   * <!-- begin-user-doc -->
   * Creates an instance.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected EPackageImpl()
  {
    super();

    setEFactoryInstance(new EFactoryImpl());

    ecorePackage = EcorePackage.eINSTANCE;
    ecoreFactory = EcoreFactory.eINSTANCE;
  }

  /**
   * Creates an instance with a factory.
   * @param eFactory the factory of the new package.
   */
  protected EPackageImpl(EFactory eFactory)
  {
    super();

    setEFactoryInstance(eFactory);

    ecorePackage = EcorePackage.eINSTANCE;
    ecoreFactory = EcoreFactory.eINSTANCE;
  }

  /**
   * Creates a {@link org.eclipse.emf.ecore.EPackage.Registry#INSTANCE registered} instance that has a default factory.
   * @param packageURI the registered {@link #getNsURI namespace URI} of the new package.
   */
  protected EPackageImpl(String packageURI)
  {
    this(packageURI, new EFactoryImpl());
  }

  /**
   * Creates a {@link org.eclipse.emf.ecore.EPackage.Registry#INSTANCE registered} instance with a factory.
   * @param packageURI the registered {@link #getNsURI namespace URI} of the new package.
   * @param factory the factory of the new package.
   */
  protected EPackageImpl(String packageURI, final EFactory factory)
  {
    super();

    Object registration = Registry.INSTANCE.get(packageURI);
    if (registration instanceof Descriptor)
    {
      Registry.INSTANCE.put
        (packageURI, 
         new Descriptor()
         {
           public EPackage getEPackage()
           {
             return EPackageImpl.this;
           }

           public EFactory getEFactory()
           {
             return factory;
           }
         });
    }
    else
    {
      Registry.INSTANCE.put(packageURI, this);
    }

    setEFactoryInstance(factory);

    if (factory == EcoreFactory.eINSTANCE)
    {
      ecorePackage = (EcorePackage)this;
      ecoreFactory = (EcoreFactory)factory;
    }
    else
    {
      ecorePackage = EcorePackage.eINSTANCE;
      ecoreFactory = EcoreFactory.eINSTANCE;
    }
  }

  @Override
  public void freeze()
  {
    if (eClassifiers != null)
    {
      for (int i = 0, size = eClassifiers.size(); i < size; ++i)
      {
        freeze(eClassifiers.get(i));
      }
    }
    if (eSubpackages != null)
    {
      for (int i = 0, size = eSubpackages.size(); i < size; ++i)
      {
        freeze(eSubpackages.get(i));
      }
    }
    super.freeze();
  }
  
  @Override
  public void eSetProxyURI(URI uri)
  {
    // If we turn the package into a proxy, ensure that the child classifiers clear their cached container.
    //
    if (uri != null && eClassifiers != null)
    {
      for (Object eClassifier : eClassifiers)
      {
        if (eClassifier instanceof EClassifierImpl)
        {
          ((EClassifierImpl)eClassifier).ePackage = null;
        }
      }
    }
    super.eSetProxyURI(uri);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EcorePackage.Literals.EPACKAGE;
  }

  /**
   * @generated modifiable
   */
  @Deprecated
  public void setNamespaceURI(String nsURI)
  {
    // Do nothing.
  }

  /**
   * The cached value of the '{@link #getNsURI() <em>Ns URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNsURI()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected String nsURI = NS_URI_EDEFAULT;

  /**
   * The default value of the '{@link #getNsPrefix() <em>Ns Prefix</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNsPrefix()
   * @generated
   * @ordered
   */
  protected static final String NS_PREFIX_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getNsPrefix() <em>Ns Prefix</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNsPrefix()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected String nsPrefix = NS_PREFIX_EDEFAULT;

  /**
   * The cached value of the '{@link #getEFactoryInstance() <em>EFactory Instance</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEFactoryInstance()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EFactory eFactoryInstance;

  /**
   * The cached value of the '{@link #getEClassifiers() <em>EClassifiers</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEClassifiers()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EClassifier> eClassifiers;

  /**
   * The cached value of the '{@link #getESubpackages() <em>ESubpackages</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getESubpackages()
   * @generated
   * @ordered
   */
  @GwtTransient
  protected EList<EPackage> eSubpackages;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getNsURI()
  {
    return nsURI;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNsURI(String newNsURI)
  {
    String oldNsURI = nsURI;
    nsURI = newNsURI;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.EPACKAGE__NS_URI, oldNsURI, nsURI));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getNsPrefix()
  {
    return nsPrefix;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNsPrefix(String newNsPrefix)
  {
    String oldNsPrefix = nsPrefix;
    nsPrefix = newNsPrefix;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.EPACKAGE__NS_PREFIX, oldNsPrefix, nsPrefix));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EFactory getEFactoryInstance()
  {
    return eFactoryInstance;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEFactoryInstance(EFactory newEFactoryInstance)
  {
    if (newEFactoryInstance != eFactoryInstance)
    {
      NotificationChain msgs = null;
      if (eFactoryInstance != null)
        msgs = ((InternalEObject)eFactoryInstance).eInverseRemove(this, EcorePackage.EFACTORY__EPACKAGE, EFactory.class, msgs);
      if (newEFactoryInstance != null)
        msgs = ((InternalEObject)newEFactoryInstance).eInverseAdd(this, EcorePackage.EFACTORY__EPACKAGE, EFactory.class, msgs);
      msgs = basicSetEFactoryInstance(newEFactoryInstance, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, EcorePackage.EPACKAGE__EFACTORY_INSTANCE, newEFactoryInstance, newEFactoryInstance));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetEFactoryInstance(EFactory newEFactoryInstance, NotificationChain msgs)
  {
    EFactory oldEFactoryInstance = eFactoryInstance;
    eFactoryInstance = newEFactoryInstance;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EcorePackage.EPACKAGE__EFACTORY_INSTANCE, oldEFactoryInstance, newEFactoryInstance);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<EClassifier> getEClassifiers()
  {
    if (eClassifiers == null)
    {
      eClassifiers = 
        new EObjectContainmentWithInverseEList.Resolving<EClassifier>
          (EClassifier.class, this, EcorePackage.EPACKAGE__ECLASSIFIERS, EcorePackage.ECLASSIFIER__EPACKAGE)
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected void didChange()
          {
            eNameToEClassifierMap = null;
          }
        };
    }
    return eClassifiers;
  }

  public EClassifier getEClassifier(String name)
  {
    return getEClassifierGen(name);
  }

  /**
   * @generated modifiable
   */
  public EClassifier getEClassifierGen(String name)
  {
    if (eNameToEClassifierMap == null)
    {
      List<EClassifier> eClassifiers = getEClassifiers();
      Map<String, EClassifier> result = new HashMap<String, EClassifier>(eClassifiers.size());
      for (EClassifier eClassifier : eClassifiers)
      {
        result.put(eClassifier.getName(), eClassifier);
      }
      eNameToEClassifierMap = result;
    }

    return eNameToEClassifierMap.get(name);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case EcorePackage.EPACKAGE__EANNOTATIONS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEAnnotations()).basicAdd(otherEnd, msgs);
      case EcorePackage.EPACKAGE__EFACTORY_INSTANCE:
        if (eFactoryInstance != null)
          msgs = ((InternalEObject)eFactoryInstance).eInverseRemove(this, EcorePackage.EFACTORY__EPACKAGE, EFactory.class, msgs);
        return basicSetEFactoryInstance((EFactory)otherEnd, msgs);
      case EcorePackage.EPACKAGE__ECLASSIFIERS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEClassifiers()).basicAdd(otherEnd, msgs);
      case EcorePackage.EPACKAGE__ESUBPACKAGES:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getESubpackages()).basicAdd(otherEnd, msgs);
      case EcorePackage.EPACKAGE__ESUPER_PACKAGE:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, EcorePackage.EPACKAGE__ESUPER_PACKAGE, msgs);
    }
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case EcorePackage.EPACKAGE__EANNOTATIONS:
        return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
      case EcorePackage.EPACKAGE__EFACTORY_INSTANCE:
        return basicSetEFactoryInstance(null, msgs);
      case EcorePackage.EPACKAGE__ECLASSIFIERS:
        return ((InternalEList<?>)getEClassifiers()).basicRemove(otherEnd, msgs);
      case EcorePackage.EPACKAGE__ESUBPACKAGES:
        return ((InternalEList<?>)getESubpackages()).basicRemove(otherEnd, msgs);
      case EcorePackage.EPACKAGE__ESUPER_PACKAGE:
        return eBasicSetContainer(null, EcorePackage.EPACKAGE__ESUPER_PACKAGE, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
      case EcorePackage.EPACKAGE__ESUPER_PACKAGE:
        return eInternalContainer().eInverseRemove(this, EcorePackage.EPACKAGE__ESUBPACKAGES, EPackage.class, msgs);
    }
    return eDynamicBasicRemoveFromContainer(msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<EPackage> getESubpackages()
  {
    if (eSubpackages == null)
    {
      eSubpackages = new EObjectContainmentWithInverseEList.Resolving<EPackage>(EPackage.class, this, EcorePackage.EPACKAGE__ESUBPACKAGES, EcorePackage.EPACKAGE__ESUPER_PACKAGE);
    }
    return eSubpackages;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EPackage getESuperPackage()
  {
    return (eContainerFeatureID() == EcorePackage.EPACKAGE__ESUPER_PACKAGE) ? (EPackage)eContainer : null;
  }


  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EPackage basicGetESuperPackage()
  {
    if (eContainerFeatureID() != EcorePackage.EPACKAGE__ESUPER_PACKAGE) return null;
    return (EPackage)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case EcorePackage.EPACKAGE__EANNOTATIONS:
        return getEAnnotations();
      case EcorePackage.EPACKAGE__NAME:
        return getName();
      case EcorePackage.EPACKAGE__NS_URI:
        return getNsURI();
      case EcorePackage.EPACKAGE__NS_PREFIX:
        return getNsPrefix();
      case EcorePackage.EPACKAGE__EFACTORY_INSTANCE:
        return getEFactoryInstance();
      case EcorePackage.EPACKAGE__ECLASSIFIERS:
        return getEClassifiers();
      case EcorePackage.EPACKAGE__ESUBPACKAGES:
        return getESubpackages();
      case EcorePackage.EPACKAGE__ESUPER_PACKAGE:
        if (resolve) return getESuperPackage();
        return basicGetESuperPackage();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case EcorePackage.EPACKAGE__EANNOTATIONS:
        getEAnnotations().clear();
        getEAnnotations().addAll((Collection<? extends EAnnotation>)newValue);
        return;
      case EcorePackage.EPACKAGE__NAME:
        setName((String)newValue);
        return;
      case EcorePackage.EPACKAGE__NS_URI:
        setNsURI((String)newValue);
        return;
      case EcorePackage.EPACKAGE__NS_PREFIX:
        setNsPrefix((String)newValue);
        return;
      case EcorePackage.EPACKAGE__EFACTORY_INSTANCE:
        setEFactoryInstance((EFactory)newValue);
        return;
      case EcorePackage.EPACKAGE__ECLASSIFIERS:
        getEClassifiers().clear();
        getEClassifiers().addAll((Collection<? extends EClassifier>)newValue);
        return;
      case EcorePackage.EPACKAGE__ESUBPACKAGES:
        getESubpackages().clear();
        getESubpackages().addAll((Collection<? extends EPackage>)newValue);
        return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case EcorePackage.EPACKAGE__EANNOTATIONS:
        getEAnnotations().clear();
        return;
      case EcorePackage.EPACKAGE__NAME:
        setName(NAME_EDEFAULT);
        return;
      case EcorePackage.EPACKAGE__NS_URI:
        setNsURI(NS_URI_EDEFAULT);
        return;
      case EcorePackage.EPACKAGE__NS_PREFIX:
        setNsPrefix(NS_PREFIX_EDEFAULT);
        return;
      case EcorePackage.EPACKAGE__EFACTORY_INSTANCE:
        setEFactoryInstance((EFactory)null);
        return;
      case EcorePackage.EPACKAGE__ECLASSIFIERS:
        getEClassifiers().clear();
        return;
      case EcorePackage.EPACKAGE__ESUBPACKAGES:
        getESubpackages().clear();
        return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case EcorePackage.EPACKAGE__EANNOTATIONS:
        return eAnnotations != null && !eAnnotations.isEmpty();
      case EcorePackage.EPACKAGE__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case EcorePackage.EPACKAGE__NS_URI:
        return NS_URI_EDEFAULT == null ? nsURI != null : !NS_URI_EDEFAULT.equals(nsURI);
      case EcorePackage.EPACKAGE__NS_PREFIX:
        return NS_PREFIX_EDEFAULT == null ? nsPrefix != null : !NS_PREFIX_EDEFAULT.equals(nsPrefix);
      case EcorePackage.EPACKAGE__EFACTORY_INSTANCE:
        return eFactoryInstance != null;
      case EcorePackage.EPACKAGE__ECLASSIFIERS:
        return eClassifiers != null && !eClassifiers.isEmpty();
      case EcorePackage.EPACKAGE__ESUBPACKAGES:
        return eSubpackages != null && !eSubpackages.isEmpty();
      case EcorePackage.EPACKAGE__ESUPER_PACKAGE:
        return basicGetESuperPackage() != null;
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
      case EcorePackage.EPACKAGE___GET_EANNOTATION__STRING:
        return getEAnnotation((String)arguments.get(0));
      case EcorePackage.EPACKAGE___GET_ECLASSIFIER__STRING:
        return getEClassifier((String)arguments.get(0));
    }
    return eDynamicInvoke(operationID, arguments);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (nsURI: ");
    result.append(nsURI);
    result.append(", nsPrefix: ");
    result.append(nsPrefix);
    result.append(')');
    return result.toString();
  }

  private static Resource.Factory resourceFactory;

  protected Resource createResource(String uri)
  {
    Resource resource = eResource();
    if (resource == null) 
    {
      if (resourceFactory == null)
      {
        resourceFactory = new Resource.Factory()
        {
          public Resource createResource(URI uri)
          {
            return new BinaryResourceImpl(uri);
          }
        };
      }
      URI actualURI = URI.createURI(uri);
      resource =  resourceFactory.createResource(actualURI);
      resource.getContents().add(this);
    }
    return resource;
  }

  protected EClass createEClass(int id)
  {
    EClassImpl c = (EClassImpl)ecoreFactory.createEClass();
    c.setClassifierID(id);
    getEClassifiers().add(c);
    return c;
  }

  protected EEnum createEEnum(int id)
  {
    EEnumImpl e = (EEnumImpl)ecoreFactory.createEEnum();
    e.setClassifierID(id);
    getEClassifiers().add(e);
    return e;
  }

  protected EDataType createEDataType(int id)
  {
    EDataTypeImpl d = (EDataTypeImpl)ecoreFactory.createEDataType();
    d.setClassifierID(id);
    getEClassifiers().add(d);
    return d;
  }

  protected void createEAttribute(EClass owner, int id)
  {
    EAttributeImpl a = (EAttributeImpl)ecoreFactory.createEAttribute();
    a.setFeatureID(id);
    owner.getEStructuralFeatures().add(a);
  }

  protected void createEReference(EClass owner, int id)
  {
    EReferenceImpl r = (EReferenceImpl)ecoreFactory.createEReference();
    r.setFeatureID(id);
    owner.getEStructuralFeatures().add(r);
  }

  /**
   * @since 2.6
   */
  protected void createEOperation(EClass owner, int id)
  {
    EOperationImpl o = (EOperationImpl)ecoreFactory.createEOperation();
    o.setOperationID(id);
    owner.getEOperations().add(o);
  }

  protected ETypeParameter addETypeParameter(EClassifier owner, String name)
  {
    ETypeParameter eTypeParameter = ecoreFactory.createETypeParameter();
    eTypeParameter.setName(name);
    owner.getETypeParameters().add(eTypeParameter);
    return eTypeParameter;
  }

  protected ETypeParameter addETypeParameter(EOperation owner, String name)
  {
    ETypeParameter eTypeParameter = ecoreFactory.createETypeParameter();
    eTypeParameter.setName(name);
    owner.getETypeParameters().add(eTypeParameter);
    return eTypeParameter;
  }
  
  protected EGenericType createEGenericType()
  {
    EGenericType eGenericType = ecoreFactory.createEGenericType();
    return eGenericType;
  }

  protected EGenericType createEGenericType(ETypeParameter eTypeParameter)
  {
    EGenericType eGenericType = ecoreFactory.createEGenericType();
    eGenericType.setETypeParameter(eTypeParameter);
    return eGenericType;
  }

  protected EGenericType createEGenericType(EClassifier eClassifier)
  {
    EGenericType eGenericType = ecoreFactory.createEGenericType();
    eGenericType.setEClassifier(eClassifier);
    return eGenericType;
  }

  final static protected boolean IS_ABSTRACT = true;
  final static protected boolean IS_INTERFACE = true;
  final static protected boolean IS_GENERATED_INSTANCE_CLASS = true;

  protected EClass initEClass(EClass c, Class<?> instanceClass, String name, boolean isAbstract, boolean isInterface)
  {
    initEClassifier(c, ecorePackage.getEClass(), instanceClass, name);
    c.setAbstract(isAbstract);
    c.setInterface(isInterface);
    return c;
  }

  protected EClass initEClass(EClass c, Class<?> instanceClass, String name, boolean isAbstract, boolean isInterface, boolean isGenerated)
  {
    initEClassifier(c, ecorePackage.getEClass(), instanceClass, name, isGenerated);
    c.setAbstract(isAbstract);
    c.setInterface(isInterface);
    return c;
  }

  protected EClass initEClass
    (EClass c, Class<?> instanceClass, String name, boolean isAbstract, boolean isInterface, boolean isGenerated, String instanceTypeName)
  {
    initEClass(c, instanceClass, name, isAbstract, isInterface, isGenerated);
    if (instanceTypeName != null)
    {
      setInstanceTypeName(c, instanceTypeName);
    }
    return c;
  }

  protected EEnum initEEnum(EEnum e, Class<?> instanceClass, String name)
  {
    initEClassifier(e, ecorePackage.getEEnum(), instanceClass, name, true);
    return e;
  }

  final static protected boolean IS_SERIALIZABLE = true;

  protected EDataType initEDataType(EDataType d, Class<?> instanceClass, String name, boolean isSerializable)
  {
    initEClassifier(d, ecorePackage.getEDataType(), instanceClass, name, false);
    d.setSerializable(isSerializable);
    return d;
  }

  protected EDataType initEDataType(EDataType d, Class<?> instanceClass, String name, boolean isSerializable, boolean isGenerated)
  {
    initEClassifier(d, ecorePackage.getEDataType(), instanceClass, name, isGenerated);
    d.setSerializable(isSerializable);
    return d;
  }

  protected EDataType initEDataType
    (EDataType d, Class<?> instanceClass, String name, boolean isSerializable, boolean isGenerated, String instanceTypeName)
  {
    initEDataType(d, instanceClass, name, isSerializable, isGenerated);
    if (instanceTypeName != null)
    {
      setInstanceTypeName(d, instanceTypeName);
    }
    return d;
  }

  private void initEClassifier(EClassifier o, EClass metaObject, Class<?> instanceClass, String name)
  {
    o.setName(name);
    if (instanceClass != null)
    {
      o.setInstanceClass(instanceClass);
    }
  }

  private void initEClassifier(EClassifier o, EClass metaObject, Class<?> instanceClass, String name, boolean isGenerated)
  {
    o.setName(name);
    if (instanceClass != null)
    {
      o.setInstanceClass(instanceClass);
    }
    if (isGenerated)
    {
      setGeneratedClassName(o);
    }
  }

  protected void setGeneratedClassName(EClassifier eClassifier)
  {
    ((EClassifierImpl)eClassifier).setGeneratedInstanceClass(true);
  }

  protected void setInstanceTypeName(EClassifier eClassifier, String instanceTypeName)
  {
    ((EClassifierImpl)eClassifier).basicSetInstanceTypeName(instanceTypeName);
  }

  protected static final boolean IS_DERIVED = true;
  protected static final boolean IS_TRANSIENT = true;
  protected static final boolean IS_VOLATILE = true;
  protected static final boolean IS_CHANGEABLE = true;
  protected static final boolean IS_UNSETTABLE = true;
  protected static final boolean IS_UNIQUE = true;
  protected static final boolean IS_ID = true;
  protected static final boolean IS_ORDERED = true;

  /**
   * @deprecated
   */
  @Deprecated
  protected EAttribute initEAttribute
    (EAttribute a, 
     EClassifier type,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isUnsettable)
  {
    return 
      initEAttribute
        (a, type, name, defaultValue, lowerBound, upperBound, isTransient, isVolatile, isChangeable, isUnsettable, false, true);
  }

  /**
   * @deprecated
   */
  @Deprecated
  protected EAttribute initEAttribute
    (EAttribute a, 
     EClassifier type,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isUnsettable,
     boolean isID)
  {
    return 
      initEAttribute
        (a, type, name, defaultValue, lowerBound, upperBound, isTransient, isVolatile, isChangeable, isUnsettable, isID, true);
  }

  /**
   * @deprecated
   */
  @Deprecated
  protected EAttribute initEAttribute
    (EAttribute a, 
     EClassifier type,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isUnsettable,
     boolean isID,
     boolean isUnique)
  {
    return 
      initEAttribute
        (a, type, name, defaultValue, lowerBound, upperBound, isTransient, isVolatile, isChangeable, isUnsettable, isID, isUnique, false);
  }

  protected EAttribute initEAttribute
    (EAttribute a, 
     EClassifier type,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isUnsettable,
     boolean isID,
     boolean isUnique,
     boolean isDerived)
  {
    return 
      initEAttribute
        (a, 
         type, 
         name, 
         defaultValue, 
         lowerBound, 
         upperBound, 
         isTransient, 
         isVolatile, 
         isChangeable, 
         isUnsettable, 
         isID, 
         isUnique, 
         isDerived, 
         true);
  }

  protected EAttribute initEAttribute
    (EAttribute a, 
     EClassifier type,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isUnsettable,
     boolean isID,
     boolean isUnique,
     boolean isDerived,
     boolean isOrdered)
  {
    initEAttribute
      (a, 
       type, 
       name, 
       defaultValue, 
       lowerBound, 
       upperBound, 
       ((EClassifier)a.eContainer()).getInstanceClass(),
       isTransient, 
       isVolatile, 
       isChangeable, 
       isUnsettable, 
       isID,
       isUnique, 
       isDerived, 
       isOrdered);
    return a;
  }

  protected EAttribute initEAttribute
    (EAttribute a, 
     EClassifier type,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     Class<?> containerClass,
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isUnsettable,
     boolean isID,
     boolean isUnique,
     boolean isDerived,
     boolean isOrdered)
  {
    initEStructuralFeature
      (a, 
       type, 
       name, 
       defaultValue, 
       lowerBound, 
       upperBound, 
       containerClass,
       isTransient, 
       isVolatile, 
       isChangeable, 
       isUnsettable, 
       isUnique, 
       isDerived, 
       isOrdered);
    a.setID(isID);
    return a;
  }

  protected EAttribute initEAttribute
    (EAttribute a, 
     EGenericType type,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     Class<?> containerClass,
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isUnsettable,
     boolean isID,
     boolean isUnique,
     boolean isDerived,
     boolean isOrdered)
  {
    initEStructuralFeature
      (a, 
       type, 
       name, 
       defaultValue, 
       lowerBound, 
       upperBound, 
       containerClass,
       isTransient, 
       isVolatile, 
       isChangeable, 
       isUnsettable, 
       isUnique, 
       isDerived, 
       isOrdered);
    a.setID(isID);
    return a;
  }

  final static protected boolean IS_COMPOSITE = true;
  final static protected boolean IS_RESOLVE_PROXIES = true;
  final static protected boolean IS_RESOLVABLE = true;

  /**
   * @deprecated
   */
  @Deprecated
  protected EReference initEReference
    (EReference r, 
     EClassifier type, 
     EReference otherEnd,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isContainment, 
     boolean isResolveProxies)
  {
    initEReference
      (r, 
       type, 
       otherEnd, 
       name, 
       defaultValue, 
       lowerBound, 
       upperBound, 
       isTransient, 
       isVolatile, 
       isChangeable, 
       isContainment, 
       isResolveProxies, 
       false, 
       true);
    return r;
  }

  /**
   * @deprecated
   */
  @Deprecated
  protected EReference initEReference
    (EReference r, 
     EClassifier type, 
     EReference otherEnd,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isContainment, 
     boolean isResolveProxies,
     boolean isUnsettable)
  {
    initEReference
      (r, 
       type, 
       otherEnd, 
       name, 
       defaultValue, 
       lowerBound, 
       upperBound, 
       isTransient, 
       isVolatile, 
       isChangeable, 
       isContainment, 
       isResolveProxies, 
       isUnsettable, 
       true);
    return r;
  }

  /**
   * @deprecated
   */
  @Deprecated
  protected EReference initEReference
    (EReference r, 
     EClassifier type, 
     EReference otherEnd,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isContainment, 
     boolean isResolveProxies,
     boolean isUnsettable,
     boolean isUnique)
  {
    initEReference
      (r, 
       type, 
       otherEnd, 
       name, 
       defaultValue, 
       lowerBound, 
       upperBound, 
       isTransient, 
       isVolatile, 
       isChangeable, 
       isContainment, 
       isResolveProxies, 
       isUnsettable, 
       isUnique,
       false);
    return r;
  }

  protected EReference initEReference
    (EReference r, 
     EClassifier type, 
     EReference otherEnd,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isContainment, 
     boolean isResolveProxies,
     boolean isUnsettable,
     boolean isUnique,
     boolean isDerived)
  {
    initEReference
      (r, 
       type, 
       otherEnd, 
       name, 
       defaultValue, 
       lowerBound, 
       upperBound, 
       isTransient, 
       isVolatile, 
       isChangeable, 
       isContainment, 
       isResolveProxies, 
       isUnsettable, 
       isUnique,
       isDerived,
       true);
    return r;
  }

  protected EReference initEReference
    (EReference r, 
     EClassifier type, 
     EReference otherEnd,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isContainment, 
     boolean isResolveProxies,
     boolean isUnsettable,
     boolean isUnique,
     boolean isDerived,
     boolean isOrdered)
  {
    initEReference
      (r, 
       type, 
       otherEnd,
       name, 
       defaultValue, 
       lowerBound, 
       upperBound, 
       ((EClassifier)r.eContainer()).getInstanceClass(),
       isTransient, 
       isVolatile, 
       isChangeable, 
       isContainment,
       isResolveProxies,
       isUnsettable, 
       isUnique, 
       isDerived, 
       isOrdered);
    return r;
  }

  protected EReference initEReference
    (EReference r, 
     EClassifier type, 
     EReference otherEnd,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     Class<?> containerClass,
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isContainment, 
     boolean isResolveProxies,
     boolean isUnsettable,
     boolean isUnique,
     boolean isDerived,
     boolean isOrdered)
  {
    initEStructuralFeature
      (r, 
       type, 
       name, 
       defaultValue, 
       lowerBound, 
       upperBound, 
       containerClass,
       isTransient, 
       isVolatile, 
       isChangeable, 
       isUnsettable, 
       isUnique, 
       isDerived, 
       isOrdered);
    r.setContainment(isContainment);
    if (otherEnd != null)
    {
      r.setEOpposite(otherEnd);
    }
    r.setResolveProxies(isResolveProxies);
    return r;
  }

  protected EReference initEReference
    (EReference r, 
     EGenericType type, 
     EReference otherEnd,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     Class<?> containerClass,
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable, 
     boolean isContainment, 
     boolean isResolveProxies,
     boolean isUnsettable,
     boolean isUnique,
     boolean isDerived,
     boolean isOrdered)
  {
    initEStructuralFeature
      (r, 
       type, 
       name, 
       defaultValue, 
       lowerBound, 
       upperBound, 
       containerClass,
       isTransient, 
       isVolatile, 
       isChangeable, 
       isUnsettable, 
       isUnique, 
       isDerived, 
       isOrdered);
    r.setContainment(isContainment);
    if (otherEnd != null)
    {
      r.setEOpposite(otherEnd);
    }
    r.setResolveProxies(isResolveProxies);
    return r;
  }

  private void initEStructuralFeature
    (EStructuralFeature s, 
     EClassifier type,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     Class<?> containerClass,
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable,
     boolean isUnsettable,
     boolean isUnique,
     boolean isDerived,
     boolean isOrdered)
  {
    s.setName(name);
    ((EStructuralFeatureImpl)s).setContainerClass(containerClass);
    s.setTransient(isTransient);
    s.setVolatile(isVolatile);
    s.setChangeable(isChangeable);
    s.setUnsettable(isUnsettable);
    s.setUnique(isUnique);
    s.setDerived(isDerived);
    s.setOrdered(isOrdered);
    s.setLowerBound(lowerBound);
    s.setUpperBound(upperBound);
    s.setEType(type);
    if (defaultValue != null)
    {
      s.setDefaultValueLiteral(defaultValue);
    }
  }

  private void initEStructuralFeature
    (EStructuralFeature s, 
     EGenericType type,
     String name, 
     String defaultValue,
     int lowerBound, 
     int upperBound, 
     Class<?> containerClass,
     boolean isTransient, 
     boolean isVolatile, 
     boolean isChangeable,
     boolean isUnsettable,
     boolean isUnique,
     boolean isDerived,
     boolean isOrdered)
  {
    s.setName(name);
    ((EStructuralFeatureImpl)s).setContainerClass(containerClass);
    s.setTransient(isTransient);
    s.setVolatile(isVolatile);
    s.setChangeable(isChangeable);
    s.setUnsettable(isUnsettable);
    s.setUnique(isUnique);
    s.setDerived(isDerived);
    s.setOrdered(isOrdered);
    s.setLowerBound(lowerBound);
    s.setUpperBound(upperBound);
    s.setEGenericType(type);
    if (defaultValue != null)
    {
      s.setDefaultValueLiteral(defaultValue);
    }
  }
  
  protected EOperation addEOperation(EClass owner, EClassifier type, String name)
  {
    EOperation o = ecoreFactory.createEOperation();
    initEOperation(o, type, name);
    owner.getEOperations().add(o);
    return o;
  }

  protected EOperation addEOperation(EClass owner, EClassifier type, String name, int lowerBound, int upperBound)
  {
    EOperation o = ecoreFactory.createEOperation();
    initEOperation(o, type, name, lowerBound, upperBound);
    owner.getEOperations().add(o);
    return o;
  }

  protected EOperation addEOperation(EClass owner, EClassifier type, String name, int lowerBound, int upperBound, boolean isUnique, boolean isOrdered)
  {
    EOperation o = ecoreFactory.createEOperation();
    initEOperation(o, type, name, lowerBound, upperBound, isUnique, isOrdered);
    owner.getEOperations().add(o);
    return o;
  }

  /**
   * @since 2.6
   */
  protected EOperation initEOperation(EOperation eOperation, EClassifier type, String name)
  {
    eOperation.setEType(type);
    eOperation.setName(name);
    return eOperation;
  }

  /**
   * @since 2.6
   */
  protected EOperation initEOperation(EOperation eOperation, EClassifier type, String name, int lowerBound, int upperBound)
  {
    initEOperation(eOperation, type, name);
    eOperation.setLowerBound(lowerBound);
    eOperation.setUpperBound(upperBound);
    return eOperation;
  }

  /**
   * @since 2.6
   */
  protected EOperation initEOperation(EOperation eOperation, EClassifier type, String name, int lowerBound, int upperBound, boolean isUnique, boolean isOrdered)
  {
    initEOperation(eOperation, type, name, lowerBound, upperBound);
    eOperation.setUnique(isUnique);
    eOperation.setOrdered(isOrdered);
    return eOperation;
  }

  protected void initEOperation(EOperation eOperation, EGenericType eGenericType)
  {
    eOperation.setEGenericType(eGenericType);
  }

  private EParameter internalAddEParameter(EOperation owner, EClassifier type, String name)
  {
    EParameter p = ecoreFactory.createEParameter();
    p.setEType(type);
    p.setName(name);
    owner.getEParameters().add(p);
    return p;
  }

  protected void addEParameter(EOperation owner, EClassifier type, String name)
  {
    internalAddEParameter(owner, type, name);
  }

  protected void addEParameter(EOperation owner, EClassifier type, String name, int lowerBound, int upperBound)
  {
    EParameter p = internalAddEParameter(owner, type, name);
    p.setLowerBound(lowerBound);
    p.setUpperBound(upperBound);
  }

  protected EParameter addEParameter(EOperation owner, EClassifier type, String name, int lowerBound, int upperBound, boolean isUnique, boolean isOrdered)
  {
    EParameter p = internalAddEParameter(owner, type, name);
    p.setLowerBound(lowerBound);
    p.setUpperBound(upperBound);
    p.setUnique(isUnique);
    p.setOrdered(isOrdered);
    return p;
  }

  @Deprecated
  protected void addEParameter(EOperation owner, EGenericType type, String name, int lowerBound, int upperBound)
  {
    EParameter p = ecoreFactory.createEParameter();
    p.setEGenericType(type);
    p.setName(name);
    owner.getEParameters().add(p);
    p.setLowerBound(lowerBound);
    p.setUpperBound(upperBound);
  }

  protected void addEParameter(EOperation owner, EGenericType type, String name, int lowerBound, int upperBound, boolean isUnique, boolean isOrdered)
  {
    EParameter p = ecoreFactory.createEParameter();
    p.setEGenericType(type);
    p.setName(name);
    owner.getEParameters().add(p);
    p.setLowerBound(lowerBound);
    p.setUpperBound(upperBound);
    p.setUnique(isUnique);
    p.setOrdered(isOrdered);
  }

  protected void addEException(EOperation owner, EClassifier exception)
  {
    owner.getEExceptions().add(exception);
  }

  protected void addEException(EOperation owner, EGenericType exception)
  {
    owner.getEGenericExceptions().add(exception);
  }

  protected void addEEnumLiteral(EEnum owner, Enumerator e)
  {
    EEnumLiteralImpl l = (EEnumLiteralImpl)ecoreFactory.createEEnumLiteral();
    l.setInstance(e);
    l.setGeneratedInstance(true);
    owner.getELiterals().add(l);
  }

  protected void addAnnotation(ENamedElement eNamedElement, String source, String [] details)
  {
    addAnnotation(eNamedElement, source, details, null);
  }

  protected void addAnnotation(ENamedElement eNamedElement, String source, String [] details, URI [] references)
  {
    addAnnotation(eNamedElement, 0, source, details, references);
  }

  protected void addAnnotation(ENamedElement eNamedElement, int depth, String source, String [] details)
  {
    addAnnotation(eNamedElement, depth, source, details, null);
  }

  protected void addAnnotation(ENamedElement eNamedElement, int depth, String source, String [] details, URI [] references)
  {
    EAnnotation eAnnotation = ecoreFactory.createEAnnotation();
    eAnnotation.setSource(source);
    EMap<String, String> theDetails = eAnnotation.getDetails();
    for (int i = 1; i < details.length; i += 2)
    {
      theDetails.put(details[i - 1], details[i]);
    }
    EList<EAnnotation> annotations = eNamedElement.getEAnnotations();
    for (int i = 0; i < depth; ++i)
    {
      @SuppressWarnings("unchecked") EList<EAnnotation> childAnnotations = 
        (EList<EAnnotation>)(EList<?>)annotations.get(annotations.size() - 1).getContents();
      annotations = childAnnotations;
    }
    annotations.add(eAnnotation);
    if (references != null)
    {
      InternalEList<EObject> eAnnotationReferences = (InternalEList<EObject>)eAnnotation.getReferences();
      for (URI reference : references)
      {
        InternalEObject internalEObject = (InternalEObject)ecoreFactory.createEObject();
        internalEObject.eSetProxyURI(reference);
        eAnnotationReferences.addUnique(internalEObject);
      }
    }
  }

  protected void initializeFromLoadedEPackage(EPackage target, EPackage source)
  {
    target.setName(source.getName());
    target.setNsPrefix(source.getNsPrefix());
    target.setNsURI(source.getNsURI());

    target.getEClassifiers().addAll(source.getEClassifiers());
    target.getEAnnotations().addAll(source.getEAnnotations());

    for (EPackage sourceSubpackage : source.getESubpackages())
    {
      EPackage targetSubpackage = EPackage.Registry.INSTANCE.getEPackage(sourceSubpackage.getNsURI());
      initializeFromLoadedEPackage(targetSubpackage, sourceSubpackage);
      target.getESubpackages().add(targetSubpackage);
    }
  }
  
  protected void fixEClassifiers()
  {
    int id = 0;
    
    for (Iterator<EClassifier> i = getEClassifiers().iterator(); i.hasNext(); )
    {
      EClassifierImpl eClassifier = (EClassifierImpl)i.next();
      if (eClassifier instanceof EClass)
      {
        eClassifier.setClassifierID(id++);
        fixInstanceClass(eClassifier);
        fixEStructuralFeatures((EClass)eClassifier);
        fixEOperations((EClass)eClassifier);
      }
    }
    
    for (Iterator<EClassifier> i = getEClassifiers().iterator(); i.hasNext(); )
    {
      EClassifierImpl eClassifier = (EClassifierImpl)i.next();
      if (eClassifier.metaObjectID == -1 && eClassifier instanceof EEnum)
      {
        eClassifier.setClassifierID(id++);
        fixInstanceClass(eClassifier);
        fixEEnumLiterals((EEnum)eClassifier);
      }
    }

    for (Iterator<EClassifier> i = getEClassifiers().iterator(); i.hasNext(); )
    {
      EClassifierImpl eClassifier = (EClassifierImpl)i.next();
      if (eClassifier.metaObjectID == -1 && eClassifier instanceof EDataType)
      {
        eClassifier.setClassifierID(id++);
        if (eClassifier.getInstanceClassName() == "org.eclipse.emf.common.util.AbstractEnumerator")
        {
          EDataType baseType = ExtendedMetaData.INSTANCE.getBaseType((EDataType)eClassifier);
          if (baseType instanceof EEnum)
          {
            eClassifier.setInstanceClass(baseType.getInstanceClass());
            setGeneratedClassName(eClassifier);
          }
        }
      }
    }
  }

  protected void fixInstanceClass(EClassifier eClassifier)
  {
    if (eClassifier.getInstanceClassName() == null)
    {
      String className = getClass().getName();
      int i = className.lastIndexOf('.', className.lastIndexOf('.') - 1);
      className = i == -1 ? eClassifier.getName() : className.substring(0, i + 1) + eClassifier.getName();
      eClassifier.setInstanceClassName(className);
      setGeneratedClassName(eClassifier);
    }
  }

  protected void fixEStructuralFeatures(EClass eClass)
  {
    List<EStructuralFeature> features = eClass.getEStructuralFeatures();
    if (!features.isEmpty())
    {
      // The container class must be null for the open content features of the document root
      // to ensure that they are looked up in the actual eClass() 
      // rather than assumed to be a feature with a feature ID relative to the actual class.
      // Otherwise, it's good to have this optimization.
      //
      Class<?> containerClass = ExtendedMetaData.INSTANCE.getDocumentRoot(this) == eClass ? null : eClass.getInstanceClass();

      int id = eClass.getFeatureID(features.get(0));
      
      for (Iterator<EStructuralFeature> i = features.iterator(); i.hasNext(); )
      {
        EStructuralFeatureImpl eStructuralFeature = (EStructuralFeatureImpl)i.next();
        eStructuralFeature.setFeatureID(id++);
        eStructuralFeature.setContainerClass(containerClass);
      }
    }
  }

  /**
   * @since 2.6
   */
  protected void fixEOperations(EClass eClass)
  {
    List<EOperation> operations = eClass.getEOperations();
    if (!operations.isEmpty())
    {
      int id = eClass.getOperationID(operations.get(0));
      
      for (Iterator<EOperation> i = operations.iterator(); i.hasNext(); )
      {
        EOperationImpl eOperation = (EOperationImpl)i.next();
        eOperation.setOperationID(id++);
      }
    }
  }

  protected void fixEEnumLiterals(EEnum eEnum)
  {
    // TODO
    /*
    Class<?> enumClass = eEnum.getInstanceClass();
    
    try
    {
      Method getter = enumClass.getMethod("get", new Class[] { Integer.TYPE });

      for (EEnumLiteral eEnumLiteral : eEnum.getELiterals())
      {
        Enumerator instance = (Enumerator)getter.invoke(null, new Object[] { eEnumLiteral.getValue() });
        eEnumLiteral.setInstance(instance);
        ((EEnumLiteralImpl)eEnumLiteral).setGeneratedInstance(true);
      }
    }
    catch (Exception e)
    {
      // Do nothing
    }
    */
  }

  @GwtTransient
  protected BasicExtendedMetaData.EPackageExtendedMetaData ePackageExtendedMetaData;

  public BasicExtendedMetaData.EPackageExtendedMetaData getExtendedMetaData()
  {
    return ePackageExtendedMetaData;
  }

  public void setExtendedMetaData(BasicExtendedMetaData.EPackageExtendedMetaData ePackageExtendedMetaData)
  {
    this.ePackageExtendedMetaData = ePackageExtendedMetaData;
  }

  @Override
  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    EObject result = getEClassifierGen(uriFragmentSegment);
    return result != null ? result : super.eObjectForURIFragmentSegment(uriFragmentSegment);
  }

  /**
   * This interface is provided to support single sourcing GWT runtime and regular runtime applications.
   * Generated WhiteList classes in generated packages will implement this and this extends the {@link URIService.WhiteList white list used in URI service}.
   * This avoids having generated classes depend on classes not available in the regular runtime.
   * @since 2.7
   */
  protected static interface EBasicWhiteList extends URIService.WhiteList
  {
    // This is a dummy placeholder class.
  }
}
