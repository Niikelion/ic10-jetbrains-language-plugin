package com.niikelion.ic10_language.test.logic

import com.intellij.testFramework.fixtures.BareTestFixtureTestCase
import com.niikelion.ic10_language.logic.Instructions
import com.niikelion.ic10_language.logic.StationeersRegistryData
import kotlin.test.Test
import kotlin.test.assertTrue

class Instructions: BareTestFixtureTestCase() {
    @Test
    fun `All instructions from stationpedia are defined`() {
        val scriptCommandNames = StationeersRegistryData.data.scriptCommands.keys
        val definedInstructions = Instructions.all.map { it.name }.toSet() + setOf("label")

        val missing = scriptCommandNames - definedInstructions
        assertTrue(missing.isEmpty(), "Instructions in stationpedia.json but not defined: $missing")
    }

    @Test
    fun `All instructions are implemented`() {
        val notImplemented = Instructions.all.filter { it.action == null }.map { it.name }
        assertTrue(notImplemented.isEmpty(), "Instructions defined, but not implemented: $notImplemented")
    }

    // Tests below are based on running the scripts in-game

    @Test
    fun `Instruction add matches the in-game behaviour`() {
        simulate {
            exec("add", reg("r0"), num(1), num(-10))
            assert { register("r0", -9) }
        }
    }

    @Test
    fun `Instruction div matches the in-game behaviour`() {
        simulate {
            exec("div", reg("r0"), num(1), num(2))
            assert {
                register("r0", 0.5)
            }
        }
        simulate {
            exec("div", reg("r0"), num(-1), num(2))
            assert {
                register("r0", -0.5)
            }
        }
        simulate {
            exec("div", reg("r0"), num(1), num(0))
            assert {
                register("r0", Double.POSITIVE_INFINITY, 0.0)
            }
        }
        simulate {
            exec("div", reg("r0"), num(-1), num(0))
            assert {
                register("r0", Double.NEGATIVE_INFINITY, 0.0)
            }
        }
        simulate {
            exec("div", reg("r0"), num(Double.POSITIVE_INFINITY), num(0))
            assert {
                register("r0", Double.POSITIVE_INFINITY, 0.0)
            }
        }
        simulate {
            exec("div", reg("r0"), num(Double.POSITIVE_INFINITY), num(1))
            assert {
                register("r0", Double.POSITIVE_INFINITY, 0.0)
            }
        }
        simulate {
            exec("div", reg("r0"), num(Double.POSITIVE_INFINITY), num( -1))
            assert {
                register("r0", Double.NEGATIVE_INFINITY, 0.0)
            }
        }
        simulate {
            exec("div", reg("r0"), num(Double.POSITIVE_INFINITY), num(Double.POSITIVE_INFINITY))
            assert {
                register("r0", Double.NaN, 0.0)
            }
        }
        simulate {
            exec("div", reg("r0"), num(Double.POSITIVE_INFINITY), num(Double.NEGATIVE_INFINITY))
            assert {
                register("r0", Double.NaN, 0.0)
            }
        }
    }

    @Test
    fun `Instruction mod matches the in-game behaviour`() {
        simulate {
            exec("mod", reg("r0"), num(10), num(3))
            assert {
                register("r0", 1)
            }
        }
        simulate {
            exec("mod", reg("r0"), num(1.3), num(1.2))
            assert {
                register("r0", 0.1)
            }
        }
        simulate {
            exec("mod", reg("r0"), num(-0.9), num(1))
            assert {
                register("r0", 0.1)
            }
        }
    }

    @Test
    fun `Instruction sra matches the in-game behaviour`() {
        // positive: same as logical shift
        simulate {
            exec("sra", reg("r0"), num(16), num(2))
            assert { register("r0", 4) }
        }
        // negative: sign bit is copied into vacated bits, result stays negative
        simulate {
            exec("sra", reg("r0"), num(-16), num(2))
            assert { register("r0", -4) }
        }
        // shifting a negative value far right produces -1 (all bits become the sign bit)
        simulate {
            exec("sra", reg("r0"), num(-4), num(60))
            assert { register("r0", -1) }
        }
    }

    @Test
    fun `Instruction srl matches the in-game behaviour`() {
        // positive: same as arithmetic shift
        simulate {
            exec("srl", reg("r0"), num(16), num(2))
            assert { register("r0", 4) }
        }
        // negative: vacated bits are filled with zero, so result is positive
        simulate {
            exec("srl", reg("r0"), num(-16), num(2))
            assert { register("r0", 4503599627370490.0, 10.0) }
        }
        // shifting -4 by 60 yields 0
        simulate {
            exec("srl", reg("r0"), num(-4), num(60))
            assert { register("r0", 0) }
        }
    }

    @Test
    fun `Instruction trunc matches the in-game behaviour`() {
        simulate {
            setup {
                register("r0", 1.1)
            }
            exec("trunc", reg("r1"), reg("r0"))
            assert {
                register("r1", 1)
            }
        }
        simulate {
            setup {
                register("r0", -1.1)
            }
            exec("trunc", reg("r1"), reg("r0"))
            assert {
                register("r1", -1)
            }
        }
        simulate {
            setup {
                register("r0", 1.9)
            }
            exec("trunc", reg("r1"), reg("r0"))
            assert {
                register("r1", 1)
            }
        }
    }
}