package com.shatteredpixel.shatteredpixeldungeon.plugins.events;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

public class ChatEvent extends Event {
    public String message;
    public Hero sender;
    public boolean canceled = false;
    public ChatEvent(String message, Hero sender) {
        this.message = message;
        this.sender = sender;
    }
}
