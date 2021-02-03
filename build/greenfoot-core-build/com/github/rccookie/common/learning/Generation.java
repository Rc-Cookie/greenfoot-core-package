package com.github.rccookie.common.learning;

import java.util.List;

public abstract class Generation {

    final List<Network> networks;
    
    public Generation(List<Network> networks) {
        this.networks = networks;
    }
}
