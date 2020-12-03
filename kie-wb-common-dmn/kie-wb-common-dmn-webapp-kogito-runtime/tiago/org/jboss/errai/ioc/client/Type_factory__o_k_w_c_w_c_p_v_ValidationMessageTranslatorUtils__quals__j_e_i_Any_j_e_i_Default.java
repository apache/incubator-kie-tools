package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationMessageTranslator;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationMessageTranslatorUtils;

public class Type_factory__o_k_w_c_w_c_p_v_ValidationMessageTranslatorUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<ValidationMessageTranslatorUtils> { private class Type_factory__o_k_w_c_w_c_p_v_ValidationMessageTranslatorUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ValidationMessageTranslatorUtils implements Proxy<ValidationMessageTranslatorUtils> {
    private final ProxyHelper<ValidationMessageTranslatorUtils> proxyHelper = new ProxyHelperImpl<ValidationMessageTranslatorUtils>("Type_factory__o_k_w_c_w_c_p_v_ValidationMessageTranslatorUtils__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ValidationMessageTranslatorUtils instance) {

    }

    public ValidationMessageTranslatorUtils asBeanType() {
      return this;
    }

    public void setInstance(final ValidationMessageTranslatorUtils instance) {
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

    @Override public List translate(List messages) {
      if (proxyHelper != null) {
        final ValidationMessageTranslatorUtils proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.translate(messages);
        return retVal;
      } else {
        return super.translate(messages);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ValidationMessageTranslatorUtils proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_p_v_ValidationMessageTranslatorUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ValidationMessageTranslatorUtils.class, "Type_factory__o_k_w_c_w_c_p_v_ValidationMessageTranslatorUtils__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ValidationMessageTranslatorUtils.class, Object.class });
  }

  public ValidationMessageTranslatorUtils createInstance(final ContextManager contextManager) {
    final Instance<ValidationMessageTranslator> _checkTranslators_0 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { ValidationMessageTranslator.class }, new Annotation[] { });
    final ValidationMessageTranslatorUtils instance = new ValidationMessageTranslatorUtils(_checkTranslators_0);
    registerDependentScopedReference(instance, _checkTranslators_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ValidationMessageTranslatorUtils> proxyImpl = new Type_factory__o_k_w_c_w_c_p_v_ValidationMessageTranslatorUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}