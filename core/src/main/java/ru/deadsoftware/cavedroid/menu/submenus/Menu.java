package ru.deadsoftware.cavedroid.menu.submenus;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;
import ru.deadsoftware.cavedroid.MainConfig;
import ru.deadsoftware.cavedroid.menu.MenuProc;
import ru.deadsoftware.cavedroid.menu.objects.BooleanOptionButton;
import ru.deadsoftware.cavedroid.menu.objects.Button;
import ru.deadsoftware.cavedroid.menu.objects.ButtonEventListener;
import ru.deadsoftware.cavedroid.menu.objects.ButtonRenderer;
import ru.deadsoftware.cavedroid.misc.Assets;
import ru.deadsoftware.cavedroid.misc.utils.AssetLoader;
import ru.fredboy.cavedroid.domain.assets.usecase.GetTextureRegionByNameUseCase;

import java.util.HashMap;

public abstract class Menu {

    protected final MainConfig mMainConfig;
    protected final MenuProc.Input mMenuInput;
    protected final AssetLoader mAssetLoader;

    protected final GetTextureRegionByNameUseCase mGetTextureRegionByNameUseCase;

    private final ButtonRenderer mButtonRenderer;

    private final float mWidth;
    private final float mHeight;

    /**
     * {@link ArrayMap} of {@link Button Buttons} of this menu screen
     */
    private ArrayMap<String, Button> buttons;

    /**
     * @param width          Viewport width
     * @param height         Viewport height
     * @param buttonRenderer {@link ButtonRenderer} that will draw the buttons of this menu
     */
    Menu(float width,
         float height,
         ButtonRenderer buttonRenderer,
         MainConfig mainConfig,
         MenuProc.Input menuInput,
         AssetLoader assetLoader,
         GetTextureRegionByNameUseCase getTextureRegionByNameUseCase) {
        mWidth = width;
        mHeight = height;
        mButtonRenderer = buttonRenderer;
        mMainConfig = mainConfig;
        mMenuInput = menuInput;
        mAssetLoader = assetLoader;
        mGetTextureRegionByNameUseCase = getTextureRegionByNameUseCase;
    }

    final void init() {
        initButtons();
    }

    /**
     * If you are loading buttons from json,
     * override this method and create a HashMap with buttons' keys from json as keys
     * and {@link ButtonEventListener ButtonEventListeners} as values.
     *
     * @return empty HashMap if not overridden
     */
    protected HashMap<String, ButtonEventListener> getButtonEventListeners() {
        return new HashMap<>();
    }

    /**
     * You can call this from {@link #initButtons()} to load buttons from json
     *
     * @param jsonFile A {@link FileHandle} to json file
     */
    void loadButtonsFromJson(FileHandle jsonFile) {
        if (buttons == null) {
            buttons = new ArrayMap<>();
        }
        HashMap<String, ButtonEventListener> eventListeners = getButtonEventListeners();
        JsonValue json = Assets.jsonReader.parse(jsonFile);
        int y = (int) mHeight / 4;
        for (JsonValue key = json.child(); key != null; key = key.next(), y += Button.HEIGHT + 10) {

            if (Gdx.app.getType() == Application.ApplicationType.Android &&
                    !Assets.getBooleanFromJson(key, "visible_on_android", true)) {
                continue;
            }

            String optionType = Assets.getStringFromJson(key, "option_type", "");
            String label = Assets.getStringFromJson(key, "label", "");
            int x = (int) mWidth / 2 - Button.WIDTH / 2;
            int type = Assets.getIntFromJson(key, "type", Button.NORMAL);
            String defaultValue = Assets.getStringFromJson(key, "default_value", "");


            Button button = switch (optionType) {
                case "boolean" ->
                    new BooleanOptionButton(mMainConfig, key.name(), Boolean.parseBoolean(defaultValue), label, x, y, type);
                default ->
                    new Button(label, x, y, type, eventListeners.containsKey(key.name()) ? eventListeners.get(key.name()) : () -> {});
            };

            buttons.put(key.name(), button);
        }
    }

    /**
     * Draws the menu with background, logo and it's buttons
     *
     * @param spriter {@link SpriteBatch} that will draw it. Should be already started.
     */
    public void draw(SpriteBatch spriter) {
        TextureRegion background = mGetTextureRegionByNameUseCase.get("background");
        TextureRegion gamelogo = mGetTextureRegionByNameUseCase.get("gamelogo");

        for (int x = 0; x <= mWidth / 16; x++) {
            for (int y = 0; y <= mHeight / 16; y++) {
                spriter.draw(background, x * 16, y * 16);
            }
        }
        spriter.draw(gamelogo, mWidth / 2 - (float) gamelogo.getRegionWidth() / 2, 8);

        float inputX = Gdx.input.getX() * mWidth / Gdx.graphics.getWidth();
        float inputY = Gdx.input.getY() * mHeight / Gdx.graphics.getHeight();
        for (Button button : buttons.values()) {
            if (button.getType() > 0) {
                if (button.getRect().contains(inputX, inputY) && (/*!CaveGame.TOUCH || */Gdx.input.isTouched())) {
                    button.setType(2);
                } else {
                    button.setType(1);
                }
            }
            button.draw(mButtonRenderer);
        }
    }

    public ArrayMap<String, Button> getButtons() {
        return buttons;
    }

    /**
     * This method is called from constructor and should initialize {@link #buttons} <br>
     * You can run {@link #loadButtonsFromJson(FileHandle)} from it
     */
    protected abstract void initButtons();
}
