package ar.edu.microprocesador

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows

class TestMicroprocessor : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("dado un microprocesador") {
        val micro : Microprocessor = MicroprocessorImpl()
        it("si quiero ejecutar un paso y no hay un programa cargado da error") {
            assertThrows<SystemException> { micro.step() }
        }
        it("si quiero ejecutar un paso con un programa cargado y no se inició debe dar error") {
            micro.loadProgram(ProgramBuilder()
                .NOP()
                .build()
            )
            assertThrows<SystemException> { micro.step() }
        }
        it("si quiero ejecutar un programa manualmente más allá de la última instrucción debe dar error") {
            micro.loadProgram(ProgramBuilder()
                .NOP()
                .build()
            )
            micro.start()
            micro.step()
            assertThrows<SystemException> { micro.step() }
        }
        it("si quiero ejecutar una instrucción que no existe debe dar error") {
            val instruccionInexistente = 150.toByte()
            micro.loadProgram(listOf(instruccionInexistente))
            assertThrows<SystemException> { micro.step() }
        }
        it("si quiero cargar un programa cuando hay otro en ejecución debe dar error") {
            val program = ProgramBuilder()
                .NOP()
                .build()
            micro.loadProgram(program)
            micro.start()
            assertThrows<SystemException> { micro.loadProgram(program) }
        }
        it("no puedo generar un programa vacío para cargarlo") {
            assertThrows<BusinessException> {
                micro.loadProgram(ProgramBuilder()
                    .build()
                )
            }
        }
        it("ejecuta correctamente el programa NOP") {
            micro.loadProgram(ProgramBuilder()
                .NOP()
                .NOP()
                .NOP()
                .build()
            )
            micro.run()

            micro.programCounter shouldBe 3
        }
        it("ejecuta correctamente una suma chica") {
            micro.loadProgram(ProgramBuilder()
                .LODV(10)
                .SWAP()
                .LODV(22)
                .ADD()
                .build()
            )
            micro.run()

            micro.programCounter shouldBe 4
            micro.aAcumulator shouldBe 32
            micro.bAcumulator shouldBe 0
        }
        it("ejecuta correctamente una suma grande") {
            micro.loadProgram(ProgramBuilder()
                .LODV(120)
                .SWAP()
                .LODV(15)
                .ADD()
                .build()
            )
            micro.run()

            micro.programCounter shouldBe 4
            micro.aAcumulator shouldBe 127
            micro.bAcumulator shouldBe 8
        }
        it("podemos deshacer la instrucción SWAP") {
            micro.loadProgram(ProgramBuilder()
                .LODV(25)
                .SWAP()
                .build()
            )
            micro.start()
            micro.step()
            val swap = micro.step()

            micro.programCounter shouldBe 2
            micro.aAcumulator shouldBe 0
            micro.bAcumulator shouldBe 25

            swap.undo(micro)

            micro.programCounter shouldBe 1
            micro.aAcumulator shouldBe 25
            micro.bAcumulator shouldBe 0
        }
    }
})