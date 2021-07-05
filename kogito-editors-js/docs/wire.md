# How to Wire a Kogito editor components with a GWT

The development workflow for GWT and React works seamlessly, as each React component has its web app, and GWT editors also run on standalone web apps.

This guide will show you how to **wire** both worlds.

### 1. Configure GWT web app on IntelliJ

In this example, I will show the DMN testing web app configuration, but the same works for BPMN development web app.

Here's the configuration you for the GWT IntelliJ plugin:

![IntelliJ configuration](./intellij.png?raw=true)

Notice, that different from usual, now we:

- Check the **Update resources on frame deactivation** checkbox
- Add the dev mode parameter `-war /tmp/webapp` (let's keep this special directory in mind)

### 2. Wire React components with the GWT web app

Run the following command in the `kogito-editors-js` module directory:

```bash
wire=/tmp/webapp/kogito-editors-js yarn gwt:wire
```

> **Notice**: we're using `wire=<special directory>/kogito-editors-js`

### 3. It works!

Now, you've **wired** both worlds! When you update anything in the React side, all changes reflect on your GWT web app.

## Contributing

If you find anything unclear in this guide, please contribute. But, before you start, read the [contribution guide](../../CONTRIBUTING.md).
