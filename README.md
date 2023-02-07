## Minecraft Python Language


### Some goals

- Everything should be written in lowercase, except strings and enums.
- Variables and functions are written in snake_case.
- Code should be easy to understand for beginners.
- Features should be intended for use in Minecraft.
- Inspired by Kotlin, Rust, Java, and Python.

### Data Types
```c++
# int
x = 5

# string
name = "ZeEpic"

# list
bombs = []

# args
args = (max: int, power: int)

# player
p = player("ZeEpic")

# location
loc = @(0, 65, 0)

# function
def function(p: player): string {
    return p.name
}

# bool
b = false
c = true

# material
mat = STONE

# permission
admin = permission("group.admin")
cool_people = permission("people.cool")

# world
w = world("world")

# block
b = w@(10, 10, 10)
b.type = GRASS_BLOCK

# event
block_break_event = blocks.break
block_place_event = blocks.place
player_death_by_player_event = players.death by player
entity_right_clicked_by_entity = entities.interact by entity

# entity
zombie = entity.ZOMBIE
zombie.location = w@(10, 11, 10)
```

### Examples

```c++
# Define an event
on (blocks.break) {
    # This code is run when any block is broken by a player
    send("You broke a block of type {block.type}.")
    if block.type == GRASS_BLOCK {
        cancel()
    }
}

# Define a player trait
trait(player) {
    game_level = 0
}

# Define a command
command("level") {
    args = (target: player, level: int = -1)
    aliases = ["set-level"]
    trigger = {
        if (level < 0) {
            sender.send("{target.name} is level {target.game_level}.")
        } else {
            sender.send("{target.name} is now level {level}.")
            target.game_level = level
        }
    }
}

# Define a command that can only be done by players, not console
command("tp") by player {
    args = (target: player)
    trigger = {
        sender.send("You have been teleported to {target.name}.")
        sender.teleport(@target)
    }
}

# Define a timer
timer(60) {
    broadcast("1 minute has passed.")
}

# Some other syntax
match expression {
    case_1 {
        # code
    }
    case_2 {
        # code
    }
    else {
        # code
    }
}

if expression {
    # code
}

def function(arg: type): return_type {
    # code
}

def function(arg: type)
    = pass # replace with code

type.property = value
print(type.property)
type.method(arg)

# Specify conditions of type
# Used for events and commands
# Type will only only happen if other_type is involved
type by other_type
```
