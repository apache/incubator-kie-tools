package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter.View;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpView;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.mvp.ParameterizedCommand;

public class Type_factory__o_u_e_e_c_c_f_p_DeletePopUpPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DeletePopUpPresenter> { private class Type_factory__o_u_e_e_c_c_f_p_DeletePopUpPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DeletePopUpPresenter implements Proxy<DeletePopUpPresenter> {
    private final ProxyHelper<DeletePopUpPresenter> proxyHelper = new ProxyHelperImpl<DeletePopUpPresenter>("Type_factory__o_u_e_e_c_c_f_p_DeletePopUpPresenter__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_e_e_c_c_f_p_DeletePopUpPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final DeletePopUpPresenter instance) {

    }

    public DeletePopUpPresenter asBeanType() {
      return this;
    }

    public void setInstance(final DeletePopUpPresenter instance) {
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

    @Override public void setup() {
      if (proxyHelper != null) {
        final DeletePopUpPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setup();
      } else {
        super.setup();
      }
    }

    @Override public void show(ParameterizedCommand command) {
      if (proxyHelper != null) {
        final DeletePopUpPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(command);
      } else {
        super.show(command);
      }
    }

    @Override public void show(Validator validator, ParameterizedCommand command) {
      if (proxyHelper != null) {
        final DeletePopUpPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(validator, command);
      } else {
        super.show(validator, command);
      }
    }

    @Override public void cancel() {
      if (proxyHelper != null) {
        final DeletePopUpPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.cancel();
      } else {
        super.cancel();
      }
    }

    @Override public ParameterizedCommand getCommand() {
      if (proxyHelper != null) {
        final DeletePopUpPresenter proxiedInstance = proxyHelper.getInstance(this);
        final ParameterizedCommand retVal = proxiedInstance.getCommand();
        return retVal;
      } else {
        return super.getCommand();
      }
    }

    @Override public boolean isOpened() {
      if (proxyHelper != null) {
        final DeletePopUpPresenter proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isOpened();
        return retVal;
      } else {
        return super.isOpened();
      }
    }

    @Override public void delete() {
      if (proxyHelper != null) {
        final DeletePopUpPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.delete();
      } else {
        super.delete();
      }
    }

    @Override public void setPrompt(String prompt) {
      if (proxyHelper != null) {
        final DeletePopUpPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setPrompt(prompt);
      } else {
        super.setPrompt(prompt);
      }
    }

    @Override public void setCommentIsHidden(boolean hidden) {
      if (proxyHelper != null) {
        final DeletePopUpPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setCommentIsHidden(hidden);
      } else {
        super.setCommentIsHidden(hidden);
      }
    }

    @Override public ToggleCommentPresenter getToggleCommentPresenter() {
      if (proxyHelper != null) {
        final DeletePopUpPresenter proxiedInstance = proxyHelper.getInstance(this);
        final ToggleCommentPresenter retVal = proxiedInstance.getToggleCommentPresenter();
        return retVal;
      } else {
        return super.getToggleCommentPresenter();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DeletePopUpPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_e_c_c_f_p_DeletePopUpPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DeletePopUpPresenter.class, "Type_factory__o_u_e_e_c_c_f_p_DeletePopUpPresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DeletePopUpPresenter.class, Object.class });
  }

  public DeletePopUpPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (DeletePopUpView) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_DeletePopUpView__quals__j_e_i_Any_j_e_i_Default");
    final ToggleCommentPresenter _toggleCommentPresenter_1 = (ToggleCommentPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_c_ToggleCommentPresenter__quals__j_e_i_Any_j_e_i_Default");
    final DeletePopUpPresenter instance = new DeletePopUpPresenter(_view_0, _toggleCommentPresenter_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _toggleCommentPresenter_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DeletePopUpPresenter instance) {
    instance.setup();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_e_e_c_c_f_p_DeletePopUpPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter ([org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter$View, org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DeletePopUpPresenter> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}