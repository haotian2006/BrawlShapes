package SimpleUI;

import EasingLib.*;

public class UiTweenInfo {
    public final EasingFunction func;
    public final EasingType type;
    public final double duration;

    public UiTweenInfo(double duration) {
        this.func = EasingFunction.Linear;
        this.type = EasingType.easeIn;
        this.duration = duration;
    }

    public UiTweenInfo(double duration,EasingFunction func, EasingType type) {
        this.func = func;
        this.type = type;
        this.duration = duration;
    }

}