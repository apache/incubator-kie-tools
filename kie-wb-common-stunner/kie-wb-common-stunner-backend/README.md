Stunner - Default backend services implementations
===================================================

This module provides some backend implementations on top of the Stunner backend-api and backend-common modules.

By using this services a new file system is being automatically created into the VFS, this way the different services will refer to it.

**IMPORTANT**
* DO NOT use this module if you rely on the _project_ integration modules. Otherwise another repository and additional stuff will be generated as well as with the (guvnor/KIE) project stuff
* This way this module is actually NOT being part of the KIE workbench libraries, as for the workbench the Stunner's _project_ integration modules are the ones in use
* You can use this module for your webapps that do not rely on the _project_ integration modules, such as the Stunner's standalone showcase

