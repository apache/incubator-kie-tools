# Low-level Storage API

Uberfire provides a hierarchical preferences management API.

## Introduction

First of all, let's define some concepts:

* Preference: a unique key (String) and a value (any Object);
* Scope: It's represented by a `PreferenceScope`. It is composed by a unique type (String) and a key unique for each type (String). It can also have child scopes, to support a more complex hierarchy;
* Scope Resolution Strategy: Defines the order in which the scopes will be searched to find a preference, as well the default scope where a preference should be persisted (if none is provided). It's represented by a `PreferenceScopeResolutionStrategy`;
* Preference Scoped Value: a preference value (any Object) and its scope. It's represented by a `PreferenceScopedValue`;

You can check the "How it works" session (below) to see some usage examples of these concepts.

### Hierarchy

The preferences module hierarchy is defined by Scopes. A preference can be defined in one or multiple scopes. Every time you have to define a preference, you must specify the scope which it will belong to. When you fetch a preference (or several), you need to provide one of the following:

* A scope: the preference will be searched only in that scope;
* A scope resolution strategy: The preference returned will be the one in the first scope of the order that has that preference defined.
* Nothing: the default scope resolution order will be used. The examples in the "How it works" session (below) show how that works.

### Storage

The preferences are stored inside a `preferences.git` repository. A preference path has the structure `<REPOSITORY_ROOT>/<SCOPE_PATH>/<PREFERENCE_KEY>.preferences`.

The `<SCOPE_PATH>` can be defined by:
* A scope without a child scope. It has a path with the structure: `<SCOPE_TYPE>/<SCOPE_KEY>`;
* A scope with a child scope. Then the scope path structure will be: `<SCOPE_TYPE>/<SCOPE_KEY>/<CHILD_SCOPE_TYPE>/<CHILD_SCOPE_KEY>`, and so on, since the child can have another child.

## API

The API is very direct. It provides the following operations:

* `put`: Inserts a new preference in an specific scope;
* `putIfAbsent`: Inserts a new preference in an specific scope, but only if it is not defined there;
* `get`: Returns a preference;
* `getScoped`: Returns a preference scoped value;
* `search`: Searches for preferences by its keys;
* `searchScoped`: Searches for preferences scoped values by its keys;
* `all`: Returns all preferences defined in all scopes (of that context);
* `allScoped`: Returns all preferences scoped values defined in all scopes (of that context);
* `remove`: Removes preferences;

You can find more details about these methods and their parameters in the `PreferenceStore` class javadoc.

## How it works

### "Global" manner

This is a simple example on how to write and read a preference, using this module just as a global map of properties.

#### Server-side

```
import org.uberfire.ext.preferences.shared.PreferenceStore;

@Inject
private PreferenceStore preferenceStore;

public void writingAndReadingAPreference() {
    // Defines a preference.
    preferenceStore.put( "my.preference.key", "my-value" );

    // Reads a preference, if defined.
    // If not, the value returned will be null.
    String value = preferenceStore.get( "my.preference.key" );
}
```

#### Client-side

```
import org.uberfire.ext.preferences.client.ioc.store.PreferenceStore;

@Inject
private PreferenceStore preferenceStore;

public void writingAndReadingAPreference() {
    // Defines a preference.
    preferenceStore.put( "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );

    // Reads a preference, if defined.
    // If not, the value returned will be null.
    preferenceStore.get( "my.preference.key", preference -> {
        System.out.println( "Preference read: " + preference );
    } );
}
```

### "Per user" manner

This is a simple example on how to write and read a preference, optionally grouping its values by user. This way, when a preference is persisted, it can be only used by the logged user that persisted it.

#### Server-side

```
import org.uberfire.ext.preferences.shared.PreferenceStore;

@Inject
private PreferenceStore preferenceStore;

public void writingAndReadingAUserPreference() {
    // Gets the scope resolver
    final PreferenceScopeResolver preferenceScopeResolver =
            preferenceStore.getDefaultScopeResolver();

    // Gets the user scope
    PreferenceScope userScope =
            preferenceScopeResolver.resolve( DefaultScopes.USER.type() );

    // Gets the all-users scope
    PreferenceScope allUsersScope =
            preferenceScopeResolver.resolve( DefaultScopes.ALL_USERS.type() );

    // Defines a preference just for the logged user.
    preferenceStore.put( userScope, "my.preference.key", "my-value" );

    // Defines a preference for all users.
    preferenceStore.put( allUsersScope, "my.preference.key", "my-value" );
    // or
    preferenceStore.put( "my.preference.key", "my-value" );

    // Reads a preference for the logged user, if defined.
    // If not, reads a preference for all users, if defined.
    // If not, the value returned will be null.
    String value = preferenceStore.get( "my.preference.key" );
}
```

