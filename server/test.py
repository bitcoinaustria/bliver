#!/usr/bin/env python
from json import dumps
import bitcoinrpc
from bitcoinrpc.exceptions import BitcoinException,InsufficientFunds
from bitcoinrpc.config import read_config_file

def gen_2of3(conn, pub1, pub2, pub3):
  return conn.proxy.addmultisigaddress(2, [pub1, pub2, pub3])

def check_rcv_2of3(conn, adr_2of3, value):
  cur_block = conn.getblockcount()
  while cur_block > 33840:
    print cur_block
    blkhash = conn.proxy.getblockhash(cur_block)
    blk = conn.proxy.getblock(blkhash)
    for tx in blk["tx"]:
      raw = conn.proxy.getrawtransaction(tx)
      t = conn.proxy.decoderawtransaction(raw)
      for v in t["vout"]:
        for adr in v["scriptPubKey"].get("addresses", []):
          if adr_2of3 == adr and v["value"] >= value:
            return True
    cur_block -= 1
  return False

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
  multisig = gen_2of3(conn, "mrvQxKbe321W58xaTs65YS6mvUVGQyP52B",
    "03b08df6e673619b93fc0dd39be70d7bf56873241fcfde9e87332d79b87de80fcd", "023d7a2768855435b221003cb23f26d950a4ee22f3d47c9833778326d221253afc")
  print multisig

  print
  print "RECIEVED?"
  print check_rcv_2of3(conn, multisig, 0.1)

if __name__ == "__main__":
  run()
  #try:
  #  run()
  #except Exception as e:
  #  print "ERROR"
  #  print e
  #  print e.error
