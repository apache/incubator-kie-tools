package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.categories.Form;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.workbench.category.Category;

public class Type_factory__o_g_c_s_p_c_Form__quals__j_e_i_Any_j_e_i_Default extends Factory<Form> { private class Type_factory__o_g_c_s_p_c_Form__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends Form implements Proxy<Form> {
    private final ProxyHelper<Form> proxyHelper = new ProxyHelperImpl<Form>("Type_factory__o_g_c_s_p_c_Form__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final Form instance) {

    }

    public Form asBeanType() {
      return this;
    }

    public void setInstance(final Form instance) {
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

    @Override public String getName() {
      if (proxyHelper != null) {
        final Form proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getName();
        return retVal;
      } else {
        return super.getName();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final Form proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_g_c_s_p_c_Form__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Form.class, "Type_factory__o_g_c_s_p_c_Form__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Form.class, Category.class, Object.class });
  }

  public Form createInstance(final ContextManager contextManager) {
    final Form instance = new Form();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<Form> proxyImpl = new Type_factory__o_g_c_s_p_c_Form__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}