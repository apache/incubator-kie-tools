# sonataflow-management-console-webapp

## Enabling Keycloak security

### Starting and Configuring the Keycloak Server

To start a Keycloak Server you can use Docker and just run the following command:

```
docker run -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -v ./dev/config/sonataflow-realm.json:/opt/keycloak/data/import/realm.json -p 8280:8080 quay.io/keycloak/keycloak:25.0.3 start-dev --import-realm
```

You should be able to access your Keycloak Server at [localhost:8280/auth](http://localhost:8280)
and verify keycloak server is running properly: log in as the admin user to access the Keycloak Administration Console.
Username should be admin and password admin.

The following are the users available in keycloak

| Login | Password | Roles               |
| ----- | -------- | ------------------- |
| admin | admin    | _admin_, _managers_ |
| alice | alice    | _user_              |
| jdoe  | jdoe     | _managers_          |

To change any of this client configuration access to http://localhost:8280/auth/admin/master/console/#/realms/sonataflow.

### Changing configs

Enable `PROD` env mode by running the app with:

`SONATAFLOW_MANAGEMENT_CONSOLE_WEBAPP__sonataflowEnvMode=PROD pnpm dev`
