import socket
import sys
import message
from message import Message

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

    message.set_name_message('robo-py').send_from(connection)

    try:
        print('connection from', client_address)

        # Receive the data in small chunks and retransmit it
        while True:
            data = connection.recv(512)
            print('received {!r}'.format(data))
            message.pong_message().send_from(connection)

    finally:
        # Clean up the connection
        connection.close()