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


package org.jboss.errai.databinding.client.api;

import java.awt.Checkbox;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.databinding.client.AbstractOneWayConverter;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.ConverterRegistrationKey;
import org.jboss.errai.databinding.client.OneWayConverter;
import org.jboss.errai.databinding.client.TwoWayConverter;

/**
 * Type conversion utility used by the generated {@link Bindable} proxies.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class Convert {

  private static class AnyToStringConverter<D> extends AbstractOneWayConverter<D, String> {
    public AnyToStringConverter(final Class<D> domainType) {
      super(domainType, String.class);
    }

    @Override
    public String convert(final D value) {
      if (value == null) {
        return "";
      }
      else {
        return value.toString();
      }
    }
  }

  private static class IdentityConverter<T> extends AbstractOneWayConverter<T, T> {
    public IdentityConverter(final Class<T> type) {
      super(type, type);
    }

    @Override
    public T convert(final T value) {
      return value;
    }
  }

  private static class StringToIntegerConverter extends AbstractOneWayConverter<String, Integer> {
    public StringToIntegerConverter() {
      super(String.class, Integer.class);
    }

    @Override
    public Integer convert(final String value) {
      if (isEmpty(value)) {
        return null;
      }
      else {
        return Integer.parseInt(value);
      }
    }
  }

  private static class StringToLongConverter extends AbstractOneWayConverter<String, Long> {
    public StringToLongConverter() {
      super(String.class, Long.class);
    }

    @Override
    public Long convert(final String value) {
      if (isEmpty(value)) {
        return null;
      }
      else {
        return Long.parseLong(value);
      }
    }
  }

  private static class StringToFloatConverter extends AbstractOneWayConverter<String, Float> {
    public StringToFloatConverter() {
      super(String.class, Float.class);
    }

    @Override
    public Float convert(final String value) {
      if (isEmpty(value)) {
        return null;
      }
      else {
        return Float.parseFloat(value);
      }
    }
  }

  private static class StringToDoubleConverter extends AbstractOneWayConverter<String, Double> {
    public StringToDoubleConverter() {
      super(String.class, Double.class);
    }

    @Override
    public Double convert(final String value) {
      if (isEmpty(value)) {
        return null;
      }
      else {
        return Double.parseDouble(value);
      }
    }
  }

  private static class StringToBigIntegerConverter extends AbstractOneWayConverter<String, BigInteger> {
    public StringToBigIntegerConverter() {
      super(String.class, BigInteger.class);
    }

    @Override
    public BigInteger convert(final String value) {
      if (isEmpty(value)) {
        return null;
      }
      else {
        return new BigInteger(value);
      }
    }
  }

  private static class StringToBigDecimalConverter extends AbstractOneWayConverter<String, BigDecimal> {
    public StringToBigDecimalConverter() {
      super(String.class, BigDecimal.class);
    }

    @Override
    public BigDecimal convert(final String value) {
      if (isEmpty(value)) {
        return null;
      }
      else {
        return new BigDecimal(value);
      }
    }
  }

  private static class StringToBooleanConverter extends AbstractOneWayConverter<String, Boolean> {
    public StringToBooleanConverter() {
      super(String.class, Boolean.class);
    }

    @Override
    public Boolean convert(final String value) {
      if (isEmpty(value)) {
        return null;
      }
      else {
        return Boolean.parseBoolean(value);
      }
    }
  }

  private static final OneWayConverter<String, Integer> STRING_TO_INT = new StringToIntegerConverter();
  private static final OneWayConverter<String, Long> STRING_TO_LONG = new StringToLongConverter();
  private static final OneWayConverter<String, Float> STRING_TO_FLOAT = new StringToFloatConverter();
  private static final OneWayConverter<String, Double> STRING_TO_DOUBLE = new StringToDoubleConverter();
  private static final OneWayConverter<String, BigInteger> STRING_TO_BIG_INTEGER = new StringToBigIntegerConverter();
  private static final OneWayConverter<String, BigDecimal> STRING_TO_BIG_DECIMAL = new StringToBigDecimalConverter();
  private static final OneWayConverter<String, Boolean> STRING_TO_BOOLEAN = new StringToBooleanConverter();

  private static final Map<Class<?>, Class<?>> boxedTypesByPrimitive = new HashMap<>();

  static {
    boxedTypesByPrimitive.put(boolean.class, Boolean.class);
    boxedTypesByPrimitive.put(short.class, Short.class);
    boxedTypesByPrimitive.put(int.class, Integer.class);
    boxedTypesByPrimitive.put(long.class, Long.class);
    boxedTypesByPrimitive.put(float.class, Float.class);
    boxedTypesByPrimitive.put(double.class, Double.class);
    boxedTypesByPrimitive.put(byte.class, Byte.class);
    boxedTypesByPrimitive.put(char.class, Character.class);
  }

  @SuppressWarnings("rawtypes")
  private static final Map<ConverterRegistrationKey, Converter> defaultConverters =
      new HashMap<ConverterRegistrationKey, Converter>();

  /**
   * Lookup a default converter.
   *
   * @return A {@link Converter} between the given types, or else {@code null} if no such default converter exists.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static <M, W> Converter<M, W> getConverter(final Class<M> modelValueType, final Class<W> componentValueType) {
    Converter converter = defaultConverters.get(new ConverterRegistrationKey(modelValueType, componentValueType));
    if (converter == null) {
      converter = maybeCreateBuiltinConverter(maybeBoxPrimitive(modelValueType), maybeBoxPrimitive(componentValueType));
    }

    return converter;
  }

  private static boolean isEmpty(final String value) {
    return value == null || value.equals("");
  }

  private static <M, C> Converter<M, C> maybeCreateBuiltinConverter(final Class<M> modelValueType, final Class<C> widgetValueType) {
    Assert.notNull(modelValueType);
    Assert.notNull(widgetValueType);

    final OneWayConverter<M, C> modelToWidget = getOneWayConverter(modelValueType, widgetValueType);
    final OneWayConverter<C, M> widgetToModel = getOneWayConverter(widgetValueType, modelValueType);

    if (modelToWidget == null || widgetToModel == null) {
      return null;
    }
    else {
      return TwoWayConverter.createConverter(modelToWidget, widgetToModel);
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static <D, T> OneWayConverter<D, T> getOneWayConverter(final Class<D> domainType, final Class<T> targetType) {
    if (domainType.equals(targetType)) {
      return new IdentityConverter(domainType);
    }
    else if (targetType.equals(String.class)) {
      return new AnyToStringConverter(domainType);
    }
    else if (domainType.equals(String.class)) {
      if (targetType.equals(Integer.class)) {
        return (OneWayConverter<D, T>) STRING_TO_INT;
      }
      else if (targetType.equals(Long.class)) {
        return (OneWayConverter<D, T>) STRING_TO_LONG;
      }
      else if (targetType.equals(Float.class)) {
        return (OneWayConverter<D, T>) STRING_TO_FLOAT;
      }
      else if (targetType.equals(Double.class)) {
        return (OneWayConverter<D, T>) STRING_TO_DOUBLE;
      }
      else if (targetType.equals(BigInteger.class)) {
        return (OneWayConverter<D, T>) STRING_TO_BIG_INTEGER;
      }
      else if (targetType.equals(BigDecimal.class)) {
        return (OneWayConverter<D, T>) STRING_TO_BIG_DECIMAL;
      }
      else if (targetType.equals(Boolean.class)) {
        return (OneWayConverter<D, T>) STRING_TO_BOOLEAN;
      }
      else {
        return null;
      }
    }

    return new IdentityConverter(targetType);
  }

  private static Class<?> maybeBoxPrimitive(final Class<?> type) {
    if (type.isPrimitive()) {
      return Assert.notNull("Unrecognized primitive " + type.getName(), boxedTypesByPrimitive.get(type));
    }
    else {
      return type;
    }
  }

  /**
   * Return an converter that does not modify model or component values.
   */
  public static <T> Converter<T, T> identityConverter(final Class<T> type) {
    final IdentityConverter<T> idOneWay = new IdentityConverter<>(type);

    return TwoWayConverter.createConverter(idOneWay, idOneWay);
  }

  /**
   * Registers a {@link Converter} as a default for the provided model and widget types. The default converter will be
   * used in case no custom converter is provided when binding a model to a widget.
   *
   * @param <M>
   *          The type of the model value (field type of the model)
   * @param <W>
   *          The type of the widget value (e.g. String for a {@link TextBox} (=HasValue&lt;String&gt;) or Boolean for a
   *          {@link Checkbox} (=HasValue&lt;Boolean&gt;)))
   * @param modelValueType
   *          The model type the provided converter converts to, must not be null.
   * @param widgetValueType
   *          The widget type the provided converter converts to, must not be null.
   * @param converter
   *          The converter to register as a default for the provided model and widget types.
   */
  public static <M, W> void registerDefaultConverter(final Class<M> modelValueType, final Class<W> widgetValueType,
      final Converter<M, W> converter) {
    Assert.notNull(modelValueType);
    Assert.notNull(widgetValueType);
    defaultConverters.put(new ConverterRegistrationKey(modelValueType, widgetValueType), converter);
  }

  /**
   * Deletes all registrations of default converters.
   */
  public static void deregisterDefaultConverters() {
    defaultConverters.clear();
  }

  @SuppressWarnings("rawtypes")
  public static Class inferWidgetValueType(final Widget widget, final Class<?> defaultWidgetValueType) {
    Class widgetValueType = null;

    if (widget instanceof ElementWrapperWidget) {
      widgetValueType = ((ElementWrapperWidget<?>) widget).getValueType();
    }
    else if (widget instanceof TakesValue) {
      Object value = ((TakesValue) widget).getValue();
      if (value != null) {
        if (value instanceof BindableProxy) {
          value = ((BindableProxy) value).unwrap();
        }
        widgetValueType = value.getClass();
      }
      else if (widget instanceof TextBoxBase) {
        widgetValueType = String.class;
      }
      else if (widget instanceof DateBox || widget instanceof DatePicker) {
        widgetValueType = Date.class;
      }
      else if (widget instanceof CheckBox || widget instanceof ToggleButton) {
        widgetValueType = Boolean.class;
      }
      else if (widget instanceof LongBox) {
        widgetValueType = Long.class;
      }
      else if (widget instanceof DoubleBox) {
        widgetValueType = Double.class;
      }
      else if (widget instanceof IntegerBox) {
        widgetValueType = Integer.class;
      }
      else {
        widgetValueType = defaultWidgetValueType;
      }
    }
    else if (widget instanceof HasText) {
        widgetValueType = String.class;
    }
    else {
      widgetValueType = String.class;
    }

    return widgetValueType;
  }
}
