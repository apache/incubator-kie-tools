/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package jakarta.json.bind;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;

/**
 * <a id="supportedProps"></a> <b>Supported Properties</b><br>
 *
 * <blockquote>
 *
 * <p>All JSON Binding providers are required to support the following set of properties. Some
 * providers may support additional properties.
 *
 * <dl>
 *   <dt><code>jsonb.to.json.formatted</code> - java.lang.Boolean
 *   <dd>Controls whether or not the {@link jakarta.json.bind.Jsonb Jsonb} {@code toJson()} methods
 *       will format the resulting JSON data with line breaks and indentation. A true value for this
 *       property indicates human readable indented data, while a false value indicates unformatted
 *       data. Default value is false (unformatted) if this property is not specified.
 * </dl>
 *
 * <dl>
 *   <dt><code>jsonb.to.json.encoding</code> - java.lang.String
 *   <dd>The {@link jakarta.json.bind.Jsonb Jsonb} serialization {@code toJson()} methods will
 *       default to this property for encoding of output JSON data. Default value is 'UTF-8' if this
 *       property is not specified.
 * </dl>
 *
 * <dl>
 *   <dt><code>jsonb.from.json.encoding</code> - java.lang.String
 *   <dd>The {@link jakarta.json.bind.Jsonb Jsonb} deserialization {@code fromJson()} methods will
 *       default to this property encoding of input JSON data if the encoding cannot be detected.
 * </dl>
 *
 * </blockquote>
 *
 * This object is not thread safe. Implementations are expected to make a defensive copy of the
 * object before applying the configuration.
 *
 * @since JSON Binding 1.0
 */
public class JsonbConfig {

  private final Map<String, Object> configuration = new HashMap<>();

  /**
   * Property used to specify whether or not the serialized JSON data is formatted with line feeds
   * and indentation.
   */
  public static final String FORMATTING = "jsonb.formatting";

  /**
   * The Jsonb serialization {@code toJson()} methods will default to this property for encoding of
   * output JSON data. Default value is 'UTF-8'.
   *
   * <p>The Jsonb deserialization {@code fromJson()} methods will default to this property encoding
   * of input JSON data if the encoding cannot be detected automatically.
   */
  public static final String ENCODING = "jsonb.encoding";

  /** Property used to specify custom naming strategy. */
  public static final String PROPERTY_NAMING_STRATEGY = "jsonb.property-naming-strategy";

  /** Property used to specify custom order strategy. */
  public static final String PROPERTY_ORDER_STRATEGY = "jsonb.property-order-strategy";

  /** Property used to specify null values serialization behavior. */
  public static final String NULL_VALUES = "jsonb.null-values";

  /** Property used to specify strict I-JSON serialization compliance. */
  public static final String STRICT_IJSON = "jsonb.strict-ijson";

  /** Property used to specify custom visibility strategy. */
  public static final String PROPERTY_VISIBILITY_STRATEGY = "jsonb.property-visibility-strategy";

  /** Property used to specify custom mapping adapters for generic types. */
  public static final String ADAPTERS = "jsonb.adapters";

  /** Property used to specify custom serializers. */
  public static final String SERIALIZERS = "jsonb.serializers";

  /** Property used to specify custom deserializers. */
  public static final String DESERIALIZERS = "jsonb.derializers";

  /** Property used to specify custom binary data strategy. */
  public static final String BINARY_DATA_STRATEGY = "jsonb.binary-data-strategy";

  /** Property used to specify custom date format globally. */
  public static final String DATE_FORMAT = "jsonb.date-format";

  /** Property used to specify locale globally. */
  public static final String LOCALE = "jsonb.locale";

  /** Property used to specify required creator parameters. */
  public static final String CREATOR_PARAMETERS_REQUIRED = "jsonb.creator-parameters-required";

