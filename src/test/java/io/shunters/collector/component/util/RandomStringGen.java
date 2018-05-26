package io.shunters.collector.component.util;

/**
 * Created by mykidong on 2018-03-05.
 */
public class RandomStringGen {

    public static String getRandomString(int num)
    {
        String sum = "";

        for(int i = 0; i < num; i++)
        {
            sum += "" + i;
        }

        return sum;
    }
}
