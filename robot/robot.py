import socketserver

print("Starting robot code...")


class RobotServer(socketserver.BaseRequestHandler):

    def handle(self):
        self.data = self.request.recv(1024).strip()
        print("{} wrote:".format(self.client_address[0]))
        print(self.data)

        # just send back the same data, but upper-cased
        self.request.sendall(self.data.upper())


print("Starting server!")
HOST, PORT = "localhost", 10489
server = socketserver.TCPServer((HOST, PORT), RobotServer)

server.serve_forever()
