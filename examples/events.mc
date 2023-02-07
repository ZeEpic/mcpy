# Define an event
when blocks.break {
    # This code is run when any block is broken by a player
    player.send("You broke a block of type {block.type}.")
    # You can find out the details of the event by using the event keyword
    if event.block.type == GRASS_BLOCK {
        cancel() # Cancel the event, so the block is not broken
    }
}
