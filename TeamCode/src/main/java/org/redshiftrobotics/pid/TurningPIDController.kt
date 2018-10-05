package org.redshiftrobotics.pid

class TurningPIDController(hw: RobotHardware): PIDController(hw) {
    private val ANGLE_THRESHOLD: Angle = 5.0

    override fun execute(correction: Double, dTime: Long, totalTime: Long) {
        hw.left.power = correction
        hw.right.power = -correction
        hw.telemetry.addData("Correction", correction)
        hw.telemetry.addData("dTime", dTime)
        hw.telemetry.addData("totalTime", totalTime)
        hw.telemetry.update()
    }

    override fun stop() {
        hw.left.power = 0.0
        hw.right.power = 0.0
    }

    override fun predicate(): Boolean = Math.abs(target - hw.imu.angle) < ANGLE_THRESHOLD

    fun move(targetAngle: Angle, time: Millis) {
        target += targetAngle
        move(time)
    }
}