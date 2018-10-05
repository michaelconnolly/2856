package org.redshiftrobotics.pid

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.robotcore.external.Telemetry

@TeleOp(name = "PID Tuner")
class PIDTunerOpMode: LinearOpMode() {
    enum class ConfigValue {
        P, I, D;
        fun next(): ConfigValue = when (this) {
            P -> I
            I -> D
            D -> P
        }
    }

    val ctx = this

    var configValues = mutableMapOf<ConfigValue, Double>()
    var activeConfig = ConfigValue.P

    var activeValue: Double
        get() = configValues.getOrElse(activeConfig) { 0.0 }
        set(value) = configValues.set(activeConfig, value)

    var scale: Double = 1.0

    var lastLT: Boolean = false
    var lastRT: Boolean = false
    var lastA: Boolean = false
    var lastB: Boolean = false
    var lastX: Boolean = false
    var lastY: Boolean = false
    var lastDPADLEFT: Boolean = false

    override fun runOpMode() {
        val hw = object: RobotHardware {
            override val opMode: LinearOpMode = ctx

            val hwIMU: BNO055IMU = hardwareMap.get(BNO055IMU::class.java, "imu").also {
                val parameters = BNO055IMU.Parameters()
                parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES
                parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
                parameters.calibrationDataFile = "IMUConfig.json"
                parameters.loggingEnabled = true
                parameters.loggingTag = "IMU"
                it.initialize(parameters)
            }


            override val telemetry: Telemetry
                get() = ctx.telemetry

            override val imu: IMU = object: IMU {
                override val angle: Double
                    get() = hwIMU.getAngularOrientation().firstAngle.toDouble()
            }

            override val left: DcMotor
                get() {
                    val motor = ctx.hardwareMap.dcMotor.get("left")
                    motor.direction = DcMotorSimple.Direction.REVERSE
                    return motor
                }

            override val right: DcMotor
                get() {
                    val motor = ctx.hardwareMap.dcMotor.get("right")
                    motor.direction = DcMotorSimple.Direction.FORWARD
                    return motor
                }
        }

        val controller = StraightPIDController(hw)

        telemetry.addLine("Welcome")
        telemetry.update()
        waitForStart()
        while (opModeIsActive()) {
            telemetry.addData("key", activeConfig)
            telemetry.addData("value", activeValue)
            telemetry.addData("scale", scale)
            telemetry.addLine("LT = Scale-, RT=Scale+, A = Increase, B = Decrease, X = Next, Y = Run")
            telemetry.update()

            if (gamepad1.left_bumper && !lastLT) scale /= 10
            if (gamepad1.right_bumper && !lastRT) scale *= 10
            if (gamepad1.a && !lastA) activeValue += scale
            if (gamepad1.b && !lastB) activeValue -= scale
            if (gamepad1.x && !lastX) activeConfig = activeConfig.next()
            if (gamepad1.y && !lastY) {
                controller.target = hw.imu.angle
                controller.tuning = PIDCalculator.Tuning(configValues[ConfigValue.P] ?: 0.0, configValues[ConfigValue.I] ?: 0.0, configValues[ConfigValue.D] ?: 0.0, Double.POSITIVE_INFINITY)
                controller.move(0.5, 50000)
                hw.left.power = 0.0
                hw.right.power = 0.0
            }
            if (gamepad1.dpad_left && !lastDPADLEFT) {
                val controller = TurningPIDController(hw)
                controller.tuning = PIDCalculator.Tuning(configValues[ConfigValue.P] ?: 0.0, configValues[ConfigValue.I] ?: 0.0, configValues[ConfigValue.D] ?: 0.0, Double.POSITIVE_INFINITY)
                controller.move(hw.imu.angle + 90.0, 10000)

            }

            lastLT = gamepad1.left_bumper
            lastRT = gamepad1.right_bumper
            lastA = gamepad1.a
            lastB = gamepad1.b
            lastX = gamepad1.x
            lastY = gamepad1.y
            lastDPADLEFT = gamepad1.dpad_left
        }
    }

    private fun configureP() {
        telemetry.addLine()

    }
}