#### Client-side

```
import org.uberfire.ext.preferences.client.ioc.store.PreferenceStore;

@Inject
private PreferenceStore preferenceStore;

public void writingAndReadingAPreference() {
    // Gets the scope resolver
    final PreferenceScopeResolver preferenceScopeResolver =
            preferenceStore.getDefaultScopeResolver();

    // Gets the user scope
    PreferenceScope userScope =
            preferenceScopeResolver.resolve( DefaultScopes.USER.type() );

    // Gets the all-users scope
    PreferenceScope allUsersScope =
            preferenceScopeResolver.resolve( DefaultScopes.ALL_USERS.type() );

    // Defines a preference just for the logged user.
    preferenceStore.put( userScope, "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );

    // Defines a preference for all users.
    preferenceStore.put( allUsersScope, "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );
    // or
    preferenceStore.put( "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );

    // Reads a preference for the logged user, if defined.
    // If not, reads a preference for all users, if defined.
    // If not, the value returned will be null.
    preferenceStore.get( "my.preference.key", preference -> {
        System.out.println( "Preference read: " + preference );
    } );
}
```

### "Per component" manner

This is a simple example on how to write and read a preference, grouping its values by component. A component is represented here by a key (String).

In order to use this feature, you must qualify your PreferenceStore bean with `@ComponentKey( <YOUR_COMPONENT_IDENTIFIER> )`

#### Server-side

```
import org.uberfire.ext.preferences.shared.PreferenceStore;
import org.uberfire.ext.preferences.backend.annotations.ComponentKey;

@Inject
@ComponentKey( "my-component" )
private PreferenceStore preferenceStore;

public void writingAndReadingAComponentPreference() {
    // Gets the scope resolver
    final PreferenceScopeResolver preferenceScopeResolver =
            preferenceStore.getDefaultScopeResolver();

    // Gets the component scope
    PreferenceScope componentScope =
            preferenceScopeResolver.resolve( DefaultScopes.COMPONENT.type() );

    // Gets the entire-application scope
    PreferenceScope entireApplicationScope =
            preferenceScopeResolver.resolve( DefaultScopes.ENTIRE_APPLICATION.type() );

    // Defines a preference just for my-component.
    preferenceStore.put( componentScope, "my.preference.key", "my-value" );

    // Defines a preference for the entire application.
    preferenceStore.put( entireApplicationScope, "my.preference.key", "my-value" );
    // or
    preferenceStore.put( "my.preference.key", "my-value" );

    // Reads a preference of my-component, if defined.
    // If not, reads a preference for the entire application, if defined.
    // If not, the value returned will be null.
    String value = preferenceStore.get( "my.preference.key" );
}
```

#### Client-side

```
import org.uberfire.ext.preferences.client.ioc.store.PreferenceStore;
import org.uberfire.ext.preferences.client.ioc.annotations.ComponentKey;

@Inject
@ComponentKey( "my-component" )
private PreferenceStore preferenceStore;

public void writingAndReadingAPreference() {
    // Gets the scope resolver
    final PreferenceScopeResolver preferenceScopeResolver =
            preferenceStore.getDefaultScopeResolver();

    // Gets the component scope
    PreferenceScope componentScope =
            preferenceScopeResolver.resolve( DefaultScopes.COMPONENT.type() );

    // Gets the entire-application scope
    PreferenceScope entireApplicationScope =
            preferenceScopeResolver.resolve( DefaultScopes.ENTIRE_APPLICATION.type() );

    // Defines a preference just for my-component.
    preferenceStore.put( componentScope, "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );

    // Defines a preference for the entire application.
    preferenceStore.put( entireApplicationScope, "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );
    // or
    preferenceStore.put( "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );

    // Reads a preference of my-component, if defined.
    // If not, reads a preference for the entire application, if defined.
    // If not, the value returned will be null.
    preferenceStore.get( "my.preference.key", preference -> {
        System.out.println( "Preference read: " + preference );
    } );
}
```

### "Per component and user" manner

This is a simple example on how to write and read a preference, grouping its values by component and user. A component is represented here by a key (String).

In order to use this feature, you must qualify your PreferenceStore bean with `@ComponentKey( <YOUR_COMPONENT_IDENTIFIER> )`

#### Server-side

