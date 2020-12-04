package org.jboss.errai.ioc.client;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.dnd.CompassWidgetImpl;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.PanelManagerImpl;
import org.uberfire.client.workbench.events.DropPlaceEvent;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.client.workbench.widgets.dnd.CompassWidget;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

public class Type_factory__o_u_c_w_w_d_CompassDropController__quals__j_e_i_Any_j_e_i_Default extends Factory<CompassDropController> { public Type_factory__o_u_c_w_w_d_CompassDropController__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CompassDropController.class, "Type_factory__o_u_c_w_w_d_CompassDropController__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CompassDropController.class, Object.class, DropController.class });
  }

  public CompassDropController createInstance(final ContextManager contextManager) {
    final CompassDropController instance = new CompassDropController();
    setIncompleteInstance(instance);
    final PanelManagerImpl CompassDropController_panelManager = (PanelManagerImpl) contextManager.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    CompassDropController_PanelManager_panelManager(instance, CompassDropController_panelManager);
    final WorkbenchDragAndDropManager CompassDropController_dndManager = (WorkbenchDragAndDropManager) contextManager.getInstance("Type_factory__o_u_c_w_w_d_WorkbenchDragAndDropManager__quals__j_e_i_Any_j_e_i_Default");
    CompassDropController_WorkbenchDragAndDropManager_dndManager(instance, CompassDropController_dndManager);
    final CompassWidgetImpl CompassDropController_compass = (CompassWidgetImpl) contextManager.getInstance("Type_factory__o_u_c_v_p_d_CompassWidgetImpl__quals__j_e_i_Any_j_e_i_Default");
    CompassDropController_CompassWidget_compass(instance, CompassDropController_compass);
    final Event CompassDropController_workbenchPartDroppedEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DropPlaceEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, CompassDropController_workbenchPartDroppedEvent);
    CompassDropController_Event_workbenchPartDroppedEvent(instance, CompassDropController_workbenchPartDroppedEvent);
    setIncompleteInstance(null);
    return instance;
  }

  native static WorkbenchDragAndDropManager CompassDropController_WorkbenchDragAndDropManager_dndManager(CompassDropController instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.dnd.CompassDropController::dndManager;
  }-*/;

  native static void CompassDropController_WorkbenchDragAndDropManager_dndManager(CompassDropController instance, WorkbenchDragAndDropManager value) /*-{
    instance.@org.uberfire.client.workbench.widgets.dnd.CompassDropController::dndManager = value;
  }-*/;

  native static PanelManager CompassDropController_PanelManager_panelManager(CompassDropController instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.dnd.CompassDropController::panelManager;
  }-*/;

  native static void CompassDropController_PanelManager_panelManager(CompassDropController instance, PanelManager value) /*-{
    instance.@org.uberfire.client.workbench.widgets.dnd.CompassDropController::panelManager = value;
  }-*/;

  native static Event CompassDropController_Event_workbenchPartDroppedEvent(CompassDropController instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.dnd.CompassDropController::workbenchPartDroppedEvent;
  }-*/;

  native static void CompassDropController_Event_workbenchPartDroppedEvent(CompassDropController instance, Event<DropPlaceEvent> value) /*-{
    instance.@org.uberfire.client.workbench.widgets.dnd.CompassDropController::workbenchPartDroppedEvent = value;
  }-*/;

  native static CompassWidget CompassDropController_CompassWidget_compass(CompassDropController instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.dnd.CompassDropController::compass;
  }-*/;

  native static void CompassDropController_CompassWidget_compass(CompassDropController instance, CompassWidget value) /*-{
    instance.@org.uberfire.client.workbench.widgets.dnd.CompassDropController::compass = value;
  }-*/;
}