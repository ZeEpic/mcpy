# Time is a type added in this language
def time_examples() {
    current_time = now()
    epoch_time = time(1000000000)
    time_length = time(120000) # represents 120 seconds
    print(time_length.length) # prints '2 minutes'
    print(epoch_time) # This prints the date 1 billion milliseconds after 1970
}
