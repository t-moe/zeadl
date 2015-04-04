package ch.bfh.android.zeadl.impl;

import java.util.Random;

import ch.bfh.android.zeadl.SensorChannel;

/**
 * Created by timo on 4/4/15.
 */
public class RandomChannel implements SensorChannel {


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

    @Override
    public int getMaximalSampleRate() {
        return 0;
    }

    private Random _rand = new Random();

    @Override
    public double getSample() {
        return _rand.nextInt((_max - _min) + 1) + _min;
    }
}
