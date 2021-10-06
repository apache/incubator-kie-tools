# Kogito Tooling Notifications

This package provides a type-safe Notifications library for a Typescript project.

## Install

Can be installed with `yarn` or `npm`:

- `yarn add @kie-tooling-core/notifications`
- `npm install @kie-tooling-core/notifications`

## Usage

The library is separated into two submodules:

- api
  All the APIs and the main classes needed are in this submodule.

  to use the core:

  - `import { NotificationsApi } from "@kie-tooling-core/notifications/dist/api"`
  - `import { Notification } from "@kie-tooling-core/notifications/dist/api"`
  - `import { NotificationSeverity } from "@kie-tooling-core/notifications/dist/api"`
  - `import { NotificationType } from "@kie-tooling-core/notifications/dist/api"`

- vscode

  All the classes needed to use in vscode channel implementation

  to use the vscode classes:

  ```ts
  import { VsCodeNotificationsApi } from "@kie-tooling-core/i18n/dist/react-components";

  const api: NotificationsApi = new VsCodeNotificationsApi(workspaceApi, i18n);
  ```

## API

NotificationsApi main attributes:

- messages: The text that will be shown to the user
- path: File location.
- severity: `"INFO" | "WARNING" | "ERROR" | "SUCCESS" | "HINT"`
- type: `"PROBLEM" | "ALERT"`

### VS Code

The VsCodeNotificationsApi is the only "public" class users have access. Under the hood it contains two different implementations depending on `Notification.type`.

So if:

- `Notifications.type === "PROBLEM"` The notifications are going to be shown in Problems Tab.
- `Notifications.type === "ALERT"` The notifications are going to be shown as Popups.

If, for some reason, there is not type, the default is `PROBLEM`

In both cases path is mandatory and it will let the user to open the file where those notifications come from.

`Notification.severity` also depends on `Notification.type`, if:

- `PROBLEM`, the supported severities are `"INFO" | "WARNING" | "ERROR" | "SUCCESS" | "HINT"`. `SUCCESS` converts to `INFO` which is the default severity.
- `ALERT`, the supported severities are `"ERROR" | "WARNING" | "INFO"`. Any other types defaults to `INFO`
