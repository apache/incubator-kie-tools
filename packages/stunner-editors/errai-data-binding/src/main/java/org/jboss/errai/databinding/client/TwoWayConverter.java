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

import org.jboss.errai.databinding.client.api.Converter;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class TwoWayConverter<M, W> implements Converter<M, W> {

  public static <M, W> Converter<M, W> createConverter(final OneWayConverter<M, W> modelToWidget, final OneWayConverter<W, M> widgetToModel) {
    if (!((modelToWidget.getDomainType().equals(widgetToModel.getTargetType())
            || (modelToWidget.getDomainType().isInterface() ^ widgetToModel.getTargetType().isInterface()))
            && (modelToWidget.getTargetType().equals(widgetToModel.getDomainType())
                    || (modelToWidget.getTargetType().isInterface() ^ widgetToModel.getDomainType().isInterface())))) {
      throw new IllegalArgumentException("The first converter converts " + modelToWidget.getDomainType().getName()
              + " to " + modelToWidget.getTargetType().getName() + " but the second converts from "
              + widgetToModel.getDomainType().getName() + " to " + widgetToModel.getTargetType().getName());
    }

    return new TwoWayConverter<M, W>(modelToWidget, widgetToModel);
  }

  private final OneWayConverter<W, M> widgetToModel;
  private final OneWayConverter<M, W> modelToWidget;

  private TwoWayConverter(final OneWayConverter<M, W> modelToWidget, final OneWayConverter<W, M> widgetToModel) {
    this.modelToWidget = modelToWidget;
    this.widgetToModel = widgetToModel;
  }

  @Override
  public Class<M> getModelType() {
    return modelToWidget.getDomainType();
  }

  @Override
  public Class<W> getComponentType() {
    return modelToWidget.getTargetType();
  }

  @Override
  public M toModelValue(final W widgetValue) {
    try {
      return widgetToModel.convert(widgetValue);
    } catch (final Throwable t) {
      throw new RuntimeException("There was an error while converting widget value [" + widgetValue + "] with "
              + widgetToModel.getDomainType().getSimpleName() + " -> " + widgetToModel.getTargetType().getSimpleName()
              + " converter.", t);
    }
  }

  @Override
  public W toWidgetValue(final M modelValue) {
    try {
      return modelToWidget.convert(modelValue);
    } catch (final Throwable t) {
      throw new RuntimeException("There was an error while converting widget value [" + modelValue + "] with "
              + modelToWidget.getDomainType().getSimpleName() + " -> " + modelToWidget.getTargetType().getSimpleName()
              + " converter.", t);
    }
  }

}
