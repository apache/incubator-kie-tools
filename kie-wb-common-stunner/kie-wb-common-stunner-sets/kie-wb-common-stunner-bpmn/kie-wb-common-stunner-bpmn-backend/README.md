# BPMN Backend

## Experimental Marshallers

Enable with flag:

    -Dbpmn.marshaller.experimental=true
    
### Design

- Entry point: `BPMNDirectDiagramMarshaller` which implements `BPMNDirectDiagramMarshaller` 
- Actual unmarshalling from XML is delegated to Eclipse BPMN2 library
- Mapping between Eclipse BPMN2 data model to Stunner BPMN data model is performed through **Converter** classes

#### Converters

- A **Converter** is a class with a `convert` method, that converts an Eclipse BPMN2 object into either:

   - a `Node<View<BPMNDiagramImpl>, ?>` in the case of a `Process`
   - a `Node<? extends View<? extends BPMNViewDefinition>>` instance in the case of a FlowElement 
     (e.g. `StartEvent`, `Task`, etc.), a Lane, or a SubProcess
   - an `Edge<? extends View<? extends BPMNViewDefinition>, ?>` in the case of a SequenceFlow
   
- Each converter is responsible of handling a set of classes from Eclipse BPMN2 model. For instance,
  `TaskConverter` handles `Task`s. It instances a `Node`/`Edge` object (throught the `TypedFactoryManager` -- see below)
  for the recognized type, and fills its fields with all the supported values in the original model.
  At the end of the conversion it usually **return the element**, and/or, in some cases, it may **add it to the canvas** 
  (e.g., subprocess converters return their subprocess node, but also add in their child nodes).
  
  Fields from the Eclipse BPMN2 model, for convience, are generally accessed through a `PropertyReader`. 
  
  
#### Property Readers
  
- `PropertyReader`s are returned from a `PropertyReaderFactory`. They retrieve properties from
  each Eclipse BPMN2 instance especially implementing custom logic for
  extended drools-related attributes, such as 
  - `<bpmn2:extensionElements>` (e.g. `elementname`)
  - `itemDefinition`s / `dataInput`s / `dataOutput`s (e.g. `Subject`, `Comment`)
  - attributes under the`drools:` namespace (e.g. `drools:docker`)
  - attributes under the `color:` namespace (e.g. `color:background-color`)
  - bounds/shape/edge data defined in the `BPMNDiagram/BPMNPlane/BPMNShape` section
  
  For instance, for `BusinesRuleTask task`:
  
      BusinessRuleTaskPropertyReader p = propertyReaderFactory.of(task);
      // look up the extended name under the `elementname` extension metadata,
      // falling back onto the regular <bpmn2:businessRuleTask name="...">
      String name = p.getName();
      
      // look for the documentation sequence and return a string
      String documentation = p.getDocumentation();
      
      // returns a stunner-compatible representation of the coordinates, bounds
      // of this element
      Bounds b = p.getBounds();
      
      // returns background formatting of the element
      BackgroundSet backgroundSet = p.getBackgroundSet();
      
      // ... etc.

- a `GraphBuildingContext` object issues commands to the canvas while building the graph. 
  It is a wrapper around: 
   - `GraphCommandExecutionContext`
   - `GraphCommandFactory` 
   - `GraphCommandManager` 
   
  `GraphBuildingContext` is used for convenience, to avoid explicitly creating command instances.
  It also implements custom logic for some actions. For example, in the case of adding child nodes, 
  it translates the coordinates of a child node into the new reference system (the parent boundaries).

#### Utilities
 
- In order to minimize casts (mostly for legibility, cosmetic reasons) the following classes exist:

   - `TypedFactoryManager`: a wrapper around `FactoryManager` that creates 
     an instance of `Node<T,U>`, `Edge<T,U>`, `Graph<T,U>`, with proper type parameters. Examples:
     
         Node<View<StartNoneEvent>, Edge> node = 
            factoryManager.newNode(nodeId, StartNoneEvent.class);

     
        Graph<DefinitionSet, Node> graph =
                typedFactoryManager.newGraph(
                        definitionsId, BPMNDefinitionSet.class);
                        

     
   - `Match` and `VoidMatch` provide a fluent interface to type matching (`instanceof`)
      avoiding explicit casts. Moreover it provides shorthands for skipping unhandled types (`ignore()`) 
      or marking them as currently unsupported (`missing()`). Each branch of a `Match` 
      expects a `Function` that consumes the input and returns a `Node<T,U>` or an `Edge<T,U>`. 
      A `VoidMatch` implements a side-effecting `Match`, i.e. a `Match` that does not return a value.
      
      An example from `TaskConverter`:
      
              // a match for Task subclasses that returns Node<? extends View<? extends BPMNViewDefinition?>>
              Match.ofNode(Task.class, BPMNViewDefinition.class)
                      .when(org.eclipse.bpmn2.BusinessRuleTask.class, t -> {
                          // t is already casted to BusinessRuleTask
                          Node<View<BusinessRuleTask>, Edge> node = 
                            factoryManager.newNode(t.getId(), BusinessRuleTask.class);
                            BusinessRuleTask definition = node.getContent().getDefinition();
                            BusinessRuleTaskPropertyReader p = propertyReaderFactory.of(task);
                    
                            definition.setGeneral(new TaskGeneralSet(
                                    new Name(p.getName()),
                                    new Documentation(p.getDocumentation())
                            ));

                          ...
                          return node;
                      })
                      .when(org.eclipse.bpmn2.ScriptTask.class, t -> { ... })
                      .when(org.eclipse.bpmn2.UserTask.class, userTaskConverter::convert) // method references
                      .missing(org.eclipse.bpmn2.ServiceTask.class) // report this as missing
                      .missing(org.eclipse.bpmn2.ManualTask.class)
                      .ignore(SomeClassYouSkip.class)
                      .orElse(t -> {
                          /* handle the default case: NoneTask */
                      })
                      .apply(task)
                      .asSuccess().value();
      
