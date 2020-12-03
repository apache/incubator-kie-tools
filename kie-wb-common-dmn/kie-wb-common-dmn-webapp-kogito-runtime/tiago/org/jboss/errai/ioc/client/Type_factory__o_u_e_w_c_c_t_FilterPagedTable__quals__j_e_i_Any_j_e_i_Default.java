package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;

public class Type_factory__o_u_e_w_c_c_t_FilterPagedTable__quals__j_e_i_Any_j_e_i_Default extends Factory<FilterPagedTable> { public Type_factory__o_u_e_w_c_c_t_FilterPagedTable__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FilterPagedTable.class, "Type_factory__o_u_e_w_c_c_t_FilterPagedTable__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FilterPagedTable.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class });
  }

  public FilterPagedTable createInstance(final ContextManager contextManager) {
    final FilterPagedTable instance = new FilterPagedTable();
    setIncompleteInstance(instance);
    final Caller FilterPagedTable_preferencesService = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { UserPreferencesService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, FilterPagedTable_preferencesService);
    FilterPagedTable_Caller_preferencesService(instance, FilterPagedTable_preferencesService);
    setIncompleteInstance(null);
    return instance;
  }

  native static Caller FilterPagedTable_Caller_preferencesService(FilterPagedTable instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.tables.FilterPagedTable::preferencesService;
  }-*/;

  native static void FilterPagedTable_Caller_preferencesService(FilterPagedTable instance, Caller<UserPreferencesService> value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.tables.FilterPagedTable::preferencesService = value;
  }-*/;
}