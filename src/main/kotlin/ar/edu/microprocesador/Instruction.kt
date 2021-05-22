package ar.edu.microprocesador

import kotlin.comparisons.minOf
import kotlin.comparisons.maxOf

abstract class Instruction {
    lateinit var microBefore: Microprocessor

    fun execute(micro: Microprocessor) {
        microBefore = micro.copy()
        micro.advanceProgram()
        this.doExecute(micro)
    }

    abstract fun doExecute(micro: Microprocessor)

    fun undo(micro: Microprocessor) {
        micro.copyFrom(microBefore)
    }
}

class NOP : Instruction() {
    override fun doExecute(micro: Microprocessor) {
        // no hacemos nada
    }

}

class LODV(val value: Byte) : Instruction() {
    override fun doExecute(micro: Microprocessor) {
        micro.aAcumulator = value
    }
}

class SWAP : Instruction() {
    override fun doExecute(micro: Microprocessor) {
        val buffer = micro.aAcumulator
        micro.aAcumulator = micro.bAcumulator
        micro.bAcumulator = buffer
    }
}

class ADD : Instruction() {
    override fun doExecute(micro: Microprocessor) {
        val suma = micro.aAcumulator + micro.bAcumulator
        val maxValue = Byte.MAX_VALUE.toInt()

        // en el acumulador A queda
        //    10 + 22 = 32    vs. 127  ==> 32
        //    120 + 15 = 135  vs. 127  ==> 127
        val aAcumulator = minOf(suma, maxValue)

        // en el acumulador A queda
        //    10 + 22 = 32, 127 - 32 < 0 ==> 0
        //    120 + 15 = 135 - 127 > 0   ==> 8
        val bAcumulator = maxOf(0, suma - maxValue)

        micro.aAcumulator = aAcumulator.toByte()
        micro.bAcumulator = bAcumulator.toByte()
    }
}

abstract class CompoundNotZeroInstruction(open val instructions: List<Instruction>) : Instruction() {

    override fun doExecute(micro: Microprocessor) {
        micro.run(instructions)
    }

    fun Microprocessor.notZero() = this.aAcumulator.toInt() != 0
}

class IFNZ(override val instructions: List<Instruction>) : CompoundNotZeroInstruction(instructions) {

    override fun doExecute(micro: Microprocessor) {
        if (micro.notZero()) {
            super.doExecute(micro)
        }
    }
}

class WHNZ(override val instructions: List<Instruction>) : CompoundNotZeroInstruction(instructions) {

    override fun doExecute(micro: Microprocessor) {
        while (micro.notZero()) {
            super.doExecute(micro)
        }
    }
}

// Instrucciones para testear el while
class STR(val address: Int) : Instruction() {
    override fun doExecute(micro: Microprocessor) {
        micro.setData(address, micro.aAcumulator)
    }
}

class LOD(val address: Int) : Instruction() {
    override fun doExecute(micro: Microprocessor) {
        micro.aAcumulator = micro.getData(address)
    }
}

class SUB : Instruction() {
    override fun doExecute(micro: Microprocessor) {
        micro.aAcumulator = (micro.aAcumulator - micro.bAcumulator).toByte()
        micro.bAcumulator = 0
    }

}