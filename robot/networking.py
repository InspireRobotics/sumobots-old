import socket
import sys
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

    message = Message('SET_NAME')
    message.add_data('name', 'robo-py')
    connection.sendall(message.format_message())

    try:
        print('connection from', client_address)

        # Receive the data in small chunks and retransmit it
        while True:
            data = connection.recv(512)
            print('received {!r}'.format(data))
            message = Message("PONG")
            connection.sendall(message.format_message())

    finally:
        # Clean up the connection
        connection.close()