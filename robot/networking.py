import socket
import sys

def utf8len(s):
    return len(s.encode('utf-8'))

def format_message(string):
    xs = bytearray(1);
    xs.append(utf8len(string) + 1)
    xs.extend(string.encode('utf-8'))
    xs.append(4)
    print('Formatted message: {}'.format(xs))
    return xs;

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
    try:
        print('connection from', client_address)
        connection.sendall(format_message('{"message_type":"LIB_VERSION","is_response":"false","version":"0.2.1"}'))
        connection.sendall(format_message('{"message_type":"SET_NAME","name":"robo-py"}'))

        # Receive the data in small chunks and retransmit it
        while True:
            data = connection.recv(512)
            print('received {!r}'.format(data))
            if data:
                print('sending data back to the client')
                connection.sendall(format_message('{"message_type":"PONG"}'))
            else:
                print('no data from', client_address)
                break

    finally:
        # Clean up the connection
        connection.close()