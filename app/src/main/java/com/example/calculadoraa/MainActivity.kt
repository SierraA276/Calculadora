package com.example.calculadoraa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.lang.Exception
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    var TvRes: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TvRes = findViewById(R.id.TvRes)
    }

    // Función para manejar los clicks de los botones
    fun calcular(view: View) {
        val boton = view as Button
        val textoBoton = boton.text.toString()

        if (textoBoton == "=") {
            // Ejecuta la evaluación de la expresión
            try {
                val resultado = eval(TvRes?.text.toString())
                TvRes?.text = resultado.toString()
            } catch (e: Exception) {
                TvRes?.text = "Error"
            }
        } else if (textoBoton == "Reset") {
            // Resetea la pantalla
            TvRes?.text = "0"
        } else {
            // Concatenación de los números u operadores
            val concatenar = TvRes?.text.toString() + textoBoton
            TvRes?.text = eliminar0(concatenar)
        }
    }

    // Función para eliminar los ceros a la izquierda
    fun eliminar0(str: String): String {
        return if (str.startsWith("0") && str.length > 1 && str[1] != '.') {
            str.substring(1) // Elimina el primer 0 si no es parte de un número decimal
        } else {
            str
        }
    }

    // Evaluador de expresiones matemáticas
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].toInt() else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.toInt()) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.toInt())) x += parseTerm() // Suma
                    else if (eat('-'.toInt())) x -= parseTerm() // Resta
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.toInt())) x *= parseFactor() // Multiplicación
                    else if (eat('/'.toInt())) x /= parseFactor() // División
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.toInt())) return parseFactor() // Operador unario positivo
                if (eat('-'.toInt())) return -parseFactor() // Operador unario negativo

                var x: Double
                val startPos = pos
                if (eat('('.toInt())) { // Paréntesis
                    x = parseExpression()
                    eat(')'.toInt())
                } else if (ch in '0'.toInt()..'9'.toInt() || ch == '.'.toInt()) { // Números
                    while (ch in '0'.toInt()..'9'.toInt() || ch == '.'.toInt()) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }

                if (eat('^'.toInt())) x = x.pow(parseFactor()) // Exponenciación
                else if (eat('√'.toInt())) x = sqrt(x) // Raíz cuadrada
                else if (eat('l'.toInt())) { // Logaritmo
                    eat('o'.toInt())
                    eat('g'.toInt())
                    x = log10(x)
                }
                return x
            }
        }.parse()
    }
}
