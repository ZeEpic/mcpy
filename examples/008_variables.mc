# Variables can be outside of a function like so:
x = 10
# Variables don't need types, it's inferred when compiled

def some_function(z: num) {
    # Local variables are only available inside the function
    y = 20.5
    print(x + y + z)
}
