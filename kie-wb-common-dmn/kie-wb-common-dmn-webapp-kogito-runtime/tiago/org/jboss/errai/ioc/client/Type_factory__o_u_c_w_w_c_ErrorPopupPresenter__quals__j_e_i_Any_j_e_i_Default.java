package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.views.pfly.modal.ErrorPopupView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter.View;
import org.uberfire.mvp.Command;

public class Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ErrorPopupPresenter> { private class Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ErrorPopupPresenter implements Proxy<ErrorPopupPresenter> {
    private final ProxyHelper<ErrorPopupPresenter> proxyHelper = new ProxyHelperImpl<ErrorPopupPresenter>("Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final ErrorPopupPresenter instance) {

    }

    public ErrorPopupPresenter asBeanType() {
      return this;
    }

    public void setInstance(final ErrorPopupPresenter instance) {
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

    @Override public void showMessage(String msg, Command afterShow, Command afterClose) {
      if (proxyHelper != null) {
        final ErrorPopupPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showMessage(msg, afterShow, afterClose);
      } else {
        super.showMessage(msg, afterShow, afterClose);
      }
    }

    @Override public void showMessage(String msg) {
      if (proxyHelper != null) {
        final ErrorPopupPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showMessage(msg);
      } else {
        super.showMessage(msg);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ErrorPopupPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ErrorPopupPresenter.class, "Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ErrorPopupPresenter.class, Object.class });
  }

  public ErrorPopupPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (ErrorPopupView) contextManager.getInstance("Type_factory__o_u_c_v_p_m_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default");
    final ErrorPopupPresenter instance = new ErrorPopupPresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter ([org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter$View])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ErrorPopupPresenter> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}