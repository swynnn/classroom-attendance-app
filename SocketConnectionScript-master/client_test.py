# Import socket module
import socket

# Create a socket object
s = socket.socket()

# Define the port on which you want to connect
host = socket.gethostbyaddr('34.87.188.208')
port = 47858
print(socket.gethostbyaddr('34.87.188.208'))
# connect to the server on local computer
s.connect((host[0], port))
s.send("hello world".encode())
# receive data from the server
print(s.recv(1024).decode())
# close the connection
s.close()
