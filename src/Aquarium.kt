/**
 * Lesson 3.1: Classes and objects in Kotlin
 * หัวข้อ 3-6: Create a class, Add class constructors,
 * Visibility modifiers, Subclasses and inheritance
 */
package example.myapp

import java.lang.Math.PI

// หัวข้อ 3-4: สร้างคลาส Aquarium พร้อม primary constructor และค่า default
// หัวข้อ 6: ใส่ open เพื่อให้สืบทอด (subclass) ได้
open class Aquarium(open var length: Int = 100, open var width: Int = 20, open var height: Int = 40) {

    // หัวข้อ 4 Step 2: init blocks ทำงานตามลำดับที่ประกาศ
    init {
        println("aquarium initializing")
    }

    // หัวข้อ 4 Step 4-5: property getter/setter ของ volume (คำนวณจากขนาดตู้)
    open var volume: Int
        get() = width * height * length / 1000  // 1000 cm^3 = 1 l
        set(value) {
            height = (value * 1000) / (width * length)
        }

    // หัวข้อ 6 Step 1: property รูปทรงและปริมาณน้ำ (90% ของ volume)
    open val shape = "rectangle"

    open var water: Double = 0.0
        get() = volume * 0.9

    // หัวข้อ 4 Step 3: secondary constructor คำนวณความสูงจากจำนวนปลา
    constructor(numberOfFish: Int) : this() {
        // ปลา 1 ตัวใช้น้ำ 2,000 cm^3 + เผื่อพื้นที่กันน้ำล้น 10%
        val tank = numberOfFish * 2000 * 1.1
        // คำนวณความสูงที่ต้องใช้
        height = (tank / (length * width)).toInt()
    }

    // หัวข้อ 3 Step 4 + หัวข้อ 6: เมธอดพิมพ์ขนาดตู้ รูปทรง และปริมาณน้ำ
    fun printSize() {
        println(shape)
        println("Width: $width cm " +
                "Length: $length cm " +
                "Height: $height cm ")
        // 1 l = 1000 cm^3
        println("Volume: $volume l Water: $water l (${water / volume * 100.0}% full)")
    }
}

// หัวข้อ 6 Step 2: subclass TowerTank (ตู้ทรงกระบอก) override ค่าต่างๆ จาก Aquarium
class TowerTank(override var height: Int, var diameter: Int) :
    Aquarium(height = height, width = diameter, length = diameter) {

    override var volume: Int
        // พื้นที่วงรี = π * r1 * r2
        get() = (width / 2 * length / 2 * height / 1000 * PI).toInt()
        set(value) {
            height = ((value * 1000 / PI) / (width / 2 * length / 2)).toInt()
        }

    // ตู้ทรงกระบอกใส่น้ำ 80% ของ volume
    override var water = volume * 0.8

    override val shape = "cylinder"
}