package ar.edu.unsam.algo2.microprocesador

interface Microprocessor {
    /**
     * programacion: carga y ejecuta un conjunto de instrucciones en memoria
     */
    fun run(program: List<Instruction>)

    /**
     * Getters y setters de acumuladores A y B
     */
    var aAcumulator: Byte
    var bAcumulator: Byte

    /**
     * Manejo de program counter
     */
    fun advanceProgram()
    val programCounter: Byte

    fun reset()

    /**
     * Manejo de dirección de memoria de datos: getter y setter
     */
    fun getData(addr: Int): Byte
    fun setData(addr: Int, value: Byte)
    fun copy(): Microprocessor
    fun copyFrom(other: Microprocessor)
}

class MicroprocessorImpl : Microprocessor, Cloneable {
    override fun run(program: List<Instruction>) {
        program.forEach { instruction -> instruction.execute(this) }
    }

    override var aAcumulator: Byte = 0
    override var bAcumulator: Byte = 0
    override var programCounter: Byte = 0
    var data = mutableMapOf<Int, Byte>()

    override fun advanceProgram() {
        programCounter++
    }

    override fun reset() {
        aAcumulator = 0
        bAcumulator = 0
        programCounter = 0
        data.clear()
    }

    override fun getData(addr: Int): Byte = data[addr] ?: 0
    override fun setData(addr: Int, value: Byte) {
        data[addr] = value
    }

    override fun copy(): Microprocessor = this.clone() as Microprocessor

    override fun copyFrom(other: Microprocessor) {
        this.aAcumulator = other.aAcumulator
        this.bAcumulator = other.bAcumulator
        this.programCounter = other.programCounter
        (0..1023).forEach { i -> this.setData(i, other.getData(i))}
    }
}