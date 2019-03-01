package de.devland.lockscreenbirthdays.util;

import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class ForwardConditionedSlide extends SimpleSlide {


    private final Condition forwardCondition;

    private ForwardConditionedSlide(ForwardConditionedBuilder builder) {
        super(builder);
        this.forwardCondition = builder.forwardCondition;
    }

    @Override
    public boolean canGoForward() {
        return super.canGoForward() && forwardCondition.canGoForward();
    }

    public interface Condition {
        boolean canGoForward();
    }


    public static class ForwardConditionedBuilder extends SimpleSlide.Builder {
        private Condition forwardCondition;

        public ForwardConditionedBuilder canGoForward(Condition condition) {
            forwardCondition = condition;
            return this;
        }

        @Override
        public SimpleSlide build() {
            return new ForwardConditionedSlide(this);
        }
    }

}
