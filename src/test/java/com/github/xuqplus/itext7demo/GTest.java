package com.github.xuqplus.itext7demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
class GTest {

    @Test
    void a() throws IOException {
        H haha = () -> log.info("haha");

        haha.h();

        log.info("{}", haha instanceof H);

//		List<Number> a = new ArrayList<>();
//		List<? extends Number> a = new ArrayList<>();
//		a = new ArrayList<Integer>();

        List<Number> a = new ArrayList<>();
        a.add(Integer.MIN_VALUE);
    }

    interface H {
        void h();
    }

    @Test
    void b() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            log.info("haha");
            executor.shutdown();
        });

        Thread.sleep(10000L);
    }

    @Test
    void c() {
        Object[] objectArray = new Long[1];
        objectArray[0] = "I don't fit in";

        List<Object> ol = new ArrayList<>();
        ol.add("I don't fit in");
    }

    @Test
    void d() {
        EnumSet<A> a = EnumSet.of(A.A);
        EnumSet<A> b = EnumSet.of(A.B);
        boolean add = a.add(A.B);

        log.info("{}", a);

        log.info("{}", a.contains(b));
        log.info("{}", a.contains(A.B));

        Map<A, Set<A>> map = new EnumMap<>(A.class);
        for (A value : A.values()) {
            map.put(value, new HashSet<>());
        }
        log.info("{}", a.contains(A.B));

        HashMap<A, Set<A>> aSetHashMap = new HashMap<>();
        for (A value : A.values()) {
            aSetHashMap.put(value, new HashSet<>());
        }

        log.info("{}", a.contains(A.B));

        new ArrayList<>().parallelStream();
//		new ArrayList<>().stream().map().parallel()

        aaa((Integer) null);
    }

    enum A {
        A, B, C, D, E, F
    }

    void aaa(int a, int b, int... c) {

    }

    void aaa(Integer a) {

    }

    void aaa(int a) {

    }

    void aaa(Long a) {

    }

    @Test
    void e() {
        int a;
        Integer b = null;
        a
                =
                b;
    }

    @Test
    void f() {
        int a = 3333;
        Integer b = 3333;
        Integer c = 3333;
        log.info("{}", a == b);
        log.info("{}", b == a);
        log.info("{}", c == b);
        log.info("{}", b == c);
    }
}
