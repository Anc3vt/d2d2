package com.ancevt.d2d2.motion;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.event.BaseEventDispatcher;
import com.ancevt.d2d2.event.Event;
import lombok.Getter;

import java.util.function.Consumer;

import static com.ancevt.d2d2.motion.Motion.State.*;

public class Motion<N extends Number> extends BaseEventDispatcher {

    @Getter
    private final N beginValue;
    @Getter
    private final N endValue;
    @Getter
    private final N step;
    @Getter
    private State state;

    private final Consumer<N> applyFunction;
    private N currentValue;
    private final NumberOperations<N> numberOperations;

    public Motion(N beginValue, N endValue, N step, Consumer<N> applyFunction) {
        this.beginValue = beginValue;
        this.endValue = endValue;
        this.step = step;
        this.applyFunction = applyFunction;
        state = NEW;

        if (beginValue instanceof Integer) {
            numberOperations = (NumberOperations<N>) new IntegerOperations();
        } else if (beginValue instanceof Float) {
            numberOperations = (NumberOperations<N>) new FloatOperations();
        } else if (beginValue instanceof Double) {
            numberOperations = (NumberOperations<N>) new DoubleOperations();
        } else {
            throw new IllegalArgumentException("type not supported");
        }
    }

    public void start() {
        D2D2.stage().removeEventListener(this, Event.LOOP_UPDATE);
        D2D2.stage().addEventListener(this, Event.LOOP_UPDATE, this::stage_loopUpdate);
        dispatchEvent(Event.builder().type(Event.START).build());
        state = IN_ACTION;
    }

    public void stop() {
        D2D2.stage().removeEventListener(this, Event.LOOP_UPDATE);
    }

    private void stage_loopUpdate(Event event) {
        process();
    }

    private void process() {
        if (currentValue == null) {
            currentValue = beginValue;
        }

        if (numberOperations.lessThan(currentValue, endValue)) {
            currentValue = numberOperations.add(currentValue, step);

            if (numberOperations.greaterThanOrEqual(currentValue, endValue)) {
                currentValue = endValue;
                complete();
            }

        } else if (numberOperations.greaterThan(currentValue, endValue)) {
            currentValue = numberOperations.subtract(currentValue, step);

            if (numberOperations.lessThanOrEqual(currentValue, endValue)) {
                currentValue = endValue;
                complete();
            }
        }

        applyFunction.accept(currentValue);
    }

    private void complete() {
        stop();
        state = COMPLETE;
        dispatchEvent(Event.builder().type(Event.COMPLETE).build());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "beginValue=" + beginValue +
                ", endValue=" + endValue +
                ", step=" + step +
                ", currentValue=" + currentValue +
                ", state=" + state +
                '}';
    }

    enum State {
        NEW,
        IN_ACTION,
        COMPLETE
    }
}

