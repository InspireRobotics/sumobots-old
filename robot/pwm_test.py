# Source: https://pythonhosted.org/RPIO/pwm_py.html#rpio-pwm-servo
from RPIO import PWM
import time
import math

servo = PWM.Servo()

def roundupTen(x):
    return int(math.ceil(x / 10.0)) * 10

def set_speed(servo, speed, t):
    print('Setting Speed: %f' % (speed))
    
    speed /= 2
    speed += .5
    speed *= 500
    speed += 500
    
    print(speed)
    print(roundupTen(speed))
    servo.set_servo(servo, roundupTen(speed))
    time.sleep(t)

def stop_servo(servo):
    servo.stop_servo(servo)

set_speed(14, 1, 1)
set_speed(14, 0.0, 1)
set_speed(14, -1.0, 1)
set_speed(14, .5, 1)
stop_servo(14)

    
    