  /**
   * Set the particular configuration property to a new value. The method can only be used to set
   * one of the standard JSON Binding properties defined in this class or a provider specific
   * property.
   *
   * @param name The name of the property to be set. This value can either be specified using one of
   *     the constant fields or a user supplied string.
   * @param value The value of the property to be set
   * @return This JsonbConfig instance.
   * @throws NullPointerException if the name parameter is null.
   */
  public final JsonbConfig setProperty(final String name, final Object value) {
    configuration.put(name, value);
    return this;
  }

  /**
   * Return value of particular configuration property. The method can only be used to retrieve one
   * of the standard JSON Binding properties defined in this class or a provider specific property.
   * Attempting to get an undefined property will result in an empty Optional value. See <a
   * href="#supportedProps">Supported Properties</a>.
   *
   * @param name The name of the property to retrieve
   * @return The value of the requested property
   * @throws NullPointerException if the name parameter is null.
   */
  public final Optional<Object> getProperty(final String name) {
    return Optional.ofNullable(configuration.get(name));
  }

  /**
   * Return all configuration properties as an unmodifiable map.
   *
   * @return All configuration properties as an unmodifiable map
   */
  public final Map<String, Object> getAsMap() {
    return Collections.unmodifiableMap(configuration);
  }

  /**
   * Property used to specify whether or not the serialized JSON data is formatted with linefeeds
   * and indentation.
   *
   * <p>Configures value of {@link #FORMATTING} property.
   *
   * @param formatted True means serialized data is formatted, false (default) means no formatting.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withFormatting(final Boolean formatted) {
    return setProperty(FORMATTING, formatted);
  }

  /**
   * Property used to specify whether null values should be serialized to JSON document or skipped.
   *
   * <p>Configures value of {@link #NULL_VALUES} property.
   *
   * @param serializeNullValues True means that null values will be serialized into JSON document,
   *     otherwise they will be effectively skipped.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withNullValues(final Boolean serializeNullValues) {
    return setProperty(NULL_VALUES, serializeNullValues);
  }

  /**
   * The binding operations will default to this property for encoding of JSON data. For input data
   * (fromJson), selected encoding is used if the encoding cannot be detected automatically. Default
   * value is 'UTF-8'.
   *
   * <p>Configures value of {@link #ENCODING} property.
   *
   * @param encoding Valid character encoding as defined in the <a
   *     href="http://tools.ietf.org/html/rfc7159">RFC 7159</a> and supported by Java Platform.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withEncoding(final String encoding) {
    return setProperty(ENCODING, encoding);
  }

  /**
   * Property used to specify whether strict I-JSON serialization compliance should be enforced.
   *
   * <p>Configures value of {@link #STRICT_IJSON} property.
   *
   * @param enabled True means data is serialized in strict compliance according to RFC 7493.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withStrictIJSON(final Boolean enabled) {
    return setProperty(STRICT_IJSON, enabled);
  }

  /**
   * Property used to specify custom naming strategy.
   *
   * <p>Configures value of {@link #PROPERTY_NAMING_STRATEGY} property.
   *
   * @param propertyNamingStrategy Custom naming strategy which affects serialization and
   *     deserialization.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withPropertyNamingStrategy(
      final PropertyNamingStrategy propertyNamingStrategy) {
    return setProperty(PROPERTY_NAMING_STRATEGY, propertyNamingStrategy);
  }

  /**
   * Property used to specify custom naming strategy.
   *
   * <p>Configures value of {@link #PROPERTY_NAMING_STRATEGY} property.
   *
   * @param propertyNamingStrategy Predefined naming strategy which affects serialization and
   *     deserialization.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withPropertyNamingStrategy(final String propertyNamingStrategy) {
    return setProperty(PROPERTY_NAMING_STRATEGY, propertyNamingStrategy);
  }

  /**
   * Property used to specify property order strategy.
   *
   * <p>Configures values of {@link #PROPERTY_ORDER_STRATEGY} property.
   *
   * @param propertyOrderStrategy Predefined property order strategy which affects serialization.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withPropertyOrderStrategy(final String propertyOrderStrategy) {
    return setProperty(PROPERTY_ORDER_STRATEGY, propertyOrderStrategy);
  }

  /**
   * Property used to specify custom property visibility strategy.
   *
   * <p>Configures value of {@link #PROPERTY_VISIBILITY_STRATEGY} property.
   *
   * @param propertyVisibilityStrategy Custom property visibility strategy which affects
   *     serialization and deserialization.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withPropertyVisibilityStrategy(
      final PropertyVisibilityStrategy propertyVisibilityStrategy) {
    return setProperty(PROPERTY_VISIBILITY_STRATEGY, propertyVisibilityStrategy);
  }

  /**
   * Property used to specify custom mapping adapters.
   *
   * <p>Configures value of {@link #ADAPTERS} property.
   *
   * <p>Calling withAdapters more than once will merge the adapters with previous value.
   *
   * @param adapters Custom mapping adapters which affects serialization and deserialization.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withAdapters(final JsonbAdapter... adapters) {
    mergeProperties(ADAPTERS, adapters, JsonbAdapter.class);
    return this;
  }

  /**
   * Property used to specify custom serializers.
   *
   * <p>Configures value of {@link #SERIALIZERS} property.
   *
   * <p>Calling withSerializers more than once will merge the serializers with previous value.
   *
   * @param serializers Custom serializers which affects serialization.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withSerializers(final JsonbSerializer... serializers) {
    mergeProperties(SERIALIZERS, serializers, JsonbSerializer.class);
    return this;
  }

  /**
   * Property used to specify custom deserializers.
   *
   * <p>Configures value of {@link #DESERIALIZERS} property.
   *
   * <p>Calling withDeserializers more than once will merge the deserializers with previous value.
   *
   * @param deserializers Custom deserializers which affects deserialization.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withDeserializers(final JsonbDeserializer... deserializers) {
    mergeProperties(DESERIALIZERS, deserializers, JsonbDeserializer.class);
    return this;
  }

  /**
   * Property used to specify custom binary data strategy.
   *
   * <p>Configures value of {@link #BINARY_DATA_STRATEGY} property.
   *
   * @param binaryDataStrategy Custom binary data strategy which affects serialization and
   *     deserialization.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withBinaryDataStrategy(final String binaryDataStrategy) {
    return setProperty(BINARY_DATA_STRATEGY, binaryDataStrategy);
  }

  /**
   * Property used to specify custom date format. This format will be used by default for all date
   * classes serialization and deserialization.
   *
   * <p>Configures values of {@link #DATE_FORMAT} and {@link #LOCALE} properties.
   *
   * @param dateFormat Custom date format as specified in {@link
   *     java.time.format.DateTimeFormatter}.
   * @param locale Locale, if null is specified {@link Locale#getDefault} will be used.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withDateFormat(final String dateFormat, final Locale locale) {
    return setProperty(DATE_FORMAT, dateFormat)
        .setProperty(LOCALE, locale != null ? locale : Locale.getDefault());
  }

  /**
   * Property used to specify custom locale.
   *
   * <p>Configures value of {@link #LOCALE} property.
   *
   * @param locale Locale, must not be null.
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withLocale(final Locale locale) {
    return setProperty(LOCALE, locale);
  }

  /**
   * Property used to specify whether all creator parameters should be treated as required. <br>
   * Default value is {@code false}.
   *
   * @param requiredParameters Whether creator parameters are required
   * @return This JsonbConfig instance.
   */
  public final JsonbConfig withCreatorParametersRequired(final boolean requiredParameters) {
    return setProperty(CREATOR_PARAMETERS_REQUIRED, requiredParameters);
  }

  @SuppressWarnings("unchecked")
  private <T> void mergeProperties(
      final String propertyKey, final T[] values, final Class<T> tClass) {
    throw new JsonbException("Not implemented");
  }
}
