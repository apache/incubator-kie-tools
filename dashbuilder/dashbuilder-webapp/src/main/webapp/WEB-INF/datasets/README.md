How to deploy a data set definition
============================

* Create a "<mydataset>.dset" file containing the data set JSON definition.

* Create an empty "<mydataset>.dset.deploy" file in order to launch the automatic deployment.

To undeploy/remove a data set definition
============================

* Create an empty "<uuid>.undeploy" file in order to force the removal of the data set from the system.

* Notice that <uuid> is the identifier of the data set you want to remove. If the data set has been previously deployed
  (using the above mechanism) then the UUID is the one defined within the ".dset" file. If it's been defined
  through the UI tooling then it can be read on the data set editor screen.
