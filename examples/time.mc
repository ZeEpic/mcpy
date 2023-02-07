# Time is a type added in this language
def time_examples() {
    current_time = now()
    epoch_time = time(10000000)
    time_length = time(120000) # represents 120 seconds
    print(time_length.length) # prints '2 minutes'
    print(epoch_time) # This prints the date 10 million milliseconds after 1970
}