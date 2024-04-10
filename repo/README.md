#### Repo dependencies version

The related `build-dependencies-version.json` contains a centralized view of all required tools to correctly
build the project. CI\CD tasks should rely on this file only, avoiding to hard-coding these version or store
them in another place. Be aware that any key addition, change or removal can have an impact on CI\CD process
that rely on this file. Any tool version update must be update on this file as well, to guarantee the same
condition in all building processes.

#### Packages dependency graph

![Apache KIE Tools packages dependency graph](https://g.gravizo.com/source/svg?https%3A%2F%2Fraw.githubusercontent.com%2Fapache%2Fincubator-kie-tools%2Fmain%2Frepo%2Fgraph.dot)

Nodes:

- `Blue`: Packages that are published to the NPM registry.
- `Purple`: Core packages that are published to the NPM registry.
- `Dotted black`: Packages that are deployed to other mediums.
- `Dotted orange`: Examples packages.

Edges:

- `Dotted black`: devDependency.
- `Solid black`: dependency.
