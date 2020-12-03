package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.widgets.client.menu.AboutCommand;
import org.kie.workbench.common.widgets.client.popups.about.AboutPopup;
import org.uberfire.mvp.Command;

public class Type_factory__o_k_w_c_w_c_m_AboutCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<AboutCommand> { private class Type_factory__o_k_w_c_w_c_m_AboutCommand__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends AboutCommand implements Proxy<AboutCommand> {
    private final ProxyHelper<AboutCommand> proxyHelper = new ProxyHelperImpl<AboutCommand>("Type_factory__o_k_w_c_w_c_m_AboutCommand__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final AboutCommand instance) {

    }

    public AboutCommand asBeanType() {
      return this;
    }

    public void setInstance(final AboutCommand instance) {
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

    @Override public void execute() {
      if (proxyHelper != null) {
        final AboutCommand proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.execute();
      } else {
        super.execute();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final AboutCommand proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_m_AboutCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AboutCommand.class, "Type_factory__o_k_w_c_w_c_m_AboutCommand__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AboutCommand.class, Object.class, Command.class });
  }

  public AboutCommand createInstance(final ContextManager contextManager) {
    final AboutPopup _popup_0 = (AboutPopup) contextManager.getInstance("Type_factory__o_k_w_c_w_c_p_a_AboutPopup__quals__j_e_i_Any_j_e_i_Default");
    final AboutCommand instance = new AboutCommand(_popup_0);
    registerDependentScopedReference(instance, _popup_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<AboutCommand> proxyImpl = new Type_factory__o_k_w_c_w_c_m_AboutCommand__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}