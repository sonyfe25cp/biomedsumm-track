package utils;

import java.util.Random;

public class TestR {

    public static void main(String[] args) {
        for (int i = 0; i < 30; i++) {
            Random random = new Random();
            int pos = Math.round(random.nextFloat() * 3);
            System.out.println(pos);
        }
    }

}
