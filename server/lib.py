#!/usr/bin/env python
import bitcoinrpc
#from bitcoinrpc.exceptions import BitcoinException,InsufficientFunds
#from bitcoinrpc.config import read_config_file

def gen_2of3(conn, pub1, pub2, pub3):
  return conn.proxy.addmultisigaddress(2, [pub1, pub2, pub3])

def check_rcv_2of3(conn, adr_2of3, value, searchdepth = 100):
  def check(tx_list):
    for tx in tx_list:
      raw = conn.proxy.getrawtransaction(tx)
      t = conn.proxy.decoderawtransaction(raw)
      for v in t["vout"]:
        for adr in v["scriptPubKey"].get("addresses", []):
          if adr_2of3 == adr and v["value"] >= value:
            return True, tx, v["n"]

  proxy = conn.proxy

  mempool = proxy.getrawmempool()
  c = check(mempool)
  if c and c[0]:
    print "true in mempool"
    return c

  cur_block = conn.getblockcount()
  limit_blocknr = cur_block - searchdepth
  while cur_block > limit_blocknr:
    print cur_block
    blkhash = conn.proxy.getblockhash(cur_block)
    blk = conn.proxy.getblock(blkhash)
    c = check(blk["tx"])
    if c and c[0]:
      print "true in block %s" % cur_block
      return c
    cur_block -= 1
  return False

def gen_partial_tx(conn, target_addr, txid, voutid, amount):
  proxy = conn.proxy
  rawtx = proxy.createrawtransaction([{'txid' : txid, 'vout': voutid}], {target_addr : amount })
  return rawtx

def sign_rawtx(conn, partial_tx):
  signed = conn.proxy.signrawtransaction(partial_tx)
  decoded = conn.proxy.decoderawtransaction(signed['hex'])
  assert decoded['vout'][0]['scriptPubKey']['reqSigs'] == 1
  return signed['hex']


def gen_uri(order_id, order_descr):
  import urllib
  payload = urllib.urlencode([
    #("server-url", server_url),
    ("order-id", order_id),
    ("order-description", order_descr)
  ])
  return payload

def run():
  conn = bitcoinrpc.connect_to_local()
  info = conn.getinfo()
  print "Blockcount:  %i" % info.blocks
  print "Connections: %i" % info.connections
  print "Difficulty:  %f" % info.difficulty

  for ac in conn.listaccounts():
    print
    bal = conn.getbalance(ac)
    print "Account: '%s'. Balance: %s" % (ac, bal)
    txs = conn.listtransactions(ac)
    for tx in txs:
      txid = tx.txid
      print "transaction:", tx.amount, "to", tx.address, "|", txid

  print
  print "Multisig 2 of 3:",
  #t = conn.proxy.listunspent()
  #print t
  multisig = gen_2of3(conn, "mrvQxKbe321W58xaTs65YS6mvUVGQyP52B", \
    "03b08df6e673619b93fc0dd39be70d7bf56873241fcfde9e87332d79b87de80fcd", "023d7a2768855435b221003cb23f26d950a4ee22f3d47c9833778326d221253afc")
  print multisig

  print
  print "RECIEVED?"
  check = check_rcv_2of3(conn, multisig, 0.1, searchdepth = 100)
  print check
  if not check[0]:
    print "ERROR: didn't received payment at %s" % multisig
    return

  print
  target_addr = "myKPhLdmfk6Ss8j6ugqCVwsC3bcUHpZCg5"
  amount = 0.1
  print "RELEASE %s BTC to %s" % (amount, target_addr)
  _, txid, voutid = check
  partial_tx = gen_partial_tx(conn, target_addr, txid, voutid, amount)
  print partial_tx

  print
  print "SIGNING RAW TX"
  # by default, uses all available private keys
  signed_partial_tx = sign_rawtx(conn, partial_tx)
  print signed_partial_tx

  print
  fn = "qr.png"
  print "GENERATING QR CODE for partial transaction: %s" % fn
  import qrencode as qr
  qrdata = signed_partial_tx.decode("hex").encode("base64")
  qrcode = qr.encode_scaled(qrdata, 512)
  qrcode[2].save(fn, format="png")

  print
  server_url = "http://10.200.1.73/multisig"
  order_id = "123"
  order_descr = "test bestellung 123"
  uri = gen_uri(server_url, order_id, order_descr)
  print "URI:", uri

if __name__ == "__main__":
  #run()
  try:
    run()
  except Exception as e:
    print "ERROR"
    print e
    print e.error
