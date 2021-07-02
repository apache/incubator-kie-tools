## Build env

This package contains the tooling to handle environment variables and other build-related scripting necessary to build the Kogito Tooling project.

---

#### Packages dependency graph

![Kogito Tooling packages dependency graph](https://g.gravizo.com/source/svg?https%3A%2F%2Fraw.githubusercontent.com%2Ftiagobento%2Fkogito-tooling%2Fmonorepo%2Fpackages%2Fbuild-env%2Fgraph.dot)

Nodes:

- `Blue`: Packages that are published to the NPM registry.
- `Purple`: Core packages that are published to the NPM registry.
- `Dotted black`: Packages that are deployed to other mediums.

Edges:

- `Dotted black`: devDependency.
- `Solid black`: dependency.
