package connectfour

data class Player(val name: String, val marker: Char, var points: Int = 0)

object ConnectFour {
    data class House(val column: Int, val row: Int, var status: Char = ' ')
    private val firstPlayer: Player
    private val secondPlayer: Player
    private var numberOfGames = 0

    object Board {
        var row = 6
        var column = 7
    }

    private val create = { row: Int, column: Int -> House(row, column) }
    private var allHouses = mutableListOf<House>()
    private val createAllHouses = {
        val houses = mutableListOf<House>()
        for (column in 1..Board.column) {
            for (row in Board.row downTo 1) {
                houses.add(create(column, row))
            }
        }
        houses
    }
    private var available = {
        val available = mutableListOf<House>()
        for (column in 1..Board.column) {
            loop@ for (house in allHouses) {
                if (house.column == column && house.status == ' ') {
                    available.add(house)
                    break@loop
                }
            }
        }
        available
    }
    private val evaluateInput = { input: String -> input.matches("[5-9](x|X)[5-9]".toRegex()) }
    private val printBoard = {
        for (column in 1..Board.column) {
            print(" $column")
        }
        println()
        for (row in 1..Board.row) {
            var line = "║"
            for (column in 1..Board.column) {
                line += "${findHouse(column, row)}║"
            }
            println(line)
        }
        var line = "╚═"
        for (column in 0 until Board.column - 1) line += "╩═"
        println("$line╝")
    }
    private val printStatus = { game: Int ->
        println(
            if (numberOfGames > 1 && game == 1) {
                "Total $numberOfGames games\nGame #$game"
            } else if (numberOfGames > 1) {
                "Game #$game"
            } else "Single game"
        )
    }


    init {
        println("Connect Four\nFirst player's name:")
        firstPlayer = Player(readLine()!!, 'o')
        println("Second player's name:")
        secondPlayer = Player(readLine()!!, '*')
        getBoard()
        getNumberOfGames()
        println("${firstPlayer.name} VS ${secondPlayer.name}")
        println("${Board.row} X ${Board.column} board")

    }

    private fun getBoard() {
        while (true) {
            println("Set the board dimensions (Rows x Columns)\nPress Enter for default (6 x 7)")
            val dimensions = readLine()!!.filter { it.isLetterOrDigit() }
            when {
                dimensions == "" -> break
                evaluateInput(dimensions) -> {
                    Board.column = dimensions[2].toString().toInt()
                    Board.row = dimensions[0].toString().toInt()
                    break
                }
                else -> {
                    println(
                        when {
                            dimensions.matches("([1-4]|\\d\\d)(x|X)\\d".toRegex()) -> "Board rows should be from 5 to 9"
                            dimensions.matches("\\d(x|X)([1-4]|\\d\\d)".toRegex()) -> "Board columns should be from 5 to 9"
                            else -> "Invalid input"
                        }
                    )
                }
            }
        }
    }

    private fun getNumberOfGames() {
        while (true) {
            println(
                "Do you want to play single or multiple games?\n" +
                        "For a single game, input 1 or press Enter\n" +
                        "Input a number of games:"
            )
            var number = readLine()!!
            if (number == "") {
                numberOfGames = 1
                break
            } else {
                try {
                    if (number.toInt() > 0) {
                        numberOfGames = number.toInt()
                        break
                    } else {
                        println("Invalid input")
                    }
                } catch (e: Exception) {
                    println("Invalid input")
                }
            }
        }
    }

    private fun findHouse(column: Int, row: Int): Char {
        for (house in allHouses) {
            if (house.column == column && house.row == row) return house.status
        }
        return 'E'
    }

    private fun findHouse(column: Int): House {
        for (house in available()) {
            if (house.column == column) return house
        }
        return House(-1, -1, 'E')
    }

    private fun definePlayersSequence(game: Int): MutableList<Player> {
        return if(game % 2 != 0) {
            mutableListOf(firstPlayer, secondPlayer)
        } else {
            mutableListOf(secondPlayer, firstPlayer)
        }
    }

    private fun verify(): String {
        // vertical
        for (column in 1..Board.column) {
            val houses = allHouses.filter { it.column == column }.sortedBy { it.row }
            for (j in 0..houses.size - 4) {
                if (houses[j].status != ' ' && houses[j].status == houses[j + 1].status && houses[j + 1].status == houses[j + 2].status && houses[j + 2].status == houses[j + 3].status) {
                    return "WINNER"
                }
            }
        }
        // horizontal
        for (row in 1..Board.row) {
            val houses = allHouses.filter { it.row == row }.sortedBy { it.column }
            for (j in 0..houses.size - 4) {
                if (houses[j].status != ' ' && houses[j].status == houses[j + 1].status && houses[j + 1].status == houses[j + 2].status && houses[j + 2].status == houses[j + 3].status) {

                    return "WINNER"
                }
            }
        }
        // diagonal
        for (column in 1..Board.column - 3) {
            for (row in 4..Board.row) {
                if (findHouse(column, row) != ' '
                    && findHouse(column, row) == findHouse(column + 1, row - 1)
                    && findHouse(column + 1, row - 1) == findHouse(column + 2, row - 2)
                    && findHouse(column + 2, row - 2) == findHouse(column + 3, row - 3)
                ) return "WINNER"
                //println(("($column, $row), ($column, $row), ($column, $row), ($column, $row) "))
            }
        }
        // diagonal
        for (column in 1..Board.column - 3) {
            for (row in 1..Board.row - 3) {
                if (findHouse(column, row) != ' '
                    && findHouse(column, row) == findHouse(column + 1, row + 1)
                    && findHouse(column + 1, row + 1) == findHouse(column + 2, row + 2)
                    && findHouse(column + 2, row + 2) == findHouse(column + 3, row + 3)
                ) return "WINNER"
                //println(("($column, $row), ($column, $row), ($column, $row), ($column, $row) "))
            }
        }
        if (available().size == 0) return "DRAW"
        return "NO WINNER"
    }

    fun playGame() {
        loop@ for (game in 1..numberOfGames) {
            printStatus(game)
            allHouses = createAllHouses()
            printBoard()
            var players = definePlayersSequence(game)
            loop0@ while (true) {
                for (player in players) {
                    loop1@ while (true) {
                        println("${player.name}'s turn:")
                        val input = readLine()!!
                        if (input == "end") break@loop
                        try {
                            val house = findHouse(input.toInt())
                            if (house.status != 'E') {
                                house.status = player.marker
                                printBoard()
                                when (verify()) {
                                    "WINNER" -> {
                                        println("Player ${player.name} won")
                                        player.points += 2
                                        break@loop0
                                    }
                                    "DRAW" -> {
                                        println("It is a draw")
                                        for (player in players) {
                                            player.points += 1
                                        }
                                        break@loop0
                                    }
                                    else -> break@loop1
                                }
                            } else {
                                if (input.toInt() !in 1..Board.column) println("The column number is out of range (1 - ${Board.column})")
                                else println("Column ${input.toInt()} is full")
                            }
                        } catch (e: Exception) {
                            println("Incorrect column number")
                        }
                    }
                }
            }
            println("Score\n${firstPlayer.name}: ${firstPlayer.points} ${secondPlayer.name}: ${secondPlayer.points}")
        }
        println("Game over!")
    }

}

fun main() {
    ConnectFour.playGame()
}