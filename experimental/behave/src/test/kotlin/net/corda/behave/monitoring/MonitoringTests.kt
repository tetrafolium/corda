package net.corda.behave.monitoring

import net.corda.behave.second
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import rx.Observable

class MonitoringTests {

    @Test
    fun `watch gets triggered when pattern is observed`() {
        val observable = Observable.just("first", "second", "third")
        val result = PatternWatch("c.n").await(observable, 1.second)
        assertThat(result).isTrue()
    }

    @Test
    fun `watch does not get triggered when pattern is not observed`() {
        val observable = Observable.just("first", "second", "third")
        val result = PatternWatch("forth").await(observable, 1.second)
        assertThat(result).isFalse()
    }

    @Test
    fun `conjunctive watch gets triggered when all its constituents match on the input`() {
        val observable = Observable.just("first", "second", "third")
        val watch1 = PatternWatch("fir")
        val watch2 = PatternWatch("ond")
        val watch3 = PatternWatch("ird")
        val aggregate = watch1 * watch2 * watch3
        assertThat(aggregate.await(observable, 1.second)).isTrue()
    }

    @Test
    fun `conjunctive watch does not get triggered when one or more of its constituents do not match on the input`() {
        val observable = Observable.just("first", "second", "third")
        val watch1 = PatternWatch("fir")
        val watch2 = PatternWatch("ond")
        val watch3 = PatternWatch("baz")
        val aggregate = watch1 * watch2 * watch3
        assertThat(aggregate.await(observable, 1.second)).isFalse()
    }

    @Test
    fun `disjunctive watch gets triggered when one or more of its constituents match on the input`() {
        val observable = Observable.just("first", "second", "third")
        val watch1 = PatternWatch("foo")
        val watch2 = PatternWatch("ond")
        val watch3 = PatternWatch("bar")
        val aggregate = watch1 / watch2 / watch3
        assertThat(aggregate.await(observable, 1.second)).isTrue()
    }

    @Test
    fun `disjunctive watch does not get triggered when none its constituents match on the input`() {
        val observable = Observable.just("first", "second", "third")
        val watch1 = PatternWatch("foo")
        val watch2 = PatternWatch("baz")
        val watch3 = PatternWatch("bar")
        val aggregate = watch1 / watch2 / watch3
        assertThat(aggregate.await(observable, 1.second)).isFalse()
    }
}