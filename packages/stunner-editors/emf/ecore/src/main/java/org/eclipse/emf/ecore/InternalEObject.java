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
package org.eclipse.emf.ecore;


import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.InvocationTargetException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;


/**
 * An internal interface implemented by all {@link EObject}s.
 */
public interface InternalEObject extends EObject
{
  /**
   * The base value for negative, i.e., opposite-end, eContainerFeatureID values.
   */
  static final int EOPPOSITE_FEATURE_BASE = -1;

  /**
   * Returns whether {@link org.eclipse.emf.common.notify.Notifier#eNotify eNotify} needs to be called.
   * <p>
   * This may return <code>true</code> even when {@link org.eclipse.emf.common.notify.Notifier#eDeliver eDeliver} is <code>false</code>
   * or when {@link org.eclipse.emf.common.notify.Notifier#eAdapters eAdapters} is empty.
   * <p>
   * @return <code>true</code> if eNotify needs to be called.
   */
  boolean eNotificationRequired();

  /**
   * Returns the fragment segment that, 
   * when passed to {@link #eObjectForURIFragmentSegment eObjectForURIFragmentSegment},
   * will resolve to the given object in this object's given feature.
   * <p>
   * The feature argument may be <code>null</code> in which case it will be deduced, if possible.
   * The default result will be of the form:
   *<pre>
   *  "@&lt;feature-name>[.&lt;index>]"
   *</pre>
   * The index is used only for {@link ETypedElement#isMany() many-valued} features;
   * it represents the position within the list.
   * </p>
   * @param eFeature the feature relating the given object to this object, or <code>null</code>.
   * @param eObject the object to be identified.
   * @return the fragment segment that resolves to the given object in this object's given feature.
   */
  String eURIFragmentSegment(EStructuralFeature eFeature, EObject eObject);

  /**
   * Returns the object resolved by the fragment segment.
   * <p>
   * The fragment segment encoding will typically be of the form returned by {@link #eURIFragmentSegment eURIFragmentSegment}.
   * @param uriFragmentSegment a fragment segment.
   * @return the fragment segment that resolves to the given object in this object's given feature.
   */
  EObject eObjectForURIFragmentSegment(String uriFragmentSegment);

  /**
   * Sets the meta class.
   * An object with a set meta class is, by definition, a dynamic instance;
   * it will support dynamic {@link #eSetting settings}.
   * an.
   * @param eClass the dynamic meta class.
   * @see #eSetting(EStructuralFeature)
   */
  void eSetClass(EClass eClass);

  /**
   * Returns a setting that can be used to access this object's feature.
   * @param feature the feature of the desired setting.
   * @return a setting that can be used to access this object's feature.
   */
  EStructuralFeature.Setting eSetting(EStructuralFeature feature);

