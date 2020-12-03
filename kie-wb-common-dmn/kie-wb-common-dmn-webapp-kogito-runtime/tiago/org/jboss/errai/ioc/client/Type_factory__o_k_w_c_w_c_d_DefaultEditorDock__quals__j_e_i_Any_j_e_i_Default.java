package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.client.docks.EditorDock;

public class Type_factory__o_k_w_c_w_c_d_DefaultEditorDock__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultEditorDock> { public Type_factory__o_k_w_c_w_c_d_DefaultEditorDock__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultEditorDock.class, "Type_factory__o_k_w_c_w_c_d_DefaultEditorDock__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultEditorDock.class, Object.class });
  }

  public DefaultEditorDock createInstance(final ContextManager contextManager) {
    final Instance<EditorDock> _dock_0 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { EditorDock.class }, new Annotation[] { });
    final DefaultEditorDock instance = new DefaultEditorDock(_dock_0);
    registerDependentScopedReference(instance, _dock_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}