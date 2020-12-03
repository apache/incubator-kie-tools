package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.editor.commons.service.ValidationService;

public class Type_factory__o_u_e_e_c_c_v_DefaultFileNameValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultFileNameValidator> { private class Type_factory__o_u_e_e_c_c_v_DefaultFileNameValidator__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefaultFileNameValidator implements Proxy<DefaultFileNameValidator> {
    private final ProxyHelper<DefaultFileNameValidator> proxyHelper = new ProxyHelperImpl<DefaultFileNameValidator>("Type_factory__o_u_e_e_c_c_v_DefaultFileNameValidator__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefaultFileNameValidator instance) {

    }

    public DefaultFileNameValidator asBeanType() {
      return this;
    }

    public void setInstance(final DefaultFileNameValidator instance) {
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

    @Override public void validate(String value, ValidatorCallback callback) {
      if (proxyHelper != null) {
        final DefaultFileNameValidator proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.validate(value, callback);
      } else {
        super.validate(value, callback);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultFileNameValidator proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_e_c_c_v_DefaultFileNameValidator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultFileNameValidator.class, "Type_factory__o_u_e_e_c_c_v_DefaultFileNameValidator__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultFileNameValidator.class, Object.class, Validator.class });
  }

  public DefaultFileNameValidator createInstance(final ContextManager contextManager) {
    final DefaultFileNameValidator instance = new DefaultFileNameValidator();
    setIncompleteInstance(instance);
    final Caller DefaultFileNameValidator_validationService = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { ValidationService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, DefaultFileNameValidator_validationService);
    DefaultFileNameValidator_Caller_validationService(instance, DefaultFileNameValidator_validationService);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultFileNameValidator> proxyImpl = new Type_factory__o_u_e_e_c_c_v_DefaultFileNameValidator__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Caller DefaultFileNameValidator_Caller_validationService(DefaultFileNameValidator instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator::validationService;
  }-*/;

  native static void DefaultFileNameValidator_Caller_validationService(DefaultFileNameValidator instance, Caller<ValidationService> value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator::validationService = value;
  }-*/;
}