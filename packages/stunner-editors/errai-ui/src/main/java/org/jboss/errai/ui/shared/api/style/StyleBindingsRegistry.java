/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.ui.shared.api.style;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.extension.InitVotes;

import com.google.gwt.user.client.Element;

/**
 * @author Mike Brock
 */
@SuppressWarnings("deprecation")
public class StyleBindingsRegistry {
  private static StyleBindingsRegistry INSTANCE = new StyleBindingsRegistry();

  private final Set<Object> instancesWithElementBindings = new HashSet<Object>();
  private final Map<Object, List<Object>> houseKeepingMap = new HashMap<Object, List<Object>>();
  private final Map<Class<? extends Annotation>, List<StyleBindingExecutor>> styleBindings =
      new HashMap<Class<? extends Annotation>, List<StyleBindingExecutor>>();

  private final Map<Annotation, List<ElementBinding>> elementBindings = new HashMap<Annotation, List<ElementBinding>>();
  private final Map<Class<? extends Annotation>, Set<Annotation>> mapping =
      new HashMap<Class<? extends Annotation>, Set<Annotation>>();

  private boolean isInitCallbackRegistered = false;

  public BindingRegistrationHandle addStyleBinding(final Class<? extends Annotation> annotation,
          final StyleBindingExecutor binding) {

    List<StyleBindingExecutor> styleBindingList = styleBindings.get(annotation);
    if (styleBindingList == null) {
      styleBindings.put(annotation, styleBindingList = new ArrayList<StyleBindingExecutor>());
    }
    styleBindingList.add(binding);

    if (InitVotes.isInitialized()) {
      updateStyles();
    } else if (!isInitCallbackRegistered) {
      isInitCallbackRegistered = true;
      InitVotes.registerOneTimeInitCallback(new Runnable() {
        @Override
        public void run() {
          updateStyles();
        }
      });
    }

    return new BindingRegistrationHandle() {

      @Override
      public void cleanup() {
        styleBindings.get(annotation).remove(binding);
      }
    };
  }

  public void addElementBinding(final Object beanInst, final Annotation annotation, final Element element) {
    instancesWithElementBindings.add(beanInst);
    addElementBinding(annotation, new ElementBinding(this, element, beanInst));
  }

  private void addElementBinding(final Annotation annotation, final ElementBinding binding) {
    List<ElementBinding> elementsList = elementBindings.get(annotation);
    if (elementsList == null) {
      elementBindings.put(annotation, elementsList = new ArrayList<ElementBinding>());
    }
    elementsList.add(binding);
    Set<Annotation> mappedAnnotations = mapping.get(annotation.annotationType());
    if (mappedAnnotations == null) {
      mapping.put(annotation.annotationType(), mappedAnnotations = new HashSet<Annotation>());
    }
    mappedAnnotations.add(annotation);
    recordHouskeepingData(binding.getBeanInstance(), binding);
  }

  private void recordHouskeepingData(final Object beanInst, final Object toClean) {
    List<Object> toCleanList = houseKeepingMap.get(beanInst);
    if (toCleanList == null) {
      houseKeepingMap.put(beanInst, toCleanList = new ArrayList<Object>());
    }
    toCleanList.add(toClean);
  }

  public void cleanAllForBean(final Object beanInst) {
    final List<Object> toCleanList = houseKeepingMap.get(beanInst);
    if (toCleanList != null) {
      for (final Object o : toCleanList) {
        for (final List<StyleBindingExecutor> executorList : styleBindings.values()) {
          executorList.remove(o);
        }
        for (final List<ElementBinding> bindingList : elementBindings.values()) {
          bindingList.remove(o);
        }
      }

      houseKeepingMap.remove(beanInst);
      instancesWithElementBindings.remove(beanInst);
    }
  }

  public void updateStyles() {
    updateStyles(null);
  }

  public void updateStyles(Object beanInst) {
    if (beanInst != null && !instancesWithElementBindings.contains(beanInst)) {
      return;
    }
    for (final Map.Entry<Class<? extends Annotation>, List<StyleBindingExecutor>> entry : styleBindings.entrySet()) {
      if (mapping.containsKey(entry.getKey())) {
        final Set<Annotation> annoMappings = mapping.get(entry.getKey());
        for (final Annotation mappedAnnotation : annoMappings) {
          final List<ElementBinding> elementList = elementBindings.get(mappedAnnotation);
          if (elementList != null) {
            for (final ElementBinding binding : elementList) {
              if (beanInst == null || beanInst.equals(binding.getBeanInstance())) {
                for (final StyleBindingExecutor executor : entry.getValue()) {
                  if (executor instanceof AnnotationStyleBindingExecutor) {
                    ((AnnotationStyleBindingExecutor) executor).invokeBinding(binding.getElement(), mappedAnnotation);
                  }
                  else {
                    executor.invokeBinding(binding.getElement());
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  public static StyleBindingsRegistry get() {
    return INSTANCE;
  }

  public static void set(StyleBindingsRegistry registry) {
    INSTANCE = registry;
  }

  public static void reset() {
    if (INSTANCE != null) {
      INSTANCE.elementBindings.clear();
      INSTANCE.houseKeepingMap.clear();
      INSTANCE.mapping.clear();
      INSTANCE.styleBindings.clear();
    }

    set(new StyleBindingsRegistry());
  }
}
