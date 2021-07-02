# Kogito Tooling i18n

This package provides a type-safe i18n library for a Typescript project.

## Install

Can be installed with `yarn` or `npm`:

- `yarn add @kie-tooling-core/i18n`
- `npm install @kie-tooling-core/i18n`

## Usage

The library is separated into two submodules:

- core
  All core functionalities, which includes the types, and the I18n class.
  to use the core:
  `import * as I18nCore from "@kie-tooling-core/i18n/dist/core"`
- react-components

  All components and types necessaries to integrate on your React project.

  to use the React components:
  `import * as I18nReact from "@kie-tooling-core/i18n/dist/react-components"`

## Examples

- [Typescript](examples/typescript.md)
- [React](examples/react.md)

## Core

### Class

The core class `I18n` is under the `core` submodule "@kie-tooling-core/i18n/dist/core".

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
