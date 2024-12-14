package com.pixelfabric.data;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface IPlayerHealthData extends Component {
    int getExtraHearts();
    void setExtraHearts(int hearts);
    int getGoldenHearts();
    void setGoldenHearts(int goldenHearts);
}