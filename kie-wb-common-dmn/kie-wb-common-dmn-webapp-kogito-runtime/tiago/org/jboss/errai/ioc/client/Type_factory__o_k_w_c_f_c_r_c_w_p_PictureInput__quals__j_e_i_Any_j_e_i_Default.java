package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HasWidgets.ForIsWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.PictureInput;
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidget;

public class Type_factory__o_k_w_c_f_c_r_c_w_p_PictureInput__quals__j_e_i_Any_j_e_i_Default extends Factory<PictureInput> { public Type_factory__o_k_w_c_f_c_r_c_w_p_PictureInput__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PictureInput.class, "Type_factory__o_k_w_c_f_c_r_c_w_p_PictureInput__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PictureInput.class, SimplePanel.class, Panel.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, ForIsWidget.class, HasWidgets.class, Iterable.class, HasOneWidget.class, AcceptsOneWidget.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class });
  }

  public PictureInput createInstance(final ContextManager contextManager) {
    final PictureWidget _widget_0 = (PictureWidget) contextManager.getInstance("Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidget__quals__j_e_i_Any_j_e_i_Default");
    final PictureInput instance = new PictureInput(_widget_0);
    registerDependentScopedReference(instance, _widget_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final PictureInput instance) {
    PictureInput_init(instance);
  }

  public native static void PictureInput_init(PictureInput instance) /*-{
    instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.PictureInput::init()();
  }-*/;
}