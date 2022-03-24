package org.jboss.errai.common.client.util;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

public class AnnotationPropertyAccessorBuilder {
  private final SortedMap<String, Function<Annotation, String>> accessorsByPropertyName = new TreeMap<>();

  private AnnotationPropertyAccessorBuilder() {}

  public static AnnotationPropertyAccessorBuilder create() {
    return new AnnotationPropertyAccessorBuilder();
  }

  public AnnotationPropertyAccessorBuilder with(final String propertyName, final Function<Annotation, String> accessor) {
    accessorsByPropertyName.put(propertyName, accessor);

    return this;
  }

  public AnnotationPropertyAccessor build() {
    return new AnnotationPropertyAccessor(createOrderedPropertyMap());
  }

  private Map<String, Function<Annotation, String>> createOrderedPropertyMap() {
    return (accessorsByPropertyName.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(accessorsByPropertyName));
  }
}