# Import Java Classes component

This editor provides the possibility to edit the expression related to a Decision Node, or to a Business Knowledge Model's function.

## Static deployed showcase

[There](https://yesamer.github.io/import-java-classes/) you can access to the static deployed version of the showcase application for this editor. It will be manually updated as soon as new features will be added.

## Structure

In the `showcase` folder, there is a tiny React application, which represent the Proof Of Value about how it is possible to integrate the `ImportJavaClasses` component inside another existing application.

## Scripts

In the main project (where the components actually live), it is possible to execute, from the root folder, the following scripts (`pnpm` is recommended):

```sh

# Remove 'dist' folder (such script is automatically called when the build is executed)
pnpm prebuild

# Build a production-ready artifact to be deployed
pnpm build

# Execute all tests
pnpm test

# Trigger static code analysis
pnpm lint

# Trigger type checking
pnpm type-check

# Perform all the three checks above (tests, lint and type checking)
pnpm quality-checks
```

In the showcase project, only two scripts are available:

```sh
# Start a local server to see the 'ImportJavaClasses' in action
pnpm start
# Compiles a production ready showcase application
pnpm build
```
