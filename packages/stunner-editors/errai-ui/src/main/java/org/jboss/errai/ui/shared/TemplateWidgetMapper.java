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

package org.jboss.errai.ui.shared;

import java.util.IdentityHashMap;
import java.util.Map;

import org.jboss.errai.ioc.client.container.Factory;

import com.google.gwt.user.client.ui.Composite;

/**
 * Maps templates that do not extend {@link Composite} to their root widgets.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class TemplateWidgetMapper {

  private static class Pair {
    Object bean;
    TemplateWidget widget;

    Pair(Object bean, TemplateWidget widget) {
      this.bean = bean;
      this.widget = widget;
    }
  }

  // There is no identity bimap so we do this.
  private static final Map<Object, Pair> widgetBeanMap = new IdentityHashMap<Object, Pair>();

  private TemplateWidgetMapper() {}

  public static void put(Object bean, final TemplateWidget widget) {
    bean = Factory.maybeUnwrapProxy(bean);

    if (widgetBeanMap.containsKey(bean)) {
      throw new RuntimeException(
              "There is already a widget mapped for the " + bean.getClass().getName() + " bean: " + bean.toString());
    } else if (widgetBeanMap.containsKey(widget)) {
      throw new RuntimeException("There is already a bean mapped for the given " + TemplateWidget.class.getSimpleName() + ".");
    } else {
      final Pair pair = new Pair(bean, widget);
      widgetBeanMap.put(bean, pair);
      widgetBeanMap.put(widget, pair);
    }
  }

  public static boolean containsKey(Object bean) {
    bean = Factory.maybeUnwrapProxy(bean);

    return widgetBeanMap.containsKey(bean);
  }

  public static TemplateWidget get(Object bean) {
    bean = Factory.maybeUnwrapProxy(bean);
    final Pair pair = widgetBeanMap.get(bean);
    if (pair == null) {
      throw new RuntimeException("There is no widget mapped to the " + bean.getClass().getName() + " bean: " + bean.toString());
    } else {
      return pair.widget;
    }
  }

  public static Object reverseGet(final TemplateWidget widget) {
    final Pair pair = widgetBeanMap.get(widget);
    if (pair == null) {
      throw new RuntimeException("There is no bean mapped to the templated widget with contents:\n" + widget.getElement().getInnerHTML());
    } else {
      return pair.bean;
    }
  }

  public static void remove(Object obj) {
    obj = Factory.maybeUnwrapProxy(obj);
    final Pair pair = widgetBeanMap.get(obj);
    if (pair != null) {
      widgetBeanMap.remove(pair.bean);
      widgetBeanMap.remove(pair.widget);
    }
  }

}
