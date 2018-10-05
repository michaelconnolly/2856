package org.redshiftrobotics.pid

class PIDCalculator(val tuning: Tuning) {
    data class Tuning(val P: Double, val I: Double, val D: Double, val maxI: Double)

    private var P = 0.0
    private var I = 0.0
    private var D = 0.0
    private var lastError = 0.0

    var target = 0.0

    /**
     * @param sample: the current value
     * @param dTime: time since last compute()
     */
    internal fun compute(sample: Double, dTime: Millis): Double {
        val error = target - sample
        P = error
        I += error * dTime
        D = (error - lastError) / (dTime * 1000) // convert dTime to seconds because otherwise tuning.D would need to be HUGE
        lastError = error
        return P * tuning.P + I * tuning.I + D * tuning.D
    }

    constructor(tuning: Tuning, target: Double): this(tuning) {
        this.target = target
    }
}