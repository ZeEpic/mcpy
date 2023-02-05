# Define a player trait
# Traits can also be for entities, items, and blocks
# This line gives all players a property called "game_level"
# This could also be used to store amount of money, etc.
trait game_level(level: num) by player

# Command using traits
cmd level(target: player, level: num = -1) {
    aliases = ["set-level", "lvl", "setlvl"]
    if level < 0 {
        sender.send("{target.name} is level {target.game_level}.")
        return
    }
    target.game_level = level
    sender.send("{target.name} is now level {level}.")
}
