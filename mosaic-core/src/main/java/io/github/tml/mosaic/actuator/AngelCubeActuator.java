package io.github.tml.mosaic.actuator;

import io.github.tml.mosaic.core.NamedThreadFactory;
import io.github.tml.mosaic.core.execption.ActuatorException;
import io.github.tml.mosaic.cube.external.AngelCube;
import io.github.tml.mosaic.cube.external.MosaicCube;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
public class AngelCubeActuator extends AbstractCubeActuator{

    private final Map<String, AngelCubeWorker> angelCubeWorkerMap = new ConcurrentHashMap<>();

    private final ExecutorService angelCubeExecutor;

    /**
     * 清道夫线程池，负责清理stop还未结束的cube
     */
    private final ExecutorService scavenger;

    /**
     * 临终关怀任务队列
     * 等待stop关闭的cube
     */
    private final BlockingQueue<AngelCubeWorker> deathbedCareList = new LinkedBlockingQueue<>();

    public AngelCubeActuator() {
        angelCubeExecutor = new ThreadPoolExecutor(0 ,Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory("angel-cube-thread")
        );
        scavenger = Executors.newSingleThreadExecutor(new NamedThreadFactory("angel-cube-scavenger"));

        runScavenger();
    }

    /**
     * 清道夫线程启动
     * 负责对正在运行stop的天使线程进行清道夫处理
     * 如果在2s内未完成stop操作则强制清理
     */
    private void runScavenger() {
        scavenger.execute(()->{
            for(;;){
                AngelCubeWorker worker = deathbedCareList.poll();
                if (worker != null){
                    log.info("scavenger ready handle angle cube {} stop", worker.angelCube.cubeId());
                    Future<?> stopFuture = worker.getStopFuture();
                    if (stopFuture != null && !stopFuture.isDone()){
                        try {
                            stopFuture.get(2, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            log.error("scavenger handle angle cube {} stop get error:{}", worker.angelCube.cubeId(), e.getMessage());
                        }
                        log.error("angle cube:{} stop time over 2s, force stop", worker.angelCube.cubeId());
                        stopFuture.cancel(true);
                    }
                    removeAngleCube(worker.angelCube.cubeId());
                }
            }
        });
    }

    /**
     * 从执行上下文中获取天使方块，如果不是则抛出移除
     * @param executeContext
     * @return
     */
    private AngelCube checkAndGetAngleCube(ExecuteContext executeContext) throws ActuatorException{
        MosaicCube cube = executeContext.getCube();
        if (!(cube instanceof AngelCube)) {
            throw new ActuatorException("{} is not an AngelCube.");
        }
        return (AngelCube) cube;
    }

    @Override
    public <T> T execute(ExecuteContext executeContext) {
        AngelCube cube = checkAndGetAngleCube(executeContext);
        String cubeId = cube.cubeId();
        synchronized (cubeId){
            AngelCubeWorker worker = angelCubeWorkerMap.getOrDefault(cubeId, new AngelCubeWorker(cube));
            if (!angelCubeWorkerMap.containsKey(cubeId)) {
                worker.start();
                angelCubeWorkerMap.put(cubeId, worker);
            }
        }
        return null;
    }



    @Override
    public boolean stop(ExecuteContext executeContext) {
        AngelCube cube = checkAndGetAngleCube(executeContext);
        String cubeId = cube.cubeId();
        synchronized (cubeId){
            AngelCubeWorker worker = angelCubeWorkerMap.get(cubeId);
            if(Objects.isNull(worker)){
                return false;
            }
            if(Objects.isNull(worker.getStopFuture())){
                worker.stop();
            }else{
                // 再次停止会交给清道夫去关停cube
                deathbedCareList.add(worker);
            }
            return true;
        }
    }

    public void removeAngleCube(String cubeId){
        synchronized (cubeId){
            angelCubeWorkerMap.remove(cubeId);
        }
    }
    public class AngelCubeWorker{

        private final AngelCube angelCube;

        private final String name;

        private Future<?> startFuture;

        @Getter
        private Future<?> stopFuture;

        private static final String workerNamePrefix = "angel-cube-worker-";

        public AngelCubeWorker(AngelCube angelCube) {
            this.angelCube = angelCube;
            this.name = workerNamePrefix+angelCube.cubeId();
        }

        public void start() {
            startFuture = angelCubeExecutor.submit(()->{
                log.info("Starting AngelCubeWorker: {}", name);
                angelCube.start();
            });
        }

        public void stop(){
            stopFuture = angelCubeExecutor.submit(() -> {
                log.info("Stopping AngelCubeWorker: {}", name);
                angelCube.stop();
                if (!startFuture.isDone()) {
                    startFuture.cancel(true);
                }
                removeAngleCube(angelCube.cubeId());
            });
        }
    }

}
