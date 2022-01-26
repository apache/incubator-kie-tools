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


import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.ecore.xml.type.ProcessingInstruction;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;


public final class FeatureMapUtil
{
  protected static final Class<Validator> VALIDATOR_CLASS = Validator.class;

  private FeatureMapUtil()
  {
    super();
  }

  public static void addText(FeatureMap featureMap, String text)
  {
    featureMap.add(XMLTypeFeatures.TEXT, text);
  }

  public static void addText(FeatureMap featureMap, int index, String text)
  {
    featureMap.add(index, XMLTypeFeatures.TEXT, text);
  }

  public static boolean isText(FeatureMap.Entry entry)
  {
    return entry.getEStructuralFeature() == XMLTypeFeatures.TEXT;
  }

  public static boolean isText(EStructuralFeature eStructuralFeature)
  {
    return eStructuralFeature == XMLTypeFeatures.TEXT;
  }

  public static void addCDATA(FeatureMap featureMap, String cdata)
  {
    featureMap.add(XMLTypeFeatures.CDATA, cdata);
  }

  public static void addCDATA(FeatureMap featureMap, int index, String cdata)
  {
    featureMap.add(index, XMLTypeFeatures.CDATA, cdata);
  }

  public static boolean isCDATA(FeatureMap.Entry entry)
  {
    return entry.getEStructuralFeature() == XMLTypeFeatures.CDATA;
  }

  public static boolean isCDATA(EStructuralFeature eStructuralFeature)
  {
    return eStructuralFeature == XMLTypeFeatures.CDATA;
  }

  public static void addComment(FeatureMap featureMap, String comment)
  {
    featureMap.add(XMLTypeFeatures.COMMENT, comment);
  }

  public static void addComment(FeatureMap featureMap, int index, String comment)
  {
    featureMap.add(index, XMLTypeFeatures.COMMENT, comment);
  }

  public static boolean isComment(FeatureMap.Entry entry)
  {
    return entry.getEStructuralFeature() == XMLTypeFeatures.COMMENT;
  }

  public static boolean isComment(EStructuralFeature eStructuralFeature)
  {
    return eStructuralFeature == XMLTypeFeatures.COMMENT;
  }

  public static void addProcessingInstruction(FeatureMap featureMap, String target, String data)
  {
    ProcessingInstruction processingInstruction = XMLTypeFactory.eINSTANCE.createProcessingInstruction();
    processingInstruction.setTarget(target);
    processingInstruction.setData(data);
    featureMap.add(XMLTypeFeatures.PROCESSING_INSTRUCTION, processingInstruction);
  }

  public static void addProcessingInstruction(FeatureMap featureMap, int index, String target, String data)
  {
    ProcessingInstruction processingInstruction = XMLTypeFactory.eINSTANCE.createProcessingInstruction();
    processingInstruction.setTarget(target);
    processingInstruction.setData(data);
    featureMap.add(index, XMLTypeFeatures.PROCESSING_INSTRUCTION, processingInstruction);
  }

  public static boolean isProcessingInstruction(FeatureMap.Entry entry)
  {
    return entry.getEStructuralFeature() == XMLTypeFeatures.PROCESSING_INSTRUCTION;
  }

  public static boolean isProcessingInstruction(EStructuralFeature eStructuralFeature)
  {
    return eStructuralFeature == XMLTypeFeatures.PROCESSING_INSTRUCTION;
  }

  public static boolean isFeatureMap(EStructuralFeature eStructuralFeature)
  {
    return ((EStructuralFeature.Internal)eStructuralFeature).isFeatureMap();
  }

  public static boolean isFeatureMapEntry(EClassifier eClassifier)
  {
    return eClassifier.getInstanceClassName() == "org.eclipse.emf.ecore.util.FeatureMap$Entry";
  }

  public static FeatureMap.Entry createTextEntry(String value)
  {
    return XMLTypeFeatures.TEXT_PROTOTYPE.createEntry(value);
  }

  public static FeatureMap.Entry createCDATAEntry(String value)
  {
    return XMLTypeFeatures.CDATA_PROTOTYPE.createEntry(value);
  }

  public static FeatureMap.Entry createCommentEntry(String value)
  {
    return XMLTypeFeatures.COMMENT_PROTOTYPE.createEntry(value);
  }

  public static FeatureMap.Entry createProcessingInstructionEntry(String target, String data)
  {
    return createRawProcessingInstructionEntry(target, data);
  }

  public static FeatureMap.Entry createEntry(EStructuralFeature eStructuralFeature, Object value)
  {
    FeatureMap.Entry.Internal prototype = ((EStructuralFeature.Internal)eStructuralFeature).getFeatureMapEntryPrototype();
    prototype.validate(value);
    return prototype.createEntry(value);
  }

  public static FeatureMap.Entry.Internal createRawEntry(EStructuralFeature eStructuralFeature, Object value)
  {
    FeatureMap.Entry.Internal prototype = ((EStructuralFeature.Internal)eStructuralFeature).getFeatureMapEntryPrototype();
    return prototype.createEntry(value);
  }

  public static FeatureMap.Entry.Internal createRawTextEntry(String value)
  {
    return XMLTypeFeatures.TEXT_PROTOTYPE.createEntry(value);
  }

  public static FeatureMap.Entry.Internal createRawCDATAEntry(String value)
  {
    return XMLTypeFeatures.CDATA_PROTOTYPE.createEntry(value);
  }

  public static FeatureMap.Entry.Internal createRawCommentEntry(String value)
  {
    return XMLTypeFeatures.COMMENT_PROTOTYPE.createEntry(value);
  }

