package com.zylex.livebetbot.controller.dao;

import java.sql.Connection;

public class TmlDao {

    private Connection connection;

    public TmlDao(Connection connection) {
        this.connection = connection;
    }
}
