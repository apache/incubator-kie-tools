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
import org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetView;
import org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl;

public class Type_factory__o_k_w_c_w_c_d_DiscussionWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiscussionWidgetViewImpl> { public Type_factory__o_k_w_c_w_c_d_DiscussionWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiscussionWidgetViewImpl.class, "Type_factory__o_k_w_c_w_c_d_DiscussionWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiscussionWidgetViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, DiscussionWidgetView.class });
  }

  public DiscussionWidgetViewImpl createInstance(final ContextManager contextManager) {
    final DiscussionWidgetViewImpl instance = new DiscussionWidgetViewImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}