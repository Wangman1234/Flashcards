package flashcards

import java.io.File
import java.lang.IndexOutOfBoundsException
import kotlin.system.exitProcess


var cards = mutableMapOf<String, String>()
var cardList = mutableSetOf<String>()
var input1: String = ""
var input2: String = ""
var number: Int = 0
var stats: MutableMap<String, Int> = mutableMapOf("" to 0)
var export = 0

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        for (i in args.indices) {
            if (i % 2 == 0) {
                when (args[i]) {
                    "-import" -> importCards(args[i + 1])
                    "-export" -> export = i + 1
                }
            }
        }
    }
    while (true) {
        action()
        when (input1) {
            "add" -> addCard()
            "remove" -> removeCard()
            "import" -> {
                println("File name:")
                input1 = readLine()!!
                importCards(input1)
            }
            "export" -> {
                println("File name:")
                input1 = readLine()!!
                exportCards(input1)
            }
            "ask" -> ask()
            "exit" -> exit(args)
            "list" -> list()
            "log" -> log()
            "hardest card" -> showHardTask()
            "reset stats" -> resetStats()
            else -> println("Action does not exist.")
        }
    }
}

fun resetStats() {
    stats = mutableMapOf("" to 0)
    println("Card statistics have been reset.")
}

fun showHardTask() {
    val largest = mutableSetOf("")
    var largestInt = 0
    var largestString = ""
    if (stats.size == 1) {
        println("There are no cards with errors.")
    } else {
        for (i in stats) {
            if (i.value > largestInt) {
                largest.clear()
                largest.add(i.key)
                largestInt = i.value
            } else if (i.value == largestInt) {
                largest.add(i.key)
            } else {
                continue
            }
        }
        if (largest.size == 1) {
            for (i in largest) {
                largestString = i
            }
            println("The hardest card is \"$largestString\". You have $largestInt errors answering it.")
        } else {
            for (i in largest) {
                if (largestString.isBlank()) {
                    largestString = "\"$i\""
                } else largestString += ", \"$i\""
            }
            println("The hardest cards are ${largestString/*.toString().substringAfter("[").substringBefore("]")*/}. You have $largestInt errors answering them.")
        }
    }
}

fun log() {
    println("File name:")
    input1 = readLine()!!
    File(input1).writeText(stats.toString())
    println("The log has been saved.")
}

fun addCard() {
    println("The card:")
    input1 = readLine()!!
    if (cards.containsValue(input1)) {
        println("The card, \"$input1\" already exists. Try again:")
        return
    }
    println("The definition of the card:")
    input2 = readLine()!!
    // Key is definition, Value is term
    if (cards.containsKey(input2)) {
        println("The definition \"$input2\" already exists. Try again:")
        return
    } else cards[input2] = input1
    cardList.add(input2)
    println("The pair (\"$input2\":\"$input1\") has been added.")
}

fun removeCard() {
    println("Which card?")
    input1 = readLine()!!
    if (cards.containsValue(input1)) {
        cardList.remove(cards.getKey(input1))
        stats.remove(input1)
        cards.values.remove(input1)
        println("The card has been removed.")
    } else println("Can't remove \"$input1\": there is no such card.")
}

fun ask() {
    println("How many times to ask?")
    number = readLine()!!.toInt()
    for (j in 0 until number) {
        input2 = cardList.random()
        checkCard(input2)
    }
}

fun checkCard(i: String) {
    println("Print the definition of \"${cards[i]}\":")
    input1 = readLine()!!
    when {
        input1 == i -> {
            println("Correct!")
        }
        cards.containsKey(input1) -> {
            println("Wrong. The right answer is \"${i}\", but your definition is correct for \"${cards[input1]}\"")
            addT(i)
        }
        else -> {
            println("Wrong. The right answer is \"${i}\".")
            addT(i)
        }
    }
}

fun action() {
    println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
    input1 = readLine()!!
}

fun importCards(input: String) {
    // val pSize = cards.size
    var count = 0
    val key = mutableSetOf<String>()
    val value = mutableSetOf<String>()
    try {
        val input3 = File(input).readText().split("\n").toTypedArray()
        val cs = input3[0].stringToMap()
        for (i in cs) {
            if (cards.containsValue(i.value)) {
                key.add(cards.getKey(i.value))
                value.add(i.value)
                cards.remove(cards.getKey(i.value))
            }
            cards[i.key] = i.value
            count++
        }
        // cards.add(input3[0].stringToMap())
        val cList = input3[1].stringToList()
        for (num in key) {
            cardList.remove(num)
        }
        for (i in cList.indices) {
            /*if (key.contains(cList[i])) {
                cardList.remove()
            }*/
            cardList.add(cList[i])
        }
        val sList = input3[2].stringToMap()
        for (i in sList) {
            stats[i.key] = i.value.toInt()
        }
        // cardList.add(input3[1].stringToList())
        // cardList = input3[1].split(",").map { it.trim() }.toMutableList()
        println("$count cards have been loaded.")
    } catch (e: NoSuchFileException) {
        println("File not found.")
    }
}

fun exportCards(input: String) {
    var cardsToString: String? = ""
    var cardListToString: String? = ""
    var statsToString: String? = ""
    var count = 0
    for (i in cards) {
        cardsToString += if (cardsToString.isNullOrBlank()) {
            "${i.key}=${i.value}"
        } else ", ${i.key}=${i.value}"
    }
    for (i in cardList) {
        cardListToString += if (cardListToString.isNullOrBlank()) {
            i
        } else ", $i"
        count++
    }
    for (i in stats) {
        statsToString += if (statsToString.isNullOrBlank()) {
            "${i.key}=${i.value}"
        } else ", ${i.key}=${i.value}"
    }
    cardsToString?.let { File(input).writeText(it) }
    File(input).appendText("\n$cardListToString")
    File(input).appendText("\n$statsToString")
    println("$count cards have been saved.")
}

fun exit(input: Array<String>) {
    println("Bye bye!")
    if (export != 0) {
        try {
            exportCards(input[export])
        } catch (e: IndexOutOfBoundsException) {
            exitProcess(0)
        }
    }
    exitProcess(0)
}

fun String.stringToMap(): MutableMap<String, String> {
    val gString = this.filter { it != '[' || it != ']' || it != '{' || it != '}' }
    val listEnd = gString.split(", ").toTypedArray()
    val finalMap = mutableMapOf<String, String>()
    for (i in listEnd.indices) {
        val kit = listEnd[i].split("=")
        finalMap[kit[0]] = kit[1]
    }
    return finalMap
}

fun String.stringToList(): MutableList<String> {
    val gString = this.filter { it != '[' || it != ']' || it != '{' || it != '}' }
    return gString.split(", ").toTypedArray().toMutableList()
}

fun list() {
    println(cards)
    println(cardList)
    println(stats)
}

fun MutableMap<String, String>.getKey(value: String): String {
    var key = ""
    for (i in this) {
        if (i.value == value) {
            key = i.key
        }
    }
    return key
}

fun addT(i: String) {
    val cNum: Int?
    if (!stats.containsKey(cards[i])) {
        stats[cards[i].toString()] = 1
    } else {
        cNum = stats[cards[i]]
        if (cNum != null) {
            stats[cards[i].toString()] = cNum + 1
        }
    }
}
