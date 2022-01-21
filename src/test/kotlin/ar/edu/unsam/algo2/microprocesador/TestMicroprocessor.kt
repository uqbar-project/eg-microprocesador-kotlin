package ar.edu.unsam.algo2.microprocesador

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TestMicroprocessor : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("dado un microprocesador") {
        val micro : Microprocessor = MicroprocessorImpl()
        it("ejecuta correctamente el programa NOP") {
            micro.run(listOf(
                NOP(),
                NOP(),
                NOP()
            ))

            micro.programCounter shouldBe 3
        }
        it("ejecuta correctamente una suma chica") {
            micro.run(listOf(
                LODV(10),
                SWAP(),
                LODV(22),
                ADD()
            ))

            micro.programCounter shouldBe 4
            micro.aAcumulator shouldBe 32
            micro.bAcumulator shouldBe 0
        }
        it("ejecuta correctamente una suma grande") {
            micro.run(listOf(
                LODV(120),
                SWAP(),
                LODV(15),
                ADD()
            ))

            micro.programCounter shouldBe 4
            micro.aAcumulator shouldBe 127
            micro.bAcumulator shouldBe 8
        }
        it("podemos deshacer la instrucción SWAP") {
            val swap = SWAP()
            micro.run(listOf(
                LODV(25),
                swap
            ))

            micro.programCounter shouldBe 2
            micro.aAcumulator shouldBe 0
            micro.bAcumulator shouldBe 25

            swap.undo(micro)

            micro.programCounter shouldBe 1
            micro.aAcumulator shouldBe 25
            micro.bAcumulator shouldBe 0
        }
        it("ejecuta correctamente un programa con IFNZ - rama true") {
            micro.run(listOf(
                LODV(15),
                SWAP(),
                LODV(26),
                IFNZ(listOf(
                    ADD(),
                    SWAP()
                ))
            ))

            micro.programCounter shouldBe 6
            micro.aAcumulator shouldBe 0
            micro.bAcumulator shouldBe 41
        }
        it("ejecuta correctamente un programa con IFNZ - rama false") {
            micro.run(listOf(
                LODV(10),
                SWAP(),
                IFNZ(listOf(
                    SWAP()
                ))
            ))

            micro.programCounter shouldBe 3
            micro.aAcumulator shouldBe 0
            micro.bAcumulator shouldBe 10
        }
        it("ejecuta correctamente un programa con WHNZ que suma los primeros 4 números") {
            micro.run(listOf(
                // Total en address 1
                LODV(0),
                STR(1),
                // Indice se guardará en address 0
                LODV(4),
                WHNZ(listOf(
                    // Total = Total + Indice y queda en address 1
                    STR(0),
                    SWAP(),
                    LOD(1),
                    ADD(),
                    STR(1),
                    //
                    // Resto 1 a i
                    LODV(1),
                    SWAP(),
                    LOD(0), // recupero el valor del índice
                    SUB()
                )),
                LOD(1)     // deja el total en el acumulador A
            ))

            micro.aAcumulator shouldBe 10
            micro.bAcumulator shouldBe 0

            micro.reset()

            micro.programCounter shouldBe 0
            micro.aAcumulator shouldBe 0
            micro.bAcumulator shouldBe 0
            micro.getData(0) shouldBe 0
            micro.getData(1) shouldBe 0
        }

    }
})