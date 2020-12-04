package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;

public class Type_factory__o_u_e_e_c_c_m_RestoreVersionCommandProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<RestoreVersionCommandProvider> { public Type_factory__o_u_e_e_c_c_m_RestoreVersionCommandProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RestoreVersionCommandProvider.class, "Type_factory__o_u_e_e_c_c_m_RestoreVersionCommandProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RestoreVersionCommandProvider.class, Object.class });
  }

  public RestoreVersionCommandProvider createInstance(final ContextManager contextManager) {
    final RestoreVersionCommandProvider instance = new RestoreVersionCommandProvider();
    setIncompleteInstance(instance);
    final BusyIndicatorView RestoreVersionCommandProvider_busyIndicatorView = (BusyIndicatorView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_c_BusyIndicatorView__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, RestoreVersionCommandProvider_busyIndicatorView);
    RestoreVersionCommandProvider_BusyIndicatorView_busyIndicatorView(instance, RestoreVersionCommandProvider_busyIndicatorView);
    final Event RestoreVersionCommandProvider_restoreEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RestoreEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, RestoreVersionCommandProvider_restoreEvent);
    RestoreVersionCommandProvider_Event_restoreEvent(instance, RestoreVersionCommandProvider_restoreEvent);
    final SavePopUpPresenter RestoreVersionCommandProvider_savePopUpPresenter = (SavePopUpPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_SavePopUpPresenter__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, RestoreVersionCommandProvider_savePopUpPresenter);
    RestoreVersionCommandProvider_SavePopUpPresenter_savePopUpPresenter(instance, RestoreVersionCommandProvider_savePopUpPresenter);
    final Caller RestoreVersionCommandProvider_versionService = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { VersionService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, RestoreVersionCommandProvider_versionService);
    RestoreVersionCommandProvider_Caller_versionService(instance, RestoreVersionCommandProvider_versionService);
    setIncompleteInstance(null);
    return instance;
  }

  native static Event RestoreVersionCommandProvider_Event_restoreEvent(RestoreVersionCommandProvider instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider::restoreEvent;
  }-*/;

  native static void RestoreVersionCommandProvider_Event_restoreEvent(RestoreVersionCommandProvider instance, Event<RestoreEvent> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider::restoreEvent = value;
  }-*/;

  native static BusyIndicatorView RestoreVersionCommandProvider_BusyIndicatorView_busyIndicatorView(RestoreVersionCommandProvider instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider::busyIndicatorView;
  }-*/;

  native static void RestoreVersionCommandProvider_BusyIndicatorView_busyIndicatorView(RestoreVersionCommandProvider instance, BusyIndicatorView value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider::busyIndicatorView = value;
  }-*/;

  native static Caller RestoreVersionCommandProvider_Caller_versionService(RestoreVersionCommandProvider instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider::versionService;
  }-*/;

  native static void RestoreVersionCommandProvider_Caller_versionService(RestoreVersionCommandProvider instance, Caller<VersionService> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider::versionService = value;
  }-*/;

  native static SavePopUpPresenter RestoreVersionCommandProvider_SavePopUpPresenter_savePopUpPresenter(RestoreVersionCommandProvider instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider::savePopUpPresenter;
  }-*/;

  native static void RestoreVersionCommandProvider_SavePopUpPresenter_savePopUpPresenter(RestoreVersionCommandProvider instance, SavePopUpPresenter value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider::savePopUpPresenter = value;
  }-*/;
}