  /**
   * Returns the feature ID relative to the base class, given a feature ID relative to this derived object's actual class.
   * @param derivedFeatureID the ID in the actual derived class
   * @param baseClass the base class for which a relative ID is desired.
   * @return the up-cast feature ID.
   */
  int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass);

  /**
   * Returns the container feature ID.
   * If the container isn't a navigable feature, this will be a negative ID indicating the inverse of the containment feature's ID.
   * @return the container feature ID.
   * @see #eContainmentFeature()
   * @see #EOPPOSITE_FEATURE_BASE
   */
  int eContainerFeatureID();

  /**
   * Returns the feature ID relative to this derived object's actual class, given a feature ID relative to the given base class.
   * @param baseFeatureID the ID relative to a base class.
   * @param baseClass the base class to which the ID is relative.
   * @return the down-cast feature ID.
   */
  int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass);

  /**
   * Returns the operation ID relative to this derived object's actual class, given an operation ID relative to the given base class.
   * @param baseOperationID the ID relative to a base class.
   * @param baseClass the base class to which the ID is relative.
   * @return the down-cast operation ID.
   * @since 2.6
   */
  int eDerivedOperationID(int baseOperationID, Class<?> baseClass);

  /**
   * Sets this object to be directly contained by the resource 
   * and returns accumulated notifications.
   * This is only done as the inverse of {@link Resource#getContents()}<code>.add(this)</code>.
   * @return accumulated notifications.
   */
  NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications);

  /**
   * Adds the object at the other end of a bidirectional reference to the appropriate feature
   * and returns accumulated notifications.
   * @return accumulated notifications.
   */
  NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class<?> baseClass, NotificationChain notifications);

  /**
   * Removes the object at the other end of a bidirectional reference from the appropriate feature
   * and returns accumulated notifications.
   * @return accumulated notifications.
   */
  NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class<?> baseClass, NotificationChain notifications);

  /**
   * Sets the container to be new container and appropriate feature.
   * and returns accumulated notifications.
   * Since the container feature may not be navigable, i.e., may not exist, the containment feature may be encoded instead.
   * @return accumulated notifications.
   */
  NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID, NotificationChain notifications);

  /**
   * Removes this object from whatever container holds it,
   * and returns accumulated notifications.
   * @return accumulated notifications.
   */
  NotificationChain eBasicRemoveFromContainer(NotificationChain notifications);

  /**
   * Returns the proxy URI of this object.
   * It can be used to {@link org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject, EObject) resolve} to the actual object.
   * @return the proxy URI of this object.
   * @see org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject, org.eclipse.emf.ecore.resource.ResourceSet)
   * @see Resource#unload
   */
  URI eProxyURI();

  /**
   * Set the proxy URI of this object.
   * It will be used to {@link org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject, EObject) resolve} to the actual object.
   * @param uri the URI.
   * @see org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject, org.eclipse.emf.ecore.resource.ResourceSet)
   * @see Resource#unload
   */
  void eSetProxyURI(URI uri);

  /**
   * Resolves the {@link EObject#eIsProxy proxy} object relative to this object.
   * @see org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject,EObject)
   * @return the resolved object, or the original if the proxy can't be resolved.
   */
  EObject eResolveProxy(InternalEObject proxy);

  /**
   * Returns the internal container, or <code>null</code>.
   * It does not resolve a proxy.
   * @return the internal container.
   * @see EObject#eContainer
   * @see InternalEObject#eDirectResource
   */
  InternalEObject eInternalContainer();

  /**
   * Returns the containing internal resource, or <code>null</code>.
   * @return the containing internal resource.
   * @see EObject#eResource
   */
  Resource.Internal eInternalResource();

  /**
   * Returns the directly containing internal resource, or <code>null</code>.
   * @return the directly containing internal resource.
   * @see EObject#eResource
   * @see InternalEObject#eInternalResource
   * @see InternalEObject#eInternalContainer
   */
  Resource.Internal eDirectResource();

  /**
   * Returns the store associated with this object.
   * @return the store associated with this object.
   */
  EStore eStore();

  /**
   * Set the store associated with this object.
   * Most objects will <b>not</b> support this.
   * @param store the store to associate with this object.
   * @throws UnsupportedOperationException
   */
  void eSetStore(EStore store);

  /**
   * An external backing store to which an {@link EObject object} may delegate all access.
   */
  public interface EStore
  {
    /**
     * A value indicating that no index is specified.
     * It is used in the case of accessing {@link ETypedElement#isMany() single-valued} features 
     * where an index would be meaningless.
     */
    int NO_INDEX = -1;

    /**
     * Returns the value at the index in the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature.
     * @param object the object in question.
     * @param feature a feature of the object.
     * @param index an index within the content or {@link #NO_INDEX}.
     * @return the value at the index in the content of the object's feature.
     */
    Object get(InternalEObject object, EStructuralFeature feature, int index);

    /**
     * Sets the value at the index in the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature.
     * @param object the object in question.
     * @param feature a feature of the object.
     * @param index an index within the content or {@link #NO_INDEX}.
     * @param value the new value.
     * @return the previous value.
     */
    Object set(InternalEObject object, EStructuralFeature feature, int index, Object value);

    /**
     * Returns whether the object's feature is considered set.
     * @param object the object in question.
     * @param feature a feature of the object.
     * @return <code>true</code> if the object's feature is considered set.
     */
    boolean isSet(InternalEObject object, EStructuralFeature feature);

    /**
     * Unsets the feature of the object.
     * @param object the object in question.
     * @param feature a feature of the object.
     */
    void unset(InternalEObject object, EStructuralFeature feature);

    /**
     * Returns whether the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature is empty.
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     * @return <code>true</code> if the content of the object's feature is empty.
     */
    boolean isEmpty(InternalEObject object, EStructuralFeature feature);

    /**
     * Returns the number of values in the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature.
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     * @return the number of values in the content of the object's feature.
     */
    int size(InternalEObject object, EStructuralFeature feature);

    /**
     * Returns whether the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature contains the given value.
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     * @param value the value in question.
     * @return <code>true</code> if the content of the object's feature contains the given value.
     */
    boolean contains(InternalEObject object, EStructuralFeature feature, Object value);
    
    /**
     * Returns the first index of the given value in the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature.
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     * @param value the value in question.
     * @return the first index of the given value in the content of the object's feature.
     */
    int indexOf(InternalEObject object, EStructuralFeature feature, Object value);

    /**
     * Returns the last index of the given value in the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature.
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     * @param value the value in question.
     * @return the last index of the given value in the content of the object's feature.
     */
    int lastIndexOf(InternalEObject object, EStructuralFeature feature, Object value);

    /**
     * Adds the value at the index in the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature.
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     * @param index an index within the content.
     * @param value the value to add.
     */
    void add(InternalEObject object, EStructuralFeature feature, int index, Object value);

    /**
     * Removes the value at the index in the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature.
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     * @param index the index within the feature's content of the value to remove.
     * @return the removed value.
     */
    Object remove(InternalEObject object, EStructuralFeature feature, int index);

    /**
     * Moves the value at the source index in the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature
     * to the target index.
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     * @param targetIndex the index within the feature's content to which to move the value.
     * @param sourceIndex the index within the feature's content of the value to move.
     * @return the moved value.
     */
    Object move(InternalEObject object, EStructuralFeature feature, int targetIndex, int sourceIndex);

    /**
     * Removes all values form the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     */
    void clear(InternalEObject object, EStructuralFeature feature);
      
    /**
     * Returns a new array of the values in the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature.
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     * @return a new array of the values in the content of the object's feature.
     */
    Object[] toArray(InternalEObject object, EStructuralFeature feature);

    /**
     * Returns an array of the values in the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature.
     * The given array will be used, unless it's too small, in which case a new array of the same type is allocated instead.
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     * @param array the array to fill.
     * @return an array of the values in the content of the object's feature.
     */
    <T> T[] toArray(InternalEObject object, EStructuralFeature feature, T[] array);

    /**
     * Returns the hash code of the {@link EObject#eGet(EStructuralFeature,boolean) content} of the object's feature.
     * @param object the object in question.
     * @param feature a {@link ETypedElement#isMany() many-valued} feature of the object.
     * @return the hash code of the content of the object's feature.
     */ 
    int hashCode(InternalEObject object, EStructuralFeature feature);
   
    /**
     * Returns the object's {@link EObject#eContainer container}.
     * @return the object's container.
     * @see EObject#eContainer
     */
    InternalEObject getContainer(InternalEObject object);  

    /**
     * Returns the object's {@link EObject#eContainingFeature containing feature}.
     * @return the object's containing feature.
     * @see EObject#eContainingFeature
     */
    EStructuralFeature getContainingFeature(InternalEObject object);  

    /**
     * Creates a new instance of the class.
     * @param eClass the class to instantiate.
     * @return a new instance of the class.
     */
    EObject create(EClass eClass);

  }

  /**
   * Returns the value of the given feature of the object; 
   * the value is optionally {@link org.eclipse.emf.ecore.util.EcoreUtil#resolve(EObject, EObject) resolved} before it is returned.
   * <p>
   * If the feature is {@link ETypedElement#isMany() many-valued},
   * the result will be an {@link EList}
   * and each object in the list will be {@link EClassifier#isInstance an instance of} 
   * the feature's {@link ETypedElement#getEType() type};
   * the list's contents are <b>not</b> affected by <code>resolve</code> argument.
   * Otherwise the result directly will be an instance of the feature's type;
   * if it is a {@link EObject#eIsProxy() proxy},
   * it is resolved.
   * The core type specifies whether to return the core reflective value,  
   * e.g., {@link org.eclipse.emf.common.util.EMap},
   * or the public API value,
   * e.g., {@link java.util.Map}.
   * @param eFeature the feature of the value to fetch.
   * @param resolve whether to resolve.
   * @param coreType whether to return the core type value or the API type value.
   * @return the value of the given feature of the object.
   * @see EObject#eGet(EStructuralFeature, boolean)
   */
  public Object eGet(EStructuralFeature eFeature, boolean resolve, boolean coreType);

  /**
   * Does the equivalent of <code>eObject.eGet(eObject.eClass().getEStructuralFeature(featureID), resolve, coreType)</code>.
   * @see #eGet(EStructuralFeature, boolean, boolean)
   */
  Object eGet(int featureID, boolean resolve, boolean coreType);

  /**
   * Does the equivalent of <code>eObject.eSet(eObject.eClass().getEStructuralFeature(featureID), newValue)</code>.
   * @see #eSet(EStructuralFeature, Object)
   */
  void eSet(int featureID, Object newValue);

  /**
   * Does the equivalent of <code>eObject.eUnset(eObject.eClass().getEStructuralFeature(featureID))</code>.
   * @see #eUnset(EStructuralFeature)
   */
  void eUnset(int featureID);

  /**
   * Does the equivalent of <code>eObject.eIsSet(eObject.eClass().getEStructuralFeature(featureID))</code>.
   * @see #eIsSet(EStructuralFeature)
   */
  boolean eIsSet(int featureID);

  /**
   * Does the equivalent of <code>eObject.eInvoke(eObject.eClass().getEOperation(featureID), arguments)</code>.
   * @see #eInvoke(EOperation, EList<?>)
   * @since 2.6
   */
  Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException;
}