  public static FeatureMap.Entry.Internal createRawProcessingInstructionEntry(String target, String data)
  {
    ProcessingInstruction processingInstruction = XMLTypeFactory.eINSTANCE.createProcessingInstruction();
    processingInstruction.setTarget(target);
    processingInstruction.setData(data);
    return XMLTypeFeatures.PROCESSING_INSTRUCTION_PROTOTYPE.createEntry(processingInstruction);
  }

  public static class EntryImpl implements FeatureMap.Entry
  {
    protected final EStructuralFeature eStructuralFeature;
    protected final Object value;

    public EntryImpl(EStructuralFeature eStructuralFeature, Object value)
    {
      this.eStructuralFeature = eStructuralFeature;
      this.value = value;
      validate();
    }

    protected void validate()
    {
      if (value != null && !eStructuralFeature.getEType().isInstance(value))
      {
        String valueClass = value instanceof EObject ? ((EObject)value).eClass().getName() : value.getClass().getName();
        throw 
          new ClassCastException
            ("The feature '" + eStructuralFeature.getName()  + "'s type '" + 
                eStructuralFeature.getEType().getName() + "' does not permit a value of type '" + valueClass + "'");
      }
    }

    public EStructuralFeature getEStructuralFeature()
    {
      return eStructuralFeature;
    }

    public Object getValue()
    {
      return value;
    }

    @Override
    public boolean equals(Object that)
    {
      if (this == that)
      {
        return true;
      }
      else if (!(that instanceof FeatureMap.Entry))
      {
        return false;
      }
      else
      {
        FeatureMap.Entry entry = (FeatureMap.Entry)that;
        return 
          entry.getEStructuralFeature() == eStructuralFeature &&
          (value == null ? entry.getValue() == null : value.equals(entry.getValue()));
      }
    }

    @Override
    public int hashCode()
    {
     return eStructuralFeature.hashCode() ^ (value == null ? 0 : value.hashCode());
    }

    @Override
    public String toString()
    {
      String prefix = eStructuralFeature.getEContainingClass().getEPackage().getNsPrefix();
      eStructuralFeature.getName();
      return 
         (prefix != null && prefix.length() != 0 ? 
            prefix + ":" + eStructuralFeature.getName() : 
            eStructuralFeature.getName()) + 
           "=" + value;
/*
      StringBuffer result = new StringBuffer(super.toString());
      result.append(" (feature: ");
      result.append(eStructuralFeature.getName());
      result.append(", value: ");
      result.append(value);
      result.append(")");
      return result.toString();
*/
    }
  }

  public static abstract class BasicFeatureEIterator<E> implements ListIterator<E>
  {
    protected final EStructuralFeature eStructuralFeature;
    protected final FeatureMap.Internal featureMap;

    protected int entryCursor;
    protected int cursor;
    protected int prepared;
    protected E preparedResult;
    protected int expectedModCount;
    protected int lastCursor;
    protected boolean isFeatureMap;
    protected Validator validator;

    public BasicFeatureEIterator(EStructuralFeature eStructuralFeature, FeatureMap.Internal featureMap)
    {
      this.eStructuralFeature = eStructuralFeature;
      this.featureMap = featureMap;
      expectedModCount = featureMap.getModCount();
      isFeatureMap = isFeatureMap(eStructuralFeature);
      validator = getValidator(featureMap.getEObject().eClass(), eStructuralFeature);
    }

    protected boolean resolve()
    {
      return false;
    }

    @SuppressWarnings("unchecked")
    protected E extractValue(FeatureMap.Entry entry)
    {
      return isFeatureMap ? (E)entry : (E)entry.getValue();
    }

    public boolean hasNext()
    {
      switch (prepared)
      {
        case 2:
        {
          return true;
        }
        case 1:
        {
          return false;
        }
        case -1:
        {
          ++entryCursor;
        }
        default:
        {
          return scanNext();
        }
      }
    }

    protected abstract boolean scanNext();
/*
    {
      FeatureMap.Entry [] entries = (FeatureMap.Entry [])featureMap.data;
      while (entryCursor < size)
      {
        FeatureMap.Entry entry = entries[entryCursor];
        if (entry.getEStructuralFeature() == eStructuralFeature)
        {
          preparedResult = entry.getValue();
          prepared = 2;
          return true;
        }
        ++entryCursor;
      }

      prepared = 1;
      lastCursor = -1;
      return false;

    }
*/

    public E next()
    {
      if (hasNext())
      {
        checkModCount();

        if (resolve())
        {
          @SuppressWarnings("unchecked") E newPreparedResult = (E)featureMap.resolveProxy(eStructuralFeature, entryCursor, cursor, preparedResult);
          preparedResult = newPreparedResult;
        }

        lastCursor = cursor;
        ++cursor;

        ++entryCursor;
        prepared = 0;
        return preparedResult;
      }
      else
      {
        throw new NoSuchElementException();
      }
    }

    public int nextIndex()
    {
      return cursor;
    }

    public boolean hasPrevious()
    {
      switch (prepared)
      {
        case -2:
        {
          return true;
        }
        case -1:
        {
          return false;
        }
        case 1:
        {
          --entryCursor;
        }
        default:
        {
          return scanPrevious();
        }
      }
    }

    protected abstract boolean scanPrevious();
/*
    {
      FeatureMap.Entry [] entries = (FeatureMap.Entry [])featureMap.data;
      while (--entryCursor >= 0)
      {
        FeatureMap.Entry entry = entries[entryCursor];
        if (entry.getEStructuralFeature() == eStructuralFeature)
        {
          preparedResult = entry.getValue();
          prepared = -2;
          return true;
        }
      }

      prepared = -1;
      lastCursor = -1;
      return false;
    }
*/

    public E previous()
    {
      if (hasPrevious())
      {
        checkModCount();
        lastCursor = --cursor;
        if (resolve())
        {
          @SuppressWarnings("unchecked") E newPreparedResult = (E)featureMap.resolveProxy(eStructuralFeature, entryCursor, cursor, preparedResult);
          preparedResult = newPreparedResult;
        }
        // --entryCursor;
        prepared = 0;
        return preparedResult;
      }
      else
      {
        throw new NoSuchElementException();
      }
    }

