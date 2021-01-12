
import socket
import sys


def socket_interface():
    """
    To accept connections from mobile applcation and expects a return message
    from the server
    """
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = socket.gethostname()
    port = 47858

    # bind socket
    try:
        print("Binding....")
        server.bind((host, port))
        server.listen(100)

    except socket.error as msg:
        print('Bind failed.')
        sys.exit()

    print("socket listening")
    try:
        while True:
            print("Accepting requests...")
            # accepts connection
            connection, address = server.accept()
            print("Connected with", address[0], ":", str(address[1]))
            # receive of bytes
            data = connection.recv(8192)
            if not data:
                break
            message = data.decode()
            # create socket object to connect with server
            s = socket.socket()
            mdi_host = socket.gethostbyaddr('35.187.235.26')
            mdi_port = 55619
            # connect to MyDigital ID server
            s.connect((mdi_host[0], mdi_port))
            print("message", message)
            # send message of mobile application
            s.sendall(message.encode())
            # receive message from MyDigital ID server
            return_message = s.recv(8192).decode()
            print("return_message", return_message)
            # send message which is from the MyDigital ID server to mobile application 
            connection.sendall(return_message.encode())
            connection.close()

    except KeyboardInterrupt:
        server.close()


def main():

    socket_interface()


if __name__ == "__main__":
    main()
