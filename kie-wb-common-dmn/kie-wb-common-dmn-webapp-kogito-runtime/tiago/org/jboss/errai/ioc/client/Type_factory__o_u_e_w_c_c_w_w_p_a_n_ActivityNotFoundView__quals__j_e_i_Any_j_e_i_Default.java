package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasOneWidget;
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
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundPresenter.View;
import org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView;

public class Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityNotFoundView> { public Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ActivityNotFoundView.class, "Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ActivityNotFoundView.class, SimplePanel.class, Panel.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, ForIsWidget.class, HasWidgets.class, Iterable.class, HasOneWidget.class, AcceptsOneWidget.class, View.class, UberView.class, HasPresenter.class });
  }

  public ActivityNotFoundView createInstance(final ContextManager contextManager) {
    final ActivityNotFoundView instance = new ActivityNotFoundView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ActivityNotFoundView instance) {
    instance.init();
  }
}