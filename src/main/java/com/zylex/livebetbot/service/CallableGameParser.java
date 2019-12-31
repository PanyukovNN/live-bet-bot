package com.zylex.livebetbot.service;

import com.zylex.livebetbot.model.Game;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Thread for parsing one league link.
 */
public class CallableGameParser implements Callable<List<Game>> {

    @Override
    public List<Game> call() throws Exception {
        return null;
    }
}
