
/**
 *
 *
 * @author jmsohn
 */
@TargetClass(cls="java/nio/channels/SocketChannel", type="SCH")
public class SocketChannelWrapper {

  /**
   *
   *
   * @param addr
   * @return
   */
  @TargetMethod("open(Ljava/net/SocketAddress;)Ljava/nio/channels/SocketChannel;")
  public static SocketChannel open(SocketAddress addr) throws IOException {
    SocketChannel channel = SocketChannel.open();
    return channel;
  }

  /**
   *
   *
   * @param channel
   * @param addr
   * @return
   */
  @TargetMethod("connect(Ljava/net/SocketAddress;)Z")
  public static boolean connect(SocketChannel channel, SocketAddress addr) throws IOException {
    boolean result = channel.connect(addr);
    return result;
  }
}
