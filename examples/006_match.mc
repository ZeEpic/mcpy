def match_me(thing: str) {
    match thing {
        case "something" {
            # code runs if expression matches this case
        }
        case "something else" {
           # code
       }
        case "another thing" {
            # code
        }
        case _ {
            # default code, if nothing else is matched
        }
    }
}