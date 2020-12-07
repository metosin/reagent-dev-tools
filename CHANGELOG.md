# Unreleased

- Add option to toggle panel placement between bottom and right
- Add `:margin-element` option to automatically set margin-bottom/right on some
element, so that panel doesn't go over the application content

# 0.2.1 (2020-03-06)

- Use `reagent.dom/render` instead of `reagent.core/render` to prepare for
next Reagent releases

# 0.2.0 (2018-01-25)

- Made the panel resizeable
- Save the state (open, height, active panel) on `localStorage`
- Added `start!` function for easier configuration
- Added some colors to the state tree
- Add collection type name to state tree
    - Type can be clicked to open/close collection items
- Fixes

# 0.1.0 (2015-11-29)

- Initial release
