# Preferences

Uberfire provides a hierarchical preferences management API.

## Introduction

First of all, let's define some concepts:

* Preference: a unique key (String) and a value (any Object);
* Scope: It's represented by a `PreferenceScope`. It is composed by a unique type (String) and a key unique for each type (String).
* Scope Resolution Strategy: Defines the order in which the scopes will be searched to find a preference, as well the default scope where a preference should be persisted if none is provided. It's represented by a `PreferenceScopeResolutionStrategy`;
* Preference Scoped Value: a preference value (any Object) and its scope. It's represented by a `PreferenceScopedValue`;

You can check the "Default implementation" session (below) to see an usage example of these concepts.

### Hierarchy

The preferences module hierarchy is defined by Scopes. A preference can be defined in one or multiple scopes. Every time you have to define a preference, you must specify the scope which it will belong to. When you fetch a preference, you need to provide one of the following:

* A scope: the preference will be searched only in that scope;
* A scope type (ONLY if the scope has a default key): the preference will be searched in the scope defined by the passed type and its default key;
* A scope resolution strategy: The preference returned will be the one in the first scope of the order that has that preference defined.
* Nothing: the default scope resolution order will be used.

### Storage

The preferences are stored inside a `preferences.git` repository. A preference path has the structure `<REPOSITORY_ROOT>/<SCOPE_TYPE>/<SCOPE_KEY>/<PREFERENCE_KEY>.preferences`.

### Default implementation

Uberfire provides a default hierarchy implementation with two scope types: "user" and "global".

The "user" scope type has usernames as default keys (this way no key must be provided). This scope type is the first to be searched, and it allows each user to define their own preferences.

The "global" scope type has the String "global" as its default key (this way no key must be provided). This scope type is the second (and last) to be searched, and it allows the definition of global preferences values for all application users.

If a preference is persisted without a scope being provided, then the "user" scope type will be used.

### Customizing

If you want to, you can customize this entire module implementation by providing new implementation to the involved interfaces, and letting CDI do its magic.

For instance, you can provide implementation of the interfaces `PreferenceScopeTypes` and `PreferenceScopeResolutionStrategy`, which will then be used as a default. This way you can define your own scopes and their hierarchy.

## How to use it

This is a simple example on how to write and read a preference:

```
@Inject
private Caller<PreferenceStore> preferenceStore;

public void writingAndReadingAPreference() {
    // Writes a preference in the default scope.
    preferenceStore.call().put( "my.preference.key", "my-value" );

    // Reads a preference using the default scope resolution strategy.
    preferenceStore.call().get( "my.preference.key" );
}
```

If you are using the provided default implementation, the preference will be stored and read from the "user" scope type.

## API

The API is very direct. It provide the following operations:

* `put`: Inserts a new preference in an specific scope;
* `putIfAbsent`: Inserts a new preference in an specific scope, but only if it is not defined there;
* `get`: Returns a preference;
* `getScoped`: Returns a preference scoped value;
* `search`: Searches for preferences by its keys;
* `searchScoped`: Searches for preferences scoped values by its keys;
* `all`: Returns all preferences defined in all scopes (of that context);
* `allScoped`: Returns all preferences scoped values defined in all scopes (of that context);
* `remove`: Removes preferences;

You can find more details about these methods and its parameters in the `PreferenceStore` class javadoc.

## Roadmap
This is an early version of the preferences management module.
In the upcoming versions we plan to offer the following features:

* A new workbench part that represents a preferences configuration screen. This way you can define your own configuration screen and use it to any component you want to.
* A central preferences configuration UI, where all preferences configurations screens will be displayed so the user can manage all preferences defined  for the system.
