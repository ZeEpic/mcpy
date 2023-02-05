match expression {
    case something {
        # code runs if expression matches this case
    }
    case something_else {
       # code
   }
    case another_thing {
        # code
    }
    case _ {
        # default code, if nothing else is matched
    }
}