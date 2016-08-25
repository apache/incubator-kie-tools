
# Change Impact / Impact Analysis

We have an "indexing" infrastructure which has `org.uberfire.ext.metadata.engine.Indexer` implementations for almost all of our 
resource types. A resource, in the workbench, is something like a BPMN2 file, a (Drools) rule, a Java class or almost any
type of file that can be found in a KJAR. These files are indexed when they are added or changed in a (workbench) git repository. 

The `Indexer` implementations add meta-information about the indexed resource to a document in the Lucence index. (A Lucene index is basically an 
in-memory collection of key-value documents that can be rapidly searched.)

This allows us to then quickly find lucene documents (which have a 1:1 mapping to actual resources). This ***query*** functionality will
make it possible to implement things like automatic refactoring or basic search functionality in the Workbench, much like what 
other modern IDEs have. 

In summary: 

* A **resource** is a "standalone" entity that is defined in a file in a repository. For example:
  * A BPMN2 process 
  * A rule 
  * A Java class file
* An **Indexer** implementation adds meta-information about a **resource** to a **document in the Lucene index**. 
* The **index** can be **queried** later to find all documents (which contain information about a resource) with particular characteristics (for example, all resources that reference/use Java class `org.acme.Person`).

In many cases, there is only one resource per file. However, it's also possible for a file to contain *multiple* resources. For example: 

* A `.drl` file that defines a `function` as well as a `rule` (a Drools function is also a resource!)
* A `.bpmn2` file that defines multiple processes

At the moment, only Java, BPMN2 and Drools rule files are indexed. However, indexing of other standard formats is of course possisble.

## Types of Information

When indexing files or "resources", it's important to store 2 different types of information.
This information is later used by the query infrastructure.

1. Identifying information: this information identifies the resource and is (only) about the resource being indexed.

   For example, when indexing a Java class file, some of the information added would be:

  * the (fully-qualified) class name
  * the names of the fields and methods present in the class
  * the package of the resource
  * the project it's located in

2. Reference information: the references to other resources in the resource being indexed. 

   For example, if we're indexing a Java Class file, there would be :
   * Java class imports (references to other java classes)
   * fields whose types are other Java classs (references to other java classes)
   * methods whose parameters or return types are other Java classs (references to other java classes)
 
   If we were indexing a rule, then we might add information about:  
   * the use of a DRL function defined in another rule (reference to another resource)
   * a shared `agenda-group` attribute used in several rules (reference to a *shared* part, explained below)

3. Relationship information: reference information only stores that another resource is referenced in the indexed resource.
   _Relationship_ information _qualifies_ reference information.
   
   * Some examples of relationship information:
     * `MyChildImpl` (Java class) *implements* `MyParent` (Java interface)
     * `SavingsAccount` (rule) *extends* `BankAccount` (parent rule)
     * `NewCustomer` (process) *calls* `CreditCheck` (other process, called as a subprocess)

   * Relationship information is *not* yet stored. However, the following classes, as an example of how it could be stored
   and easily fit into the existing infrastructure, can be found here:
     * [RelationshipType](kie-wb-common-refactoring-api/src/main/java/org/kie/workbench/common/services/refactoring/service/RelationshipType.java)
     * [RelationshipIndexTerm](kie-wb-common-refactoring-api/src/main/java/org/kie/workbench/common/services/refactoring/model/index/terms/RelationshipIndexTerm.java)
     * [ValueRelationshipIndexTerm](kie-wb-common-refactoring-api/src/main/java/org/kie/workbench/common/services/refactoring/model/index/terms/valueterms/ValueRelationshipIndexTerm.java)

   * in the lucence index, you would then find something like this: 
     * `child` => `org.acme.Parent`
   
   * Searching for that key would return all documents that were children ("extends", whether a rule or java class) of
   the `org.acme.Parent` resource.
     
---

#### NOTE: Specificity of Type Information 

While it may also seem necessary to always save information such as what ***specific*** *type of resource* a resource is 
(interface, class, annotation, enum, etc.. ), I have limited this.

For context, look at how an IDE such as Eclipse or Intellij works.

* In an IDE, you can search for classes that implement, or are implemented by another class (this is relationship information)
* ... but you can *not* search for "all interfaces" or "all enums".

My conclusion has been that, for the mean time at least, we don't always need to store this granularity/specificity of information.

However, when we index (Drools) rules, functions and queries, we do save the type information for those resources. 

---

## Types of Identifying information

The information is divided into basically 2 parts:

1. Resource-level information
  * This is basically just the FQN of the resource with the type of Resource
  
  For example: 
  * `Java` => `org.acme.Person`
  * `Process` => `org.acme.new.customer`
  * `Rule` => `org.acme.credit.check`
  
  See [ResourceType](kie-wb-common-refactoring-api/src/main/java/org/kie/workbench/common/services/refactoring/service/ResourceType.java)

