package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.guvnor.common.services.project.client.GAVEditor;
import org.guvnor.common.services.project.client.GAVEditorView;
import org.guvnor.common.services.project.client.GAVEditorView.Presenter;
import org.guvnor.common.services.project.client.GAVEditorViewImpl;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_g_c_s_p_c_GAVEditor__quals__j_e_i_Any_j_e_i_Default extends Factory<GAVEditor> { public Type_factory__o_g_c_s_p_c_GAVEditor__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GAVEditor.class, "Type_factory__o_g_c_s_p_c_GAVEditor__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GAVEditor.class, Object.class, Presenter.class, IsWidget.class });
  }

  public GAVEditor createInstance(final ContextManager contextManager) {
    final GAVEditorView _view_0 = (GAVEditorViewImpl) contextManager.getInstance("Type_factory__o_g_c_s_p_c_GAVEditorViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final GAVEditor instance = new GAVEditor(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}