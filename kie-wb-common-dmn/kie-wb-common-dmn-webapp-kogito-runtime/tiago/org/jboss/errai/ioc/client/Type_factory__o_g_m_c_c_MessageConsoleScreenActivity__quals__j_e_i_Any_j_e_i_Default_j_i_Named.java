package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.guvnor.messageconsole.client.console.MessageConsoleScreen;
import org.guvnor.messageconsole.client.console.MessageConsoleScreenActivity;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ContextSensitiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_g_m_c_c_MessageConsoleScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<MessageConsoleScreenActivity> { public Type_factory__o_g_m_c_c_MessageConsoleScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(MessageConsoleScreenActivity.class, "Type_factory__o_g_m_c_c_MessageConsoleScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "org.kie.workbench.common.screens.messageconsole.MessageConsole", true));
    handle.setAssignableTypes(new Class[] { MessageConsoleScreenActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("org.kie.workbench.common.screens.messageconsole.MessageConsole") });
  }

  public MessageConsoleScreenActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final MessageConsoleScreenActivity instance = new MessageConsoleScreenActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final MessageConsoleScreen MessageConsoleScreenActivity_realPresenter = (MessageConsoleScreen) contextManager.getInstance("Type_factory__o_g_m_c_c_MessageConsoleScreen__quals__j_e_i_Any_j_e_i_Default");
    MessageConsoleScreenActivity_MessageConsoleScreen_realPresenter(instance, MessageConsoleScreenActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static MessageConsoleScreen MessageConsoleScreenActivity_MessageConsoleScreen_realPresenter(MessageConsoleScreenActivity instance) /*-{
    return instance.@org.guvnor.messageconsole.client.console.MessageConsoleScreenActivity::realPresenter;
  }-*/;

  native static void MessageConsoleScreenActivity_MessageConsoleScreen_realPresenter(MessageConsoleScreenActivity instance, MessageConsoleScreen value) /*-{
    instance.@org.guvnor.messageconsole.client.console.MessageConsoleScreenActivity::realPresenter = value;
  }-*/;
}