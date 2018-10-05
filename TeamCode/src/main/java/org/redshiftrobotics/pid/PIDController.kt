package org.redshiftrobotics.pid

abstract class PIDController(val hw: RobotHardware) {
    var tuning: PIDCalculator.Tuning = PIDCalculator.Tuning(0.0, 0.0,0.0,0.0)

    var target: Angle = hw.imu.angle

    protected var startTime: Millis = 0
    protected var dTime: Millis = 0
    protected var now: Millis = 0
    protected var lastLoopTime: Millis = 0

    abstract fun stop()
    abstract fun execute(correction: Double, dTime: Millis, totalTime: Millis)
    open fun predicate(): Boolean { return true; }

    protected fun move(time: Long) {
        val calculator = PIDCalculator(tuning, target)

        startTime = System.currentTimeMillis()
        lastLoopTime = 0
        do {
            now = System.currentTimeMillis()
            dTime = now - lastLoopTime
            lastLoopTime = now
            hw.telemetry.addData("imuangle", hw.imu.angle)
            hw.telemetry.addData("tuning", tuning)
            hw.telemetry.addData("startTime", startTime)
            hw.telemetry.addData("lastLoopTime", lastLoopTime)
            hw.telemetry.addData("now", now)
            hw.telemetry.addData("dTime", dTime)
            val correction = calculator.compute(hw.imu.angle, dTime) // dTime is in seconds, not ms
            execute(correction, dTime, now - startTime)
        } while ((System.currentTimeMillis() - startTime) <= time && hw.opMode.opModeIsActive() && predicate())
    }
}