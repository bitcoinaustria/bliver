#!/usr/bin/env python
from json import dumps
import bitcoinrpc
from bitcoinrpc.exceptions import BitcoinException,InsufficientFunds
from bitcoinrpc.config import read_config_file

#def connection():
#  import os
#  cfg = read_config_file(os.path.expanduser("~/.bitcoin/testnet3/bitcoin.conf"))
#  port = int(cfg.get('rpcport', '8332'))
#  rcpuser = cfg.get('rpcuser', '')
#  from bitcoinrpc.connection import BitcoinConnection
#  return BitcoinConnection(rcpuser,cfg['rpcpassword'],'localhost',port)

def gen_2of3(conn, pub1, pub2, pub3):
  return conn.proxy.addmultisigaddress(2, [pub1, pub2, pub3])

if __name__ == "__main__":
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
  print "custom command"
  #t = conn.proxy.listunspent()
  #print t
  multisig = gen_2of3(conn, "mrvQxKbe321W58xaTs65YS6mvUVGQyP52B", "03b08df6e673619b93fc0dd39be70d7bf56873241fcfde9e87332d79b87de80fcd", "023d7a2768855435b221003cb23f26d950a4ee22f3d47c9833778326d221253afc")
  print multisig

