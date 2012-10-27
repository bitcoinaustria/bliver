#!/usr/bin/env python

from flask import Flask, Response, render_template, request, url_for
app = Flask(__name__)

import bitcoinrpc
conn = bitcoinrpc.connect_to_local()

@app.route('/multisig', methods=["GET", "POST"])
def multisig():
  import json
  order_id = request.args.get('order-id', None)
  order_descr = request.args.get('order-description', None)
  data = json.dumps([order_id, order_descr])
  if request.method == 'POST':
    pubkey = request.form.get('pubkey')
    #html = "POST %s<br>pubkey: %s" % (data, pubkey)
    from lib import gen_2of3
    multisig = gen_2of3(conn, pubkey,
      "03b08df6e673619b93fc0dd39be70d7bf56873241fcfde9e87332d79b87de80fcd",
      "023d7a2768855435b221003cb23f26d950a4ee22f3d47c9833778326d221253afc")
    return Response(multisig, mimetype='text/plain')
  else:
    return "GET %s" % data

@app.route('/check/<addr>')
def check(addr):
  return addr

@app.route('/')
def hello_world():
  from lib import gen_uri
  data = gen_uri(123, "des cription of ...")
  return render_template('index.html',
    check_url=url_for('check', addr = '2NCK67mvVJSG1v2wj2NfnEsCCQBXpRqpgC7'),
    ms_url = url_for('multisig'),
    ms_data = data)

if __name__ == '__main__':
  app.run(port=14992, debug=True)
