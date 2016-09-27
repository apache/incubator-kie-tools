# Preferences

## Introduction

The Uberfire Preferences Extension is a way to define any kind of preference that you want to define, from simple application properties to complex preferences defined by objects.

The preferences are separated for each user, and they can modify them independently.

We provide two types of API:

* The recommended one, for a more clean and organized use, is the **Beans API**. By using it, you can map a preference as an object, and save and/or load it just by CDI injection, anywhere you want;
* There is also a **Low-level API**, which provides a low-level control of scopes.

For more details on these two types of API, see the next sessions.

## Roadmap
This is an early version of the preferences management module.
In the upcoming versions we plan to offer the following features:

* A central preferences configuration UI, where all preferences configurations screens will be displayed so the user can manage all preferences defined  for the system.
