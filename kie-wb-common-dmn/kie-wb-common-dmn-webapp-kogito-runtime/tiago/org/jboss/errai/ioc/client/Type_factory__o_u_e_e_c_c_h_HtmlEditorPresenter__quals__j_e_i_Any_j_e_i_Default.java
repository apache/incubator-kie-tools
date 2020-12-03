package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorPresenter;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorPresenter.View;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView;

public class Type_factory__o_u_e_e_c_c_h_HtmlEditorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<HtmlEditorPresenter> { public Type_factory__o_u_e_e_c_c_h_HtmlEditorPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(HtmlEditorPresenter.class, "Type_factory__o_u_e_e_c_c_h_HtmlEditorPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { HtmlEditorPresenter.class, Object.class });
  }

  public HtmlEditorPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (HtmlEditorView) contextManager.getInstance("Type_factory__o_u_e_e_c_c_h_HtmlEditorView__quals__j_e_i_Any_j_e_i_Default");
    final HtmlEditorPresenter instance = new HtmlEditorPresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}