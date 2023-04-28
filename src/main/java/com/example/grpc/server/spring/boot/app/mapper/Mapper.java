package com.example.grpc.server.spring.boot.app.mapper;

import com.example.grpc.server.spring.boot.app.stock.Stock;
import com.example.grpc.server.spring.boot.app.stock.StockQuote;

import static java.lang.Math.abs;
import static java.util.concurrent.ThreadLocalRandom.current;

public class Mapper {
    public static StockQuote stockToStockQuote(Stock stock) {
        return StockQuote.newBuilder()
                .setPrice(fetchStockPriceBid(stock))
                .setOfferNumber(abs(current().nextInt()))
                .setDescription("Price for stock:" + stock.getTickerSymbol())
                .build();
    }

    public static double fetchStockPriceBid(Stock stock) {

        return stock.getTickerSymbol()
                .length()
                + current()
                .nextDouble(-0.1d, 0.1d);
    }
}
