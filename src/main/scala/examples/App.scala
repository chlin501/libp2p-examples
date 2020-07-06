package examples

import io.libp2p.core.dsl.HostBuilder
import io.libp2p.core.multiformats.Multiaddr
import io.libp2p.core.multistream.Multistream
import io.libp2p.protocol.Ping
import io.libp2p.security.secio.SecIoSecureChannel
import io.libp2p.transport.tcp.TcpTransport
import io.libp2p.mux.mplex.MplexStreamMuxer

object App {

  @throws(classOf[Exception])
  def main(args: Array[String]): Unit = {
    // https://github.com/libp2p/jvm-libp2p/blob/626765e312fdacdbb2bbc49eb269c126b18d14db/src/test/java/io/libp2p/core/HostTestJava.java#L25
    val listeningAddress = "/ip4/127.0.0.1/tcp/2020"
    val server = new HostBuilder()
      .transport(new TcpTransport(_))
      .secureChannel(new SecIoSecureChannel(_))
      .muxer { () =>
        new MplexStreamMuxer
      }
      .protocol(new Ping)
      .listen(listeningAddress)
      .build()
    server.start().get
    println(s"Server (peer id: ${server.getPeerId}) started ... ")
    // Server listening format [/ip4/127.0.0.1/tcp/2020/ipfs/QmUHChaeCUjtGi4YAJr4LZt6pHdkkgDa857k3tyvZ99Bp4]
    println(
      s"Server listens on addresses: ${server.listenAddresses().toString}"
    )
    val client = new HostBuilder()
      .transport { new TcpTransport(_) }
      .secureChannel(new SecIoSecureChannel(_))
      .muxer { () =>
        new MplexStreamMuxer
      }
      .build()
    client.start().get
    val pinger = client.getNetwork
      .connect(server.getPeerId, new Multiaddr(listeningAddress))
      .thenApply { it =>
        it.muxerSession()
          .createStream(Multistream.create(new Ping()).toStreamHandler())
      }
      .get()
    val stream = pinger.getStream().get
    val ctrl = pinger.getController().get
    for (_ <- 1 to 5) {
      val latency = ctrl.ping().get
      println(s"ping latency $latency")
    }
    //stream
    client.stop
    server.stop
    println("Done!")
  }

}
