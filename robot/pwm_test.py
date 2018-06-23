# Source: https://pythonhosted.org/RPIO/pwm_py.html#rpio-pwm-servo
from RPIO import PWM
import time
import math

servo = PWM.Servo()


def roundupTen(x):
    return int(math.ceil(x / 10.0)) * 10


def set_speed(s, speed, t):
    print(speed)
    print('Setting Speed: %f' % (speed))

    speed *= 500
    speed += 1500

    print(speed)
    print(roundupTen(speed))
    servo.set_servo(s, roundupTen(speed))
    time.sleep(t)


def stop_servo(s):
    servo.stop_servo(s)
