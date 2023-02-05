def for_each_loop() {
    list = range(15)
    for i in list {
        if (len(players) < i) {
            players[i].send("Hello, world!")
        }
    }
}