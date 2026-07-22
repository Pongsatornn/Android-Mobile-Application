
package example.myapp.decor


data class Decoration(val rocks: String)


data class Decoration2(val rocks: String, val wood: String, val diver: String)

fun makeDecorations() {

    val decoration1 = Decoration("granite")
    println(decoration1)

    val decoration2 = Decoration("slate")
    println(decoration2)

    val decoration3 = Decoration("slate")
    println(decoration3)

    println(decoration1.equals(decoration2))   // false (granite != slate)
    println(decoration3.equals(decoration2))   // true  (slate == slate)


    val d5 = Decoration2("crystal", "wood", "diver")
    println(d5)

    val (rock, wood, diver) = d5
    println(rock)
    println(wood)
    println(diver)

    val (rock2, _, diver2) = d5
    println("$rock2 $diver2")
}

fun main() {
    makeDecorations()
}