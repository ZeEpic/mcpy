def if_statement(arg: num) {
    # While not shown here, you can use the 'and' and 'or' keywords to check for multiple conditions in one line
    if arg > 5 {
        print("More than 5.")
    elif arg == 5 {
        print("It's 5!")
    } else {
        print("Less than 5.")
    }
}