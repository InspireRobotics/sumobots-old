# Source: https://pythonhosted.org/RPIO/pwm_py.html#rpio-pwm-servo
from RPIO import PWM
import time
import math

def roundupTen(x):
    return int(math.ceil(x / 10.0)) * 10

servo = PWM.Servo()

def setSpeed(speed, t):
    print('Setting Speed: %f' % (speed))
    
    speed /= 2
    speed += .5
    speed *= 500;
    speed += 500;
    
    print(speed)
    print(roundupTen(speed))
    servo.set_servo(14, roundupTen(speed))
    time.sleep(t)

setSpeed(1, 1)
setSpeed(0.0, 1)
setSpeed(-1.0, 1)
setSpeed(.5, 1)

# Clear servo on GPIO17
servo.stop_servo(14)


    
    


