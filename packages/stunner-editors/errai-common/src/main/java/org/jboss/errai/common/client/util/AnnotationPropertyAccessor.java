package org.jboss.errai.common.client.util;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.function.Function;

public class AnnotationPropertyAccessor {
  final Map<String, Function<Annotation, String>> accessorsByPropertyName;

  public AnnotationPropertyAccessor(final Map<String, Function<Annotation, String>> accessorsByPropertyName) {
    this.accessorsByPropertyName = accessorsByPropertyName;
  }
}