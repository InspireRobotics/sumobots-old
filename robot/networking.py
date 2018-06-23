import socket
import sys
import message
from message import Message
import json

# import pwm_test

library_version = "0.2.1"
name = "pybot"


def bytes_to_json(data):
    # Remove the first two bytes and last byte which are details about the message
    try:
        json_string = str(data[2:-1], 'utf-8')
    except UnicodeDecodeError:
        print("test")
        return None

    return json.loads(json_string)


def handle_non_simple_message(type, json, connection):
    if type == 'LIB_VERSION':
        version = json['version']
        print("Client version:", version)

        if version == library_version:
            print("Library versions match!")
        else:
            print("VERSIONS DO NOT MATCH!!! Robot:", library_version, "\tClient:", version)
            print("Terminated Robot Connection!")
            message.stream_terminated().send_from(connection)

    if type == "JOY_UPDATE":
        handle_joystick(json)


def handle_joystick(json):
    leftX = 0.0
    leftY = 0.0
    rightX = 0.0
    rightY = 0.0

    if "Right Y Axis" in json:
        rightY = json["Right Y Axis"]

    if "Right X Axis" in json:
        rightX = json["Right X Axis"]

    if "Left Y Axis" in json:
        leftY = json["Left Y Axis"]

    if "Left X Axis" in json:
        leftX = json["Left X Axis"]

    print("Joystick Values: RX: {}, RY: {}, LX: {}, LY: {}".format(rightX, rightY, leftX, leftY))
    # pwm_test.set_speed(14, float(leftY), 0)
    # pwm_test.set_speed(15, float(leftY), 0)
    # pwm_test.set_speed(18, float(leftY), 0)

    # pwm_test.set_speed(23, float(rightY), 0)
    # pwm_test.set_speed(24, float(rightY), 0)
    # pwm_test.set_speed(25, float(rightY), 0)


def handle_message(json, connection):
    message_type = json['message_type']

    print("Message Type:", message_type)

    if message_type == 'PING':
        message.pong_message().send_from(connection)
    elif message_type == 'STREAM_TERMINATED':
        print("The client has terminated the stream...")
        return True
    else:
        handle_non_simple_message(message_type, json, connection)

    return False


def on_data_received(data, connection):
    print('received {!r}'.format(data))

    json = None

    try:
        json = bytes_to_json(data)
    except ValueError:
        json = None

    if json is None:
        print("Couldn't format JSON:", data)
        return False

    return handle_message(json, connection)


def run():
    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Bind the socket to the port
    server_address = ('0.0.0.0', 10489)
    print('starting up on {} port {}'.format(*server_address))
    sock.bind(server_address)

    # Listen for incoming connections
    sock.listen(1)

    while True:
        # Wait for a connection
        print('waiting for a connection')
        connection, client_address = sock.accept()

        message.set_name_message(name).send_from(connection)
        message.ping_message().send_from(connection)
        message.library_version_message(library_version).send_from(connection)

        try:
            print('connection from', client_address)

            # Receive the data in small chunks and retransmit it
            while True:
                data = connection.recv(512)
                if on_data_received(data, connection):
                    print("Closing connection!")
                    break

        finally:
            # Clean up the connection
            connection.close()


# Run the robot code
run()
