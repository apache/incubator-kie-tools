package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.forms.client.session.StunnerFormsHandler;

public class Type_factory__o_k_w_c_s_f_c_s_StunnerFormsHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerFormsHandler> { private class Type_factory__o_k_w_c_s_f_c_s_StunnerFormsHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends StunnerFormsHandler implements Proxy<StunnerFormsHandler> {
    private final ProxyHelper<StunnerFormsHandler> proxyHelper = new ProxyHelperImpl<StunnerFormsHandler>("Type_factory__o_k_w_c_s_f_c_s_StunnerFormsHandler__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final StunnerFormsHandler instance) {

    }

    public StunnerFormsHandler asBeanType() {
      return this;
    }

    public void setInstance(final StunnerFormsHandler instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public void refreshCurrentSessionForms() {
      if (proxyHelper != null) {
        final StunnerFormsHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.refreshCurrentSessionForms();
      } else {
        super.refreshCurrentSessionForms();
      }
    }

    @Override public void refreshCurrentSessionForms(Class defSetType) {
      if (proxyHelper != null) {
        final StunnerFormsHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.refreshCurrentSessionForms(defSetType);
      } else {
        super.refreshCurrentSessionForms(defSetType);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final StunnerFormsHandler proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_f_c_s_StunnerFormsHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerFormsHandler.class, "Type_factory__o_k_w_c_s_f_c_s_StunnerFormsHandler__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StunnerFormsHandler.class, Object.class });
  }

  public StunnerFormsHandler createInstance(final ContextManager contextManager) {
    final Event<RefreshFormPropertiesEvent> _refreshFormsEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshFormPropertiesEvent.class }, new Annotation[] { });
    final SessionManager _sessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final StunnerFormsHandler instance = new StunnerFormsHandler(_sessionManager_0, _refreshFormsEvent_1);
    registerDependentScopedReference(instance, _refreshFormsEvent_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<StunnerFormsHandler> proxyImpl = new Type_factory__o_k_w_c_s_f_c_s_StunnerFormsHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}