package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.widgets.popups.PopupUtil;
import org.uberfire.client.views.pfly.widgets.Button.ButtonStyleType;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.client.views.pfly.widgets.InlineNotification.InlineNotificationType;
import org.uberfire.mvp.Command;

public class Type_factory__o_k_w_c_s_c_w_p_PopupUtil__quals__j_e_i_Any_j_e_i_Default extends Factory<PopupUtil> { private class Type_factory__o_k_w_c_s_c_w_p_PopupUtil__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PopupUtil implements Proxy<PopupUtil> {
    private final ProxyHelper<PopupUtil> proxyHelper = new ProxyHelperImpl<PopupUtil>("Type_factory__o_k_w_c_s_c_w_p_PopupUtil__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PopupUtil instance) {

    }

    public PopupUtil asBeanType() {
      return this;
    }

    public void setInstance(final PopupUtil instance) {
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

    @Override public void showConfirmPopup(String title, String okButtonText, String confirmMessage, Command okCommand) {
      if (proxyHelper != null) {
        final PopupUtil proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showConfirmPopup(title, okButtonText, confirmMessage, okCommand);
      } else {
        super.showConfirmPopup(title, okButtonText, confirmMessage, okCommand);
      }
    }

    @Override public void showConfirmPopup(String title, String inlineNotificationMessage, InlineNotificationType inlineNotificationType, String okButtonText, ButtonStyleType okButtonType, String confirmMessage, Command okCommand) {
      if (proxyHelper != null) {
        final PopupUtil proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showConfirmPopup(title, inlineNotificationMessage, inlineNotificationType, okButtonText, okButtonType, confirmMessage, okCommand);
      } else {
        super.showConfirmPopup(title, inlineNotificationMessage, inlineNotificationType, okButtonText, okButtonType, confirmMessage, okCommand);
      }
    }

    @Override public void showYesNoCancelPopup(String title, String message, Command yesCommand, Command noCommand) {
      if (proxyHelper != null) {
        final PopupUtil proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showYesNoCancelPopup(title, message, yesCommand, noCommand);
      } else {
        super.showYesNoCancelPopup(title, message, yesCommand, noCommand);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PopupUtil proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_w_p_PopupUtil__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PopupUtil.class, "Type_factory__o_k_w_c_s_c_w_p_PopupUtil__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PopupUtil.class, Object.class });
  }

  public PopupUtil createInstance(final ContextManager contextManager) {
    final ConfirmPopup _confirmPopup_0 = (ConfirmPopup) contextManager.getInstance("Type_factory__o_u_c_v_p_w_ConfirmPopup__quals__j_e_i_Any_j_e_i_Default");
    final PopupUtil instance = new PopupUtil(_confirmPopup_0);
    registerDependentScopedReference(instance, _confirmPopup_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PopupUtil> proxyImpl = new Type_factory__o_k_w_c_s_c_w_p_PopupUtil__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}