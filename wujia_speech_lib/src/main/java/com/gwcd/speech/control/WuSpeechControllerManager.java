package com.gwcd.speech.control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sy on 2017/3/22.<br>
 * Function: <br>
 * Creator: sy<br>
 * Create time: 2017/3/22 18:24<br>
 * Revise Record:<br>
 * 2017/3/22: 创建并完成初始实现<br>
 */

public class WuSpeechControllerManager {

    /**
     * 控制接口集合
     */
    private List<WuSpeechController> mControllers;
    private static WuSpeechControllerManager mInstance;

    private WuSpeechControllerManager() {
        mControllers = new ArrayList<>();
    }

    public static WuSpeechControllerManager getInstance() {
        if (mInstance == null) {
            synchronized (WuSpeechControllerManager.class) {
                if (mInstance == null) {
                    mInstance = new WuSpeechControllerManager();
                }
            }
        }

        return mInstance;
    }

    /**
     * 注册控制接口
     * @param controller
     */
    public void registerController (WuSpeechController controller) {
        if (controller == null) {
            return;
        }
        mControllers.add(controller);
    }

    /**
     * 取消注册控制接口
     * @param controller
     */
    public void unregisterController(WuSpeechController controller) {
        if (controller == null) {
            return;
        }
        mControllers.remove(controller);
    }

    /**
     * 清除所有控制接口回调
     */
    public void unregisterAllControllers() {
        mControllers.clear();
    }

    protected List<WuSpeechController> getControllers() {
        return mControllers;
    }

    /**
     * 获取所有温控类控制接口
     * @return
     */
    protected List<AirConController>  getAirConControllers() {
        List<AirConController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            if (controller instanceof AirConController) {
                specControllers.add((AirConController) controller);
            }
        }

        return specControllers;
    }

    protected List<CameraController>  getCameraControllers() {
        List<CameraController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            if (controller instanceof CameraController) {
                specControllers.add((CameraController) controller);
            }
        }

        return specControllers;
    }

    protected List<Ch2oController>  getCh2oControllers() {
        List<Ch2oController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            if (controller instanceof Ch2oController) {
                specControllers.add((Ch2oController) controller);
            }
        }

        return specControllers;
    }

    protected List<CurtainController>  getCurtainControllers() {
        List<CurtainController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            if (controller instanceof CurtainController) {
                specControllers.add((CurtainController) controller);
            }
        }

        return specControllers;
    }

    protected List<GatewayController>  getGatewayControllers() {
        List<GatewayController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            if (controller instanceof GatewayController) {
                specControllers.add((GatewayController) controller);
            }
        }

        return specControllers;
    }

    protected List<HeatingValueController>  getHeatingValueControllers() {
        List<HeatingValueController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            if (controller instanceof HeatingValueController) {
                specControllers.add((HeatingValueController) controller);
            }
        }

        return specControllers;
    }

    protected List<InductionController>  getInductionControllers() {
        List<InductionController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            controller.getClass();
            if (controller instanceof InductionController) {
                specControllers.add((InductionController) controller);
            }
        }

        return specControllers;
    }

    protected List<LightController>  getLightControllers() {
        List<LightController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            controller.getClass();
            if (controller instanceof LightController) {
                specControllers.add((LightController) controller);
            }
        }

        return specControllers;
    }

    protected List<LockController>  getLockControllers() {
        List<LockController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            controller.getClass();
            if (controller instanceof LockController) {
                specControllers.add((LockController) controller);
            }
        }

        return specControllers;
    }

    protected List<MagnetController>  getMagnetControllers() {
        List<MagnetController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            controller.getClass();
            if (controller instanceof MagnetController) {
                specControllers.add((MagnetController) controller);
            }
        }

        return specControllers;
    }

    protected List<SceneModeController>  getSceneModeControllers() {
        List<SceneModeController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            controller.getClass();
            if (controller instanceof SceneModeController) {
                specControllers.add((SceneModeController) controller);
            }
        }

        return specControllers;
    }

    protected List<SmartBoxController>  getSmartBoxControllers() {
        List<SmartBoxController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            controller.getClass();
            if (controller instanceof SmartBoxController) {
                specControllers.add((SmartBoxController) controller);
            }
        }

        return specControllers;
    }

    protected List<SocketController>  getSocketControllers() {
        List<SocketController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            controller.getClass();
            if (controller instanceof SocketController) {
                specControllers.add((SocketController) controller);
            }
        }

        return specControllers;
    }

    protected List<SoundLightController>  getSoundLightControllers() {
        List<SoundLightController> specControllers = new ArrayList<>();
        for (WuSpeechController controller : mControllers ) {
            controller.getClass();
            if (controller instanceof SoundLightController) {
                specControllers.add((SoundLightController) controller);
            }
        }

        return specControllers;
    }
}
