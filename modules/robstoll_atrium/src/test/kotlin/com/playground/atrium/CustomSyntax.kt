package com.playground.atrium

import ch.tutteli.atrium.api.cc.en_GB.toThrow
import ch.tutteli.atrium.core.coreFactory
import ch.tutteli.atrium.creating.Assert
import ch.tutteli.atrium.domain.builders.AssertImpl
import ch.tutteli.atrium.domain.builders.reporting.reporterBuilder
import ch.tutteli.atrium.domain.creating.throwable.thrown.ThrowableThrown
import ch.tutteli.atrium.reporting.translating.StringBasedTranslatable
import ch.tutteli.atrium.reporting.translating.Untranslatable

internal fun <T : Any> garantaQue(subject: T) =
    coreFactory.newReportingPlant(
        assertionVerb = AssertionVerb.GARANTA_QUE,
        subjectProvider = { subject },
        reporter = AtriumReporterSupplier.REPORTER
    )

internal fun <T : Any> garantaQue(subject: T, assertionCreator: Assert<T>.() -> Unit) =
    coreFactory.newReportingPlantAndAddAssertionsCreatedBy(
        assertionVerb = AssertionVerb.GARANTA_QUE,
        subjectProvider = { subject },
        reporter = AtriumReporterSupplier.REPORTER,
        assertionCreator = assertionCreator
    )

internal fun esperaSeQue(act: () -> Unit) =
    AssertImpl.throwable.thrownBuilder(
        AssertionVerb.ESPERA_SE_QUE, act,
        AtriumReporterSupplier.REPORTER
    )

internal enum class AssertionVerb(override val value: String) : StringBasedTranslatable {
    GARANTA_QUE("garanta que"),
    ESPERA_SE_QUE("espera-se excessao lancada"),
}

internal object AtriumReporterSupplier {
    val REPORTER by lazy {
        reporterBuilder.withoutTranslations()
            .withDetailedObjectFormatter()
            .withDefaultAssertionFormatterController()
            .withDefaultAssertionFormatterFacade()
            .withTextSameLineAssertionPairFormatter()
            .withDefaultTextCapabilities()
            .withOnlyFailureReporter()
            .build()
    }
}

internal fun <T : Any> Assert<T>.sejaIgualA(something: T) = createAndAddAssertion(
    description = Untranslatable("seja igual"),
    expected = something,
    test = { subject == something })

internal inline fun <reified TExpected : Throwable> ThrowableThrown.Builder.gereUm() {
    toThrow<TExpected> {}
}

internal typealias ErroDeExpectativa = AssertionError
