# Boxed Expression Editor

This editor provides the possibility to edit the expression related to a Decision Node, or to a Business Knowledge Model's function.

## Static deployed showcase

[There](https://cutt.ly/boxed-expression-editor) you can access to the static deployed version of the showcase application for this editor. It will be manually updated as soon as new features will be added.

## Structure

The main component is `src/components/BoxedExpressionEditor/BoxedExpressionEditor.tsx`.
It represents the entry point for using the editor.

In the `showcase` folder, there is a tiny React application, which represent the Proof Of Value about how it is possible to integrate the `BoxedExpressionEditor` component inside another existing application.

Once the showcase application gets launched, you can see on the right side of the page the JSON that is actually produced for the corresponding selected logic type.
Such JSON represents the model data that must be adopted to initialize the `BoxedExpressionEditor` component, by populating its props.

The retrieval of the updated expression is performed by making usage of global functions, belonging to `beeApiWrapper` object, that must be available in the `Window` namespace and used by the `BoxedExpressionEditor` component.
All exposed function expected to exist, are defined in `src/api/BoxedExpressionEditor.ts`.

Consider that the showcase app is able to display the most updated JSON representing an expression, because uses such APIs (please refer to `showcase/src/index.tsx`).

## Scripts

In the main project (where the components actually live), it is possible to execute, from the root folder, the following scripts (`pnpm` is recommended):

```sh

# Remove 'dist' folder (such script is automatically called when the build is executed)
pnpm prebuild

# Build a production-ready artifact to be deployed
pnpm build:prod

# Execute all tests
pnpm test

# Trigger static code analysis
pnpm lint

# Trigger type checking
pnpm type-check

# Perform all the three checks above (tests, lint and type checking)
pnpm quality-checks
```

In the showcase project, these scripts are available:

```sh
# Start a local server to see the 'BoxedExpressionEditor' in action
pnpm start
# Compiles a production ready showcase application
pnpm build
# Run PlayWright 'BoxedExpressionEditor' tests.
pnpm test:e2e
# To update the PlayWright Snapshot files, used for the regression:
pnpm test:e2e:run -u
```
