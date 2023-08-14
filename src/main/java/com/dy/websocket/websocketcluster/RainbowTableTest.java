package com.dy.websocket.websocketcluster;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ding.yi create at 2023/8/1 17:03
 */
@Slf4j
public class RainbowTableTest {

    private static final Random RANDOM = new Random();

    /**
     * 小写英文字母+数字组合
     */
    public static final char[] LOWER_ALPHA_NUMERIC;

    static {
        char[] chars = "1234567890abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] randomChars = new char[chars.length];

        for (int i = 0; i < randomChars.length; i += 1) {
            randomChars[i] = chars[RANDOM.nextInt(chars.length)];
        }
        LOWER_ALPHA_NUMERIC = randomChars;

    }

    /**
     * 最大密码长度
     */
    public static final int MAX_LEN = 5;

    public static void main(String[] args) throws Exception {
        String pwd = randomPassword(MAX_LEN);
        System.out.println("h: " + h(pwd) + ", r: " + r(h(pwd), pwd.length()));
        // violentCrack(h(pwd));

        // 创建彩虹表
        // createRainbowTable(MAX_LEN, 10000, 1000);

        System.out.println(h("123"));
    }

    static void createRainbowTable(int passwordLength, int chainLength, int seedCount) throws Exception {
        Set<String> existSet = new CopyOnWriteArraySet<>();
        List<RainbowTableRow> rainbowTableRows = new Vector<>(seedCount);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ExecutorService es = Executors.newFixedThreadPool(1000);

        while (existSet.size() < seedCount) {
            final String text = randomPassword(passwordLength);

            if (!existSet.contains(text) && existSet.size() < seedCount) {
                es.submit(() -> {
                    RainbowTableRow row = new RainbowTableRow(text);
                    String hash = text;

                    for (int i = 0; i < chainLength; i += 1) {
                        hash = r(h(hash), passwordLength);
                    }

                    row.last = hash;

                    if (existSet.size() < seedCount) {
                        rainbowTableRows.add(row);
                        existSet.add(text);
                    }
                });
            }
        }

        stopWatch.stop();
        es.shutdownNow();

        log.info("Rainbow table size {}, cost {}ms", rainbowTableRows.size(), stopWatch.getTotalTimeMillis());
        for (int i = 0; i < 100; i += 1) {
            log.info("Rainbow table row: {}", rainbowTableRows.get(i).toString());
        }
    }

    static void violentCrack(String md5) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("开始暴力破解...");
        AtomicInteger times = new AtomicInteger();

        for (int len = 1; len <= MAX_LEN; len += 1) {
            char[] pwd = new char[len];
            crackPassword(pwd, 0, (text) -> {
                times.addAndGet(1);
                if (md5.equals(h(text))) {
                    stopWatch.stop();
                    log.info("Cracked -> password: {}, md5: {}, tryTimes: {}, cost {}ms", text, h(text), times.get(), stopWatch.getTotalTimeMillis());
                    System.exit(0);
                }
            });
        }
    }

    private static void crackPassword(char[] password, int position, Consumer<String> consumer) {
        if (position == password.length) {
            consumer.accept(new String(password));
            return;
        }

        for (char c : LOWER_ALPHA_NUMERIC) {
            password[position] = c;
            crackPassword(password, position + 1, consumer);
        }
    }

    /**
     * 随机密码
     *
     * @param len 长度
     * @return
     */
    static String randomPassword(int len) {
        StringBuilder textBuilder = new StringBuilder();

        for (int i = 0; i < len; i++) {
            textBuilder.append(LOWER_ALPHA_NUMERIC[RANDOM.nextInt(LOWER_ALPHA_NUMERIC.length)]);
        }

        return textBuilder.toString();
    }

    /**
     * H函数，即md5 hash
     *
     * @param pwd 密码明文
     * @return
     */
    static String h(String pwd) {
        return DigestUtils.md5Hex(pwd);
    }

    /**
     * R函数，这里简单处理，取前八位
     *
     * @param hash md5
     * @return
     */
    static String r(String hash, int len) {
        return hash.substring(0, len);
    }

    static class RainbowTableRow {

        public RainbowTableRow(String first) {
            this.first = first;
        }

        public String first;

        public String last;

        @Override
        public String toString() {
            return first + "," + last;
        }
    }

}