2. "Part" information: examples of parts are
  * A `field` or `method` for a Java class
  * A `ruleflow-group`, `agenda-group` or value of a rule-defined `enum`

  Note that a `ruleflow-group` or `agenda-group` are special cases.
  How they are handled is defined below (they are _shared part references_).
  

  For example: 
  
  * `Field`:`org.acme.Person` => `age`
  * `Method`:`org.acme.Person` => `addRelationship(Person, RelationshipType)`
  * `Function`:`org.acme.credit.check` => `calculateScore`
  
  See [PartType](kie-wb-common-refactoring-api/src/main/java/org/kie/workbench/common/services/refactoring/service/PartType.java)
  
## Types of References

With regards to references, there are several types of references that can be found in an resource:

- **resource** references refer to another file resource.

  For example: 
  - a Java class import of another Java class 
      - the one class *references* the other class
  - use of a Java class in a rule  
      - the rule *references* the java class
  - a child rule references the parent rule when extending it 
      - the child rule *references* the parent class
  - a rule uses a function
      - the rule *references* the function, even though both might be defined in the same resource (`.drl` file)
  
- **part** references refer to a part of another resource.

  For example:
  
  - a script in a BPMN2 process calls a Java method in a Java class
      - the BPMN2 process (script task) *references* the method in the Java class
  - a rule *references* the field of a Java class
  
- ***shared* part** references refer to "parts" that are not specific to one file,
  but *shared* across multiple resources.
  
  For example: 
  - a `ruleflow-group` can be an attribute or "shared part" of multiple rules,
  but is of course *not* specific to one rule
  - a `global` fact can be used in multiple rules

---
#### NOTE: Shared Part References

How **shared parts** are indexed deviates from how other "normal" resources are indexed. 

For example, let's take a `rule` (a "normal" resrouce) and a `ruleflow-group` (a shared part).

With a `rule`, two types of different information are being saved to the index: 

1. *Definition*: a `rule` => `<rule name>` key-value pair, which identifies the resource where the rule is defined
2. *Reference*: 0 or more `ref:rule` => `<rule name>` key-value pairs, in multiple other documents (about other resources), 
in order to identify *other* resources where the rule is *reference*

However, when a `ruleflow-group` is encountered, only the following information is being saved: 

1. a `shared:ruleflow-group` => `<ruleflow group name>` key-value pair, which identifies 
a resource where the shared part is used. 

What's a little weird is that we can then have the following situation: 

* Multiple `.drl` files where `ruleflow-group` **`myRfGroup`** is "defined"
* A `.bpmn2` (process) definition that references `ruleflow-group` **`myRFGroup`**.

This is a little weird because all documents will have the same key-value pair, 
even though the `ruleflow-group` is being *defined* in the `.drl` files, 
while the `.bpmn2` file is *referencing* the `ruleflow-group`.

However, where (and whether) a shared part is *defined or referenced* ***is useless to us***.

Why? Because, for change impact and similar editor-related activities, we only need to know
which resources "reference" the shared part, *regardless of whether that's a pure reference 
or definition*.

---
  
## Implementation in the Lucene Key-Value Index

In the lucene docs, the information will be stored as follows:

`ref:<type> => <resource FQN>` is the format for a **resource reference**  
`ref:<partType>:<resource FQN> => <part name>`  is the format for a **resource part reference**   
`shared:<partType> => <part name>` is the format for a **shared part reference**   
`<type> => <resource FQN>`  is the format for **resource** info  
`<partType> => <part name>` is the format for **resource part** info  

For example:

* References in one Java class to other Java classes

```
java => org.acme.model.Customer
field => name
ref:java => java.lang.String
field => age
ref:java => java.lang.Integer
```

  While it might seem weird that the type information of fields is lost, 
the more important question is: how would you use this information in an editor? 

  In an editor, we need to know that the names of the fields, and which 
other java classes are used -- but not what the type of the field is. Compilation-related
information is not our provenance. 

  Remember, we are *not* a JAVA IDE project -- this info is being used in order
to aid editing of rule-, process-, and form-related resources. 

* References to a Java class in a DRL file

```
rule => org.acme.rules.new.student
function => org.acme.rules.new.student.evaluateAge
ref:java => org.acme.Person
ref:field:org.acme.Person => name
ref:method:org.acme.Person => getName(int)
```

* References in one BPMN to another BPMN2

```
process => org.acme.processes.new.customer
variable => customer
ref:bpmn2 => org.acme.process.credit.check
```

* References in one DRL to various other things

```
rule => org.acme.rules.new.account
ref:drl => org.acme.rule.new.customer
shared:ruleflowgroup => org.acme.rules.accounts
ref:function => org.acme.rule.new.customer.doThat
ref:query => org.acme.rule.new.customer.findPerson
drlenum => org.acme.rules.new.account.AccountType
```
