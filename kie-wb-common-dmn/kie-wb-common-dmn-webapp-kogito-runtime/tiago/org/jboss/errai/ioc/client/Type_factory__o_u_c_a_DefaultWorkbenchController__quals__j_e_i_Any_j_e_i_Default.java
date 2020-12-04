package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.authz.ActivityCheck;
import org.uberfire.client.authz.DefaultWorkbenchController;
import org.uberfire.client.authz.PerspectiveCheck;
import org.uberfire.client.authz.WorkbenchController;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PopupActivity;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.mvp.WorkbenchClientEditorActivity;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;

public class Type_factory__o_u_c_a_DefaultWorkbenchController__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultWorkbenchController> { private class Type_factory__o_u_c_a_DefaultWorkbenchController__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefaultWorkbenchController implements Proxy<DefaultWorkbenchController> {
    private final ProxyHelper<DefaultWorkbenchController> proxyHelper = new ProxyHelperImpl<DefaultWorkbenchController>("Type_factory__o_u_c_a_DefaultWorkbenchController__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_c_a_DefaultWorkbenchController__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final DefaultWorkbenchController instance) {

    }

    public DefaultWorkbenchController asBeanType() {
      return this;
    }

    public void setInstance(final DefaultWorkbenchController instance) {
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

    @Override public PerspectiveCheck perspectives() {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final PerspectiveCheck retVal = proxiedInstance.perspectives();
        return retVal;
      } else {
        return super.perspectives();
      }
    }

    @Override public ActivityCheck screens() {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.screens();
        return retVal;
      } else {
        return super.screens();
      }
    }

    @Override public ActivityCheck popupScreens() {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.popupScreens();
        return retVal;
      } else {
        return super.popupScreens();
      }
    }

    @Override public ActivityCheck splashScreens() {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.splashScreens();
        return retVal;
      } else {
        return super.splashScreens();
      }
    }

    @Override public ActivityCheck editors() {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.editors();
        return retVal;
      } else {
        return super.editors();
      }
    }

    @Override public PerspectiveCheck perspective(PerspectiveActivity perspective) {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final PerspectiveCheck retVal = proxiedInstance.perspective(perspective);
        return retVal;
      } else {
        return super.perspective(perspective);
      }
    }

    @Override public PerspectiveCheck perspective(String perspectiveId) {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final PerspectiveCheck retVal = proxiedInstance.perspective(perspectiveId);
        return retVal;
      } else {
        return super.perspective(perspectiveId);
      }
    }

    @Override public ActivityCheck screen(WorkbenchScreenActivity screen) {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.screen(screen);
        return retVal;
      } else {
        return super.screen(screen);
      }
    }

    @Override public ActivityCheck screen(String screenId) {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.screen(screenId);
        return retVal;
      } else {
        return super.screen(screenId);
      }
    }

    @Override public ActivityCheck popupScreen(PopupActivity popup) {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.popupScreen(popup);
        return retVal;
      } else {
        return super.popupScreen(popup);
      }
    }

    @Override public ActivityCheck popupScreen(String popupId) {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.popupScreen(popupId);
        return retVal;
      } else {
        return super.popupScreen(popupId);
      }
    }

    @Override public ActivityCheck editor(WorkbenchEditorActivity editor) {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.editor(editor);
        return retVal;
      } else {
        return super.editor(editor);
      }
    }

    @Override public ActivityCheck editor(WorkbenchClientEditorActivity editor) {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.editor(editor);
        return retVal;
      } else {
        return super.editor(editor);
      }
    }

    @Override public ActivityCheck editor(String editorId) {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.editor(editorId);
        return retVal;
      } else {
        return super.editor(editorId);
      }
    }

    @Override public ActivityCheck splashScreen(SplashScreenActivity splash) {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.splashScreen(splash);
        return retVal;
      } else {
        return super.splashScreen(splash);
      }
    }

    @Override public ActivityCheck splashScreen(String splashId) {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final ActivityCheck retVal = proxiedInstance.splashScreen(splashId);
        return retVal;
      } else {
        return super.splashScreen(splashId);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultWorkbenchController proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_a_DefaultWorkbenchController__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultWorkbenchController.class, "Type_factory__o_u_c_a_DefaultWorkbenchController__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultWorkbenchController.class, Object.class, WorkbenchController.class });
  }

  public DefaultWorkbenchController createInstance(final ContextManager contextManager) {
    final User _user_1 = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    final AuthorizationManager _authorizationManager_0 = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    final DefaultWorkbenchController instance = new DefaultWorkbenchController(_authorizationManager_0, _user_1);
    registerDependentScopedReference(instance, _user_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_c_a_DefaultWorkbenchController__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.client.authz.DefaultWorkbenchController an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.client.authz.DefaultWorkbenchController ([org.uberfire.security.authz.AuthorizationManager, org.jboss.errai.security.shared.api.identity.User])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultWorkbenchController> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}