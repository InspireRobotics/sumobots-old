class Message(object):
    data = {}

    def __init__(self, type):
        self.type = type;

    def add_data(self, key, val):
        self.data[key] = val

    def format_message(self):
        message = "{"
        for key, value in self.data.items():
            message += '"KEY":"VALUE",'
            message = message.replace("KEY", key)
            message = message.replace("VALUE", value)

        message += '"message_type"="TYPE"}'
        message = message.replace('TYPE', self.type)
        return format_message_to_bytes(message)

    def send_from(self, connection):
        connection.send(self.format_message())

def set_name_message(new_name):
    message = Message('SET_NAME')
    message.add_data('name', new_name)
    return message

def library_version_message(version):
    message = Message('LIB_VERSION')
    message.add_data('version', version)
    message.add_data('is_response', "false")
    return message

def pong_message():
    return Message('PONG')

def ping_message():
    return Message('PING')

def stream_terminated():
    return Message('STREAM_TERMINATED')

def format_message_to_bytes(string):
    xs = bytearray(1);
    xs.append(utf8len(string) + 1)
    xs.extend(string.encode('utf-8'))
    xs.append(4)
    print('Formatted message: {}'.format(xs))
    return xs;

def utf8len(s):
    return len(s.encode('utf-8'))
