package org.jboss.errai.ioc.client;

import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.Validator;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.validation.ClientModelValidator;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.validation.ModelValidator;
import org.kie.workbench.common.stunner.core.validation.impl.AbstractModelBeanValidator;

public class Type_factory__o_k_w_c_s_c_c_v_ClientModelValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientModelValidator> { private class Type_factory__o_k_w_c_s_c_c_v_ClientModelValidator__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientModelValidator implements Proxy<ClientModelValidator> {
    private final ProxyHelper<ClientModelValidator> proxyHelper = new ProxyHelperImpl<ClientModelValidator>("Type_factory__o_k_w_c_s_c_c_v_ClientModelValidator__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientModelValidator instance) {

    }

    public ClientModelValidator asBeanType() {
      return this;
    }

    public void setInstance(final ClientModelValidator instance) {
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

    @Override protected Validator getBeanValidator() {
      if (proxyHelper != null) {
        final ClientModelValidator proxiedInstance = proxyHelper.getInstance(this);
        final Validator retVal = ClientModelValidator_getBeanValidator(proxiedInstance);
        return retVal;
      } else {
        return super.getBeanValidator();
      }
    }

    @Override public void validate(Element element, Consumer callback) {
      if (proxyHelper != null) {
        final ClientModelValidator proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.validate(element, callback);
      } else {
        super.validate(element, callback);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientModelValidator proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_v_ClientModelValidator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientModelValidator.class, "Type_factory__o_k_w_c_s_c_c_v_ClientModelValidator__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientModelValidator.class, AbstractModelBeanValidator.class, Object.class, ModelValidator.class, org.kie.workbench.common.stunner.core.validation.Validator.class });
  }

  public ClientModelValidator createInstance(final ContextManager contextManager) {
    final Validator _beanValidator_0 = (Validator) contextManager.getInstance("Provider_factory__j_v_Validator__quals__j_e_i_Any_j_e_i_Default");
    final ClientModelValidator instance = new ClientModelValidator(_beanValidator_0);
    registerDependentScopedReference(instance, _beanValidator_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientModelValidator> proxyImpl = new Type_factory__o_k_w_c_s_c_c_v_ClientModelValidator__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static Validator ClientModelValidator_getBeanValidator(ClientModelValidator instance) /*-{
    return instance.@org.kie.workbench.common.stunner.core.client.validation.ClientModelValidator::getBeanValidator()();
  }-*/;
}