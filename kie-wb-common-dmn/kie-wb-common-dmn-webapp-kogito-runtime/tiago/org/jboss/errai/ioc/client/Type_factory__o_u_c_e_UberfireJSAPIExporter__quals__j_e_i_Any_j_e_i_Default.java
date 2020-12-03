package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.exporter.UberfireJSAPIExporter;
import org.uberfire.workbench.events.UberfireJSAPIReadyEvent;

public class Type_factory__o_u_c_e_UberfireJSAPIExporter__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireJSAPIExporter> { public Type_factory__o_u_c_e_UberfireJSAPIExporter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UberfireJSAPIExporter.class, "Type_factory__o_u_c_e_UberfireJSAPIExporter__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { UberfireJSAPIExporter.class, Object.class });
  }

  public UberfireJSAPIExporter createInstance(final ContextManager contextManager) {
    final UberfireJSAPIExporter instance = new UberfireJSAPIExporter();
    setIncompleteInstance(instance);
    final Event UberfireJSAPIExporter_jsapiReadyEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { UberfireJSAPIReadyEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, UberfireJSAPIExporter_jsapiReadyEvent);
    UberfireJSAPIExporter_Event_jsapiReadyEvent(instance, UberfireJSAPIExporter_jsapiReadyEvent);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final UberfireJSAPIExporter instance) {
    instance.export();
  }

  native static Event UberfireJSAPIExporter_Event_jsapiReadyEvent(UberfireJSAPIExporter instance) /*-{
    return instance.@org.uberfire.client.exporter.UberfireJSAPIExporter::jsapiReadyEvent;
  }-*/;

  native static void UberfireJSAPIExporter_Event_jsapiReadyEvent(UberfireJSAPIExporter instance, Event<UberfireJSAPIReadyEvent> value) /*-{
    instance.@org.uberfire.client.exporter.UberfireJSAPIExporter::jsapiReadyEvent = value;
  }-*/;
}