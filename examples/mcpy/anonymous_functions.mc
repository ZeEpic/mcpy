def anonymous_functions() {
    # Anonymous functions are functions that are not defined with "def"
    # They are useful for passing functions as arguments to other functions
    # They are also useful for defining functions in a single line
    # They can include parameters and may return a value
    # In a case where the last parameter is a function, the parentheses can be moved before the { }
    # and even removed all together in this case
    example {
        x, y -> "Your calculation is {x * y + x - 1 }"  # that arrow is important whenever you have parameters
    }
    example2({ ["orange", "banana", "apple"] }, { print(random(2, 100)) }) {
        name -> print("Hello, {name}!")
    }
}

# func1 is a function that takes two numbers and returns a string
def example(func1: function[num, num, str]) {
    for x in range(5):
        for y in range(5):
            print(func1(x, y))
}

# funny_list is a function that returns a list of strings
# randomizer is a function that takes no arguments and returns nothing
# greeting is a function that takes a string and returns nothing
def example2(funny_list: function[list[str]], randomizer: function, greeting: function[str, None]) {
    greeting("world")
    for string in funny_list():
        print("You get...")
        randomizer()
        print("{string}s")
}