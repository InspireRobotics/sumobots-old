import socket
import sys
import message
from message import Message
import json

library_version = "0.2.1"
name = "pybot"

def bytes_to_json(data):
    # Remove the first two bytes and last byte which are details about the message
    json_string = str(data[2:-1], 'utf-8')
    return json.loads(json_string)

def handle_non_ping_message(type, json, connection):
    if type == 'LIB_VERSION':
        version = json['version'];
        print("Client version:", version)

        if version == library_version:
            print("Library versions match!")
        else:
            print("VERSIONS DO NOT MATCH!!! Robot:", library_version, "\tClient:", version)
            print("Terminated Robot Connection!")
            message.stream_terminated().send_from(connection)

    if type == 'STREAM_TERMINATED':
        print("Client terminated connection!")
        exit(0)



def handle_message(json, connection):
    message_type = json['message_type']

    print("Message Type:", message_type)

    if(message == 'PING'):
        message.pong_message().send_from(connection)
    else:
        handle_non_ping_message(message_type, json, connection)

def on_data_received(data, connection):
    print('received {!r}'.format(data))
    json = bytes_to_json(data)

    handle_message(json, connection)


# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind the socket to the port
server_address = ('localhost', 10489)
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
            on_data_received(data, connection);

    finally:
        # Clean up the connection
        connection.close()

