package framework.river.http;


import com.google.gson.Gson;
import framework.river.gson.GsonFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 *
 */
public class RiverHttpServer {

//    private ResourceDirectory resourceDirectory = new ResourceDirectory();

    public RiverHttpServer addResource( String encodedURLRef, Class<?> resourceClass ) {
//        resourceDirectory.addResourceMapping( encodedURLRef, resourceClass );

        return this;
    }

    public RiverHttpServer addAlias( String sourceEncodedURLRef, String destinationEncodedUrlRef ) {
//        resourceDirectory.addAlias( sourceEncodedURLRef, destinationEncodedUrlRef );

        return this;
    }

//    public void start() {
//
//    }



    public void start( int port ) throws InterruptedException {
        EventLoopGroup bossGroup   = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 10000)   // max number of waiting connections
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new PipelineFactory());

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private class PipelineFactory extends ChannelInitializer<SocketChannel> {

        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast("http-codec", new HttpServerCodec());
//            pipeline.addLast("log", new LoggingHandler(LogLevel.INFO));

            pipeline.addLast("handler", new RFRequestHandler());
        }
    }


    // curl -v 'localhost:8080/a/b'
    // curl -X POST -H 'Content-Type: application/json; charset=utf-8' -d '{"message_type":"type","when_dtm":1000,"message":"hello","user_id":"userId1","request_id":"requestId","is_droppable_message":true}'  -v 'localhost:8080/servers/server1/apps/app1/log'


//    @ChannelHandler.Sharable
    private class RFRequestHandler extends ChannelInboundMessageHandlerAdapter {
        private Queue<HttpRequest> queue = new ArrayDeque<HttpRequest>();

        public void messageReceived( ChannelHandlerContext ctx, Object o ) throws Exception {


            if ( o instanceof HttpRequest ) {// DefaultHttpRequest ) {
                queue.add( (HttpRequest) o );
            } else if ( o instanceof LastHttpContent ) {

                HttpRequest req = queue.remove();

                req.getMethod();
                req.getUri();
                req.headers();



                LastHttpContent content = (LastHttpContent) o;

                ByteBuf buf = content.content();

                if ( buf.readableBytes() > 0 ) {
                    Gson gson = GsonFactory.createBuilder().create();


                    Reader in = new InputStreamReader( new ByteBufInputStream(buf), "utf-8" );
                    Object v = gson.fromJson( in, Class.forName("com.logbook.logbook.resources.logs.LogEntryDTO"));
                    System.out.println("v = " + v);
                }


                System.out.println( req.getMethod() + " " + req.getUri() + "    -- " + buf.readableBytes()  + " bytes");

                HttpResponse r = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                r.headers().add( "Content-Length", "0" );
                ctx.write( r );

            } else {
                System.out.println("o = " + o + " : " + o.getClass() );
            }
        }

    }

}
