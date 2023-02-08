# Define a timer function called "clock", but you can name it whatever you want
# It's automatically run every 60 seconds
timer clock(60) {
    broadcast("1 minute has passed.")
}

when players.quit {
    # Cancel the timer for everyone when a player quits
    clock.cancel()
}
