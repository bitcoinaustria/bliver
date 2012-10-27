#!/usr/bin/env python

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


