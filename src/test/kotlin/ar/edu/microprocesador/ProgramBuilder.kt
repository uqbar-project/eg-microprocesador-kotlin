package ar.edu.microprocesador

class ProgramBuilder {
    val program = mutableListOf<Byte>()

    fun LODV(value: Int): ProgramBuilder {
        program.add(9)
        program.add(value.toByte())
        return this
    }

    fun SWAP(): ProgramBuilder {
        program.add(5)
        return this
    }

    fun ADD(): ProgramBuilder {
        program.add(2)
        return this
    }

    fun NOP(): ProgramBuilder {
        program.add(1)
        return this
    }

    fun build(): List<Byte> {
        if (program.isEmpty()) {
            throw BusinessException("El programa no puede estar vacío")
        }
        return program
    }

}