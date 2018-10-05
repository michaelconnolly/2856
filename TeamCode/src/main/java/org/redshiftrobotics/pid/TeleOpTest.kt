package org.redshiftrobotics.pid

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple

@TeleOp(name = "Vanilla")
class TeleOpTest: OpMode() {
    override fun init() {
        val left = hardwareMap.dcMotor.get("left")
        val right = hardwareMap.dcMotor.get("right")
        right.direction = DcMotorSimple.Direction.FORWARD
        left.direction = DcMotorSimple.Direction.REVERSE
        this.left = left
        this.right = right
    }

    private var left: DcMotor? = null
    private var right: DcMotor? = null

    override fun loop() {
        left!!.power = gamepad1.left_stick_y.toDouble()
        right!!.power = gamepad1.right_stick_y.toDouble()
    }
}