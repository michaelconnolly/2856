package org.redshiftrobotics.pid

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.robotcore.external.Telemetry

interface RobotHardware {
    val left: DcMotor
    val right: DcMotor
    val imu: IMU
    val opMode: LinearOpMode
    val telemetry: Telemetry
}