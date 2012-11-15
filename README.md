# BLiver System

This whole system is an ecosystem for paying delivered physical goods
via [Bitcoin][btc] with escrow.

The key idea is to package the partial Bitcoin transaction for releasing
the funds to the sender in the form of a [QR Code][qr]. This transaction
is completed by the receiver on delivery (and observed by the deliverer)
or a trusted third party.

*[BLiver Presentation](http://tinyurl.com/bliver)*

## BLiver App

the android "app" client is able to react on `multisig:*` URIs and pay to multisig addresses.

## BLiver Utilities

the server component is able to create multisig addresses and sign paritial transactions using the bitcoinrpc python interface (with some monkey patching)

## License

[Apache 2.0][a20] (preliminary)

[btc]: http://www.bitcoin.org
[qr]: http://en.wikipedia.org/wiki/QR_Code
[a20]: http://www.apache.org/licenses/LICENSE-2.0.html