```
import org.uberfire.ext.preferences.shared.PreferenceStore;
import org.uberfire.ext.preferences.backend.annotations.ComponentKey;

@Inject
@ComponentKey( "my-component" )
private PreferenceStore preferenceStore;

public void writingAndReadingAComponentPreference() {
    // Gets the scope resolver
    final PreferenceScopeResolver preferenceScopeResolver =
            preferenceStore.getDefaultScopeResolver();

    // Gets the user-component scope
    PreferenceScope userComponentScope =
            preferenceScopeResolver.resolve( DefaultScopes.USER.type(),
                                             DefaultScopes.COMPONENT.type() );

    // Gets the user-entire-application scope
    PreferenceScope userEntireApplication =
            preferenceScopeResolver.resolve( DefaultScopes.USER.type(),
                                             DefaultScopes.ENTIRE_APPLICATION.type() );

    // Gets the all-users-component scope
    PreferenceScope allUsersComponentScope =
            preferenceScopeResolver.resolve( DefaultScopes.ALL_USERS.type(),
                                             DefaultScopes.COMPONENT.type() );

    // Gets the all-users-entire-application scope
    PreferenceScope allUsersEntireApplicationScope =
            preferenceScopeResolver.resolve( DefaultScopes.ALL_USERS.type(),
                                             DefaultScopes.ENTIRE_APPLICATION.type() );

    // Defines a preference just for my-component and the logged user.
    preferenceStore.put( userComponentScope, "my.preference.key", "my-value" );

    // Defines a preference for the logged user in the entire application.
    preferenceStore.put( userEntireApplication, "my.preference.key", "my-value" );

    // Defines a preference just for my-component and for all users.
    preferenceStore.put( allUsersComponentScope, "my.preference.key", "my-value" );

    // Defines a preference for all users in the entire application.
    preferenceStore.put( allUsersEntireApplicationScope, "my.preference.key", "my-value" );
    // or
    preferenceStore.put( "my.preference.key", "my-value" );

    // Reads a preference for the logged user and my-component, if defined.
    // If not, reads a preference for the logged user in the entire application, if defined.
    // If not, reads a preference for all users and my-component, if defined.
    // If not, reads a preference for all users in the entire application, if defined.
    // If not, the value returned will be null.
    String value = preferenceStore.get( "my.preference.key" );
}
```

#### Client-side

```
import org.uberfire.ext.preferences.client.ioc.store.PreferenceStore;
import org.uberfire.ext.preferences.client.ioc.annotations.ComponentKey;

@Inject
@ComponentKey( "my-component" )
private PreferenceStore preferenceStore;

public void writingAndReadingAPreference() {
    // Gets the scope resolver
    final PreferenceScopeResolver preferenceScopeResolver =
            preferenceStore.getDefaultScopeResolver();

    // Gets the user-component scope
    PreferenceScope userComponentScope =
            preferenceScopeResolver.resolve( DefaultScopes.USER.type(),
                                             DefaultScopes.COMPONENT.type() );

    // Gets the user-entire-application scope
    PreferenceScope userEntireApplication =
            preferenceScopeResolver.resolve( DefaultScopes.USER.type(),
                                             DefaultScopes.ENTIRE_APPLICATION.type() );

    // Gets the all-users-component scope
    PreferenceScope allUsersComponentScope =
            preferenceScopeResolver.resolve( DefaultScopes.ALL_USERS.type(),
                                             DefaultScopes.COMPONENT.type() );

    // Gets the all-users-entire-application scope
    PreferenceScope allUsersEntireApplicationScope =
            preferenceScopeResolver.resolve( DefaultScopes.ALL_USERS.type(),
                                             DefaultScopes.ENTIRE_APPLICATION.type() );

    // Defines a preference just for my-component and the logged user.
    preferenceStore.put( userComponentScope, "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );

    // Defines a preference for the logged user in the entire application.
    preferenceStore.put( userEntireApplication, "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );

    // Defines a preference just for my-component and for all users.
    preferenceStore.put( allUsersComponentScope, "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );

    // Defines a preference for all users in the entire application.
    preferenceStore.put( allUsersEntireApplicationScope, "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );
    // or
    preferenceStore.put( "my.preference.key", "my-value", onSuccess -> {
        System.out.println( "Preference stored successfully!" );
    } );

    // Reads a preference for the logged user and my-component, if defined.
    // If not, reads a preference for the logged user in the entire application, if defined.
    // If not, reads a preference for all users and my-component, if defined.
    // If not, reads a preference for all users in the entire application, if defined.
    // If not, the value returned will be null.
    preferenceStore.get( "my.preference.key", preference -> {
        System.out.println( "Preference read: " + preference );
    } );
}
```

## Customizing

If you want to, you can customize the provided scope hierarchy and create your own. You only have to provide new CDI @Default implementations for the interfaces `PreferenceScopeTypes` and `PreferenceScopeResolutionStrategy`, and let CDI do its magic.

To make this customization easier, we strongly suggest that you take a look at these interfaces javadoc (and possibly their default implementation).
