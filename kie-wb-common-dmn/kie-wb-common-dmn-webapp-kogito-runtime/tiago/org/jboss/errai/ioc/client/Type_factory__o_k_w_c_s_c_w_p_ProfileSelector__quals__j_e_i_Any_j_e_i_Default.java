package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.RequestSessionRefreshEvent;
import org.kie.workbench.common.stunner.client.widgets.profile.ProfileSelector;
import org.kie.workbench.common.stunner.client.widgets.views.Selector;
import org.kie.workbench.common.stunner.client.widgets.views.SelectorDelegate;
import org.kie.workbench.common.stunner.client.widgets.views.SelectorImpl;
import org.kie.workbench.common.stunner.core.api.ProfileManager;
import org.kie.workbench.common.stunner.core.client.api.ClientProfileManager;
import org.kie.workbench.common.stunner.core.profile.Profile;

public class Type_factory__o_k_w_c_s_c_w_p_ProfileSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<ProfileSelector> { public Type_factory__o_k_w_c_s_c_w_p_ProfileSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ProfileSelector.class, "Type_factory__o_k_w_c_s_c_w_p_ProfileSelector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ProfileSelector.class, SelectorDelegate.class, Object.class, Selector.class });
  }

  public ProfileSelector createInstance(final ContextManager contextManager) {
    final SelectorImpl<Profile> _selector_0 = (SelectorImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_v_SelectorImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<RequestSessionRefreshEvent> _requestSessionRefreshEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RequestSessionRefreshEvent.class }, new Annotation[] { });
    final ProfileManager _profileManager_1 = (ClientProfileManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientProfileManager__quals__j_e_i_Any_j_e_i_Default");
    final ProfileSelector instance = new ProfileSelector(_selector_0, _profileManager_1, _requestSessionRefreshEvent_2);
    registerDependentScopedReference(instance, _selector_0);
    registerDependentScopedReference(instance, _requestSessionRefreshEvent_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ProfileSelector instance) {
    instance.init();
  }
}