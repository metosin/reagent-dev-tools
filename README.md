# Reagent-dev-tools

[![Clojars Project](http://clojars.org/metosin/reagent-dev-tools/latest-version.svg)](http://clojars.org/metosin/reagent-dev-tools)

## Features

- Display state tree for Reagent atoms
- Styles embedded for easy use
- Simple toggleable development tool panel which can be extended with own tabs

## Configuration

### Options

- `:el` (optional) The element to render the dev-tool into. If the property is given,
but is nil, dev tool is not enabled. If not given, new div is created and used.
- `:margin-element` (optional) Element where to set margin-bottom/right if the panel is open.
This is helpful so that the dev tool isn't displayed over the application content.
- `:state-atom` This options adds default `state-tree` panel displaying tree for the given RAtom.
- `:state-atom-name` (optional) Overrides the name for default `state-tree` panel.
- `:panels` List of panel maps to display. This is appended to the default panels, if you
don't want to include default panels, leave out :state-atom option and define all panels here.

#### Panel options:
- `:key` (Required) React key
- `:label` (Required) Label for tab bar
- `:view` (Required) Reagent Hiccup form to display the panel content

#### Built-in panel component options:

- `reagent-dev-tools.core/state-tree`
    - `:ratom` (Required) The RAtom to display
    - `:label` (Optional) Label to display for atom root node, will default to panel :label.

### 1. :preload namespace

To enable, add `reagent-dev-tools.preload` to your `:compiler-options` `:preloads`.
This will ensure that Dev tool is only included in the output JS for
development builds.

To configure tool in this setup, you can use `:external-config :reagent-dev-tools` option:

```edn
:external-config {:reagent-dev-tools {:state-atom example.main/state
                                      :panels example.main/panels}}}}
```

### 2. Start manually based on compile time options

You could use `goog.DEBUG` or other Closure define options to call the start function
on your application code:

```cljs
(ns example.app
  (:require [reagent-dev-tools.core :as dev-tools]
            re-frame.db))

;; FIXME: Is typehint required nowadays?
(when ^boolean goog.DEBUG
  (dev-tools/start! {:state-atom re-frame.db/app-db}))
```

Note: as you are requiring the namespace always, it is possible that
Google Closure is not able to remove all reagent-dev-tools code during DCE.

### 3. Start dynamically and use separate module

You can use [JS Modules](https://clojurescript.org/reference/javascript-module-support)
or [Shadow CLJS modules](https://shadow-cljs.github.io/docs/UsersGuide.html#_modules)
so split the reagent-dev-tools code to a separate module you can load dynamically.
You could for example load some options from your backend, look at the
browser location or `localStorage`.

```cljs
(ns example.app
  (:require [shadow.loader :as loader]))

;; Load reagent-dev-tools on localhost, using specific hash url
;; or if enabled manually from JS console.
(defn enable-dev-tools []
  (case (.. js/document -location -hash)
    "#enable-dev-tool" (do
                         (.setItem js/localStorage "reagent-dev-tools" "1")
                         (loader/load "devtools"))
    "#disable-dev-tool" (.removeItem js/localStorage "reagent-dev-tools")
    nil)

  (when (or (= "localhost" (.. js/document -location -hostname))
            (.getItem js/localStorage "reagent-dev-tools"))
    (loader/load "devtools")))

(defn ^:export enable-dev-tools! []
  (.setItem js/localStorage "reagent-dev-tools" "1")
  (enable-dev-tools))

(.addEventListener js/window "load" (fn [] (enable-dev-tools)))
```

```cljs
(ns example.dev
  (:require [reagent-dev-tools.core :as dev-tools]
            re-frame.db))

(dev-tools/start! {:state-atom re-frame.db/app-db})
```

### 4. Using the components as part of the application

Reagent component `reagent-dev-tools.core/dev-tool` can also be used directly
as part of Reagent applications.

## Extending

### Panels

The panels components can access the panel options map through React context
defined in `reagent-dev-tools/context`. Check state-tree implementation.

## License

Copyright © 2015-2022 [Metosin Oy](http://www.metosin.fi)

Distributed under the Eclipse Public License, the same as Clojure.
