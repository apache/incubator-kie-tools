package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.AbstractMenuDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.ChangeProfileDevCommand;
import org.kie.workbench.common.stunner.client.widgets.profile.ProfileSelector;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.uberfire.client.views.pfly.modal.Bs3Modal;

public class Type_factory__o_k_w_c_s_c_w_m_d_i_ChangeProfileDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ChangeProfileDevCommand> { public Type_factory__o_k_w_c_s_c_w_m_d_i_ChangeProfileDevCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ChangeProfileDevCommand.class, "Type_factory__o_k_w_c_s_c_w_m_d_i_ChangeProfileDevCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ChangeProfileDevCommand.class, AbstractMenuDevCommand.class, Object.class, MenuDevCommand.class });
  }

  public ChangeProfileDevCommand createInstance(final ContextManager contextManager) {
    final Instance<Bs3Modal> _modalFactory_1 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { Bs3Modal.class }, new Annotation[] { });
    final ProfileSelector _profileSelector_2 = (ProfileSelector) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_ProfileSelector__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _sessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final ChangeProfileDevCommand instance = new ChangeProfileDevCommand(_sessionManager_0, _modalFactory_1, _profileSelector_2);
    registerDependentScopedReference(instance, _modalFactory_1);
    registerDependentScopedReference(instance, _profileSelector_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}