    public int previousIndex()
    {
      return cursor - 1;
    }

    public void add(Object o)
    {
      if (lastCursor == -1)
      {
        throw new IllegalStateException();
      }
      checkModCount();

      try
      {
        featureMap.add(eStructuralFeature, cursor, o);
        expectedModCount = featureMap.getModCount();
        next();
/*

        featureMap.add(eStructuralFeature, cursor++, o);
        expectedModCount = featureMap.getModCount();

        ++entryCursor;

        ++lastCursor;
        // lastCursor = -1;
        // prepared = 0;
*/
      }
      catch (IndexOutOfBoundsException exception)
      {
        throw new ConcurrentModificationException();
      }
    }

    public void remove()
    {
      if (lastCursor == -1)
      {
        throw new IllegalStateException();
      }
      checkModCount();

      try
      {
        featureMap.remove(eStructuralFeature, lastCursor);
        expectedModCount = featureMap.getModCount();
        if (lastCursor < cursor)
        {
          --cursor;
          --entryCursor;
        }

        --lastCursor;
        //lastCursor = -1;
        //prepared = 0;
      }
      catch (IndexOutOfBoundsException exception)
      {
        throw new ConcurrentModificationException();
      }
    }

    public void set(Object o)
    {
      if (lastCursor == -1)
      {
        throw new IllegalStateException();
      }
      checkModCount();

      try
      {
        featureMap.set(eStructuralFeature, lastCursor, o);
        expectedModCount = featureMap.getModCount();
      }
      catch (IndexOutOfBoundsException exception)
      {
        throw new ConcurrentModificationException();
      }
    }

    /**
     * Checks that the modification count is as expected.
     * @exception ConcurrentModificationException if the modification count is not as expected.
     */
    protected void checkModCount()
    {
      if (featureMap.getModCount() != expectedModCount)
      {
        throw new ConcurrentModificationException();
      }
    }
  }

