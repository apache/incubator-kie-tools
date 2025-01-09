<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

# Apache KIE Tools i18n

This package provides a type-safe i18n library for a Typescript project.

## Install

- `npm install @kie-tools-core/i18n`

## Usage

The library is separated into two submodules:

- core
  All core functionalities, which includes the types, and the I18n class.
  to use the core:
  `import * as I18nCore from "@kie-tools-core/i18n/dist/core"`
- react-components

  All components and types necessaries to integrate on your React project.

  to use the React components:
  `import * as I18nReact from "@kie-tools-core/i18n/dist/react-components"`

## Examples

- [Typescript](examples/typescript.md)
- [React](examples/react.md)

## Core

### Class

The core class `I18n` is under the `core` submodule "@kie-tools-core/i18n/dist/core".

- Constructor

```
defaults: I18nDefaults<D>
dictionaries: I18nDictionaries<D>
initialLocale?: string
```

_If no `initialLocale` is provide the default locale will be used as `initialLocale`_

- Available Methods

```
// Get the current locale
getLocale(): string

// Get the current dictionary
getCurrent(): D

// Set a new locale
setLocale(locale: string): void
```

### Types

- `ReferenceDictionary<D>`
  The type of the default dictionary

- `TranslatedDictionary<D>`
  The type of any other dictionary that isn't the default.

- `I18nDefaults<D>`
  The type of the default configs to be used on the `I18nDictionariesProvider` component or `I18n` class.

```ts
interface I18nDefaults<D extends ReferenceDictionary<D>> {
  locale: string; // current locale
  dictionary: D; // default dictionary
}
```

- `I18nDictionaries<D>`
  The type of the dictionaries to be used on the `I18nDictionariesProvider` component or `I18n` class.

```ts
type I18nDictionaries<D extends ReferenceDictionary<D>> = Map<string, TranslatedDictionary<D>>;
```

## React

### Components

- `<I18nDictionariesProvider>`
  Provides your implementation of `I18nContextType`

- `<I18nHtml>` Renders a string with HTML tags

_Be aware: the `<I18nHtml>` component uses the `dangerouslySetInnerHTML` prop._

### Types

- `I18nContextType<D>`
  The context type use by `<I18nDictionaryProvider>`, provides an object with the following properties:

```ts
interface I18nContextType<D extends ReferenceDictionary<D>> {
  locale: string; // current locale
  setLocale: React.Dispatch<string>; // a function to set the desired locale
  i18n: D; // Dictionary
}
```

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html
