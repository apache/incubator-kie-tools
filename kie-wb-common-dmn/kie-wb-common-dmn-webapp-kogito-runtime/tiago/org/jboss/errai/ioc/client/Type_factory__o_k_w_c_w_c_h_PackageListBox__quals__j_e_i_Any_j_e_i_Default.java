package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.widgets.client.handlers.PackageListBox;
import org.kie.workbench.common.widgets.client.handlers.PackageListBoxView;
import org.kie.workbench.common.widgets.client.handlers.PackageListBoxViewImpl;

public class Type_factory__o_k_w_c_w_c_h_PackageListBox__quals__j_e_i_Any_j_e_i_Default extends Factory<PackageListBox> { public Type_factory__o_k_w_c_w_c_h_PackageListBox__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PackageListBox.class, "Type_factory__o_k_w_c_w_c_h_PackageListBox__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PackageListBox.class, Object.class, IsElement.class });
  }

  public PackageListBox createInstance(final ContextManager contextManager) {
    final PackageListBoxView _view_0 = (PackageListBoxViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_w_c_h_PackageListBoxViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final Caller<KieModuleService> _moduleService_2 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { KieModuleService.class }, new Annotation[] { });
    final WorkspaceProjectContext _projectContext_1 = (WorkspaceProjectContext) contextManager.getInstance("Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default");
    final PackageListBox instance = new PackageListBox(_view_0, _projectContext_1, _moduleService_2);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _moduleService_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}