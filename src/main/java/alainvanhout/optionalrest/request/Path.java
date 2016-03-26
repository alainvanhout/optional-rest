package alainvanhout.optionalrest.request;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Path {

    private String step;
    private Queue<String> steps = new LinkedList<>();
    private List<String> passedSteps = new ArrayList<>();

    /**
     * Whether the path has arrived at its final step.
     *
     * @return Arrived
     */
    public boolean isArrived() {
        return steps.isEmpty();
    }

    public String nextStep() {
        step = steps.poll();
        passedSteps.add(step);
        return step;
    }

    public String getStep() {
        return step;
    }

    public Path steps(Queue<String> steps) {
        this.steps = steps;
        return this;
    }

    public List<String> getPassedSteps() {
        return passedSteps;
    }
}
