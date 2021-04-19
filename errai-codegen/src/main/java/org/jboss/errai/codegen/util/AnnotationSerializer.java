package org.jboss.errai.codegen.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.errai.common.client.util.SharedAnnotationSerializer;

public class AnnotationSerializer {
  
  private AnnotationSerializer() {};  

  public static String[] serialize(final Iterator<Annotation> qualifier) {
    final List<String> qualifiers = new ArrayList<String>();
    qualifier.forEachRemaining(a -> qualifiers.add(SharedAnnotationSerializer.serialize(a, CDIAnnotationUtils.createDynamicSerializer(a.annotationType()))));
    
    return qualifiers.toArray(new String[qualifiers.size()]);
  }
}
