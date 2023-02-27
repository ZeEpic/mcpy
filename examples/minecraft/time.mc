# Time is a type added in this language
def time_examples() {
    current_time = now()
    print(current_time) # prints the current time when this is run

    epoch_time = time(1000000000)
    print(epoch_time) # This prints the date 1 billion milliseconds after 1970

    time_length = time(120000) # represents 120 seconds
    print(time_length.length) # prints '2 minutes'
    print(time_length.ticks) # prints '6000' (120000 milliseconds / 20 ticks)
}
