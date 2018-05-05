# Source: https://pythonhosted.org/RPIO/pwm_py.html#rpio-pwm-servo
from RPIO import PWM
import time

servo = PWM.Servo()

# Set motor controller on port 7 to 1000us (1ms)
# or full backwards on victors
servo.set_servo(17, 1250)

# Wait for 1 second
time.sleep(1)

# Clear servo on GPIO17
servo.stop_servo(17)