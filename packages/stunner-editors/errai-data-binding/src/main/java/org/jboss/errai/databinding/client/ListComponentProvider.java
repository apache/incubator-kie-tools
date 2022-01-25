/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.databinding.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.databinding.client.components.DefaultListComponent;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.IOCProvider;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Provides {@link ListComponent} instances that lookup displayed components through Errai IoC. Any qualifiers on the
 * injection site are used when looking up displayed components.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@SuppressWarnings("rawtypes")
@IOCProvider
public class ListComponentProvider implements ContextualTypeProvider<ListComponent> {

  @SuppressWarnings("unchecked")
  @Override
  public ListComponent provide(final Class<?>[] typeargs, final Annotation[] qualifiers) {
    final Annotation[] filteredQualifiers = filterQualifiers(qualifiers);
    final Optional<ListContainer> listContainer = getListContainer(qualifiers);
    final HTMLElement root = (HTMLElement) Document.get().createElement(listContainer.map(anno -> anno.value()).orElse("div"));
    final SyncBeanDef<?> beanDef = IOC.getBeanManager().lookupBean(typeargs[1], filteredQualifiers);
    final Supplier<?> supplier = () -> beanDef.getInstance();
    final Consumer<?> destroyer = (!Dependent.class.equals(beanDef.getScope()) ? c -> {} : c -> IOC.getBeanManager().destroyBean(c));
    final Function<?, HTMLElement> elementAccessor;
    if (beanDef.isAssignableTo(IsElement.class)) {
      elementAccessor = c -> ((IsElement) c).getElement();
    }
    else if (beanDef.isAssignableTo(IsWidget.class)) {
      elementAccessor = c -> (HTMLElement) ((IsWidget) c).asWidget().getElement();
    }
    else {
      throw new RuntimeException("Cannot create element accessor for " + beanDef.getType().getName() + ". Must implement IsElement or IsWidget.");
    }

    return new DefaultListComponent(root,
                                    supplier,
                                    destroyer,
                                    elementAccessor);
  }

  private Optional<ListContainer> getListContainer(final Annotation[] qualifiers) {
    for (final Annotation qual : qualifiers) {
      if (qual.annotationType().equals(ListContainer.class)) {
        return Optional.ofNullable((ListContainer) qual);
      }
    }

    return Optional.empty();
  }

  private Annotation[] filterQualifiers(final Annotation[] qualifiers) {
    final List<Annotation> filtered = new ArrayList<>(qualifiers.length);
    for (final Annotation qual : qualifiers) {
      if (!qual.annotationType().equals(ListContainer.class)) {
        filtered.add(qual);
      }
    }

    return filtered.toArray(new Annotation[filtered.size()]);
  }

}
