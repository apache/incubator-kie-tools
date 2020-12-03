package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
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
import org.uberfire.ext.widgets.core.client.tree.Tree;

public class ExtensionProvided_factory__o_u_e_w_c_c_t_Tree__quals__j_e_i_Any_j_e_i_Default extends Factory<Tree> { public ExtensionProvided_factory__o_u_e_w_c_c_t_Tree__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Tree.class, "ExtensionProvided_factory__o_u_e_w_c_c_t_Tree__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Tree.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, HasSelectionHandlers.class, HasOpenHandlers.class, HasCloseHandlers.class });
  }

  public Tree createInstance(final ContextManager contextManager) {
    return new Tree();
  }
}