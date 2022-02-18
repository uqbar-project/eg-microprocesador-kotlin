package ar.edu.unsam.algo2.microprocesador

import kotlin.comparisons.minOf
import kotlin.comparisons.maxOf

abstract class Instruction : Cloneable {
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

    open fun prepare(programIterator: ProgramIterator) {}

    public override fun clone() = super.clone() as Instruction
}

class NOP : Instruction() {
    override fun doExecute(micro: Microprocessor) {
        // no hacemos nada
    }

}

class LODV(var value: Byte) : Instruction() {
    override fun doExecute(micro: Microprocessor) {
        micro.aAcumulator = value
    }
    override fun prepare(programIterator: ProgramIterator) {
        value = programIterator.nextValue()
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
