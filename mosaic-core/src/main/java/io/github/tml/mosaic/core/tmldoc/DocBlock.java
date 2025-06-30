package io.github.tml.mosaic.core.tmldoc;

public interface DocBlock <T extends DocProperties>{

    void assembly(DocBlock<T> block);

    T getProperties();
}
