# Tips

## Getting Started
- Learn python
- If you're unsure on how to code something, assume it works the same as it does in python but replace a colon with `{ }`
- If it's something specific to Minecraft plugins, check this example folder
  - If you're still stuck, ask in the Discord
- Unlike python, adding tab spacing is not required, but is recommended
- The error handling is intended to be as clear and helpful as possible, so if you have any improvement requests please leave them in the Discord server

## Python Users
- Use snake_case_for_everything
- Use `@` for locations, like `@target`
- Use `true` and `false` instead of `True` and `False`
- You don't need a return type on a function if it does not return anything
- Use `num` instead of `int` or `float`
- No need for f-strings, just use `"{code}"`
- 
- Strong typing is required on functions (i.e. def function(arg: **num**))
- Many helpful code structures follow this format:
```
structure name(arguments) by someone {
    # code
}
```
Such as:
```python
cmd say(message: str) by player {
    broadcast("&7&o{sender.name} says &f{message}")
}
```