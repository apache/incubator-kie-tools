package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetPresenter;
import org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetView;
import org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetView.Presenter;
import org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl;

public class Type_factory__o_k_w_c_w_c_d_DiscussionWidgetPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DiscussionWidgetPresenter> { public Type_factory__o_k_w_c_w_c_d_DiscussionWidgetPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiscussionWidgetPresenter.class, "Type_factory__o_k_w_c_w_c_d_DiscussionWidgetPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiscussionWidgetPresenter.class, Object.class, IsWidget.class, Presenter.class });
  }

  public DiscussionWidgetPresenter createInstance(final ContextManager contextManager) {
    final DiscussionWidgetView _view_0 = (DiscussionWidgetViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_w_c_d_DiscussionWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final User _identity_1 = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    final Caller<AppConfigService> _appConfigService_2 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { AppConfigService.class }, new Annotation[] { });
    final DiscussionWidgetPresenter instance = new DiscussionWidgetPresenter(_view_0, _identity_1, _appConfigService_2);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _identity_1);
    registerDependentScopedReference(instance, _appConfigService_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}