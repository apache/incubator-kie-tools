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

This package provides a type-safe internationalization (i18n) library for TypeScript projects. It enables you to manage translations in a structured and type-safe manner.

## Installation

```bash
npm install @kie-tools-core/i18n
```

## Package Structure

The library is organized into two main submodules:

### 1. Core Module

Contains all core functionality, including types and the `I18n` class.

```typescript
import * as I18nCore from "@kie-tools-core/i18n/dist/core";
```

### 2. React Components Module

Provides components and types necessary for React integration.

```typescript
import * as I18nReact from "@kie-tools-core/i18n/dist/react-components";
```

## Examples

- [TypeScript Implementation](examples/typescript.md)
- [React Integration](examples/react.md)

## Core Module

### I18n Class

The main class for handling internationalization is available in the core submodule.

#### Constructor

```typescript
constructor(
  defaults: I18nDefaults<D>,
  dictionaries: I18nDictionaries<D>,
  initialLocale?: string
)
```

Parameters:

- `defaults`: Default locale and dictionary
- `dictionaries`: Map of available dictionaries
- `initialLocale`: (Optional) Initial locale to use. If not provided, the default locale will be used.

#### Available Methods

```typescript
// Get the current locale
getLocale(): string

// Get the current dictionary
getCurrent(): D

// Set a new locale
setLocale(locale: string): void
```

**Example:**

```typescript
// Get the current locale
const locale = i18n.getLocale();

// Get the current dictionary
const dictionary = i18n.getCurrent();

// Set locale and get current dictionary in one chain
const dictionary = i18n.setLocale(locale).getCurrent();
```

### Core Types

#### ReferenceDictionary

The type of the default dictionary. It defines the structure of your translations.

```typescript
type ReferenceDictionary = {
  [k: string]: string | DictionaryInterpolation | Array<string | number | Wrapped<string>> | ReferenceDictionary;
};
```

#### TranslatedDictionary<D>

The type for any dictionary that isn't the default one. All properties are optional, allowing partial translations.

```typescript
type TranslatedDictionary<D extends ReferenceDictionary> = DeepOptional<D>;
```

#### I18nDefaults<D>

Configuration type for the default settings.

```typescript
interface I18nDefaults<D extends ReferenceDictionary> {
  locale: string; // Default locale
  dictionary: D; // Default dictionary
}
```

#### I18nDictionaries<D>

Type for the collection of available dictionaries.

```typescript
type I18nDictionaries<D extends ReferenceDictionary> = Map<string, TranslatedDictionary<D>>;
```

#### DictionaryInterpolation

Function type for dynamic string interpolation.

```typescript
type DictionaryInterpolation = (...args: Array<string | number>) => string;
```

## React Integration

### Components

#### I18nDictionariesProvider

Provides your implementation of `I18nContextType` to your React component tree.

```tsx
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";

<I18nDictionariesProvider
  defaults={{ locale: "en", dictionary: enDictionary }}
  dictionaries={new Map([["pt-BR", ptBrDictionary]])}
  ctx={MyAppI18nContext}
>
  <App />
</I18nDictionariesProvider>;
```

#### I18nHtml

Renders a string containing HTML tags.

```tsx
<I18nHtml>{i18n.someHtmlContent}</I18nHtml>
```

**Note:** This component uses React's `dangerouslySetInnerHTML` prop. Ensure your HTML content is safe to prevent XSS vulnerabilities.

#### I18nWrapped

React component wrapper that enables dynamic content replacement with localized versions based on keys in a provided components object.

````tsx
import { I18nWrapped } from "@kie-tools-core/i18n/dist/react-components";
import { wrapped } from "@kie-tools-core/i18n/dist/core";

// Define in your dictionary interface
interface MyDictionary extends ReferenceDictionary<MyDictionary> {
  message: Array<string | Wrapped<"nameOfTheComponent">>;
}

// Use in your dictionary
const en: MyDictionary = {
  message: [wrapped<"nameOfTheComponent">, "some string value"]
};

// Use in your component
<I18nWrapped
  components={{
    nameOfTheComponent: <YourComponent />
  }}
>
  {i18n.message}
</I18nWrapped>

### React Types

#### I18nContextType<D>

The context type used by `I18nDictionaryProvider`.

```typescript
interface I18nContextType<D extends ReferenceDictionary> {
  locale: string;                      // Current locale
  setLocale: React.Dispatch<string>;   // Function to change locale
  i18n: D;                             // Current dictionary
}
````

## Best Practices

1. **Define a complete reference dictionary**: Create a comprehensive default dictionary (usually English) that includes all translation keys.

2. **Use type safety**: Leverage TypeScript's type system to catch missing translations at compile time.

3. **Organize translations logically**: Group related translations together in nested objects.

4. **Use interpolation functions** for dynamic content:

   ```typescript
   greeting: (name: string) => `Hello, ${name}!`;
   ```

5. **Handle pluralization** with appropriate functions:
   ```typescript
   itemCount: (count: number) => `${count} ${count === 1 ? "item" : "items"}`;
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
