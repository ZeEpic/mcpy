def if_statement(arg: num) {
    # You can use the 'and' and 'or' keywords to check for multiple conditions in one line
    if arg > 5 {
        print("More than 5.")
    elif arg == 5 { # note: this is the same as 'else if' in java
        print("It's 5!")
    elif arg < 5 and arg >= 3 {
        print("Between 3 and 5.")
    }
    } else {
        print("Less than 3.")
    }
}