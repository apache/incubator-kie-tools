# BPMN Backend

- Experimental Marshallers
    
    Disable with flag:
    
        -Dbpmn.marshaller.legacy=true
        
    - Entry point: `BPMNDirectDiagramMarshaller` which implements `DiagramMarshaller`
    - Actual unmarshalling from XML is delegated to Eclipse BPMN2 library
    - Mapping between Eclipse BPMN2 data model to Stunner BPMN data model is performed through **Converter** classes

## Unmarshalling

Unmarshalling converts an **Eclipse BPMN2** document into a **Stunner Diagram**.
There are a few differences between the Eclipse BPMN2 data model and the Stunner graph representation:

BPMN is an XML document, therefore the Eclipse BPMN2 implementation exposes the document as a **tree**. 

The tree of the BPMN2 model, however, contains 3 different subtrees:
1. the tree of flow elements (shapes of the diagram, including visible edges a.k.a. `SequenceFlow`s) rooted
   at the `bpmn2:process` element.
2. the tree of the layout of the shapes: rooted at`bpmndi:BPMNDiagram`, it contains `BPMNShape`s and `BPMNEdge`s with their 
  coordinates on the canvas. These coordinates are **absolute**.
3. the tree of the simulation properties, rooted at `bpmn2:relationship`

Each section may refer to another; in particular, section 2. and 3. refer to
elements defined in section 1.; moreover there may be cross-references within elements of section 1.

The main differences with BPM are that 
- in Stunner, Nodes and Edges are distinct entities, while, in a BPM document, 
 SequenceFlows are represented together with other Elements
- each Stunner POJO aggregates all of 1., 2., 3. 

In fact, a stunner `Node` represents an Element (e.g. a `Task`)
with all of its other properties, such as position on the canvas, simulation properties, etc.
There is also an additional constraint: coordinates may be relative to a container,
whereas they are always absolute in the BPMN2 model (e.g. a `Lane` may contain
a `Task`: in Stunner this `Task`'s position will be relative to the `Lane`'s. 
but the `BPMNShape` will report absolute coordinates)

Although conversion can be seen as a **visit** of the BPMN2 document tree, 
the entire visit is driven by the the `Process` subtree (1.), because it contains
all of the visible elements of the diagram. However, every time an element of the `Process`
subtree is visited, all of the potential cross-references must be resolved,
thus a `Context` wrapper has to be brought along.

The visit is implemented as a series of **`Converters`** that **delegate** to each other.
For instance, there is a Converter for `FlowElements` (children of the document root, `Definitions`)
that delegates to one Converter for each subtype of `FlowElement`, e.g., `TaskConverter`, `StartEventConverter`, etc.
Each of such converters checks the type of the element that is being visited,
and if it *matches* (that is, this converter is able to *convert* that type to a Node or an Edge), 
then it dispatches the conversion, which results in returning
a Stunner `Node` or a Stunner `Edge`.

The visit is separate for nodes and edges. A few passes are implemented:

- a pass over the nodes, that is: 
    - a pass over all child of a `Process`, excepted sequence flows; including 
      `SubProcess`es, which may contain other children
    - a pass over the lanes (which are not included as children of a `Process`)
    
- a pass over the edges; includes
    - sequence flows, ignored in the previous pass
    - boundary elements, which are represented as a special kind of edge

Please also notice that the total number of Stunner edges is usually greater
that the number of edges in a BPM: in fact, in Stunner, edges may represent
both an edge that is **drawn** on the canvas, and a **relationship** between
two nodes. The former would be an element that has an actual representation
in the BPM; the latter encodes parent/child or docking relationships.
But, while the first kind of edges is actually generated during the 
visit of BPM edges (SequenceFlows), the latter are really generated 
during the visit of Nodes!

The parent/child edges form a "tree of subgraphs", whose root is the whole Diagram.
The Diagram "contains" other nodes of the graph (i.e., it has an edge to them); 
and each Node inside such a graph may "contain" other nodes, in such a way, 
that by walking all of the relationship edges, all of the nodes in the graph
are visited.


**Note.** For the reader familiar with common design patterns, because the visit is type-driven, it is -- of course --
implemented through a rendition of the traditional Visitor pattern. However, because the Visitor pattern
really implements a form of "pattern matching",  we have deemed more readable to implement a helper for "pattern matching" 
(actually, *type matching*, see Section "Utilities" and the JavaDoc for details). Moreover,
as opposed to the Visitor pattern, this makes it possible to avoid defining an `accept()` method
on each Stunner POJO, leaving it agnostic to any implementation detail of this visit.

### Implementation
     
#### Converters To Stunner model

- A **Converter _to_ the Stunner model** is a class (basically, a function) that converts an Eclipse BPMN2 object into:

- a `BPMNNode`, i.e.:  a wrapper for a `Node<? extends View<? extends BPMNViewDefinition>, ?>` ,
 enhanced with:
     - ability to record parent/child relationship; in other words, a `BPMNNode` may be child/parent of other `BPMNode`s
     - ability to "contain" `BPMNEdge`s (see below); in other words a `BPMNNode` may represent a subgraph (e.g. in the case of a `(Sub)Process`)
- a `BPMNEdge`, i.e.: a wrapper for an `Edge<? extends View<? extends BPMNViewDefinition>, ?>` 

