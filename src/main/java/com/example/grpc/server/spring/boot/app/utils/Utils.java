package com.example.grpc.server.spring.boot.app.utils;

import com.example.grpc.server.spring.boot.app.stock.OneOfExample;
import com.example.grpc.server.spring.boot.app.stock.SomeMessage;

public class Utils {
    void oneOfExample() {
        var oneOfExample = OneOfExample.newBuilder().setDbl(2.5).build();
        System.out.println(oneOfExample.hasDbl()); // true
        System.out.println(oneOfExample.getDbl()); // 2.5
        System.out.println(oneOfExample.hasInteger()); // false
        System.out.println(oneOfExample.getInteger()); // default value 0
    }

    void nestedMessageExample() {
        var message = SomeMessage.newBuilder().build();
        var nestedMessage = SomeMessage.NestedMessage.newBuilder().build();
        var timestamp = nestedMessage.getTimestamp();
    }
}
