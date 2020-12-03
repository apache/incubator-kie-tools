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
import com.google.gwt.view.client.HasCellPreviewHandlers;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.HasRows;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;
import org.uberfire.ext.widgets.table.client.UberfireSimpleTable;

public class Type_factory__o_u_e_w_c_c_t_SimpleTable__quals__j_e_i_Any_j_e_i_Default extends Factory<SimpleTable> { public Type_factory__o_u_e_w_c_c_t_SimpleTable__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SimpleTable.class, "Type_factory__o_u_e_w_c_c_t_SimpleTable__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SimpleTable.class, UberfireSimpleTable.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, HasData.class, HasRows.class, HasCellPreviewHandlers.class });
  }

  public SimpleTable createInstance(final ContextManager contextManager) {
    final SimpleTable instance = new SimpleTable();
    setIncompleteInstance(instance);
    final Caller SimpleTable_preferencesService = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { UserPreferencesService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, SimpleTable_preferencesService);
    SimpleTable_Caller_preferencesService(instance, SimpleTable_preferencesService);
    setIncompleteInstance(null);
    return instance;
  }

  native static Caller SimpleTable_Caller_preferencesService(SimpleTable instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.tables.SimpleTable::preferencesService;
  }-*/;

  native static void SimpleTable_Caller_preferencesService(SimpleTable instance, Caller<UserPreferencesService> value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.tables.SimpleTable::preferencesService = value;
  }-*/;
}