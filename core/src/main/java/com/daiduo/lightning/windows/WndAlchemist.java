package com.daiduo.lightning.windows;

import com.daiduo.lightning.Assets;
import com.daiduo.lightning.Chrome;
import com.daiduo.lightning.actors.hero.Hero;
import com.daiduo.lightning.actors.mobs.npcs.Alchemist;
import com.daiduo.lightning.items.Item;
import com.daiduo.lightning.messages.Messages;
import com.daiduo.lightning.scenes.GameScene;
import com.daiduo.lightning.scenes.PixelScene;
import com.daiduo.lightning.ui.ItemSlot;
import com.daiduo.lightning.ui.RedButton;
import com.daiduo.lightning.ui.RenderedTextMultiline;
import com.daiduo.lightning.ui.Window;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

/**
 * Created by badcw on 2017/7/27.
 */

public class WndAlchemist extends Window {
    private static final int BTN_SIZE	= 36;
    private static final float GAP		= 2;
    private static final float BTN_GAP	= 10;
    private static final int WIDTH		= 116;

    private ItemButton btnPressed;

    private ItemButton btnItem1;
    private ItemButton btnItem2;
    private RedButton btnReforge;

    public WndAlchemist(Alchemist troll, Hero hero ) {

        super();

        IconTitle titlebar = new IconTitle();
        titlebar.icon( troll.sprite() );
        titlebar.label( Messages.titleCase( troll.name ) );
        titlebar.setRect( 0, 0, WIDTH, 0 );
        add( titlebar );

        RenderedTextMultiline message = PixelScene.renderMultiline( Messages.get(this, "prompt"), 6 );
        message.maxWidth( WIDTH);
        message.setPos(0, titlebar.bottom() + GAP);
        add( message );

        btnItem1 = new ItemButton() {
            @Override
            protected void onClick() {
                btnPressed = btnItem1;
                GameScene.selectItem( itemSelector, WndBag.Mode.POTION, Messages.get(WndBlacksmith.class, "select") );
            }
        };
        btnItem1.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
        add( btnItem1 );

        btnItem2 = new ItemButton() {
            @Override
            protected void onClick() {
                btnPressed = btnItem2;
                GameScene.selectItem( itemSelector, WndBag.Mode.POTION, Messages.get(WndBlacksmith.class, "select") );
            }
        };
        btnItem2.setRect( btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE, BTN_SIZE );
        add( btnItem2 );

        btnReforge = new RedButton( Messages.get(this, "reforge") ) {
            @Override
            protected void onClick() {
                Alchemist.mix( btnItem1.item, btnItem2.item );
                hide();
            }
        };
        btnReforge.enable( true );
        btnReforge.setRect( 0, btnItem1.bottom() + BTN_GAP, WIDTH, 20 );
        add( btnReforge );


        resize( WIDTH, (int)btnReforge.bottom() );
    }

    protected WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect( Item item ) {
            if (item != null) {
                btnPressed.item( item );

                if (btnItem1.item != null && btnItem2.item != null) {
                    String result = Alchemist.verify( btnItem1.item, btnItem2.item );
                    if (result != null) {
                        GameScene.show( new WndMessage( result ) );
                        btnReforge.enable( false );
                    } else {
                        btnReforge.enable( true );
                    }
                }
            }
        }
    };

    public static class ItemButton extends Component {

        protected NinePatch bg;
        protected ItemSlot slot;

        public Item item = null;

        @Override
        protected void createChildren() {
            super.createChildren();

            bg = Chrome.get( Chrome.Type.BUTTON );
            add( bg );

            slot = new ItemSlot() {
                @Override
                protected void onTouchDown() {
                    bg.brightness( 1.2f );
                    Sample.INSTANCE.play( Assets.SND_CLICK );
                };
                @Override
                protected void onTouchUp() {
                    bg.resetColor();
                }
                @Override
                protected void onClick() {
                    ItemButton.this.onClick();
                }
            };
            slot.enable(true);
            add( slot );
        }

        protected void onClick() {};

        @Override
        protected void layout() {
            super.layout();

            bg.x = x;
            bg.y = y;
            bg.size( width, height );

            slot.setRect( x + 2, y + 2, width - 4, height - 4 );
        };

        public void item( Item item ) {
            slot.item( this.item = item );
        }
    }

}