  public static class FeatureEList<E>
    extends AbstractList<E>
    implements InternalEList.Unsettable<E>, EStructuralFeature.Setting
  {
    public static class Basic<E> extends FeatureEList<E>
    {
      public Basic(EStructuralFeature feature, FeatureMap.Internal featureMap)
      {
        super(feature, featureMap);
      }

      @Override
      public Iterator<E> iterator()
      {
        return this.basicIterator();
      }

      @Override
      public ListIterator<E> listIterator()
      {
        return this.basicListIterator();
      }

      @Override
      public ListIterator<E> listIterator(int index)
      {
        return this.basicListIterator(index);
      }

      @Override
      public List<E> basicList()
      {
        return this;
      }
    }

    protected EStructuralFeature feature;
    protected FeatureMap.Internal featureMap;

    public FeatureEList(EStructuralFeature feature, FeatureMap.Internal featureMap)
    {
      this.feature= feature;
      this.featureMap = featureMap;
    }

    @Override
    public int size()
    {
      return featureMap.size(getEStructuralFeature());
    }

    @Override
    public boolean isEmpty()
    {
      return featureMap.isEmpty(getEStructuralFeature());
    }

    @Override
    public boolean contains(Object object)
    {
      return featureMap.contains(getEStructuralFeature(), object);
    }

    @Override
    public int indexOf(Object object)
    {
      return featureMap.indexOf(getEStructuralFeature(), object);
    }

    @Override
    public int lastIndexOf(Object object)
    {
      return featureMap.lastIndexOf(getEStructuralFeature(), object);
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
      return featureMap.containsAll(getEStructuralFeature(), collection);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<E> iterator()
    {
      return (Iterator<E>)featureMap.iterator(getEStructuralFeature());
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListIterator<E> listIterator()
    {
      return (ListIterator<E>)featureMap.listIterator(getEStructuralFeature());
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListIterator<E> listIterator(int index)
    {
      return (ListIterator<E>)featureMap.listIterator(getEStructuralFeature(), index);
    }

/*
    public List subList(int from, int to)
    {
      return featureMap.subList(getEStructuralFeature(), from, to);
    }
*/

    @SuppressWarnings("unchecked")
    public E basicGet(int index)
    {
      return (E)featureMap.get(getEStructuralFeature(), index, false);
    }

    @SuppressWarnings("unchecked")
    public List<E> basicList()
    {
      return (List<E>)featureMap.basicList(getEStructuralFeature());
    }

    @SuppressWarnings("unchecked")
    public Iterator<E> basicIterator()
    {
      return (Iterator<E>)featureMap.basicIterator(getEStructuralFeature());
    }

    @SuppressWarnings("unchecked")
    public ListIterator<E> basicListIterator()
    {
      return (ListIterator<E>)featureMap.basicListIterator(getEStructuralFeature());
    }

    @SuppressWarnings("unchecked")
    public ListIterator<E> basicListIterator(int index)
    {
      return (ListIterator<E>)featureMap.basicListIterator(getEStructuralFeature(), index);
    }

    @Override
    public Object[] toArray()
    {
      return featureMap.toArray(getEStructuralFeature());
    }

    @Override
    public <T>  T [] toArray(T[] array)
    {
      return featureMap.toArray(getEStructuralFeature(), array);
    }

    public boolean basicContains(Object object)
    {
      return featureMap.basicContains(getEStructuralFeature(), object);
    }

    public boolean basicContainsAll(Collection<?> collection)
    {
      return featureMap.basicContainsAll(getEStructuralFeature(), collection);
    }

    public int basicIndexOf(Object object)
    {
      return featureMap.basicIndexOf(getEStructuralFeature(), object);
    }

    public int basicLastIndexOf(Object object)
    {
      return featureMap.basicLastIndexOf(getEStructuralFeature(), object);
    }

    public Object[] basicToArray()
    {
      return featureMap.basicToArray(getEStructuralFeature());
    }

    public <T> T[] basicToArray(T[] array)
    {
      return featureMap.basicToArray(getEStructuralFeature(), array);
    }

    @Override
    public boolean add(Object object)
    {
      return featureMap.add(getEStructuralFeature(), object);
    }

    @Override
    public void add(int index, Object object)
    {
      featureMap.add(getEStructuralFeature(), index, object);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection)
    {
      return featureMap.addAll(getEStructuralFeature(), collection);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> collection)
    {
      return featureMap.addAll(getEStructuralFeature(), index, collection);
    }

    public void addUnique(Object object)
    {
      featureMap.addUnique(getEStructuralFeature(), object);
    }

    public void addUnique(int index, Object object)
    {
      featureMap.addUnique(getEStructuralFeature(), index, object);
    }
    
    @SuppressWarnings("unchecked")
    public boolean addAllUnique(Collection<? extends E> collection)
    {
      return featureMap.addAllUnique((Collection<? extends Entry>)collection);
    }

    @SuppressWarnings("unchecked")
    public boolean addAllUnique(int index, Collection<? extends E> collection)
    {
      return featureMap.addAllUnique(index, (Collection<? extends Entry>)collection);
    }
    
    public void addUnique(Entry.Internal entry)
    {
      featureMap.addUnique(entry);
    }

    public boolean addAllUnique(Entry.Internal [] entries, int start, int end)
    {
      return addAllUnique(size(), entries, start, end);
    }

    public boolean addAllUnique(int index, Entry.Internal [] entries, int start, int end)
    {
      BasicEList<Entry.Internal> collection = new BasicEList<Entry.Internal>();
      if (start == 0)
      {
        collection.setData(end, entries);
      }
      else
      {
        collection.grow(end - start);
        for (int i = start; i < end; ++i)
        {
          collection.add(entries[i]);
        }
      }
      return featureMap.addAllUnique(index, collection);
    }

    public NotificationChain basicAdd(E object, NotificationChain notifications)
    {
      return featureMap.basicAdd(getEStructuralFeature(), object, notifications);
    }

    @Override
    public boolean remove(Object object)
    {
      return featureMap.remove(getEStructuralFeature(), object);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E remove(int index)
    {
      return (E)featureMap.remove(getEStructuralFeature(), index);
    }

    public NotificationChain basicRemove(Object object, NotificationChain notifications)
    {
      return featureMap.basicRemove(getEStructuralFeature(), object, notifications);
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
      return featureMap.removeAll(getEStructuralFeature(), collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
      return featureMap.retainAll(getEStructuralFeature(), collection);
    }

    @Override
    public void clear()
    {
      featureMap.clear(getEStructuralFeature());
    }

    public void move(int index, Object object)
    {
      featureMap.move(getEStructuralFeature(), index, object);
    }

    @SuppressWarnings("unchecked")
    public E move(int targetIndex, int sourceIndex)
    {
      return (E)featureMap.move(getEStructuralFeature(), targetIndex, sourceIndex);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E get(int index)
    {
      return (E)featureMap.get(getEStructuralFeature(), index, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E set(int index, E object)
    {
      return (E)featureMap.set(getEStructuralFeature(), index, object);
    }

    @SuppressWarnings("unchecked")
    public E setUnique(int index, E object)
    {
      return (E)featureMap.setUnique(getEStructuralFeature(), index, object);
    }

    public Object get(boolean resolve)
    {
      return this;
    }

    @SuppressWarnings("unchecked")
    public void set(Object newValue)
    {
      clear();
      addAll((List<? extends E>)newValue);
    }

    public boolean isSet()
    {
      return !isEmpty();
    }

    public void unset()
    {
      clear();
    }

    public Object getFeature()
    {
      return getEStructuralFeature();
    }

    public int getFeatureID()
    {
      return featureMap.getEObject().eClass().getFeatureID(getEStructuralFeature());
    }

    public EStructuralFeature getEStructuralFeature()
    {
      return feature;
    }

    public EObject getEObject()
    {
      return featureMap.getEObject();
    }

    @Override
    public String toString()
    {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[");
      for (Iterator<E> i = basicIterator(); i.hasNext(); )
      {
        stringBuffer.append(String.valueOf(i.next()));
        if (i.hasNext())
        {
          stringBuffer.append(", ");
        }
      }
      stringBuffer.append("]");
      return stringBuffer.toString();
    }
  }

  public static class FeatureFeatureMap extends FeatureEList<FeatureMap.Entry> implements FeatureMap.Internal, FeatureMap.Internal.Wrapper
  {
    protected FeatureMap.Internal.Wrapper wrapper = this;
    
    public FeatureFeatureMap(EStructuralFeature feature, FeatureMap.Internal featureMap)
    {
      super(feature, featureMap);
    }
    
    public FeatureMap.ValueListIterator<Object> valueListIterator()
    {
      return featureMap.valueListIterator();
    }
    
    public FeatureMap.ValueListIterator<Object> valueListIterator(int index)
    {
      return featureMap.valueListIterator(index);
    }

    public <T> EList<T> list(EStructuralFeature feature)
    {
      return featureMap.list(feature);
    }

    public EStructuralFeature getEStructuralFeature(int index)
    {
      return ((Entry)featureMap.get(getEStructuralFeature(), index, false)).getEStructuralFeature();
    }

    public Object getValue(int index)
    {
      return ((Entry)featureMap.get(getEStructuralFeature(), index, false)).getValue();
    }

    public Object setValue(int index, Object value)
    {
      Entry entry = (Entry)featureMap.get(getEStructuralFeature(), index, false);
      set(index, createEntry(entry.getEStructuralFeature(), value));
      return entry.getValue();
    }

    public boolean add(EStructuralFeature feature, Object value)
    {
      return featureMap.add(feature, value);
    }

    public void add(int index, EStructuralFeature feature, Object value)
    {
      add(index, isFeatureMap(feature) ? value : createEntry(feature, value));
    }

    public void add(EStructuralFeature feature, int index, Object value)
    {
      featureMap.add(feature, index, value);
    }

    public boolean addAll(EStructuralFeature feature, Collection<?> values)
    {
      return featureMap.addAll(feature, values);
    }

    public boolean addAll(int index, EStructuralFeature feature, Collection<?> values)
    {
      if (isFeatureMap(feature))
      {
        @SuppressWarnings("unchecked") Collection<? extends Entry> entryValues = (Collection<? extends Entry>)values;
        return addAll(index, entryValues);
      }
      else
      {
        Collection<Entry> entries = new ArrayList<Entry>(values.size());
        for (Object value : values)
        {
          entries.add(createEntry(feature, value));
        }
        return addAll(index, entries);
      }
    }

    public boolean addAll(EStructuralFeature feature, int index, Collection<?> values)
    {
      return featureMap.addAll(feature, index, values);
    }

    public int getModCount()
    {
      return featureMap.getModCount();
    }

    @Override
    public EObject getEObject()
    {
      return featureMap.getEObject();
    }

    public Object resolveProxy(EStructuralFeature feature, int entryIndex, int index, Object object)
    {
      return featureMap.resolveProxy(feature, entryIndex, index, object);
    }

    public int size(EStructuralFeature feature)
    {
      return featureMap.size(feature);
    }

    public boolean isEmpty(EStructuralFeature feature)
    {
      return featureMap.isEmpty(feature);
    }

    public boolean contains(EStructuralFeature feature, Object object)
    {
      return featureMap.contains(feature, object);
    }

    public boolean containsAll(EStructuralFeature feature, Collection<?> collection)
    {
      return featureMap.containsAll(feature, collection);
    }

    public int indexOf(EStructuralFeature feature, Object object)
    {
      return featureMap.indexOf(feature, object);
    }

    public int lastIndexOf(EStructuralFeature feature, Object object)
    {
      return featureMap.lastIndexOf(feature, object);
    }

    public Iterator<Object> iterator(EStructuralFeature feature)
    {
      return featureMap.iterator(feature);
    }

    public ListIterator<Object> listIterator(EStructuralFeature feature)
    {
      return featureMap.listIterator(feature);
    }

    public ListIterator<Object> listIterator(EStructuralFeature feature, int index)
    {
      return featureMap.listIterator(feature, index);
    }

    // List subList(EStructuralFeature feature, int from, int to);
    // EList list(EStructuralFeature feature);
    public EStructuralFeature.Setting setting(EStructuralFeature feature)
    {
      return featureMap.setting(feature);
    }

    public List<Object> basicList(EStructuralFeature feature)
    {
      return featureMap.basicList(feature);
    }

    public Iterator<Object> basicIterator(EStructuralFeature feature)
    {
      return featureMap.basicIterator(feature);
    }

    public ListIterator<Object> basicListIterator(EStructuralFeature feature)
    {
      return featureMap.basicListIterator(feature);
    }

    public ListIterator<Object> basicListIterator(EStructuralFeature feature, int index)
    {
      return featureMap.basicListIterator(feature, index);
    }

    public Object[] toArray(EStructuralFeature feature)
    {
      return featureMap.toArray(feature);
    }

    public <T> T[] toArray(EStructuralFeature feature, T [] array)
    {
      return featureMap.toArray(feature, array);
    }

    public boolean basicContains(EStructuralFeature feature, Object object)
    {
      return featureMap.basicContains(feature, object);
    }

    public boolean basicContainsAll(EStructuralFeature feature, Collection<?> collection)
    {
      return featureMap.basicContainsAll(feature, collection);
    }

    public int basicIndexOf(EStructuralFeature feature, Object object)
    {
      return featureMap.basicIndexOf(feature, object);
    }

    public int basicLastIndexOf(EStructuralFeature feature, Object object)
    {
      return featureMap.basicLastIndexOf(feature, object);
    }

    public Object[] basicToArray(EStructuralFeature feature)
    {
      return featureMap.basicToArray(feature);
    }

    public <T> T[] basicToArray(EStructuralFeature feature, T[] array)
    {
      return featureMap.basicToArray(feature, array);
    }

/*
    public boolean add(EStructuralFeature feature, Object object)
    {
      return featureMap.add(feature, object);
    }

    public void add(EStructuralFeature feature, int index, Object object)
    {
      featureMap.add(feature, index, object);
    }

    public boolean addAll(EStructuralFeature feature, Collection collection)
    {
      return featureMap.addAll(feature, collection);
    }

    boolean addAll(EStructuralFeature feature, int index, Collection collection)
    {
      return featureMap.addAll(feature, index, collection);
    }
*/

    public void addUnique(EStructuralFeature feature, Object object)
    {
      featureMap.addUnique(feature, object);
    }

    public void addUnique(EStructuralFeature feature, int index, Object object)
    {
      featureMap.addUnique(feature, index, object);
    }

    public NotificationChain basicAdd(EStructuralFeature feature, Object object, NotificationChain notifications)
    {
      return featureMap.basicAdd(feature, object, notifications);
    }

    public boolean remove(EStructuralFeature feature, Object object)
    {
      return featureMap.remove(feature, object);
    }

    public Object remove(EStructuralFeature feature, int index)
    {
      return featureMap.remove(feature, index);
    }

    public boolean removeAll(EStructuralFeature feature, Collection<?> collection)
    {
      return featureMap.removeAll(feature, collection);
    }

    public NotificationChain basicRemove(EStructuralFeature feature, Object object, NotificationChain notifications)
    {
      return featureMap.basicRemove(feature, object, notifications);
    }

    public boolean retainAll(EStructuralFeature feature, Collection<?> collection)
    {
      return featureMap.retainAll(feature, collection);
    }

    public void clear(EStructuralFeature feature)
    {
      featureMap.clear(feature);
    }

    public void move(EStructuralFeature feature, int index, Object object)
    {
      featureMap.move(feature, index, object);
    }

    public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex)
    {
      return featureMap.move(feature, targetIndex, sourceIndex);
    }

    public Object get(EStructuralFeature feature, boolean resolve)
    {
      return featureMap.get(feature, resolve);
    }

    public Object get(EStructuralFeature feature, int index, boolean resolve)
    {
      return featureMap.get(feature, index, resolve);
    }

    public void set(EStructuralFeature feature, Object object)
    {
      featureMap.set(feature, object);
    }

    public Object set(EStructuralFeature feature, int index, Object object)
    {
      return featureMap.set(feature, index, object);
    }

    public Object setUnique(EStructuralFeature feature, int index, Object object)
    {
      return featureMap.setUnique(feature, index, object);
    }

    public boolean isSet(EStructuralFeature feature)
    {
      return featureMap.isSet(feature);
    }

    public void unset(EStructuralFeature feature)
    {
      featureMap.unset(feature);
    }
    
    public Wrapper getWrapper()
    {
      return wrapper;
    }

    public void setWrapper(Wrapper wrapper)
    {
      this.wrapper = wrapper;
    }

    public FeatureMap featureMap()
    {
      return this;
    }
  }

  public static class FeatureValue implements EStructuralFeature.Setting
  {
    protected EStructuralFeature feature;
    protected FeatureMap.Internal featureMap;

    public FeatureValue(EStructuralFeature feature, FeatureMap.Internal featureMap)
    {
      this.feature = feature;
      this.featureMap = featureMap;
    }

    public Object get(boolean resolve)
    {
      return featureMap.get(getEStructuralFeature(), -1, resolve);
    }

    public void set(Object newValue)
    {
      featureMap.set(getEStructuralFeature(), newValue);
    }

    public boolean isSet()
    {
      return !featureMap.isEmpty(getEStructuralFeature());
    }

    public void unset()
    {
      featureMap.clear(getEStructuralFeature());
    }

    public Object getFeature()
    {
      return getEStructuralFeature();
    }

    public int getFeatureID()
    {
      return featureMap.getEObject().eClass().getFeatureID(getEStructuralFeature());
    }

    public EStructuralFeature getEStructuralFeature()
    {
      return feature;
    }

    public EObject getEObject()
    {
      return featureMap.getEObject();
    }
  }

  public static class FeatureENotificationImpl extends ENotificationImpl
  {
    public FeatureENotificationImpl
      (InternalEObject owner, int eventType, EStructuralFeature feature, Object oldObject, Object newObject, int index)
    {
      super(owner, eventType, feature, oldObject, newObject, index);
    }

    public FeatureENotificationImpl
      (InternalEObject owner, int eventType, EStructuralFeature feature, Object oldObject, Object newObject, int index, boolean wasSet)
    {
      super(owner, eventType, feature, oldObject, newObject, index, wasSet);
    }
    
    @Override
    public int getFeatureID(Class<?> expectedClass)
    {
      if (featureID == NO_FEATURE_ID && feature != null)
      {
        Class<?> containerClass = feature.getContainerClass();
        featureID = containerClass == null ? 
          notifier.eClass().getFeatureID(feature) : 
          notifier.eDerivedStructuralFeatureID(feature.getFeatureID(), containerClass);
      }
      return notifier.eBaseStructuralFeatureID(featureID, expectedClass);
    }

    @Override
    public boolean merge(Notification notification)
    {
      switch (eventType)
      {
        case Notification.SET:
        case Notification.UNSET:
        {
          Object notificationNotifier = notification.getNotifier();
          if (notificationNotifier == getNotifier() && getFeatureID(null) == notification.getFeatureID(null))
          {
            newValue = notification.getNewValue();
            if (notification.getEventType() == Notification.SET)
            {
              eventType = Notification.SET;
            }
            return true;
          }
          break;
        }
        case Notification.ADD:
        {
          int notificationEventType = notification.getEventType();
          switch (notificationEventType)
          {
            case Notification.ADD:
            {
              Object notificationNotifier = notification.getNotifier();
              if (notificationNotifier == getNotifier() && getFeatureID(null) == notification.getFeatureID(null))
              {
                eventType = Notification.ADD_MANY;
                BasicEList<Object> addedValues = new BasicEList<Object>(2);
                addedValues.add(newValue);
                addedValues.add(notification.getNewValue());
                newValue = addedValues;
                return true;
              }
              break;
            }
          }
          break;
        }
        case Notification.ADD_MANY:
        {
          int notificationEventType = notification.getEventType();
          switch (notificationEventType)
          {
            case Notification.ADD:
            {
              Object notificationNotifier = notification.getNotifier();
              if (notificationNotifier == getNotifier() && getFeatureID(null) == notification.getFeatureID(null))
              {
                @SuppressWarnings("unchecked") Collection<Object> collection = (Collection<Object>)newValue;
                collection.add(notification.getNewValue());
                return true;
              }
              break;
            }
          }
          break;
        }
        case Notification.REMOVE:
        {
          int notificationEventType = notification.getEventType();
          switch (notificationEventType)
          {
            case Notification.ADD:
            {
              Object notificationNotifier = notification.getNotifier();
              if (notificationNotifier == getNotifier() && getFeatureID(null) == notification.getFeatureID(null))
              {
                eventType = Notification.SET;
                newValue = notification.getNewValue();
                return true;
              }
              break;
            }
            case Notification.REMOVE:
            {
              Object notificationNotifier = notification.getNotifier();
              if (notificationNotifier == getNotifier() && getFeatureID(null) == notification.getFeatureID(null))
              {
                eventType = Notification.REMOVE_MANY;
                BasicEList<Object> removedValues = new BasicEList<Object>(2);
                removedValues.add(oldValue);
                removedValues.add(notification.getOldValue());
                oldValue = removedValues;

                int [] positions = new int [] { position, notification.getPosition() };
                newValue = positions;
                return true;
              }
              break;
            }
          }
          break;
        }
        case Notification.REMOVE_MANY:
        {
          int notificationEventType = notification.getEventType();
          switch (notificationEventType)
          {
            case Notification.REMOVE:
            {
              Object notificationNotifier = notification.getNotifier();
              if (notificationNotifier == getNotifier() && getFeatureID(null) == notification.getFeatureID(null))
              {
                @SuppressWarnings("unchecked") Collection<Object> collection = ((Collection<Object>)oldValue);
                collection.add(notification.getOldValue());

                int [] positions = (int [])newValue;
                int [] newPositions = new int [positions.length + 1];

                System.arraycopy(positions, 0, newPositions, 0, positions.length);
                newPositions[positions.length] = notification.getPosition();
                newValue = newPositions;

                return true;
              }
              break;
            }
          }
          break;
        }
      }

      return false;
    }
  }

  public interface Validator
  {
    boolean isValid(EStructuralFeature feature);
  }

  public static class BasicValidator implements Validator
  {
    protected static final List<String> ANY_WILDCARD = Collections.singletonList("##any");

    protected EClass containingClass;
    protected EStructuralFeature eStructuralFeature;
    protected List<EStructuralFeature> groupMembers;
    protected List<String> wildcards;
    protected String name;
    protected boolean isElement;
    
    protected class Cache extends HashMap<Object, Object>
    {
      private static final long serialVersionUID = 1L;

      public Boolean get(EStructuralFeature eStructuralFeature)
      {
        return (Boolean)get((Object)eStructuralFeature);
      }

      public void put(EStructuralFeature eStructuralFeature, Boolean isValid)
      {
        Cache newCache = new Cache();
        newCache.putAll(cache);
        newCache.put((Object)eStructuralFeature, (Object)isValid);
        cache = newCache;
      }
   }

    protected Cache cache = new Cache();

    public BasicValidator(EClass containingClass, EStructuralFeature eStructuralFeature)
    {
      this.containingClass = containingClass;
      this.eStructuralFeature = eStructuralFeature;

      wildcards = ExtendedMetaData.INSTANCE.getWildcards(eStructuralFeature);
      EStructuralFeature mixedFeature;
      if (!wildcards.isEmpty())
      {
        isElement = ExtendedMetaData.INSTANCE.getFeatureKind(eStructuralFeature) == ExtendedMetaData.ELEMENT_WILDCARD_FEATURE;
        if (wildcards.equals(ANY_WILDCARD))
        {
          wildcards = ANY_WILDCARD;
        }
      }
      else if ((mixedFeature = ExtendedMetaData.INSTANCE.getMixedFeature(containingClass)) == eStructuralFeature)
      {
        isElement = true;
        groupMembers = new ArrayList<EStructuralFeature>();
        wildcards = new UniqueEList<String>();
        wildcards.add(XMLTypePackage.eNS_URI);
        if (ExtendedMetaData.INSTANCE.getDocumentRoot(containingClass.getEPackage()) == containingClass)
        {
          wildcards.add(ExtendedMetaData.INSTANCE.getNamespace(containingClass));
        }
        for (EStructuralFeature feature : ExtendedMetaData.INSTANCE.getAllElements(containingClass))
        {
          switch (ExtendedMetaData.INSTANCE.getFeatureKind(feature))
          {
            case ExtendedMetaData.ELEMENT_FEATURE:
            {
              groupMembers.add(feature);
              break;
            }
            case ExtendedMetaData.ELEMENT_WILDCARD_FEATURE:
            {
              wildcards.addAll(ExtendedMetaData.INSTANCE.getWildcards(feature));
              break;
            }
          }
        }
      }
      else if (isFeatureMap(eStructuralFeature))
      {
        isElement = true;
        wildcards = null;
        groupMembers = new ArrayList<EStructuralFeature>();
        for (int i = 0, size = containingClass.getFeatureCount(); i < size; ++i)
        {
          EStructuralFeature feature = containingClass.getEStructuralFeature(i);
          for (EStructuralFeature group = ExtendedMetaData.INSTANCE.getGroup(feature); 
               group != null; 
               group = ExtendedMetaData.INSTANCE.getGroup(group))
          {
            if (group == eStructuralFeature)
            {
              groupMembers.add(feature);
            }
          }
        }
      }
      else if (ExtendedMetaData.INSTANCE.getFeatureKind(eStructuralFeature) == ExtendedMetaData.SIMPLE_FEATURE &&
                 mixedFeature != null)
      {
        wildcards = null;
        groupMembers = XMLTypeFeatures.TEXTUAL_FEATURES;
      }
      else
      {
        wildcards = null;
        isElement = true;
        groupMembers = Collections.singletonList(eStructuralFeature);
      }
    }

    public boolean isValid(EStructuralFeature feature)
    {
      if (eStructuralFeature == feature) return true;

      Boolean result = cache.get(feature);
      if (result == null)
      {
        if (isIncluded(feature))
        {
          cache.put(feature, Boolean.TRUE);
          return true;
        }
        else
        {
          cache.put(feature, Boolean.FALSE);
          return false;
        }
      }
      else
      {
        return result == Boolean.TRUE;
      }
    }

    public boolean isIncluded(EStructuralFeature feature)
    {
      if (wildcards == ANY_WILDCARD)
      {
        int featureKind = ExtendedMetaData.INSTANCE.getFeatureKind(feature);
        return 
          isElement ? 
            featureKind == ExtendedMetaData.ELEMENT_FEATURE && 
              feature != XMLTypeFeatures.TEXT && feature != XMLTypeFeatures.CDATA && feature != XMLTypeFeatures.COMMENT && feature != XMLTypeFeatures.PROCESSING_INSTRUCTION :
            featureKind == ExtendedMetaData.ATTRIBUTE_FEATURE;
      }

      if (groupMembers != null &&
            (groupMembers.contains(feature) ||
               groupMembers.contains(ExtendedMetaData.INSTANCE.getGroup(feature)) ||
               groupMembers.contains(ExtendedMetaData.INSTANCE.getAffiliation(containingClass, feature))))
      {
        return true;
      }

      if (wildcards != null)
      {
        if (ExtendedMetaData.INSTANCE.matches(wildcards, ExtendedMetaData.INSTANCE.getNamespace(feature)))
        {
          int featureKind = ExtendedMetaData.INSTANCE.getFeatureKind(feature);
          return isElement ? featureKind == ExtendedMetaData.ELEMENT_FEATURE : featureKind == ExtendedMetaData.ATTRIBUTE_FEATURE;
        }
      }

      return false;
    }
  }

  protected static Validator NULL_VALIDATOR = 
    new Validator()
    {
      public boolean isValid(EStructuralFeature eStructuralFeature)
      {
        return true;
      }
    };

  public static Validator getValidator(EClass containingClass, EStructuralFeature eStructuralFeature)
  {
    if (eStructuralFeature == null)
    {
      return NULL_VALIDATOR;
    }
    else if (eStructuralFeature == XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__MIXED ||
              (eStructuralFeature == XMLTypePackage.Literals.ANY_TYPE__MIXED ||
                 eStructuralFeature == XMLTypePackage.Literals.ANY_TYPE__ANY ||
                 eStructuralFeature == XMLTypePackage.Literals.ANY_TYPE__ANY_ATTRIBUTE) && containingClass != XMLTypePackage.Literals.ANY_TYPE)
    {
      // Don't cache this one as it will cause leaks because to dynamic demand created document roots and demand created EClasses.
      //
      return new BasicValidator(containingClass, eStructuralFeature);
    }
    else
    {
      BasicExtendedMetaData.EStructuralFeatureExtendedMetaData.Holder holder = 
        (BasicExtendedMetaData.EStructuralFeatureExtendedMetaData.Holder)eStructuralFeature;
      BasicExtendedMetaData.EStructuralFeatureExtendedMetaData extendedMetaData = holder.getExtendedMetaData();
      if (extendedMetaData == null)
      {
        // For the extended meta data to be created.
        //
        ExtendedMetaData.INSTANCE.getName(eStructuralFeature);
        extendedMetaData = holder.getExtendedMetaData();
      }
      Map<EClass, Validator> validatorMap = extendedMetaData.getValidatorMap();
      Validator result = validatorMap.get(containingClass);
      if (result == null)
      {
        validatorMap.put(containingClass, result = new BasicValidator(containingClass, eStructuralFeature));
      }
      return result;
    }
  }

  public static boolean isMany(EObject owner, EStructuralFeature feature)
  {
    if (feature.isMany())
    {
      return true;
    }
    else if (feature.getUpperBound() == ETypedElement.UNSPECIFIED_MULTIPLICITY)
    {
      if (feature == XMLTypeFeatures.TEXT ||
            feature == XMLTypeFeatures.CDATA ||
            feature == XMLTypeFeatures.COMMENT ||
            feature == XMLTypeFeatures.PROCESSING_INSTRUCTION)
      {
        return true;
      }
      else
      {
        EClass eClass = owner.eClass();
        if (eClass.getFeatureID(feature) >= 0)
        {
          return false;
        }
        else
        {
          EStructuralFeature affiliation = ExtendedMetaData.INSTANCE.getAffiliation(eClass, feature);
          if (affiliation == null)
          {
            return true;
          }
          else
          {
            int affiliationUpperBound = affiliation.getUpperBound();
            return 
              (affiliationUpperBound > 1 || affiliationUpperBound == ETypedElement.UNBOUNDED_MULTIPLICITY) && 
                ExtendedMetaData.INSTANCE.getFeatureKind(affiliation) != ExtendedMetaData.ATTRIBUTE_WILDCARD_FEATURE;
          }
        }
      }
    }
    else
    {
      return false;
    }
  }
}

final class XMLTypeFeatures
{
  public static final EStructuralFeature TEXT = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Text();
  public static final EStructuralFeature CDATA = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_CDATA();
  public static final EStructuralFeature COMMENT = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Comment();
  public static final EStructuralFeature PROCESSING_INSTRUCTION = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_ProcessingInstruction();
  public static final FeatureMap.Entry.Internal TEXT_PROTOTYPE = ((EStructuralFeature.Internal)TEXT).getFeatureMapEntryPrototype();
  public static final FeatureMap.Entry.Internal CDATA_PROTOTYPE = ((EStructuralFeature.Internal)CDATA).getFeatureMapEntryPrototype();
  public static final FeatureMap.Entry.Internal COMMENT_PROTOTYPE = ((EStructuralFeature.Internal)COMMENT).getFeatureMapEntryPrototype();
  public static final FeatureMap.Entry.Internal PROCESSING_INSTRUCTION_PROTOTYPE = ((EStructuralFeature.Internal)PROCESSING_INSTRUCTION).getFeatureMapEntryPrototype();

  public static final List<EStructuralFeature> TEXTUAL_FEATURES = Arrays.asList(new EStructuralFeature [] {TEXT, CDATA});
}

