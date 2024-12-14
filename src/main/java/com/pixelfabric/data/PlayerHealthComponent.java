package com.pixelfabric.data;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

public class PlayerHealthComponent implements Component, IPlayerHealthData {
    private int extraHearts = 0;
    private int goldenHearts = 0;  // Cantidad de corazones convertidos a dorados

    @Override
    public int getExtraHearts() {
        return extraHearts;
    }

    @Override
    public void setExtraHearts(int hearts) {
        this.extraHearts = hearts;
    }

    @Override
    public int getGoldenHearts() {
        return goldenHearts;
    }

    @Override
    public void setGoldenHearts(int goldenHearts) {
        this.goldenHearts = goldenHearts;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.extraHearts = tag.getInt("extraHearts");
        this.goldenHearts = tag.getInt("goldenHearts");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("extraHearts", this.extraHearts);
        tag.putInt("goldenHearts", this.goldenHearts);
    }
}