package tech.kotelnikov.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {

    val calculator : Calculator = Calculator()
    var lastNumeric = false
    var lastDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onOperator(view: View) {
        if (tvInput.text.isEmpty() && (view as Button).text == "-") {
            tvInput.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        } else if (lastNumeric && !isOperatorAdded(tvInput.text.toString())) {
            tvInput.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        }
    }

    fun onDigit(view: View) {
        var buttonText = (view as Button).text
        tvInput.append(buttonText)
        lastNumeric = true
    }

    fun onClear(view: View) {
        tvInput.text = ""
        lastDot = false
        lastNumeric = false
    }

    fun onDecimalPoint(view: View) {
        if (lastNumeric && !lastDot) {
            tvInput.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    fun onEqual(view: View) {
        if (lastNumeric) {
            var tvValue = tvInput.text.toString()
            var prefix = ""

            try {
                if (tvValue.startsWith("-")) {
                    prefix = "-"
                    tvValue = tvValue.substring(1)
                }

                when {
                    tvValue.contains("-") -> {
                        val split = tvValue.split("-")
                        var one = split[0]
                        val two = split[1]

                        if (prefix.isNotEmpty()) {
                            one = prefix + one
                        }

                        tvInput.text = "${one.toDouble() - two.toDouble()}".removeZeroAfterDot()
                    }
                    tvValue.contains("+") -> {
                        val split = tvValue.split("+")
                        var one = split[0]
                        val two = split[1]

                        if (prefix.isNotEmpty()) {
                            one = prefix + one
                        }

                        tvInput.text = "${one.toDouble() + two.toDouble()}".removeZeroAfterDot()
                    }
                    tvValue.contains("*") -> {
                        val split = tvValue.split("*")
                        var one = split[0]
                        val two = split[1]

                        if (prefix.isNotEmpty()) {
                            one = prefix + one
                        }

                        tvInput.text = "${one.toDouble() * two.toDouble()}".removeZeroAfterDot()
                    }
                    tvValue.contains("/") -> {
                        val split = tvValue.split("/")
                        var one = split[0]
                        val two = split[1]

                        if (two != "0") {
                            if (prefix.isNotEmpty()) {
                                one = prefix + one
                            }

                            tvInput.text = "${one.toDouble() / two.toDouble()}".removeZeroAfterDot()
                        } else tvInput.text = "Division by 0"
                    }
                }
            } catch (e: ArithmeticException) {
                e.printStackTrace()
            }
        }
    }

    private fun isOperatorAdded(value: String): Boolean {
        return if (value.startsWith("-")) {
            false
        } else {
            value.contains("/") || value.contains("*")
                    || value.contains("+") || value.contains("-")
        }
    }

    private fun String.removeZeroAfterDot() : String {
        return if (this.endsWith(".0")) {
            this.substring(0, this.length-2)
        } else this
    }
}