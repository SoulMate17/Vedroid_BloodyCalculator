package tech.kotelnikov.calculator

import java.math.BigInteger
import kotlin.system.exitProcess


class Calculator {

val variableMap = mutableMapOf<String, BigInteger>()

fun run(string: String) {
    if (checkIfItsVariableCall(string)) return callForVariable(string)
    if (checkIfItsSingleNumber(string)) return println(string)
    if (checkIfItsValidVariableAssignmentExpression(string)) return whiteVariable(string)
    if (checkIfItsValidMathExpression(string)) {
        if (!validateVariables(string)) {
            println("Invalid expression")
        } else {
            println(prepareStringAndCalculate(string))
        }
    } else println("Invalid expression")
}

fun prepareStringAndCalculate(string: String) = calculateFromPostfix(fromInfixToPostfix(replaceDuplicatedPlusMinus(string)))

fun replaceDuplicatedPlusMinus(string: String) = string.replace("[+]+".toRegex(), "+").replace("--".toRegex(), "+")

fun checkIfItsValidVariableAssignmentExpression(string: String): Boolean {
    val split = string.replace("\\s+".toRegex(), "").split("=")
    return if (isValidAssignment(string)) {
        if (isValidNameOfVariable(split[0])) {
            true
        } else {
            println("Invalid identifier")
            false
        }
    } else {
        false
    }
}

fun isHigherPrecedenceThen(first: String, second: String): Boolean {
    return ("*/".contains(first) && "-+".contains(second))
//            || ("^".contains(first) && !"^".contains(second))
}

fun calculateFromPostfix(string: String): String {
    val stack = mutableListOf<String>()
    val operands = cutStringBySpaces(string)
    for (operand in operands) {
        if (isNumber(operand)) {
            stack.add(operand)
        }
        if (isValidNameOfVariable(operand)) {
            stack.add(variableMap[operand].toString())
        }
        if (isOperator(operand)) {
            val second = stack.removeAt(stack.size - 1)
            val first = stack.removeAt(stack.size - 1)
            val mathResult = performMath(BigInteger(first), BigInteger(second), operand)
            stack.add(mathResult.toString())
        }
    }
    return stack[stack.size-1]
}

fun performMath(int1: BigInteger, int2: BigInteger, operator: String): BigInteger {
    when (operator) {
        "*" -> return int1 * int2
        "/" -> return int1 / int2
        "+" -> return int1 + int2
        "-" -> return int1 - int2
    }
    throw UnsupportedOperationException("Wrong operator")
}

fun fromInfixToPostfix(string: String): String {
    val split = cutStringBySymbols(string)
    var result = ""
    val stack = mutableListOf<String>()

    for (element in split) {
        if (element.matches("[a-zA-Z0-9]+".toRegex())) {
            result += "$element "
        } else {
            if (stack.isEmpty() || stack[stack.size - 1] == "(") {
                stack.add(element)
                continue
            }
            if (isHigherPrecedenceThen(element, stack[stack.size - 1])) {
                stack.add(element)
            } else {
                if (element == "(") {
                    stack.add(element)
                    continue
                }
                if (element == ")") {
                    while (stack[stack.size - 1] != "(") {
                        val pop = stack[stack.size - 1]
                        stack.removeAt(stack.size - 1)
                        result += "$pop "
                    }
                    stack.removeAt(stack.size - 1)
                    continue
                }
                while (stack.size != 0 && stack[stack.size - 1] != "(" && !isHigherPrecedenceThen(element, stack[stack.size - 1])) {
                    val pop = stack[stack.size - 1]
                    stack.removeAt(stack.size - 1)
                    result += "$pop "
                }
                stack.add(element)
            }

        }
    }
    for (unit in stack.reversed()) {
        result += "$unit "
    }
    return result
}

fun doHelp() = println("White expression line 5 + 7 -9, get the result!")

fun doExit() {
    println("Bye!")
    exitProcess(0)
}

fun cutStringBySpaces(string: String) = string.replace("\\s+".toRegex(), " ").split(" ")

fun cutStringBySymbols(string: String): List<String> {
    val list = arrayListOf<String>()
    val cutStringBySpaces = cutStringBySpaces(string.replace("(", " ( ").replace(")", " ) "))
    for (cut in cutStringBySpaces) {
        if (cut.matches("[a-zA-Z0-9]+".toRegex())) list.add(cut)
        else {
            for (symbol in cut) {
                list.add(""+symbol)
            }
        }
    }
    return list
}

fun callForVariable(string: String) =
    println(if (variableMap.containsKey(string)) variableMap[string] else "Unknown variable")


fun whiteVariable(string: String) {
    val split = string.replace("\\s+".toRegex(), "").split("=")
    val varName = split[0]
    val varValue = split[1]
    when {
        variableMap.containsKey(varValue) -> {
            variableMap[varName] = variableMap[varValue]!!
        }
        varValue.matches("\\d+".toRegex()) -> {
            variableMap[varName] = BigInteger(varValue)
        }
        else -> println("Unknown variable")
    }
}

fun checkIfItsSingleNumber(string: String): Boolean = string.matches("^[-+]?[0-9]+$".toRegex())

fun checkIfItsVariableCall(string: String): Boolean = string.matches("^[a-zA-Z]+$".toRegex())

fun validateVariables(string: String): Boolean {
    val varsToCheck = string.split(" ").filter { s -> s.matches("[a-zA-Z]+".toRegex()) }
    for (v in varsToCheck) {
        if (!variableMap.containsKey(v)) return false
    }
    return true
}

fun isOperator(string: String): Boolean = string.matches("[+\\-^*/]".toRegex())

fun isNumber(string: String): Boolean = string.matches("^[0-9]+".toRegex())

fun isValidNameOfVariable(string: String): Boolean = string.matches("^[a-zA-Z]+".toRegex())

fun isValidAssignment(string: String): Boolean = string.matches("^[a-zA-Z]+\\s*=\\s*([a-zA-Z]+|[0-9]+)$".toRegex())

fun checkIfItsValidMathExpression(string: String): Boolean {
    val matchPattern = string.matches("^(\\w+|\\s|\\(|\\)|[+\\-*\\/])+".toRegex())
    val matchBraces = string.count { c -> c == '(' } == string.count { c -> c == ')' }
    val matchMultiplication = !string.contains("\\*\\*+".toRegex())
    val matchDivision = !string.contains("//+".toRegex())
    return matchPattern && matchBraces && matchMultiplication && matchDivision
}
}