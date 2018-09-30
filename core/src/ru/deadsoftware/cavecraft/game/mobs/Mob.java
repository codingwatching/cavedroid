package ru.deadsoftware.cavecraft.game.mobs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public abstract class Mob implements Serializable {

    public int ANIM_SPEED = 6;
    public Vector2 position;
    public Vector2 move;
    public int width, height, dir, animation;
    public boolean canJump;
    public boolean dead;

    public abstract void ai();

    public abstract void changeDir();

    public abstract void draw(SpriteBatch spriteBatch, float x, float y);

    public abstract Rectangle getRect();

    public abstract int getType(); //0 - mob, 10 - sand, 11 - gravel
}
