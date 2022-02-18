package ar.edu.unsam.algo2.microprocesador

class ProgramIterator(val program: List<Byte>) : Iterator<Instruction> {
    var index: Int = 0

    override fun hasNext() = index < program.size

    override fun next(): Instruction {
        val instructionCode = nextValue()
        return InstructionFactory.getInstruction(this, instructionCode)
    }

    fun nextValue() = program[index++]

}

object InstructionFactory {
    val instrucciones = mutableMapOf(1 to NOP(), 2 to ADD(), 5 to SWAP(), 9 to LODV(0))

    fun getInstruction(programIterator: ProgramIterator, codigoInstruccion: Byte): Instruction {
        val instruccionAEjecutar = instrucciones[codigoInstruccion.toInt()]?.clone() ?: throw SystemException("La instrucción de código $codigoInstruccion no es reconocida")
        instruccionAEjecutar.prepare(programIterator)
        return instruccionAEjecutar
    }
}
