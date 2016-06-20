# Component
A Uberfire Component is defined by a WorkbenchActivity. This activity is the interface between UberFire framework behaviour and
application-defined behaviour

In the model-view-presenter (MVP) sense, an Activity is essentially an application-provided Presenter: it has a view
 (its widget) and it defines a set of operations that can affect that view.

Applications can implement an Activity interface directly, they can subclass one of the abstract Activity
 implementations that come with the framework, or they may rely on UberFire's annotation processors to generate
Activity implementations from annotated Java objects.
