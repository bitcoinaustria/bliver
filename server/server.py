#!/usr/bin/env python

HOSTNAME = "bitcoinrelay.com"

PUBKEY = "mrvQxKbe321W58xaTs65YS6mvUVGQyP52B"
TARGET_ADDR = "myKPhLdmfk6Ss8j6ugqCVwsC3bcUHpZCg5"
AMOUNT = 1231234

# additional 2 for the 2 of 3 (additionally to the PUBKEY)
ESCROW = "03b08df6e673619b93fc0dd39be70d7bf56873241fcfde9e87332d79b87de80fcd"
SENDER = "023d7a2768855435b221003cb23f26d950a4ee22f3d47c9833778326d221253afc"

from flask import Flask, Response, render_template, request, url_for, send_file
app = Flask(__name__)

import bitcoinrpc
conn = bitcoinrpc.connect_to_local()

@app.route('/qr/<data>')
def qr(data):
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
    return url_for('qr', data = urllib.quote_plus(data))
  else:
    return 'check_rcv_2of3_failed'

@app.route('/check/<addr>')
def check(addr):
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
  data = gen_uri(123, "des cription of ...", 12341234)
  import urllib
  H = urllib.quote_plus("http://%s" % HOSTNAME)
  return render_template('index.html',
    check_url=url_for('check', addr = gen_multisig(PUBKEY)),
    ms_uri = 'multisig:%s%s?%s' % (H,url_for('multisig'), data),
    ms_url = url_for("multisig"),
    qr_url = gen_qr(PUBKEY),
    privkey_import = url_for("privkey_import"))

if __name__ == '__main__':
  app.run(port=14992, debug=False)
