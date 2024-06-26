package ru.deadsoftware.cavedroid.game.objects.drop;

import org.jetbrains.annotations.NotNull;
import ru.deadsoftware.cavedroid.game.GameItemsHolder;
import ru.deadsoftware.cavedroid.game.GameScope;
import ru.deadsoftware.cavedroid.game.model.dto.SaveDataDto;
import ru.deadsoftware.cavedroid.game.model.item.InventoryItem;
import ru.deadsoftware.cavedroid.game.model.item.Item;
import ru.deadsoftware.cavedroid.misc.Saveable;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.LinkedList;

@GameScope
public class DropController implements Saveable {

    private static final int SAVE_DATA_VERSION = 1;

    public interface Callback {
        void run(Drop drop);
    }

    private final LinkedList<Drop> mDrops = new LinkedList<>();

    @Inject
    public DropController() {
    }

    public void initDrops(GameItemsHolder gameItemsHolder) {
        for (Drop drop : mDrops) {
            drop.initItem(gameItemsHolder);
        }
    }

    public void addDrop(float x, float y, Item item) {
        addDrop(x, y, item, 1);
    }

    public void addDrop(float x, float y, Item item, int count) {
        if (item.isNone()) {
            return;
        }
        mDrops.add(new Drop(x, y, item, count));
    }

    public void addDrop(float x, float y, @NotNull InventoryItem invItem) {
        addDrop(x, y, invItem.getItem(), invItem.getAmount());
    }

    public int getSize() {
        return mDrops.size();
    }

    public void forEach(Callback callback) {
        for (Drop drop : mDrops) {
            callback.run(drop);
        }
    }

    public Iterator<Drop> getIterator() {
        return mDrops.iterator();
    }

    @Override
    @NotNull
    public SaveDataDto.DropControllerSaveData getSaveData() {
        final LinkedList<SaveDataDto.DropSaveData> dropSaveData = new LinkedList<>();
        for (Drop drop : mDrops) {
            dropSaveData.add(drop.getSaveData());
        }
        return new SaveDataDto.DropControllerSaveData(SAVE_DATA_VERSION, dropSaveData);
    }

    public static DropController fromSaveData(
            SaveDataDto.DropControllerSaveData saveData, GameItemsHolder gameItemsHolder) {
        saveData.verifyVersion(SAVE_DATA_VERSION);

        DropController controller = new DropController();

        for (SaveDataDto.DropSaveData dropSaveData : saveData.getDrops()) {
            controller.mDrops.add(Drop.Companion.fromSaveData(dropSaveData, gameItemsHolder));
        }

        return controller;
    }

}
