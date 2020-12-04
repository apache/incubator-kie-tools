package org.jboss.errai.cdi;

import java.util.function.Function;
import javax.inject.Named;
import org.jboss.errai.common.client.util.AnnotationPropertyAccessorBuilder;
import org.jboss.errai.common.client.util.SharedAnnotationSerializer;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.enterprise.client.cdi.EventQualifierSerializer;
import org.jboss.errai.ioc.client.api.ReplyTo;
import org.jboss.errai.ioc.client.api.ToSubject;
import org.jboss.errai.ui.client.widget.Table;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.annotations.Priority;
import org.uberfire.ext.preferences.client.annotations.PreferenceForm;
import org.uberfire.preferences.client.annotations.ComponentKey;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;

public class EventQualifierSerializerImpl extends EventQualifierSerializer { public EventQualifierSerializerImpl() {
    serializers.put("org.uberfire.client.workbench.annotations.AssociatedResources", AnnotationPropertyAccessorBuilder.create().with("value", new Function() {
      public Object apply(final Object anno) {
        return SharedAnnotationSerializer.stringify(((AssociatedResources) anno).value());
      }
    }).build());
    serializers.put("javax.inject.Named", AnnotationPropertyAccessorBuilder.create().with("value", new Function() {
      public Object apply(final Object anno) {
        return SharedAnnotationSerializer.stringify(((Named) anno).value());
      }
    }).build());
    serializers.put("org.uberfire.ext.preferences.client.annotations.PreferenceForm", AnnotationPropertyAccessorBuilder.create().with("value", new Function() {
      public Object apply(final Object anno) {
        return SharedAnnotationSerializer.stringify(((PreferenceForm) anno).value());
      }
    }).build());
    serializers.put("org.jboss.errai.databinding.client.components.ListContainer", AnnotationPropertyAccessorBuilder.create().with("value", new Function() {
      public Object apply(final Object anno) {
        return SharedAnnotationSerializer.stringify(((ListContainer) anno).value());
      }
    }).build());
    serializers.put("org.jboss.errai.ui.client.widget.Table", AnnotationPropertyAccessorBuilder.create().with("root", new Function() {
      public Object apply(final Object anno) {
        return SharedAnnotationSerializer.stringify(((Table) anno).root());
      }
    }).build());
    serializers.put("org.jboss.errai.ioc.client.api.ReplyTo", AnnotationPropertyAccessorBuilder.create().with("value", new Function() {
      public Object apply(final Object anno) {
        return SharedAnnotationSerializer.stringify(((ReplyTo) anno).value());
      }
    }).build());
    serializers.put("org.uberfire.preferences.client.annotations.ComponentKey", AnnotationPropertyAccessorBuilder.create().with("value", new Function() {
      public Object apply(final Object anno) {
        return SharedAnnotationSerializer.stringify(((ComponentKey) anno).value());
      }
    }).build());
    serializers.put("org.uberfire.client.workbench.annotations.Priority", AnnotationPropertyAccessorBuilder.create().with("value", new Function() {
      public Object apply(final Object anno) {
        return SharedAnnotationSerializer.stringify(((Priority) anno).value());
      }
    }).build());
    serializers.put("org.jboss.errai.ioc.client.api.ToSubject", AnnotationPropertyAccessorBuilder.create().with("value", new Function() {
      public Object apply(final Object anno) {
        return SharedAnnotationSerializer.stringify(((ToSubject) anno).value());
      }
    }).build());
    serializers.put("org.uberfire.preferences.shared.annotations.WorkbenchPreference", AnnotationPropertyAccessorBuilder.create().with("identifier", new Function() {
      public Object apply(final Object anno) {
        return SharedAnnotationSerializer.stringify(((WorkbenchPreference) anno).identifier());
      }
    }).with("bundleKey", new Function() {
      public Object apply(final Object anno) {
        return SharedAnnotationSerializer.stringify(((WorkbenchPreference) anno).bundleKey());
      }
    }).build());
  }

}