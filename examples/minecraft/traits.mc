# Define a player trait
# Traits can also be for entities, items, and blocks
# This line gives all players a property called "game_level"
# This could also be used to store amount of money, etc.
# This is kind of like a database so it's automatically saved when the server restarts
# There are plans to add other kinds of permanent storage in the future

trait game_level(level: num, received: time) by player  # 'player' can be replaced by lots of other types

# Command using traits
cmd level(target: player, level: num) {
    if level < 0 {
        sender.send("{target.name} has been level {target.game_level.level} since {target.game_level.received}.")
        return
    }
    target.game_level = level
    sender.send("{target.name} is now level {level}.")
    target.game_level.received = now()
}
