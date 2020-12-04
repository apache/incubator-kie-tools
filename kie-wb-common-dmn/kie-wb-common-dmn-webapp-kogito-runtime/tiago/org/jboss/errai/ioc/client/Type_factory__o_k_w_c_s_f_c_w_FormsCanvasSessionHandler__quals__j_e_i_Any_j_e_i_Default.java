package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.forms.client.widgets.FormsCanvasSessionHandler;

public class Type_factory__o_k_w_c_s_f_c_w_FormsCanvasSessionHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsCanvasSessionHandler> { public Type_factory__o_k_w_c_s_f_c_w_FormsCanvasSessionHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormsCanvasSessionHandler.class, "Type_factory__o_k_w_c_s_f_c_w_FormsCanvasSessionHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormsCanvasSessionHandler.class, Object.class });
  }

  public FormsCanvasSessionHandler createInstance(final ContextManager contextManager) {
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final CanvasCommandFactory<AbstractCanvasHandler> _commandFactory_1 = (LienzoCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_c_LienzoCanvasCommandFactory__quals__j_e_i_Any_j_e_i_Default");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_2 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final FormsCanvasSessionHandler instance = new FormsCanvasSessionHandler(_definitionManager_0, _commandFactory_1, _sessionCommandManager_2);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onRefreshFormPropertiesEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent", new AbstractCDIEventCallback<RefreshFormPropertiesEvent>() {
      public void fireEvent(final RefreshFormPropertiesEvent event) {
        FormsCanvasSessionHandler_onRefreshFormPropertiesEvent_RefreshFormPropertiesEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent", new AbstractCDIEventCallback<CanvasSelectionEvent>() {
      public void fireEvent(final CanvasSelectionEvent event) {
        FormsCanvasSessionHandler_onCanvasSelectionEvent_CanvasSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent []";
      }
    }));
    thisInstance.setReference(instance, "onDomainObjectSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent", new AbstractCDIEventCallback<DomainObjectSelectionEvent>() {
      public void fireEvent(final DomainObjectSelectionEvent event) {
        FormsCanvasSessionHandler_onDomainObjectSelectionEvent_DomainObjectSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FormsCanvasSessionHandler) instance, contextManager);
  }

  public void destroyInstanceHelper(final FormsCanvasSessionHandler instance, final ContextManager contextManager) {
    instance.destroy();
    ((Subscription) thisInstance.getReferenceAs(instance, "onRefreshFormPropertiesEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasSelectionEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onDomainObjectSelectionEventSubscription", Subscription.class)).remove();
  }

  public native static void FormsCanvasSessionHandler_onCanvasSelectionEvent_CanvasSelectionEvent(FormsCanvasSessionHandler instance, CanvasSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.forms.client.widgets.FormsCanvasSessionHandler::onCanvasSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasSelectionEvent;)(a0);
  }-*/;

  public native static void FormsCanvasSessionHandler_onDomainObjectSelectionEvent_DomainObjectSelectionEvent(FormsCanvasSessionHandler instance, DomainObjectSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.forms.client.widgets.FormsCanvasSessionHandler::onDomainObjectSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/DomainObjectSelectionEvent;)(a0);
  }-*/;

  public native static void FormsCanvasSessionHandler_onRefreshFormPropertiesEvent_RefreshFormPropertiesEvent(FormsCanvasSessionHandler instance, RefreshFormPropertiesEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.forms.client.widgets.FormsCanvasSessionHandler::onRefreshFormPropertiesEvent(Lorg/kie/workbench/common/stunner/forms/client/event/RefreshFormPropertiesEvent;)(a0);
  }-*/;
}