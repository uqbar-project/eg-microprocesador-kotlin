package ar.edu.microprocesador

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TestMicroprocessor : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("dado un microprocesador") {
        val micro : Microprocessor = MicroprocessorImpl()
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