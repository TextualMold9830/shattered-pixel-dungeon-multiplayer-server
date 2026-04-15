package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndTest extends Window {

    public WndTest(Hero owner) {
        super(160, 100);
        setOwnerHero(owner);
        attachToHero(owner);

        RenderedTextBlock text = new RenderedTextBlock("This is a test window using generic UI serialization.", 6);
        text.maxWidth(140);
        text.setPos(10, 10);
        add(text);

        RedButton btn1 = new RedButton("Button 1") {
            @Override
            protected void onClick() {
                // Example action
                hide();
            }
        };
        btn1.setRect(10, text.bottom() + 10, 60, 20);
        add(btn1);

        RedButton btn2 = new RedButton("Button 2") {
            @Override
            protected void onClick() {
                // Example action
                hide();
            }
        };
        btn2.setRect(80, text.bottom() + 10, 60, 20);
        add(btn2);
    }
}
