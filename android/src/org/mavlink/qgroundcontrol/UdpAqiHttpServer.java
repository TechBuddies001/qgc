package org.mavlink.qgroundcontrol;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UdpAqiHttpServer {
     static String udpData = "Initial UDP Data";
    private static String aqiData = "Initial AQI Data";

    public static void main(String[] args) throws IOException {
        // Start UDP server in a separate thread
        startUdpServer();

        // Create an HTTP server that listens on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Create a context for the root path "/"
        server.createContext("/", new MyHandler());

        // Start the server
        server.start();

        System.out.println("Server started on port 8080");
    }

    // Custom handler for handling HTTP requests
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Set the response headers
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, 0);

            // Get the output stream to write the response
            OutputStream os = exchange.getResponseBody();

            // Generate the HTML response with UDP and AQI data
            String htmlResponse = generateHtmlPage();

            // Write the HTML response to the output stream
            os.write(htmlResponse.getBytes());

            // Close the output stream
            os.close();
        }

      /*  private String generateHtmlPage() {
            StringBuilder html = new StringBuilder();

            // HTML header
            html.append("<html><head><title>Data Dashboard</title>");

            // JavaScript for refreshing the page every 0.5 seconds
            html.append("<script>");
            html.append("function refreshPage() {");
            html.append("setTimeout(function(){location.reload(true);}, 500);");
            html.append("}");
            html.append("</script>");

            html.append("<style>");
            html.append("body { text-align: center; }");
            html.append("table { margin: 0 auto; text-align: left; }");
            html.append("</style>");

            html.append("</head><body onload='refreshPage()'>");

            // Display AQI data in a table
            html.append("<h1 style='text-align:center;'>AQI Data:</h1>");
            html.append("<table border=\"1\">");
            html.append("<tr>");
            html.append("<th>MQ135 RZero</th>");
            html.append("<th>Resistance</th>");
            html.append("<th>PPM</th>");
            html.append("<th>Temperature (C)</th>");
            html.append("<th>Pressure (hPa)</th>");
            html.append("<th>Humidity (%)</th>");
            html.append("<th>Gas Resistance (Ohms)</th>");
            html.append("<th>pm1_0</th>");
            html.append("<th>pm2_5</th>");
            html.append("<th>pm10</th>");
            html.append("<th>OZONE</th>");
            html.append("</tr>");

            // Display dynamic UDP data
            String[] udpValues = udpData.split(",");
            html.append("<tr>");
            for (String value : udpValues) {
                String[] keyValue = value.trim().split(":");
                html.append("<td>").append(keyValue[1]).append("</td>");
            }
            html.append("</tr>");

            // Table closing tag and HTML closing tags
            html.append("</table></body></html>");

            return html.toString();
        }*/
    }

    // UDP server thread to continuously receive data
     public void startUdpServer() {
        DataInterface dataInterface = null;

        Thread udpThread = new Thread(() -> {
            try {
                DatagramSocket udpSocket = new DatagramSocket(new InetSocketAddress("192.168.165.100", 5000));

                while (true) {
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    udpSocket.receive(receivePacket);
                    udpData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Received UDP data: " + udpData);
                    dataInterface.passData(udpData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        udpThread.start();
    }
}

