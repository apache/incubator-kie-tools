package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.widgets.client.widget.KSessionSelector;
import org.kie.workbench.common.widgets.client.widget.KSessionSelectorView;
import org.kie.workbench.common.widgets.client.widget.KSessionSelectorViewImpl;

public class Type_factory__o_k_w_c_w_c_w_KSessionSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<KSessionSelector> { public Type_factory__o_k_w_c_w_c_w_KSessionSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KSessionSelector.class, "Type_factory__o_k_w_c_w_c_w_KSessionSelector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KSessionSelector.class, Object.class, IsWidget.class });
  }

  public KSessionSelector createInstance(final ContextManager contextManager) {
    final Caller<KModuleService> _kModuleService_2 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { KModuleService.class }, new Annotation[] { });
    final KSessionSelectorView _view_0 = (KSessionSelectorViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final Caller<KieModuleService> _moduleService_1 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { KieModuleService.class }, new Annotation[] { });
    final KSessionSelector instance = new KSessionSelector(_view_0, _moduleService_1, _kModuleService_2);
    registerDependentScopedReference(instance, _kModuleService_2);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _moduleService_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}