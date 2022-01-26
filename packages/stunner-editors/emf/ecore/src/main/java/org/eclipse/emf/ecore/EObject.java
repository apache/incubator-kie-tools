/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *   Christian Damus (Zeligsoft) - 255469
 */
package org.eclipse.emf.ecore;


import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.resource.Resource;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EObject</b></em>'.
 * <p>
 * <code>EObject</code> is the root of all modeled objects
 * so all the method names start with "e" to distinguish 
 * the EMF methods from the client methods.
 * It provides support for the behaviors and features 
 * common to all modeled objects:
 * <ul>
 *   <li><b>Content</b></li> 
 *   <ul>
 *     <li>{@link #eResource()}</li> 
 *     <li>{@link #eContainer}</li> 
 *     <li>{@link #eContainingFeature}</li> 
 *     <li>{@link #eContainmentFeature}</li> 
 *     <li>{@link #eContents}</li> 
 *     <li>{@link #eAllContents()}</li> 
 *     <li>{@link #eCrossReferences}</li>
 *   </ul>
 *   <li><b><a name="reflection">Reflection</a></b></li> 
 *   <ul>
 *     <li>{@link #eClass}
 *     <li>{@link #eGet(EStructuralFeature)}</li> 
 *     <li>{@link #eGet(EStructuralFeature, boolean)}</li> 
 *     <li>{@link #eSet(EStructuralFeature, Object)}</li> 
 *     <li>{@link #eIsSet(EStructuralFeature)}</li>
 *     <li>{@link #eUnset(EStructuralFeature)}</li> 
 *   </ul>
 *   <li><b>Serialization</b></li>
 *   <ul>
 *     <li>{@link #eIsProxy}
 *   </ul>
 * </ul>
 * It is a full participant of the common notification framework: {@link org.eclipse.emf.common.notify}.
 * Any <code>EObject</code> is assumed to also implement {@link InternalEObject}, 
 * which provides lower-level access that is not necessarily suitable for general consumption
 * but is required for maintaining the EMF support mechanisms.
 * Implementations of EObject should extend {@link org.eclipse.emf.ecore.impl.BasicEObjectImpl BasicEObjectImpl} 
 * or one of its derived classes
 * because methods can and will be added to this API.
 * The framework also assumes that implementations will not specialize {@link #equals(Object)} (nor {@link #hashCode()})
 * so that "<code>==</code>" can be always used for equality testing;
 * {@link org.eclipse.emf.ecore.util.EcoreUtil#equals(EObject, EObject) EcoreUtil.equals} should be used for doing structural equality testing.
 * </p>
 * 
 * @extends Notifier
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.emf.ecore.EcorePackage#getEObject()
 * @model
 * @generated
 */
public interface EObject extends Notifier
{
  /**
   * <!-- begin-user-doc -->
   * Returns the meta class.
   * <p>
   * The meta class defines the {@link EClass#getEAllStructuralFeatures features} 
   * available for <a href="#reflection">reflective</a> access.
   * </p>
   * @return the meta class.
   * @see EClass#getEAllStructuralFeatures
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EClass eClass();

  /**
   * <!-- begin-user-doc -->
   * Returns the containing resource, or <code>null</code>.
   * <p>
   * An object is contained in a resource
   * if it, or one of it's {@link #eContainer containers},
   * appears in the {@link Resource#getContents contents} of that resource.
   * <p>
   * An object must be contained by a resource in order to be {@link Resource#save(java.util.Map) serialized}.
   * </p>
   * @return the containing resource.
   * @see org.eclipse.emf.ecore.util.EcoreUtil#remove(EObject)
   * @see #eContainer
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  Resource eResource();

  /**
   * <!-- begin-user-doc -->
   * Returns the containing object, or <code>null</code>.
   * <p>
   * An object is contained by another object 
   * if it appears in the {@link #eContents contents} of that object.
   * The object will be contained by a {@link #eContainmentFeature containment feature} of the containing object.
   * </p>
   * @return the containing object.
   * @see #eResource
   * @see org.eclipse.emf.ecore.util.EcoreUtil#remove(EObject)
   * @see #eContainmentFeature
   * @see #eContainingFeature
   * @see org.eclipse.emf.ecore.util.EcoreUtil#getRootContainer(EObject)
   * @see #eContents
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EObject eContainer();

  /**
   * <!-- begin-user-doc -->
   * Returns the particular {@link EClass#getEAllContainments feature}
   * of the {@link #eContainer container} that actually holds the object,
   * or <code>null</code>, if there is no container.
   * Because of support for wildcard content,
   * this feature may be an attribute representing a {@link org.eclipse.emf.ecore.util.FeatureMap feature map};
   * in this case the object is referenced by the {@link org.eclipse.emf.ecore.EReference#isContainment containment} feature
   * of an {@link org.eclipse.emf.ecore.util.FeatureMap.Entry#getEStructuralFeature entry} in the map,
   * i.e., the {@link #eContainmentFeature eContainmentFeature}.
   * <p>
   * @return the feature that actually contains the object.
   * @see #eContainer
   * @see #eContainmentFeature
   * @see EClass#getEAllContainments
   * @see EReference#isContainment
   * @see org.eclipse.emf.ecore.util.FeatureMap.Entry#getEStructuralFeature
   * @ignore
   * <!-- end-user-doc -->
   * @model
   */
  EStructuralFeature eContainingFeature();

  /**
   * <!-- begin-user-doc -->
   * Returns the {@link org.eclipse.emf.ecore.EReference#isContainment containment} feature that properly contains the object,
   * or <code>null</code>, if there is no container.
   * Because of support for wildcard content, 
   * this feature may not be a direct feature of the container's {@link #eClass class},
   * but rather a {@link org.eclipse.emf.ecore.util.FeatureMap.Entry#getEStructuralFeature feature} 
   * of an entry in a feature map {@link #eContainingFeature feature} of the container's class.
   * <p>
   * @return the feature that properly contains the object.
   * @see #eContainer
   * @see #eContainingFeature
   * @see EReference#isContainment
   * @see EClass#getEAllContainments
   * @see org.eclipse.emf.ecore.util.FeatureMap.Entry#getEStructuralFeature
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EReference eContainmentFeature();

  /**
   * <!-- begin-user-doc -->
   * Returns a list view of the content objects; it is unmodifiable.
   * <p>
   * This will be the list of {@link EObject}s determined by the
   * {@link #eGet(EStructuralFeature) contents} 
   * of the {@link EClass#getEAllContainments containment features} 
   * of this object's {@link #eClass meta class}.
   * Objects can, {@link org.eclipse.emf.ecore.util.EcoreUtil#remove(EObject) indirectly}, be removed 
   * and will change to reflect {@link #eContainer container} changes.
   * The implicit {@link #eAllContents tree} of contents is also directly available.
   * </p>
   * @return a list view of the content objects.
   * @see #eContainer
   * @see EClass#getEAllContainments
   * @see #eGet(EStructuralFeature) 
   * @see org.eclipse.emf.ecore.util.EcoreUtil#remove(EObject)
   * @see #eAllContents
   * @ignore
   * <!-- end-user-doc -->
   * @model many="false"
   * @generated
   */
  EList<EObject> eContents();

  /**
   * <!-- begin-user-doc -->
   * Returns a tree iterator that iterates over all the {@link #eContents direct contents} and indirect contents of this object.
   * @return a tree iterator that iterates over all contents.
   * @see Resource#getAllContents
   * @see org.eclipse.emf.ecore.util.EcoreUtil#getAllContents(EObject, boolean)
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  TreeIterator<EObject> eAllContents();

  /**
   * <!-- begin-user-doc -->
   * Indicates whether this object is a proxy.
   * <p> 
   * A proxy is an object that is defined in a <code>Resource</code> that has not been loaded.
   * An object may be a proxy either because proxy resolution was disabled 
   * when the object was accessed (see {@link #eGet(EStructuralFeature,boolean)})
   * or because proxy {@link org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject, EObject) resolution} failed.
   * </p>
   * @return <code>true</code> if this object is a proxy or <code>false</code>, otherwise.
   * @see Resource#unload
   * @see org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject, org.eclipse.emf.ecore.resource.ResourceSet)
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean eIsProxy();

  /**
   * <!-- begin-user-doc -->
   * Returns a list view of the cross referenced objects; it is unmodifiable.
   * <p>
   * This will be the list of {@link EObject}s determined by the
   * {@link #eGet(EStructuralFeature) contents} 
   * of the {@link EClass#getEAllReferences reference features} 
   * of this object's {@link #eClass meta class},
   * <em>excluding</em> {@link EClass#getEAllContainments containment features} 
   * and their {@link EReference#getEOpposite opposites}.
   * The cross reference list's iterator will be of type {@link org.eclipse.emf.ecore.util.EContentsEList.FeatureIterator},
   * for efficient determination of the {@link org.eclipse.emf.ecore.util.EContentsEList.FeatureIterator#feature feature} 
   * of each cross reference in the list, e.g.,
   *<pre>
   *  for (EContentsEList.FeatureIterator featureIterator = 
   *        (EContentsEList.FeatureIterator)eObject.eCrossReferences().iterator();
   *       featureIterator.hasNext(); )
   *  {
   *    EObject eObject = (EObject)featureIterator.next();
   *    EReference eReference = (EReference)featureIterator.feature();
   *    ...
   *  }
   *</pre>
   * </p>
   * @see #eContents
   * @see EClass#getEAllReferences
   * @see EClass#getEAllContainments
   * @see #eGet(EStructuralFeature) 
   * @see org.eclipse.emf.ecore.util.EcoreUtil.CrossReferencer
   * @return a list view of the cross referenced objects.
   * <!-- end-user-doc -->
   * @model many="false"
   * @generated
   */
  EList<EObject> eCrossReferences();

  /**
   * <!-- begin-user-doc -->
   * Returns the value of the given feature of this object.
   * <p>
   * It returns the resolved value, 
   * i.e., it returns
   *<pre>
   *  eObject.{@link #eGet(EStructuralFeature, boolean) eGet}(feature, true)
   *</pre>
   * @param feature the feature of the value to fetch.
   * @return the value of the given feature of this object.
   * @see #eGet(EStructuralFeature, boolean)
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  Object eGet(EStructuralFeature feature);

  /**
   * <!-- begin-user-doc -->
   * Returns the value of the given feature of the object; 
   * the value is optionally {@link org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject, EObject) resolved} before it is returned.
   * <p>
   * If the feature is {@link ETypedElement#isMany() many-valued},
   * the result will be an {@link EList}
   * and each object in the list will be {@link EClassifier#isInstance an instance of} 
   * the feature's {@link ETypedElement#getEType() type};
   * the list's contents are <b>not</b> affected by <code>resolve</code> argument.
   * Otherwise the result directly will be an instance of the feature's type;
   * if it is a {@link #eIsProxy proxy},
   * it is resolved.
   * @param feature the feature of the value to fetch.
   * @param resolve whether to resolve.
   * @return the value of the given feature of the object.
   * @exception IllegalArgumentException 
   * if the feature is not one the {@link #eClass meta class}'s 
   * {@link EClass#getEAllStructuralFeatures features}.
   * @see org.eclipse.emf.ecore.util.InternalEList#basicIterator
   * @see org.eclipse.emf.ecore.util.InternalEList#basicList
   * @see org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject, org.eclipse.emf.ecore.resource.ResourceSet) 
   *  EcoreUtil.resolve(EObject, org.eclipse.emf.ecore.resource.ResourceSet)
   * @see #eSet(EStructuralFeature, Object)
   * @see #eUnset(EStructuralFeature)
   * @see #eIsSet(EStructuralFeature)
   * @see #eGet(EStructuralFeature)
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  Object eGet(EStructuralFeature feature, boolean resolve);

  /**
   * <!-- begin-user-doc -->
   * Sets the value of the given feature of the object to the new value.
   * <p>
   * If the feature is {@link ETypedElement#isMany() many-valued},
   * the new value must be an {@link EList}
   * and each object in that list must be {@link EClassifier#isInstance an instance of} 
   * the feature's {@link ETypedElement#getEType() type};
   * the existing contents are cleared and the contents of the new value are added.
   * However, if the new value is the content list itself, or is modified as a side effect of modifying the content list 
   * (i.e., if it is a view on the content list),
   * the behavior is undefined and will likely result in simply clearing the list.
   * If the feature is single-valued, the new value directly must be an instance of the feature's type
   * and it becomes the new value of the feature of the object.
   * If the feature is {@link EStructuralFeature#isUnsettable() unsettable},
   * the modeled state becomes set;
   * otherwise, the feature may still not considered {@link #eIsSet set} if the new value is the same as the default.
   * @param feature the feature of the value to set.
   * @exception IllegalArgumentException 
   * if the feature is not one the {@link #eClass meta class}'s 
   * {@link EClass#getEAllStructuralFeatures features},
   * or it isn't {@link EStructuralFeature#isChangeable changeable}.
   * @exception ClassCastException if there is a type conflict.
   * @exception ArrayStoreException  if there is a type conflict.
   * @see #eUnset(EStructuralFeature)
   * @see #eIsSet(EStructuralFeature)
   * @see #eGet(EStructuralFeature, boolean)
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  void eSet(EStructuralFeature feature, Object newValue);

  /**
   * <!-- begin-user-doc -->
   * Returns whether the feature of the object is considered to be set.
   * <p>
   * If the feature is {@link ETypedElement#isMany() many-valued},
   * the value must be an {@link EList}
   * and the feature is considered set if the list is not empty.
   * If the feature is {@link EStructuralFeature#isUnsettable unsettable},
   * the modeled state is directly available and is used.
   * Otherwise, 
   * the {@link #eGet(EStructuralFeature, boolean) unresolved value} of the feature of the object 
   * is compared against the feature's {@link EStructuralFeature#getDefaultValue() default value}
   * or the {@link #eClass meta class}'s {@link EStructuralFeature#getDefaultValue() default value},
   * as appropriate;
   * the feature is considered set if it's not the same as the default.
   * </p>
   * <p>
   * This property can affect serialization, since defaults are typically omitted in a compact serialization.
   * </p>
   * @param feature the feature in question.
   * @exception IllegalArgumentException 
   * if the feature is not one the {@link #eClass meta class}'s 
   * {@link EClass#getEAllStructuralFeatures features}.
   * @return whether the feature of the object is set.
   * @see #eSet(EStructuralFeature, Object)
   * @see #eUnset(EStructuralFeature)
   * @see #eGet(EStructuralFeature, boolean)
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean eIsSet(EStructuralFeature feature);

  /**
   * <!-- begin-user-doc -->
   * Unsets the feature of the object.
   * <p>
   * If the feature is {@link ETypedElement#isMany() many-valued},
   * the value must be an {@link EList}
   * and that list is cleared.
   * Otherwise, 
   * the value of the feature of the object 
   * is set to the feature's {@link EStructuralFeature#getDefaultValue() default value}
   * or the {@link #eClass meta class}'s {@link EStructuralFeature#getDefaultValue() default value},
   * as appropriate.
   * If the feature is {@link EStructuralFeature#isUnsettable() unsettable},
   * the modeled state becomes unset.
   * In any case, the feature will no longer be considered {@link #eIsSet set}.
   * </p>
   * @param feature the feature in question.
   * @exception IllegalArgumentException 
   * if the feature is not one the {@link #eClass meta class}'s 
   * {@link EClass#getEAllStructuralFeatures features},
   * or it isn't {@link EStructuralFeature#isChangeable changeable}.
   * @see #eIsSet(EStructuralFeature)
   * @see #eSet(EStructuralFeature, Object)
   * @see #eGet(EStructuralFeature, boolean)
   * @ignore
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  void eUnset(EStructuralFeature feature);

  /**
   * <!-- begin-user-doc -->
   * <p>
   * Invokes the specified operation of the object.  If the operation has
   * parameters, then corresponding arguments must be supplied.  There are no
   * optional parameters in Ecore operations.
   * </p><p>
   * If the operation is a void operation, then on successful execution, the
   * result of this invocation is <code>null</code>.  Otherwise, if the
   * operation is {@link ETypedElement#isMany() multi-valued}, then an
   * {@link EList} is returned (possibly empty).  If single-valued, then an
   * instance of the operation's {@link ETypedElement#getEType() type} is
   * returned, or possibly <code>null</code>.
   * </p><p>
   * If the invoked operation fails with an
   * {@link EOperation#getEExceptions() exception}, then it is re-thrown,
   * wrapped in an {@link InvocationTargetException}.
   * </p>
   * @since 2.6
   * <!-- end-user-doc -->
   * @model exceptions="org.eclipse.emf.ecore.EInvocationTargetException" argumentsMany="false"
   * @generated
   */
  Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException;

}
