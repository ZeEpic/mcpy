# Define a timer function called "clock", but you can name it whatever you want
# It's automatically run every 60 seconds
# You must enter a number of seconds, which can be a decimal, but no other parameters can be passed to a timer function
timer clock(60) {
    broadcast("1 minute has passed.")
}

when players.quit {
    # Cancel the timer for everyone when one of the players quit
    clock.cancel()
}
