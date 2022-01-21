package ar.edu.unsam.algo2.microprocesador

interface Microprocessor {

    /**
     * carga el programa en memoria, el microcontrolador debe estar detenido
     */
    fun loadProgram(program: List<Byte>)


    // control de programa
    /**
     * Ejecuta un programa cargado en memoria
     */
    fun run()

    /**
     * Borra la memoria de datos y comienza la ejecucion del programa cargado
     * actualmente
     */
    fun start()

    /**
     * Detiene el programa en ejecucion
     */
    fun stop()

    /**
     * Ejecuta la siguiente instruccion del programa actual
     */
    fun step(): Instruction

    /**
     * Inicializa el microcontrolador
     */
    fun reset()

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

    /**
     * Manejo de dirección de memoria de datos: getter y setter
     */
    fun getData(addr: Int): Byte
    fun setData(addr: Int, value: Byte)
    fun copy(): Microprocessor
    fun copyFrom(other: Microprocessor)
}

class MicroprocessorImpl : Microprocessor, Cloneable {
    override var aAcumulator: Byte = 0
    override var bAcumulator: Byte = 0
    override var programCounter: Byte = 0
    var data = mutableMapOf<Int, Byte>()
    var programStarted: Boolean = false

    lateinit var programIterator: ProgramIterator

    override fun loadProgram(program: List<Byte>) {
        if (this.programStarted) throw SystemException("Ya hay un programa en ejecución")
        this.reset()
        this.programIterator = ProgramIterator(program)
    }

    override fun start() {
        this.programStarted = true
    }

    override fun stop() {
        this.programStarted = false
    }

    override fun step(): Instruction {
        if (!programStarted) throw SystemException("El programa no está iniciado")
        if (!programIterator.hasNext()) throw SystemException("No hay más instrucciones para ejecutar")
        val proximaInstruccion = programIterator.next()
        proximaInstruccion.execute(this)
        return proximaInstruccion
    }

    override fun run() {
        this.start()
        while (programIterator.hasNext()) {
            this.step()
        }
        this.stop()
    }

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