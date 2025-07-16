package io.github.tml.mosaic.actuator;

import io.github.tml.mosaic.core.execption.ActuatorException;
import io.github.tml.mosaic.cube.ExtPointResult;
import io.github.tml.mosaic.cube.ExtensionPoint;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import io.github.tml.mosaic.cube.external.MosaicResult;
import io.github.tml.mosaic.cube.external.MosaicVoid;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

import static io.github.tml.mosaic.core.CubeConstant.DEFAULT_RETURN_NAME;

@Slf4j
public abstract class AbstractCubeActuator implements CubeActuator{

    protected AbstractCubeActuator(){

    }

    //TODO 优化 结果处理应该抽离
    protected <T> T execute0(ExecuteContext executeContext) throws ActuatorException{

        MosaicExtPackage<?> mosaicExtPackage = executeContext.getExPackage();
        ExtensionPoint exPoint = executeContext.getExPoint();

        try {
            Method method = mosaicExtPackage.getClass().getMethod(exPoint.getMethodName(), exPoint.getParameterTypes());
            Object obj = method.invoke(mosaicExtPackage, executeContext.getArgs());
            Class<?> returnType = exPoint.getReturnType();

            MosaicResult mosaicResult = null;
            if(returnType.isAssignableFrom(MosaicResult.class)){
                mosaicResult = (MosaicResult) obj;
                mosaicResult.put(DEFAULT_RETURN_NAME, obj);
            }else if(!MosaicVoid.isVoid(returnType)){
                mosaicResult= MosaicResult.build()
                        .put(DEFAULT_RETURN_NAME, obj)
                        .build();
            }
            ExtPointResult extPointResult = executeContext.getExPoint().getReturnResult();
            if(Objects.nonNull(mosaicResult)){
                String resName = Optional.ofNullable(executeContext.getSlot().getSetupCubeInfo().getResName())
                        .orElse(DEFAULT_RETURN_NAME);

                ExtPointResult.ExtPointResultItem resultItem = extPointResult.getResultItem(resName);
                return (T) mosaicResult.getResult(resultItem.getItemName(), resultItem.getItemClass());
            }else{
                return (T) exPoint.getReturnType().cast(obj);
            }
        } catch (NoSuchMethodException e) {
            throw new ActuatorException("no such method error:" + e.getMessage());
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ActuatorException("invoke method error:" + e.getMessage());
        } catch (Exception e){
            throw new ActuatorException("cube actuator execute point error:" + e.getMessage());
        }
    }

}
