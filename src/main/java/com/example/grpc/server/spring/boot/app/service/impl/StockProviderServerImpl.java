package com.example.grpc.server.spring.boot.app.service.impl;

import com.example.grpc.server.spring.boot.app.mapper.Mapper;
import com.example.grpc.server.spring.boot.app.stock.Stock;
import com.example.grpc.server.spring.boot.app.stock.StockQuote;
import com.example.grpc.server.spring.boot.app.stock.StockQuoteProviderGrpc;
import com.example.grpc.server.spring.boot.app.stock.StockQuotes;
import com.example.grpc.server.spring.boot.app.stock.Stocks;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

import static com.example.grpc.server.spring.boot.app.mapper.Mapper.fetchStockPriceBid;
import static com.example.grpc.server.spring.boot.app.mapper.Mapper.stockToStockQuote;

@Slf4j
@GrpcService
public class StockProviderServerImpl extends StockQuoteProviderGrpc.StockQuoteProviderImplBase {

    @Override
    public void getStockQuote(Stock request, StreamObserver<StockQuote> responseObserver) {
        StockQuote stockQuote = stockToStockQuote(request);
        responseObserver.onNext(stockQuote);
        responseObserver.onCompleted();
    }

    @Override
    public void getListStocksQuotes(Stocks request, StreamObserver<StockQuotes> responseObserver) {
        StockQuotes.Builder stockQuotesBuilder = StockQuotes.newBuilder();
        request.getStockList().forEach(stock -> {
                    StockQuote stockQuote = stockToStockQuote(stock);
                    stockQuotesBuilder.addStockQuotes(stockQuote);
                }
        );
        responseObserver.onNext(stockQuotesBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getStocksQuote(Stocks request, StreamObserver<StockQuote> responseObserver) {
        List<StockQuote> stockQuotes = request.getStockList().stream().map(Mapper::stockToStockQuote).toList();
        responseObserver.onNext(stockQuotes.get(0));
        responseObserver.onCompleted();
    }

    @Override
    public void getListStockQuotes(Stock request, StreamObserver<StockQuotes> responseObserver) {
        StockQuotes.Builder stockQuotesBuilder = StockQuotes.newBuilder();
        for (int i = 1; i <= 5; i++) {
            StockQuote stockQuote = stockToStockQuote(request);
            stockQuotesBuilder.addStockQuotes(stockQuote);
        }
        responseObserver.onNext(stockQuotesBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void serverSideStreamingGetListStockQuotes(Stock request, StreamObserver<StockQuote> responseObserver) {
        for (int i = 1; i <= 5; i++) {
            StockQuote stockQuote = stockToStockQuote(request);
            responseObserver.onNext(stockQuote);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Stock> clientSideStreamingGetStatisticsOfStocks(StreamObserver<StockQuote> responseObserver) {
        return new StreamObserver<>() {
            int count;
            double price = 0.0;
            final StringBuffer sb = new StringBuffer();

            @Override
            public void onNext(Stock stock) {
                count++;
                price = +fetchStockPriceBid(stock);
                sb.append(":")
                        .append(stock.getTickerSymbol());
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(StockQuote.newBuilder()
                        .setPrice(price / count)
                        .setDescription("Statistics-" + sb)
                        .build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Stock> bidirectionalStreamingGetListsStockQuotes(StreamObserver<StockQuote> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(Stock request) {
                for (int i = 1; i <= 5; i++) {
                    StockQuote stockQuote = StockQuote.newBuilder()
                            .setPrice(fetchStockPriceBid(request))
                            .setOfferNumber(i)
                            .setDescription("Price for stock:" + request.getTickerSymbol())
                            .build();
                    responseObserver.onNext(stockQuote);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
