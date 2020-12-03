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
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget;

public class Type_factory__o_k_w_c_w_c_h_w_c_ConfigurationComboBoxItemWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<ConfigurationComboBoxItemWidget> { public Type_factory__o_k_w_c_w_c_h_w_c_ConfigurationComboBoxItemWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ConfigurationComboBoxItemWidget.class, "Type_factory__o_k_w_c_w_c_h_w_c_ConfigurationComboBoxItemWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ConfigurationComboBoxItemWidget.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class });
  }

  public ConfigurationComboBoxItemWidget createInstance(final ContextManager contextManager) {
    final ConfigurationComboBoxItemWidget instance = new ConfigurationComboBoxItemWidget();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ConfigurationComboBoxItemWidget instance) {
    ConfigurationComboBoxItemWidget_setup(instance);
  }

  public native static void ConfigurationComboBoxItemWidget_setup(ConfigurationComboBoxItemWidget instance) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget::setup()();
  }-*/;
}