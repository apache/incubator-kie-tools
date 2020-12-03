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
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView;

public class Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultFileEditorView> { public Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultFileEditorView.class, "Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultFileEditorView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class });
  }

  public DefaultFileEditorView createInstance(final ContextManager contextManager) {
    final DefaultFileEditorView instance = new DefaultFileEditorView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DefaultFileEditorView instance) {
    instance.init();
  }
}