package ch.bfh.android.zeadl.sensor.impl.dummy;

import java.util.Random;

import ch.bfh.android.zeadl.sensor.SensorChannel;

/**
 * Created by timo on 4/4/15.
 */
public class RandomChannel extends SensorChannel {


    private String _name;
    private int _max;
    private int _min;


    public RandomChannel(String name, int min, int max) {
        _name = name;
        _max = max;
        _min = min;
    }


    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    private Random _rand = new Random();

    @Override
    public float getSample() {
        return (_rand.nextInt(((_max - _min) + 1)*100)/100f + _min);

    }
}
