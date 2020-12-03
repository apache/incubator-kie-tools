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
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;
import org.uberfire.client.workbench.pmgr.unanchored.part.UnanchoredWorkbenchPartView;

public class Type_factory__o_u_c_w_p_u_p_UnanchoredWorkbenchPartView__quals__j_e_i_Any_j_e_i_Default extends Factory<UnanchoredWorkbenchPartView> { public Type_factory__o_u_c_w_p_u_p_UnanchoredWorkbenchPartView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UnanchoredWorkbenchPartView.class, "Type_factory__o_u_c_w_p_u_p_UnanchoredWorkbenchPartView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UnanchoredWorkbenchPartView.class, SimplePanel.class, Panel.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, ForIsWidget.class, HasWidgets.class, Iterable.class, HasOneWidget.class, AcceptsOneWidget.class, View.class, UberView.class, HasPresenter.class, RequiresResize.class });
  }

  public UnanchoredWorkbenchPartView createInstance(final ContextManager contextManager) {
    final UnanchoredWorkbenchPartView instance = new UnanchoredWorkbenchPartView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}