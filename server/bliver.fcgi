#!/usr/bin/python
#: optional path to your local python site-packages folder

import sys
sys.path.insert(0, '/home/andi/bliver/server')

from flup.server.fcgi import WSGIServer
from server import app

if __name__ == '__main__':
    app.config['PORT'] = 14992
    app.config['DEBUG'] = True
    WSGIServer(app).run()
