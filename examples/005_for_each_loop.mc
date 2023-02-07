def for_each_loop(p: player) {
    fruits = ["apples", "bananas", "oranges"]
    for fruit in fruits {
        p.send("You just got some {fruit}!")
    }
}