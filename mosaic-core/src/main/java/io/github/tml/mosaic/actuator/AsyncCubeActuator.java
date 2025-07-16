package io.github.tml.mosaic.actuator;

/**
 * 异步执行器
 * TODO
 */
public class AsyncCubeActuator extends AbstractCubeActuator{

    protected AsyncCubeActuator() {

    }

    @Override
    public <T> T execute(ExecuteContext executeContext) {
        return null;
    }

    @Override
    public boolean stop(ExecuteContext executeContext) {
        return true;
    }
}
