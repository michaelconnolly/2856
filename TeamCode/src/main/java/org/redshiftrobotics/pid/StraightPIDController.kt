package org.redshiftrobotics.pid

class StraightPIDController(hw: RobotHardware): PIDController(hw) {
    override fun execute(correction: Double, dTime: Millis, totalTime: Millis) {
        hw.left.power = speed + correction
        hw.right.power = speed - correction
        hw.telemetry.addData("Correction", correction)
        hw.telemetry.addData("dTime", dTime)
        hw.telemetry.addData("totalTime", totalTime)
        hw.telemetry.update()
    }

    override fun stop() {
        hw.left.power = 0.0
        hw.right.power = 0.0
    }

    private var speed = 0.0

    fun move(speed: Double, time: Millis) {
        this.speed = speed
        move(time)
        this.speed = 0.0
    }
}