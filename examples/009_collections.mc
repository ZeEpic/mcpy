def collections() {
    my_list = [] # this can contain anything seperated by commas like [24, 23, 42, 12]
    my_list += 1
    # You can only add things to the list of the same type as whatever already in it
    # If a number (or any type) is added to an empty list that's never been used before, it will become
    # a list of numbers permanently. If a player is added to an empty list, it will become a list of players
    first = my_list[0] # indexing starts at 0 and you can get the item and some index like this
    print(len(my_list)) # this prints the length of the list
    # If you want to check if a list contains an item, use this:
    if 23 in my_list {
        print("23 is in the list")
    } elif 24 not in my_list {
        print("24 is not in the list")
    }

    my_dictionary = {} # this is like a list, but with keys and values pairs
    my_dictionary["key"] = "value" # add to or change the dictionary like this
    # They operate the same way as lists do, but you can't have more than one of the same key.
    # You can have as many duplicate values as you want.
    # These work essentially the same as HashMaps in Java.

    # A very useful element of concise code is called list comprehension.
    # It's a way to create a list in one line of code.
    list_comp = [x for x in range(10)] # this creates a list of numbers from 0 to 9
    list_comp2 = [x for x in range(10) if x % 2 == 0] # this creates a list of even numbers from 0 to 9
}