- Converter classes form a delegation tree. The root of such a tree is the converter for the Diagram root, called "Process" 
when converting *to* the Stunner model, called "ViewDefinitionConverter" when converting *from* the Stunner model. 
The delegation tree follows the hierarchy and structure of the BPMN data model. 

- For instance, a Process converter will convert all of the FlowElements contained in the Process section. 
A suitable conversion method is invoked, depending on the type of each FlowElement. The way matching is done, 
is describe through code, using a `Match` helper (see Utilities section)

- Each converter is responsible of handling a set of classes from Eclipse BPMN2 model. For instance,
  `TaskConverter` handles `Task`s. It instances a `Node`/`Edge` object (throught the `TypedFactoryManager` -- see below)
  for the recognized type, and fills its fields with all the supported values in the original model.
  At the end of the conversion it usually **return the element**, and/or, in some cases, it may **add it to the canvas** 
  (e.g., subprocess converters return their subprocess node, but also add in their child nodes).

  Fields from the Eclipse BPMN2 model, for convenience, are generally accessed through a `PropertyReader`. 
  
  
#### Property Readers
  
`PropertyReader`s are returned from a `PropertyReaderFactory`. They retrieve properties from
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
      
See [docs/extending.md] for more information on extending PropertyReaders.

#### GraphBuilder

A `GraphBuilder` object issues commands to the canvas while building the graph. 
  It is a wrapper around: 
   - `GraphCommandExecutionContext`
   - `GraphCommandFactory` 
   - `GraphCommandManager` 
   
`GraphBuilder` is used for convenience, to avoid explicitly creating command instances.
 It also implements custom logic for some actions. For example, in the case of adding child nodes, 
 it translates the coordinates of a child node into the new reference system (the parent boundaries).

`GraphBuilder` builds the entire graph (`GraphBuilder#buildGraph(BpmnNode)`)
once all the conversions have took place: it traverses the entire directed graph described by the `BPMNNode`s
starting from the "root node", which represents the root of the diagram, and visiting
the parent/child relations in each BPMNNode and the `BPMNEdge` they may contain.


## Marshalling

In this case we visit the Stunner graph and convert it back into a BPMN2 tree.

The Stunner graph is represented as a bag of nodes. Each node contains references
to its **inbound** edges and its **outgoing** edges.

The visit is performed as follows: first the "root node" of the diagram is found, 
then we implement the following passes

- nodes:
    - a pass over the root process node
    - then, a pass on all non-root nodes, including 
        - a pass over all subprocesses (with all its subchildren)
        - a pass over all lanes

- let us now call E the union of all inbound and outgoing edges for all nodes in the Graph;
  then the folowing passes are implemented
    - a pass over all Child edges in E
    - a pass over all Dock edges in E
    - a pass over all SequenceFlow edges in E

During this passes, all the references to the graph nodes are resolved as
correctly assigned to the BPM nodes.


### Converters From Stunner Model

A **Converter _from_ the Stunner model** is a class that converts a 
Stunner `Node<? extends View<? extends BPMNViewDefinition>, ?>` to a `PropertyWriter`. A `PropertyWriter`
is a wrapper around an Eclipse BPMN2 element (see below)

#### Property Writers

`PropertyWriter`s are returned from a `PropertyWriterFactory`. They store properties to
  each Eclipse BPMN2 instance especially implementing custom logic for
  extended drools-related attributes, such as 
  - `<bpmn2:extensionElements>` (e.g. `elementname`)
  - `itemDefinition`s / `dataInput`s / `dataOutput`s (e.g. `Subject`, `Comment`)
  - attributes under the`drools:` namespace (e.g. `drools:docker`)
  - attributes under the `color:` namespace (e.g. `color:background-color`)
  - bounds/shape/edge data defined in the `BPMNDiagram/BPMNPlane/BPMNShape` section
  
  For instance, for `BusinesRuleTask task`:
  
      BusinessRuleTaskPropertyReader p = propertyWriterFactory.of(task);
      // write the extended name under the `elementname` extension metadata,
      // and add a regular (whitespace trimmed) <bpmn2:businessRuleTask name="...">
      p.setName(name);
      
      // look set the documentation sequence from the given string
      p.setDocumentation(documentation);
      
      // converts stunner representation of the coordinates to bounds
      // of this element
      p.setBounds(node.getContent().getBounds());
      
      // ... etc.

See [docs/extending.md] for more information on extending PropertyWriters.

### Utilities

#### Custom Attributes, Elements

Custom attributes and elements have been defined in form of singletons under the `customproperties`
subpackage of `org.kie.workbench.common.stunner.bpmn.backend.converters`.

Under this package, we define `CustomAttribute`s (such as `"drools:packageName"`), 
`CustomElement`s such as `<elementname>` in the form of singleton objects that read/write
such attributes to a given element. 

For example:

    public String getPackage() {
        return CustomAttribute.packageName.of(element).get();
    }

    public void setPackage(String value) {
        CustomAttribute.packageName.of(process).set(value);
    }


Please notice that these singletons are not supposed to be used directly,
but their getters and setters will be generally wrapped in a `PropertyReader`
or `PropertyWriter`, respectively. For instance `getPackage()` is a member
of `ProcessPropertyReader`, and `setPackage()` is a member of `ProcessPropertyWriter`.


#### Misc
 
In order to minimize casts (mostly for legibility, cosmetic reasons) the following classes exist:

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