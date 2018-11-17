package ru.deadsoftware.cavecraft.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import ru.deadsoftware.cavecraft.CaveGame;
import ru.deadsoftware.cavecraft.GameScreen;
import ru.deadsoftware.cavecraft.game.mobs.Mob;
import ru.deadsoftware.cavecraft.game.objects.Drop;
import ru.deadsoftware.cavecraft.game.objects.Player;
import ru.deadsoftware.cavecraft.misc.Assets;
import ru.deadsoftware.cavecraft.misc.Renderer;

public class GameRenderer extends Renderer {

    private GameProc gp;

    GameRenderer(GameProc gp, float width, float heigth) {
        super(width, heigth);
        Gdx.gl.glClearColor(0f, .6f, .6f, 1f);
        this.gp = gp;
    }

    private float drawX(int x) {
        return x * 16 - getCamX();
    }

    private float drawY(int y) {
        return y * 16 - getCamY();
    }

    private void drawWreck(int bl) {
        if (gp.blockDmg > 0) {
            spriter.draw(Assets.wreck[
                            10 * gp.blockDmg /
                                    GameItems.getBlock(bl).getHp()],
                    gp.curX * 16 - getCamX(),
                    gp.curY * 16 - getCamY());
        }
    }

    private void drawWorldBackground() {
        int minX = (int) (getCamX() / 16) - 1;
        int minY = (int) (getCamY() / 16) - 1;
        int maxX = (int) ((getCamX() + getWidth()) / 16) + 1;
        int maxY = (int) ((getCamY() + getHeight()) / 16) + 1;
        if (minY < 0) minY = 0;
        if (maxY > gp.world.getHeight()) maxY = gp.world.getHeight();
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                if ((gp.world.getForeMap(x, y) == 0 || GameItems.getBlock(gp.world.getForeMap(x, y)).isTransparent())
                        && gp.world.getBackMap(x, y) > 0) {
                    spriter.draw(
                            Assets.blockTex[GameItems.getBlock(gp.world.getBackMap(x, y)).getTex()],
                            drawX(x), drawY(y));
                    if (gp.world.getForeMap(x, y) == 0 && x == gp.curX && y == gp.curY)
                        drawWreck(gp.world.getBackMap(gp.curX, gp.curY));
                    Assets.shade.setPosition(drawX(x), drawY(y));
                    Assets.shade.draw(spriter);
                }
                if (gp.world.getForeMap(x, y) > 0 && GameItems.getBlock(gp.world.getForeMap(x, y)).isBackground()) {
                    spriter.draw(
                            Assets.blockTex[GameItems.getBlock(gp.world.getForeMap(x, y)).getTex()],
                            drawX(x), drawY(y));
                    if (x == gp.curX && y == gp.curY)
                        drawWreck(gp.world.getForeMap(gp.curX, gp.curY));
                }
            }
        }
    }

    private void drawWorldForeground() {
        int minX = (int) (getCamX() / 16) - 1;
        int minY = (int) (getCamY() / 16) - 1;
        int maxX = (int) ((getCamX() + getWidth()) / 16) + 1;
        int maxY = (int) ((getCamY() + getHeight()) / 16) + 1;
        if (minY < 0) minY = 0;
        if (maxY > gp.world.getHeight()) maxY = gp.world.getHeight();
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                if (gp.world.getForeMap(x, y) > 0 && !GameItems.getBlock(gp.world.getForeMap(x, y)).isBackground()) {
                    spriter.draw(
                            Assets.blockTex[GameItems.getBlock(gp.world.getForeMap(x, y)).getTex()],
                            drawX(x), drawY(y));
                    if (x == gp.curX && y == gp.curY)
                        drawWreck(gp.world.getForeMap(gp.curX, gp.curY));
                }
            }
        }
    }

    private void drawMob(Mob mob) {
        float mobDrawX = mob.pos.x - getCamX();
        float mobDrawY = mob.pos.y - getCamY();

        if (mobDrawX + mob.getWidth() - gp.world.getWidthPx() >= 0 && mobDrawX - gp.world.getWidthPx() <= getWidth())
            mob.draw(spriter, mobDrawX - gp.world.getWidthPx(), mobDrawY);

        if (mobDrawX + mob.getWidth() >= 0 && mobDrawX <= getWidth())
            mob.draw(spriter, mobDrawX, mobDrawY);

        if (mobDrawX + mob.getWidth() + gp.world.getWidthPx() >= 0 && mobDrawX + gp.world.getWidthPx() <= getWidth())
            mob.draw(spriter, mobDrawX + gp.world.getWidthPx(), mobDrawY);
    }

    private void drawDrop(Drop drop) {
        switch (GameItems.getItem(drop.getId()).getType()) {
            case 0:
                Assets.blockTex[GameItems.getItem(drop.getId()).getTex()].setPosition(
                        drop.pos.x - getCamX() - gp.world.getWidthPx(),
                        drop.pos.y - getCamY());
                Assets.blockTex[GameItems.getItem(drop.getId()).getTex()].draw(spriter);
                Assets.blockTex[GameItems.getItem(drop.getId()).getTex()].setPosition(
                        drop.pos.x - getCamX(),
                        drop.pos.y - getCamY());
                Assets.blockTex[GameItems.getItem(drop.getId()).getTex()].draw(spriter);
                Assets.blockTex[GameItems.getItem(drop.getId()).getTex()].setPosition(
                        drop.pos.x - getCamX() + gp.world.getWidthPx(),
                        drop.pos.y - getCamY());
                Assets.blockTex[GameItems.getItem(drop.getId()).getTex()].draw(spriter);
        }
    }

    private void drawPlayer(Player pl) {

        float drawX = pl.pos.x - getCamX() - 2;
        float drawY = pl.pos.y - getCamY();

        if (pl.mov.x != 0 || Assets.plSprite[0][2].getRotation() != 0) {
            Assets.plSprite[0][2].rotate(Player.ANIM_SPEED);
            Assets.plSprite[1][2].rotate(-Player.ANIM_SPEED);
            Assets.plSprite[0][3].rotate(-Player.ANIM_SPEED);
            Assets.plSprite[1][3].rotate(Player.ANIM_SPEED);
        } else {
            Assets.plSprite[0][2].setRotation(0);
            Assets.plSprite[1][2].setRotation(0);
            Assets.plSprite[0][3].setRotation(0);
            Assets.plSprite[1][3].setRotation(0);
        }
        if (Assets.plSprite[0][2].getRotation() >= 60 || Assets.plSprite[0][2].getRotation() <= -60)
            Player.ANIM_SPEED = -Player.ANIM_SPEED;

        //back hand
        Assets.plSprite[1][2].setPosition(drawX - 6, drawY);
        Assets.plSprite[1][2].draw(spriter);
        //back leg
        Assets.plSprite[1][3].setPosition(drawX - 6, drawY + 10);
        Assets.plSprite[1][3].draw(spriter);
        //front leg
        Assets.plSprite[0][3].setPosition(drawX - 6, drawY + 10);
        Assets.plSprite[0][3].draw(spriter);
        //head
        spriter.draw(Assets.plSprite[pl.getDir()][0], drawX - 2, drawY - 2);
        //body
        spriter.draw(Assets.plSprite[pl.getDir()][1], drawX - 2, drawY + 8);
        //item in hand
        if (pl.inv[gp.slot] > 0) {
            float handRotation = MathUtils.degRad * Assets.plSprite[0][2].getRotation();
            switch (GameItems.getItem(pl.inv[gp.slot]).getType()) {
                case 0:
                    Assets.blockTex[GameItems.getItem(pl.inv[gp.slot]).getTex()].setPosition(
                            drawX - 8 * MathUtils.sin(handRotation),
                            drawY + 6 + 8 * MathUtils.cos(handRotation));
                    Assets.blockTex[GameItems.getItem(pl.inv[gp.slot]).getTex()].draw(spriter);
                    break;
                default:
                    Assets.itemTex[GameItems.getItem(pl.inv[gp.slot]).getTex()].flip((pl.getDir() == 0), false);
                    Assets.itemTex[GameItems.getItem(pl.inv[gp.slot]).getTex()].setRotation(
                            -45 + pl.getDir() * 90 + Assets.plSprite[0][2].getRotation());
                    Assets.itemTex[GameItems.getItem(pl.inv[gp.slot]).getTex()].setPosition(
                            drawX - 10 + (12 * pl.getDir()) - 8 * MathUtils.sin(handRotation),
                            drawY + 2 + 8 * MathUtils.cos(handRotation));
                    Assets.itemTex[GameItems.getItem(pl.inv[gp.slot]).getTex()].draw(spriter);
                    Assets.itemTex[GameItems.getItem(pl.inv[gp.slot]).getTex()].flip((pl.getDir() == 0), false);
                    break;
            }
        }
        //front hand
        Assets.plSprite[0][2].setPosition(drawX - 6, drawY);
        Assets.plSprite[0][2].draw(spriter);
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    private void drawCreative() {
        float x = getWidth() / 2 - (float) Assets.creativeInv.getRegionWidth() / 2;
        float y = getHeight() / 2 - (float) Assets.creativeInv.getRegionHeight() / 2;
        spriter.draw(Assets.creativeInv, x, y);
        spriter.draw(Assets.creativeScr, x + 156,
                y + 18 + (gp.creativeScroll * (72f / gp.maxCreativeScroll)));
        for (int i = gp.creativeScroll * 8; i < gp.creativeScroll * 8 + 40; i++) {
            if (i > 0 && i < GameItems.getItemsSize())
                switch (GameItems.getItem(i).getType()) {
                    case 0:
                        spriter.draw(Assets.blockTex[GameItems.getItem(i).getTex()],
                                x + 8 + ((i - gp.creativeScroll * 8) % 8) * 18,
                                y + 18 + ((i - gp.creativeScroll * 8) / 8) * 18);
                        break;
                    case 1:
                        spriter.draw(Assets.itemTex[GameItems.getItem(i).getTex()],
                                x + 8 + ((i - gp.creativeScroll * 8) % 8) * 18,
                                y + 18 + ((i - gp.creativeScroll * 8) / 8) * 18);
                        break;
                }
        }
        for (int i = 0; i < 9; i++) {
            if (gp.player.inv[i] > 0)
                switch (GameItems.getItem(gp.player.inv[i]).getType()) {
                    case 0:
                        spriter.draw(Assets.blockTex[GameItems.getItem(gp.player.inv[i]).getTex()],
                                x + 8 + i * 18, y + Assets.creativeInv.getRegionHeight() - 24);
                        break;
                    case 1:
                        spriter.draw(Assets.itemTex[GameItems.getItem(gp.player.inv[i]).getTex()],
                                x + 8 + i * 18, y + Assets.creativeInv.getRegionHeight() - 24);
                        break;
                }
        }
    }

    private void drawGUI() {
        if (gp.world.getForeMap(gp.curX, gp.curY) > 0 ||
                gp.world.getBackMap(gp.curX, gp.curY) > 0 ||
                gp.ctrlMode == 1 ||
                !CaveGame.TOUCH)
            spriter.draw(Assets.guiCur,
                    gp.curX * 16 - getCamX(),
                    gp.curY * 16 - getCamY());
        spriter.draw(Assets.invBar, getWidth() / 2 - (float) Assets.invBar.getRegionWidth() / 2, 0);
        for (int i = 0; i < 9; i++) {
            if (gp.player.inv[i] > 0) {
                switch (GameItems.getItem(gp.player.inv[i]).getType()) {
                    case 0:
                        spriter.draw(Assets.blockTex[GameItems.getItem(gp.player.inv[i]).getTex()],
                                getWidth() / 2 - (float) Assets.invBar.getRegionWidth() / 2 + 3 + i * 20,
                                3);
                        break;
                    case 1:
                        spriter.draw(Assets.itemTex[GameItems.getItem(gp.player.inv[i]).getTex()],
                                getWidth() / 2 - (float) Assets.invBar.getRegionWidth() / 2 + 3 + i * 20,
                                3);
                        break;
                }
            }
        }
        spriter.draw(Assets.invBarCur,
                getWidth() / 2 - (float) Assets.invBar.getRegionWidth() / 2 - 1 + 20 * gp.slot,
                -1);
    }

    private void drawTouchGui() {
        spriter.draw(Assets.touchArrows[0], 26, getHeight() - 52);
        spriter.draw(Assets.touchArrows[1], 0, getHeight() - 26);
        spriter.draw(Assets.touchArrows[2], 26, getHeight() - 26);
        spriter.draw(Assets.touchArrows[3], 52, getHeight() - 26);
        spriter.draw(Assets.touchLMB, getWidth() - 52, getHeight() - 26);
        spriter.draw(Assets.touchRMB, getWidth() - 26, getHeight() - 26);
        spriter.draw(Assets.touchMode, 78, getHeight() - 26);
        if (gp.ctrlMode == 1) {
            Assets.shade.setPosition(83, getHeight() - 21);
            Assets.shade.draw(spriter);
        }
    }

    private void drawGamePlay() {
        drawWorldBackground();
        drawPlayer(gp.player);
        for (Mob mob : gp.mobs) drawMob(mob);
        for (Drop drop : gp.drops) drawDrop(drop);
        drawWorldForeground();
        drawGUI();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriter.begin();
        switch (CaveGame.STATE) {
            case GAME_PLAY:
                drawGamePlay();
                break;
            case GAME_CREATIVE_INV:
                drawGamePlay();
                drawCreative();
                break;
        }

        if (CaveGame.TOUCH) drawTouchGui();

        if (GameScreen.SHOW_DEBUG) {
            drawString("FPS: " + GameScreen.FPS, 0, 0);
            drawString("X: " + (int) (gp.player.pos.x / 16), 0, 10);
            drawString("Y: " + (int) (gp.player.pos.y / 16), 0, 20);
            drawString("CurX: " + gp.curX, 0, 30);
            drawString("CurY: " + gp.curY, 0, 40);
            drawString("Mobs: " + gp.mobs.size(), 0, 50);
            drawString("Drops: " + gp.drops.size(), 0, 60);
            drawString("Block: " + GameItems.getBlockKey(gp.world.getForeMap(gp.curX, gp.curY)), 0, 70);
            drawString("Game mode: " + gp.player.gameMode, 0, 80);
        }
        spriter.end();

        shaper.begin(ShapeRenderer.ShapeType.Line);
        shaper.setColor(Color.BLACK);
        shaper.rect(gp.player.pos.x - getCamX(), gp.player.pos.y - getCamY(), gp.player.getWidth(), gp.player.getHeight());
        shaper.end();
    }

}
