#!/usr/bin/env python

HOSTNAME = "bitcoinrelay.net"

PUBKEY = "mrvQxKbe321W58xaTs65YS6mvUVGQyP52B"
TARGET_ADDR = "myKPhLdmfk6Ss8j6ugqCVwsC3bcUHpZCg5"
AMOUNT = 1231234

# additional 2 for the 2 of 3 (additionally to the PUBKEY)
ESCROW = "03b08df6e673619b93fc0dd39be70d7bf56873241fcfde9e87332d79b87de80fcd"
SENDER = "023d7a2768855435b221003cb23f26d950a4ee22f3d47c9833778326d221253afc"

from flask import Flask, Response, render_template, request, url_for, send_file, make_response
app = Flask(__name__)

import bitcoinrpc
conn = bitcoinrpc.connect_to_local()

@app.route('/policy', methods = ["GET", "POST"])
def policy():
  ADDR = 'myKPhLdmfk6Ss8j6ugqCVwsC3bcUHpZCg5'
  policies = []
  policies.append('policy 1')
  policies.append('policy 2')
  option = ""
  delete = ""
  if request.method == 'POST':
    option = request.form.get("policy")
    if request.form.get("delete"):
      delete = "DELETE %s" % request.form.get("delete")
  return render_template('policy.html', addr=ADDR, policies=policies, option = option, delete = delete)

@app.route('/robots.txt', methods=['GET'])
def robots_txt():
  response = make_response(open('static/robots.txt').read())
  response.headers["Content-type"] = "text/plain"
  return response

@app.route('/logo.png', methods=['GET'])
def logo_jpeg():
  response = make_response(open('static/logo.png').read())
  response.headers["Content-type"] = "image/png"
  return response

@app.route('/qr')
def qr():
  data = request.args.get('data', '')
  from StringIO import StringIO
  img_io = StringIO()
  import qrencode as qr
  img = qr.encode_scaled(data, 256)
  img[2].save(img_io, 'PNG')
  img_io.seek(0)
  return send_file(img_io, mimetype='image/png')

@app.route('/multisig', methods=["GET", "POST"])
def multisig():
  import json
  order_id = request.args.get('order-id', None)
  order_descr = request.args.get('order-description', None)
  data = json.dumps([order_id, order_descr])
  if request.method == 'POST':
    pubkey = request.form.get('pubkey')
    #html = "POST %s<br>pubkey: %s" % (data, pubkey)
    multisig = gen_multisig(pubkey)
    return Response(multisig, mimetype='text/plain')
  else:
    return "GET %s" % data

def gen_multisig(pubkey):
  from lib import gen_2of3
  return gen_2of3(conn, pubkey, ESCROW, SENDER)

def gen_qr(pubkey, target_addr = TARGET_ADDR, amount = 0.1):
  from lib import gen_partial_tx, check_rcv_2of3, sign_rawtx
  multisig = gen_multisig(pubkey)
  check = check_rcv_2of3(conn, multisig, amount)
  if check and check[0]:
    _, txid, voutid = check
    partial_tx = gen_partial_tx(conn, target_addr, txid, voutid, amount)
    signed_partial_tx = sign_rawtx(conn, partial_tx)
    data = signed_partial_tx.decode("hex").encode("base64")
    import urllib
    return '%s?data=%s' % (url_for('qr'), urllib.quote_plus(data))
  else:
    return 'check_rcv_2of3_failed'

@app.route('/check')
def check():
  addr = request.args.get('addr','')
  from lib import check_rcv_2of3
  value = float(request.args.get('value', 0.0))
  c = check_rcv_2of3(conn, addr, value)
  return render_template('check.html', c = c, addr = addr)

@app.route('/submit', methods=["POST"])
def privkey_import():
  from lib import import_privkey, gen_partial_tx, check_rcv_2of3, sign_rawtx, send_raw_tx
  privkey = request.form.get("privkey")
  try:
    output = import_privkey(conn, privkey)
  except Exception as e:
    output = 'ERROR: %s' % e.error
  check = check_rcv_2of3(conn, multisig, AMOUNT)
  if check and check[0]:
    _, txid, voutid = check
    partial_tx = gen_partial_tx(conn, TARGET_ADDR, txid, voutid, AMOUNT)
    signed_tx = sign_rawtx(conn, partial_tx)
    output += '<br>SIGNED AND SENT!<br>%s' % send_raw_tx(signed_tx)
  else:
    output += '<br>check = FALSE :('
  return render_template('privkey.html', output = output)

@app.route('/')
def hello_world():
  from lib import gen_uri
  from random import randint, shuffle
  prnames = [ "UFO-02 Detector", "Uranium Ore", "Plutonium Enricher", "Unicorn Meat", "Beer" , "Fake Human Poop", "Emergency Underpants"]
  shuffle(prnames)
  prnames = prnames[:3]
  prices = [ randint(1e6, 1e7) for _ in range(3) ]
  psum = sum(prices)
  prices = map(lambda _ : '%.6f' % (_/1e8), prices)
  tentry = zip(prnames, prices)
  from time import time
  data = gen_uri(HOSTNAME, url_for("multisig"), int(10*time()), ', '.join(prnames), psum)
  return render_template('index.html',
    check_url='%s?addr=%s' % (url_for('check'), gen_multisig(PUBKEY)),
    ms_uri = 'multisig:%s' % (data),
    ms_url = url_for("multisig"),
    qr_url = gen_qr(PUBKEY),
    privkey_import = url_for("privkey_import"),
    psum = "%.6f" % (psum/1e8),
    tentry = tentry)

if __name__ == '__main__':
  app.run(port=14992, debug=True)
