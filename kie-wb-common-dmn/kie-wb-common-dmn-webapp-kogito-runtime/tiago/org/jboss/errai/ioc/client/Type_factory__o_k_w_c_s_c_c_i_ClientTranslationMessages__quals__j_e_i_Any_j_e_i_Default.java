package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;

public class Type_factory__o_k_w_c_s_c_c_i_ClientTranslationMessages__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientTranslationMessages> { private class Type_factory__o_k_w_c_s_c_c_i_ClientTranslationMessages__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientTranslationMessages implements Proxy<ClientTranslationMessages> {
    private final ProxyHelper<ClientTranslationMessages> proxyHelper = new ProxyHelperImpl<ClientTranslationMessages>("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationMessages__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_c_i_ClientTranslationMessages__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final ClientTranslationMessages instance) {

    }

    public ClientTranslationMessages asBeanType() {
      return this;
    }

    public void setInstance(final ClientTranslationMessages instance) {
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

    @Override public String getCanvasValidationsErrorMessage(String key, Iterable result) {
      if (proxyHelper != null) {
        final ClientTranslationMessages proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getCanvasValidationsErrorMessage(key, result);
        return retVal;
      } else {
        return super.getCanvasValidationsErrorMessage(key, result);
      }
    }

    @Override public String getCanvasCommandValidationsErrorMessage(Iterable result) {
      if (proxyHelper != null) {
        final ClientTranslationMessages proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getCanvasCommandValidationsErrorMessage(result);
        return retVal;
      } else {
        return super.getCanvasCommandValidationsErrorMessage(result);
      }
    }

    @Override public String getRuleValidationMessage(RuleViolation violation) {
      if (proxyHelper != null) {
        final ClientTranslationMessages proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getRuleValidationMessage(violation);
        return retVal;
      } else {
        return super.getRuleValidationMessage(violation);
      }
    }

    @Override public String getBeanValidationMessage(ModelBeanViolation violation) {
      if (proxyHelper != null) {
        final ClientTranslationMessages proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getBeanValidationMessage(violation);
        return retVal;
      } else {
        return super.getBeanValidationMessage(violation);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientTranslationMessages proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_i_ClientTranslationMessages__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientTranslationMessages.class, "Type_factory__o_k_w_c_s_c_c_i_ClientTranslationMessages__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientTranslationMessages.class, CoreTranslationMessages.class, Object.class });
  }

  public ClientTranslationMessages createInstance(final ContextManager contextManager) {
    final StunnerTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ClientTranslationMessages instance = new ClientTranslationMessages(_translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_c_i_ClientTranslationMessages__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages ([org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientTranslationMessages> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}