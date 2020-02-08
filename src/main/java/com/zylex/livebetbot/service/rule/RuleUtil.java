package com.zylex.livebetbot.service.rule;

import com.zylex.livebetbot.exception.RuleException;
import com.zylex.livebetbot.model.Game;

import java.io.*;

class RuleUtil {

    static Game clone(Game game) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream ous = new ObjectOutputStream(baos)) {
            ous.writeObject(game);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                return (Game) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuleException(e.getMessage(), e);
        }
    }
}
