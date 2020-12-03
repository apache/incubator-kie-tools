package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorPresenter.View;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;

public class Type_factory__o_u_e_w_c_c_e_t_TextEditorView__quals__j_e_i_Any_j_e_i_Default extends Factory<TextEditorView> { public Type_factory__o_u_e_w_c_c_e_t_TextEditorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TextEditorView.class, "Type_factory__o_u_e_w_c_c_e_t_TextEditorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TextEditorView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, RequiresResize.class, View.class });
  }

  public TextEditorView createInstance(final ContextManager contextManager) {
    final TextEditorView instance = new TextEditorView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final TextEditorView instance) {
    instance.init();
  }
}