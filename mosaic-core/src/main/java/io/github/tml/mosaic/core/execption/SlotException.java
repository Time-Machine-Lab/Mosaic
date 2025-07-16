package io.github.tml.mosaic.core.execption;

public class SlotException  extends RuntimeException  {
    public SlotException(String message) {
        super(message);
    }

    public static final SlotException CREATE_SLOT_FAILED = new SlotException("create slot failed");
}
